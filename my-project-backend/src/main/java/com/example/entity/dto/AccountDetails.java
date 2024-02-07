package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName AccountDetails
 * @Description 用户详细信息类
 * @Author su
 * @Date 2023/9/19 20:51
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("db_account_details")
public class AccountDetails implements BaseData{
    @TableId
    Integer id;
    int gender;
    String phone;
    String qq;
    String wx;
    //desc是sql关键字 mybatis-plus无法识别 必须加``
    @TableField("`desc`")
    String desc;

    public AccountDetails(Integer id) {
        this.id = id;
    }
}
