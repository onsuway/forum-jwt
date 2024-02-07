package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.BaseData;
import lombok.Data;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;

/**
 * @ClassName AccountPrivacy
 * @Description 用户隐私信息
 * @Author su
 * @Date 2023/9/22 16:01
 */
@Data
@TableName("db_account_privacy")
public class AccountPrivacy implements BaseData {
    @TableId(type = IdType.AUTO)
    final Integer id;
    boolean phone = true;
    boolean qq = true;
    boolean wx = true;
    boolean email = true;
    boolean gender = true;

    /**
     * @description 获取所有隐藏的字段
     * @return 一个字符串数组，包含所有隐藏的字段名
     */
    public String[] hiddenFields() {
        List<String> strings = new LinkedList<>();
        Field[] fields = this.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                if (field.getType().equals(boolean.class) && !field.getBoolean(this))
                    strings.add(field.getName());
            } catch (Exception ignore) {}
        }
        return strings.toArray(String[]::new);
    }
}
