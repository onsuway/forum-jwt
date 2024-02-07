package com.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.example.entity.vo.response.WeatherVO;
import com.example.service.WeatherService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

@Service
public class WeatherServiceImpl implements WeatherService {

    @Resource
    RestTemplate rest;

    @Resource
    StringRedisTemplate template;

    @Value("${spring.weather.key}")
    String key;

    /**
     * @description 获取天气信息
     * @param longitude 经度
     * @param latitude 纬度
     * @return WeatherVO 天气VO
     */
    @Override
    public WeatherVO fetchWeather(double longitude, double latitude) {
        return fetchFromCache(longitude, latitude);
    }

    /**
     * @description 从缓存里获取天气信息
     * @param longitude 经度
     * @param latitude 纬度
     * @return WeatherVO 天气VO
     */
    private WeatherVO fetchFromCache(double longitude, double latitude) {
        // 从和风天气API根据经纬度获取城市信息
        JSONObject geo = this.decompressStringToJson(rest.getForObject(
                "https://geoapi.qweather.com/v2/city/lookup?location=" + longitude + "," + latitude + "&key=" + key, byte[].class));
        if (geo == null) return null;
        JSONObject location = geo.getJSONArray("location").getJSONObject(0);
        // 获取城市ID 从缓存中根据城市ID获取天气信息
        String id = location.getString("id");
        String key = Const.FORUM_WEATHER_CACHE + id;
        String cache = template.opsForValue().get(key);
        if (cache != null)
            return JSONObject.parseObject(cache).to(WeatherVO.class);
        // 如果缓存中没有则从API获取天气信息并存入缓存
        WeatherVO vo = this.fetchFromAPI(id, location);
        if (vo == null) return null;
        template.opsForValue().set(key, JSONObject.from(vo).toJSONString(), 1, TimeUnit.HOURS);
        return vo;
    }

    /**
     * @description 从API获取天气信息
     * @param id 城市ID
     * @param location 城市信息
     * @return WeatherVO 天气VO
     */
    private WeatherVO fetchFromAPI(String id, JSONObject location){
        WeatherVO vo = new WeatherVO();
        vo.setLocation(location);
        JSONObject now = this.decompressStringToJson(rest.getForObject(
                "https://devapi.qweather.com/v7/weather/now?location=" + id + "&key=" + key, byte[].class));
        if (now == null) return null;
        vo.setNow(now.getJSONObject("now"));
        JSONObject hourly = this.decompressStringToJson(rest.getForObject(
                "https://devapi.qweather.com/v7/weather/24h?location=" + id + "&key=" + key, byte[].class));
        if (hourly == null) return null;
        vo.setHourly(new JSONArray(hourly.getJSONArray("hourly").stream().limit(5).toList()));
        return vo;
    }

    /**
     * @description 将Gzip的byte数组解压为JSON对象字符串
     * @param data 待解压的Gzip的byte数组
     * @return JsonObject JSON对象字符串
     */
    private JSONObject decompressStringToJson(byte[] data){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(data));
            byte[] buffer = new byte[1024];
            int read;
            while ((read = gzip.read(buffer)) != -1) {
                stream.write(buffer, 0, read);
            }
            gzip.close();
            stream.close();
            return JSONObject.parseObject(stream.toString());
        } catch (IOException e) {
            return null;
        }
    }
}
