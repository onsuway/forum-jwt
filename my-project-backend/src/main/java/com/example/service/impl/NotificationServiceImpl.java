package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Notification;
import com.example.entity.vo.response.NotificationVO;
import com.example.mapper.NotificationMapper;
import com.example.service.NotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName NotificationServiceImpl
 * @Description 提醒Service实现类
 * @Author su
 * @Date 2024/1/22 16:21
 */
@Service
public class NotificationServiceImpl extends ServiceImpl<NotificationMapper, Notification> implements NotificationService {

    /**
     * @description 获取用户提醒列表
     * @param uid 用户ID
     * @return List<NotificationVO> 提醒列表
     */
    @Override
    public List<NotificationVO> findUserNotification(int uid) {
        return this.list(Wrappers.<Notification>query().eq("uid", uid))
                .stream()
                .map(notification -> notification.asViewObject(NotificationVO.class))
                .toList();
    }

    /**
     * @description 删除用户单个提醒
     * @param id 提醒ID
     * @param uid 用户ID
     */
    @Override
    public void deleteUserNotification(int id, int uid) {
        this.remove(Wrappers.<Notification>query().eq("id", id).eq("uid", uid));
    }

    /**
     * @description 删除用户所有提醒
     * @param uid 用户ID
     */
    @Override
    public void deleteUserAllNotification(int uid) {
        this.remove(Wrappers.<Notification>query().eq("uid", uid));
    }

    /**
     * @description 添加提醒
     * @param uid 用户ID
     * @param title 提醒标题
     * @param content 提醒内容
     * @param type 提醒类别（对照ElementPlus：Success/Warning等）
     * @param url 提醒的帖子链接
     */
    @Override
    public void addNotification(int uid, String title, String content, String type, String url) {
        Notification notification = new Notification();
        notification.setUid(uid);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setUrl(url);
        this.save(notification);
    }
}
