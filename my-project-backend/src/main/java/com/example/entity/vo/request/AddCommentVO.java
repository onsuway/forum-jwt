package com.example.entity.vo.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * @ClassName AddCommentVO
 * @Description 添加评论VO
 * @Author su
 * @Date 2024/1/21 14:38
 */
@Data
public class AddCommentVO {
    @Min(1)
    int tid;
    String content;
    @Min(-1)
    int quote;  // 引用的评论id
}
