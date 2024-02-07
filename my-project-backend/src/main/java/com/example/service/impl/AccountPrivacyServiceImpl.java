package com.example.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.AccountPrivacy;
import com.example.entity.vo.request.AccountPrivacySaveVO;
import com.example.mapper.AccountPrivacyMapper;
import com.example.service.AccountPrivacyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @ClassName AccountPrivacyServiceImpl
 * @Description 用户隐私信息相关服务
 * @Author su
 * @Date 2023/9/22 16:05
 */
@Service
public class AccountPrivacyServiceImpl extends ServiceImpl<AccountPrivacyMapper, AccountPrivacy> implements AccountPrivacyService {

    /**
     * @description 保存用户隐私信息
     * @param id 用户ID
     * @param vo 用户隐私信息
     */
    @Override
    @Transactional
    public void savePrivacy(int id, AccountPrivacySaveVO vo) {
        //如果用户隐私信息表中没有此用户的记录，则创建一条新的记录
        AccountPrivacy privacy = Optional.ofNullable(this.getById(id)).orElse(new AccountPrivacy(id));
        boolean status = vo.isStatus();
        switch (vo.getType()) {
            case "phone" -> privacy.setPhone(status);
            case "qq" -> privacy.setQq(status);
            case "wx" -> privacy.setWx(status);
            case "gender" -> privacy.setGender(status);
            case "email" -> privacy.setEmail(status);
        }
        this.saveOrUpdate(privacy);
    }

    /**
     * @description 获取用户隐私信息（如果没有就创建新的）
     * @param id 用户ID
     * @return 用户隐私信息
     */
    @Override
    public AccountPrivacy accountPrivacy(int id) {
        return Optional.ofNullable(this.getById(id)).orElse(new AccountPrivacy(id));
    }
}
