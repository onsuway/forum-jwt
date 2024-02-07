package com.example.entity.vo.response;

import lombok.Data;

/**
 * @ClassName TopicTypeVO
 * @Description 论坛帖子类型VO
 * @Author su
 * @Date 2023/10/13 22:39
 */
@Data
public class TopicTypeVO {
    Integer id;
    String name;
    String desc;
    String color;
}
