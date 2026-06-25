package com.quye.controller;


import com.quye.dto.Result;
import com.quye.entity.ExperiencePass;
import com.quye.service.IExperiencePassService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/passes")
public class ExperiencePassController {

    @Resource
    private IExperiencePassService experiencePassService;

    /**
     * 新增普通券
     * @param experiencePass 体验券信息
     * @return 体验券id
     */
    @PostMapping
    public Result addExperiencePass(@RequestBody ExperiencePass experiencePass) {
        experiencePassService.save(experiencePass);
        return Result.ok(experiencePass.getId());
    }

    /**
     * 新增限量体验券
     * @param experiencePass 体验券信息，包含限量活动信息
     * @return 体验券id
     */
    @PostMapping("flash")
    public Result addFlashPass(@RequestBody ExperiencePass experiencePass) {
        experiencePassService.addFlashPass(experiencePass);
        return Result.ok(experiencePass.getId());
    }

    /**
     * 查询目的地的体验券列表
     * @param placeId 目的地id
     * @return 体验券列表
     */
    @GetMapping("/list/{placeId}")
    public Result queryPassesOfPlace(@PathVariable("placeId") Long placeId) {
       return experiencePassService.queryPassesOfPlace(placeId);
    }
}
