package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @ClassName TopicPreviewVO
 * @Description 论坛主页的帖子预览VO
 * @Author su
 * @Date 2023/10/14 16:03
 */
@Data
public class TopicPreviewVO {
    int id;
    int type;
    String title;
    String text;
    List<String> images;
    Date time;
    Integer uid;
    String username;
    String avatar;
    int like;
    int collect;
}
