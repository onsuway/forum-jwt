package com.example.utils;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;


/**
 * @ClassName CacheUtils
 * @Description 缓存工具类
 * @Author su
 * @Date 2023/10/14 21:09
 */
@Component
public class CacheUtils {
    @Resource
    StringRedisTemplate template;

    /**
     * @description 将数据存入缓存
     * @param key 键
     * @param data 数据
     * @param expire 过期时间，单位秒
     */
    public <T> void saveToCache(String key, T data, long expire) {
        template.opsForValue().set(key, JSONArray.from(data).toJSONString(), expire, TimeUnit.SECONDS);
    }

    /**
     * @description 将列表数据存入缓存
     * @param key 键
     * @param list 列表数据
     * @param expire 过期时间，单位秒
     */
    public <T> void saveListToCache(String key, List<T> list, long expire) {
        template.opsForValue().set(key, JSONArray.from(list).toJSONString(), expire, TimeUnit.SECONDS);
    }

    /**
     * @description 从缓存中取出列表数据
     * @param key 键
     * @param itemType 列表元素类型
     * @return 列表数据
     */
    public <T> List<T> takeListFromCache(String key, Class<T> itemType) {
        String s = template.opsForValue().get(key);
        return s == null ? null : JSONArray.parseArray(s).toList(itemType);
    }

    /**
     * @description 从缓存中取出数据
     * @param key 键
     * @param dataType 数据类型
     * @return 数据
     */
    public <T> T takeFromCache(String key, Class<T> dataType) {
        String s = template.opsForValue().get(key);
        return s == null ? null : JSONObject.parseObject(s).to(dataType);
    }

    /**
     * @description 从缓存中删除数据
     * @param key 键
     */
    public void deleteCache(String key) {
        template.delete(key);
    }

    /**
     * @description 从缓存中批量删除相同开头key的数据
     * @param key 相同的开头key + “*”
     */
    public void deleteCachePattern(String key) {
        Set<String> keys = Optional.ofNullable(template.keys(key)).orElse(Collections.emptySet());
        template.delete(keys);
    }
}
