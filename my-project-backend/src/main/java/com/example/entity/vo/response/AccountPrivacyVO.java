package com.example.entity.vo.response;

import lombok.Data;

/**
 * @ClassName AccountPrivacyVO
 * @Description 返回前端的用户隐私信息
 * @Author su
 * @Date 2023/9/22 16:17
 */
@Data
public class AccountPrivacyVO {
    boolean phone;
    boolean qq;
    boolean wx;
    boolean email;
    boolean gender;
}
