package com.example.controller;

import com.example.entity.RestBean;
import com.example.service.ImageService;
import io.minio.errors.ErrorResponseException;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName ObjectController
 * @Description 获取对象存储桶中文件相关操作
 * @Author su
 * @Date 2023/9/23 16:43
 */
@Slf4j
@RestController
public class ObjectController {

    @Resource
    ImageService service;

    @GetMapping("/images/**")
    public void imageFetch(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setHeader("Content-Type", "image/jpg");
        this.fetchImage(request, response);
    }

    private void fetchImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //获取图片路径 截取掉前面的‘/images’
        String imagePath = request.getServletPath().substring(7);
        ServletOutputStream stream = response.getOutputStream();
        if (imagePath.length() <= 13) {
            response.setStatus(404);
            stream.println(RestBean.failure(404, "Not Found").toString());
        } else {
            try {
                service.fetchImageFromMinio(stream, imagePath);
                //成功获取到图片后 设置缓存时间为30天
                response.setHeader("Cache-Control", "max-age=2592000");
            } catch (ErrorResponseException e) {
                if (e.response().code() == 404) {
                    response.setStatus(404);
                    stream.println(RestBean.failure(404, "Not Found").toString());
                } else {
                    log.error("从Minio获取图片出现异常：" + e.getMessage(), e);
                }
            }
        }
    }
}
