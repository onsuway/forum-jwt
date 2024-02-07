package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName AccountVO
 * @Description 返回前端的用户信息
 * @Author su
 * @Date 2023/9/17 17:34
 */
@Data
public class AccountVO {
    int id;
    String username;
    String email;
    String role;
    String avatar;
    Date registerTime;
}
