package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Account;
import com.example.entity.vo.request.ChangePasswordVO;
import com.example.entity.vo.request.EmailConfirmVO;
import com.example.entity.vo.request.RegisterByEmailVO;
import com.example.entity.vo.request.ResetPasswordByEmailVO;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<Account>, UserDetailsService {
    Account findAccountByNameOrEmail(String text);
    Account findAccountById(int id);
    String registerEmailVerifyCode(String type, String email, String address);
    String registerEmailAccount(RegisterByEmailVO info);
    String resetEmailAccountPassword(ResetPasswordByEmailVO info);
    String resetConfirm(EmailConfirmVO info);
    String modifyEmail(int id, EmailConfirmVO vo);

    String changePassword(int id, ChangePasswordVO vo);
}
