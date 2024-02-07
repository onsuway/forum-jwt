package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName TopicType
 * @Description 帖子类型DTO
 * @Author su
 * @Date 2023/10/13 22:41
 */
@Data
@TableName("db_topic_type")
public class TopicType implements BaseData {
    @TableId
    Integer id;
    String name;
    @TableField("`desc`")
    String desc;
    String color;
}
