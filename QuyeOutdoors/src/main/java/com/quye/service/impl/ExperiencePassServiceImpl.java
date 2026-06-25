package com.quye.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quye.dto.Result;
import com.quye.entity.ExperiencePass;
import com.quye.mapper.ExperiencePassMapper;
import com.quye.entity.FlashPass;
import com.quye.service.IFlashPassService;
import com.quye.service.IExperiencePassService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.util.List;

import static com.quye.utils.RedisConstants.FLASH_PASS_STOCK_KEY;

@Service
public class ExperiencePassServiceImpl extends ServiceImpl<ExperiencePassMapper, ExperiencePass> implements IExperiencePassService {

    @Resource
    private IFlashPassService flashPassService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Override
    public Result queryPassesOfPlace(Long placeId) {
        List<ExperiencePass> passes = getBaseMapper().queryPassesOfPlace(placeId);
        return Result.ok(passes);
    }

    @Override
    @Transactional
    public void addFlashPass(ExperiencePass experiencePass) {
        save(experiencePass);
        FlashPass flashPass = new FlashPass();
        flashPass.setPassId(experiencePass.getId());
        flashPass.setStock(experiencePass.getStock());
        flashPass.setBeginTime(experiencePass.getBeginTime());
        flashPass.setEndTime(experiencePass.getEndTime());
        flashPassService.save(flashPass);

        stringRedisTemplate.opsForValue().set(FLASH_PASS_STOCK_KEY + experiencePass.getId(), experiencePass.getStock().toString());
    }
}
