package com.quye.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.quye.dto.LoginFormDTO;
import com.quye.dto.Result;
import com.quye.entity.User;
import jakarta.servlet.http.HttpSession;

public interface IUserService extends IService<User> {

    Result sendCode(String phone, HttpSession session);

    Result login(LoginFormDTO loginForm, HttpSession session);

    Result logout(String token);

    Result sign();

    Result signCount();
}
