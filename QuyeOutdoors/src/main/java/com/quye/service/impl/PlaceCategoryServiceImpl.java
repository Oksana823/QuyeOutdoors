package com.quye.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.interfaces.Func;
import com.quye.dto.Result;
import com.quye.entity.PlaceCategory;
import com.quye.mapper.PlaceCategoryMapper;
import com.quye.service.IPlaceCategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class PlaceCategoryServiceImpl extends ServiceImpl<PlaceCategoryMapper, PlaceCategory> implements IPlaceCategoryService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public List<PlaceCategory> queryPlaceCategory() {
        String categoryJson = stringRedisTemplate.opsForValue().get("quye:category:list");
        if(StrUtil.isNotBlank(categoryJson)){
            List<PlaceCategory> categories = JSONUtil.toList(categoryJson, PlaceCategory.class);
            return categories;
        }
        List<PlaceCategory> categories = query().orderByAsc("sort").list();
        if (categories == null || categories.isEmpty()) {
            return null;
        }
        stringRedisTemplate.opsForValue().set("quye:category:list", JSONUtil.toJsonStr(categories), 30, TimeUnit.MINUTES);
        return categories;
    }
}
