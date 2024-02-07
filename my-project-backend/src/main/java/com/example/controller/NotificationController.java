package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.vo.response.NotificationVO;
import com.example.service.NotificationService;
import com.example.utils.Const;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Min;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @ClassName NotificationController
 * @Description 消息提醒相关接口
 * @Author su
 * @Date 2024/1/22 16:48
 */
@RestController
@RequestMapping("/api/notification")
public class NotificationController {
    @Resource
    NotificationService service;

    @GetMapping("/list")
    public RestBean<List<NotificationVO>> listNotification(@RequestAttribute(Const.ATTR_USER_ID) int uid){
        return RestBean.success(service.findUserNotification(uid));
    }

    @GetMapping("/delete")
    public RestBean<Void> deleteNotification(@RequestAttribute(Const.ATTR_USER_ID) int uid,
                                             @RequestParam @Min(0) int id){
        service.deleteUserNotification(id, uid);
        return RestBean.success();
    }

    @GetMapping("/delete-all")
    public RestBean<Void> deleteAllNotification(@RequestAttribute(Const.ATTR_USER_ID) int uid){
        service.deleteUserAllNotification(uid);
        return RestBean.success();
    }
}
