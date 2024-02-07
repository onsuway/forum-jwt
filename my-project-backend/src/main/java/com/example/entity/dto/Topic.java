package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName Topic
 * @Description 帖子DTO
 * @Author su
 * @Date 2023/10/13 22:55
 */
@Data
@TableName("db_topic")
public class Topic {
    @TableId(type = IdType.AUTO)
    Integer id;
    String title;
    String content;
    Integer type;
    Date time;
    Integer uid;
}
