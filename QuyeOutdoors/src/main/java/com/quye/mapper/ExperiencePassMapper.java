package com.quye.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.quye.entity.ExperiencePass;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ExperiencePassMapper extends BaseMapper<ExperiencePass> {

    List<ExperiencePass> queryPassesOfPlace(@Param("placeId") Long placeId);
}
