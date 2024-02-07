package com.example.entity.vo.response;

import lombok.Data;

/**
 * @ClassName AccountDetailsVO
 * @Description 返回前端的用户详细信息
 * @Author su
 * @Date 2023/9/19 21:20
 */
@Data
public class AccountDetailsVO {
    int gender;
    String phone;
    String qq;
    String wx;
    String desc;
}
