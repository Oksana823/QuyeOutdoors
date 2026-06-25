package com.quye.controller;


import cn.hutool.core.bean.BeanUtil;
import com.quye.dto.LoginFormDTO;
import com.quye.dto.Result;
import com.quye.dto.UserDTO;
import com.quye.entity.User;
import com.quye.entity.UserProfile;
import com.quye.service.IUserProfileService;
import com.quye.service.IUserService;
import com.quye.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpSession;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    @Resource
    private IUserService userService;

    @Resource
    private IUserProfileService userProfileService;

    /**
     * 发送手机验证码
     */
    @PostMapping("code")
    public Result sendCode(@RequestParam("phone") String phone, HttpSession session) {
        return userService.sendCode(phone,session);
    }

    /**
     * 登录功能
     * @param loginForm 登录参数，包含手机号、验证码；或者手机号、密码
     */
    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginForm, HttpSession session){
        //
        return userService.login(loginForm,session);
    }

    /**
     * 登出功能
     * @return 无
     */
    @PostMapping("/logout")
    public Result logout(@RequestHeader("authorization") String token){
        return userService.logout(token);
    }

    @GetMapping("/me")
    public Result me(){
        UserDTO user = UserHolder.getUser();
        return Result.ok(user);
    }

    @GetMapping("/info/{id}")
    public Result info(@PathVariable("id") Long userId){
        UserProfile info = userProfileService.getById(userId);
        if (info == null) {
            // 没有详情，应该是第一次查看详情
            return Result.ok();
        }
        info.setCreateTime(null);
        info.setUpdateTime(null);
        return Result.ok(info);
    }
    @PutMapping("/info")
    public Result updateProfile(@RequestBody UserProfile profile) {
        profile.setUserId(UserHolder.getUser().getId());
        return userProfileService.saveOrUpdate(profile) ? Result.ok() : Result.fail("档案保存失败");
    }

    @GetMapping("/{id}")
    public Result queryUserById(@PathVariable("id") Long userId){
        User user = userService.getById(userId);
        if (user == null) {
            return Result.ok();
        }
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);
        return Result.ok(userDTO);
    }
    @PostMapping("/sign")
    public Result sign(){
        return userService.sign();
    }
    @GetMapping("/sign/count")
    public Result signCount(){
        return userService.signCount();
    }
}
