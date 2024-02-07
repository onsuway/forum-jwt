package com.example.entity.vo.response;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

/**
 * @ClassName WeatherVO
 * @Description 返回给前端的天气信息
 * @Author su
 * @Date 2023/10/7 15:26
 */
@Data
public class WeatherVO {
    JSONObject location;
    JSONObject now;
    JSONArray hourly;
}
