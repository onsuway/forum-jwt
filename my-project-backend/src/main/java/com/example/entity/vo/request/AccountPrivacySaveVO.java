package com.example.entity.vo.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * @ClassName AccountPrivacySaveVO
 * @Description 用户隐私信息更改的表单信息
 * @Author su
 * @Date 2023/9/22 15:57
 */
@Data
public class AccountPrivacySaveVO {
    @Pattern(regexp = "(phone|qq|wx|email|gender)")
    String type;
    boolean status;
}
