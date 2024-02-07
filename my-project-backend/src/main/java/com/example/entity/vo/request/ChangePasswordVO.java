package com.example.entity.vo.request;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
 * @ClassName ChangePasswordVO
 * @Description 更改密码表单信息
 * @Author su
 * @Date 2023/9/22 14:46
 */
@Data
public class ChangePasswordVO {
    @Length(min = 6, max = 16)
    String password;
    @Length(min = 6, max = 16)
    String new_password;
}
