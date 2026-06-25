package com.quye.service;

import com.baomidou.mybatisplus.core.conditions.interfaces.Func;
import com.quye.entity.PlaceCategory;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface IPlaceCategoryService extends IService<PlaceCategory> {

    List<PlaceCategory> queryPlaceCategory();
}
