package com.quye.service.impl;

import com.quye.entity.UserProfile;
import com.quye.mapper.UserProfileMapper;
import com.quye.service.IUserProfileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class UserProfileServiceImpl extends ServiceImpl<UserProfileMapper, UserProfile> implements IUserProfileService {

}
