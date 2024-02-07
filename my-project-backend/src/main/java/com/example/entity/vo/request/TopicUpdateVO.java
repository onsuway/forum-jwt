package com.example.entity.vo.request;

import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @ClassName TopicUpdateVO
 * @Description 帖子更新VO
 * @Author su
 * @Date 2023/11/26 20:10
 */
@Data
public class TopicUpdateVO {
    @Min(0)
    int id;
    @Min(1)
    int type;
    @Length(min = 1, max = 30)
    String title;
    JSONObject content;
}
