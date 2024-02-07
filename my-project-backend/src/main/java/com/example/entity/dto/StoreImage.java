package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName StoreImage
 * @Description 用户帖子 存储的图片
 * @Author su
 * @Date 2023/10/12 15:54
 */
@Data
@TableName("db_image_store")
@AllArgsConstructor
public class StoreImage {
    Integer uid;
    String name;
    Date time;
}
