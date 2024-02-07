package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.dto.StoreImage;
import com.example.mapper.AccountMapper;
import com.example.mapper.ImageStoreMapper;
import com.example.service.ImageService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import io.minio.*;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class ImageServiceImpl extends ServiceImpl<ImageStoreMapper, StoreImage> implements ImageService {

    @Resource
    MinioClient client;

    @Resource
    AccountMapper mapper;

    @Resource
    FlowUtils flowUtils;

    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

    /**
     * @description 向Minio上传图片
     * @param file 图片文件
     * @param id 用户ID
     * @return String 图片的URL 返回null则上传失败
     */
    @Override
    public String uploadImage(MultipartFile file, int id) throws IOException {
        String key = Const.FORUM_IMAGE_COUNTER + id;
        if (!flowUtils.limitPeriodCounterCheck(key, 20, 3600))
            return null;
        String imageName = UUID.randomUUID().toString().replace("-", "");
        Date date = new Date();
        imageName = "/cache/" + format.format(date) + "/" + imageName;
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket("study")
                .object(imageName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .build();
        try {
            client.putObject(args);
            if (this.save(new StoreImage(id, imageName, date)))
                return imageName;
            else
                return null;
        } catch (Exception e) {
            log.error("图片上传出现问题：" + e.getMessage(), e);
            return null;
        }
    }

    /**
     * @description 向Minio上传头像
     * @param file 头像文件
     * @param id 用户ID
     * @return String 头像的URL 返回null则上传失败
     */
    @Override
    public String uploadAvatar(MultipartFile file, int id) throws IOException {
        String imageName = UUID.randomUUID().toString().replace("-", "");
        imageName = "/avatar/" + imageName;
        PutObjectArgs args = PutObjectArgs.builder()
                .bucket("study")
                .object(imageName)
                .stream(file.getInputStream(), file.getSize(), -1)
                .build();
        try {
            client.putObject(args);
            String avatar = mapper.selectById(id).getAvatar();
            this.deleteOldAvatar(avatar);
            if (mapper.update(null,
                    Wrappers.<Account>update()
                            .eq("id", id)
                            .set("avatar", imageName)) > 0)

                return imageName;
            else
                return null;
        } catch (Exception e) {
            log.error("头像上传出现问题：" + e.getMessage(), e);
            return null;
        }
    }

    //删除旧头像
    private void deleteOldAvatar(String avatar) throws Exception {
        if (avatar == null || avatar.isEmpty()) return;
        RemoveObjectArgs remove = RemoveObjectArgs.builder()
                .bucket("study")
                .object(avatar)
                .build();
        client.removeObject(remove);
    }

    /**
     * @description 从Minio获取图片
     * @param stream 输出流
     * @param image 图片路径
     */
    @Override
    public void fetchImageFromMinio(OutputStream stream, String image) throws Exception {
        GetObjectArgs args = GetObjectArgs.builder()
                .bucket("study")
                .object(image)
                .build();
        GetObjectResponse response = client.getObject(args);
        IOUtils.copy(response, stream);
    }
}
