package com.example.entity.vo.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @ClassName AccountDetailsSaveVO
 * @Description 用户信息更改的表单信息
 * @Author su
 * @Date 2023/9/19 20:58
 */
@Data
public class AccountDetailsSaveVO {

    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]+$")
    @Length(min = 4, max = 20)
    String username;

    @Min(0) //男
    @Max(1) //女
    int gender;

    @Pattern(regexp = "^(1[3-9]\\d{9})?$")
    String phone;

    @Length(max = 13)
    String qq;

    @Length(max = 20)
    String wx;

    @Length(max = 200)
    String desc;
}
