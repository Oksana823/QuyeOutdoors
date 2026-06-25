package com.quye.service;

import com.quye.dto.Result;
import com.quye.entity.ExperiencePass;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IExperiencePassService extends IService<ExperiencePass> {

    Result queryPassesOfPlace(Long placeId);

    void addFlashPass(ExperiencePass experiencePass);
}
