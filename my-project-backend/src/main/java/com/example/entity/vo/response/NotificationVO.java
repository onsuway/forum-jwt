package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName NotificationVO
 * @Description 提醒VO
 * @Author su
 * @Date 2024/1/22 16:24
 */
@Data
public class NotificationVO {
    Integer id;
    String title;
    String content;
    String type;
    String url;
    Date time;
}
