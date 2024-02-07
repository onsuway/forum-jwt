package com.example.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName Interact
 * @Description 交互（点赞/收藏）实体类
 * @Author su
 * @Date 2023/11/22 23:03
 */
@Data
@AllArgsConstructor
public class Interact {
    Integer tid;
    Integer uid;
    Date time;
    String type;


    // 用于存入redis的key
    public String toKey() {
        return tid + ":" + uid;
    }

    // 用于解析从redis中取出的交互数据
    public static Interact parseInteract(String str, String type) {
        String[] keys = str.split(":");
        return new Interact(
                Integer.parseInt(keys[0]),
                Integer.parseInt(keys[1]),
                new Date(), type);
    }
}
