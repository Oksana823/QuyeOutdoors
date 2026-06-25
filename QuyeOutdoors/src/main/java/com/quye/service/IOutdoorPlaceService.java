package com.quye.service;

import com.quye.dto.Result;
import com.quye.entity.OutdoorPlace;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IOutdoorPlaceService extends IService<OutdoorPlace> {

    Result queryById(Long id);

    Result update(OutdoorPlace place);

    Result queryOutdoorPlaceByType(Integer categoryId, Integer current, Double x, Double y);
}
