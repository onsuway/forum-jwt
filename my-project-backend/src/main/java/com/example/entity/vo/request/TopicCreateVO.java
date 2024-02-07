package com.example.entity.vo.request;

import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @ClassName TopicCreateVO
 * @Description 用户创建帖子的请求体
 * @Author su
 * @Date 2023/10/13 23:02
 */
@Data
public class TopicCreateVO {
    @Min(1)
    int type;
    @Length(min = 1, max = 30)
    String title;
    JSONObject content;

}
