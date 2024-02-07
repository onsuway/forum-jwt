package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName TopicTopVO
 * @Description 置顶帖子VO
 * @Author su
 * @Date 2023/10/15 17:06
 */
@Data
public class TopicTopVO {
    int id;
    String title;
    Date time;
}
