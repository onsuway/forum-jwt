package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.Notification;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName NotificationMapper
 * @Description 提醒Mapper
 * @Author su
 * @Date 2024/1/22 16:20
 */
@Mapper
public interface NotificationMapper extends BaseMapper<Notification> {
}
