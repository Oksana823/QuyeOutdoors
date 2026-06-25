package com.quye.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.quye.dto.Result;
import com.quye.entity.OutdoorPlace;
import com.quye.service.IOutdoorPlaceService;
import com.quye.utils.SystemConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/places")
@Slf4j
public class OutdoorPlaceController {

    @Resource
    public IOutdoorPlaceService outdoorPlaceService;

    /**
     * 根据id查询目的地信息
     * @param id 目的地id
     * @return 目的地详情数据
     */
    @GetMapping("/{id}")
    public Result queryOutdoorPlaceById(@PathVariable("id") Long id) {
        return outdoorPlaceService.queryById(id);
    }

    /**
     * 新增目的地信息
     * @param place 目的地数据
     * @return 目的地id
     */
    @PostMapping
    public Result saveOutdoorPlace(@RequestBody OutdoorPlace place) {
        outdoorPlaceService.save(place);
        return Result.ok(place.getId());
    }

    /**
     * 更新目的地信息
     * @param place 目的地数据
     * @return 无
     */
    @PutMapping
    public Result updateOutdoorPlace(@RequestBody OutdoorPlace place) {
        log.info("接收到更新请求，place={}", place); // 加这行
        return outdoorPlaceService.update(place);
    }

    /**
     * 根据目的地类型分页查询目的地信息
     * @param categoryId 目的地类型
     * @param current 页码
     * @return 目的地列表
     */
    @GetMapping("/of/type")
    public Result queryOutdoorPlaceByType(
            @RequestParam("categoryId") Integer categoryId,
            @RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestParam(value = "x", required = false) Double x,
            @RequestParam(value = "y", required = false) Double y
    ) {

        return outdoorPlaceService.queryOutdoorPlaceByType(categoryId,current,x,y);
    }

    /**
     * 根据目的地名称关键字分页查询目的地信息
     * @param name 目的地名称关键字
     * @param current 页码
     * @return 目的地列表
     */
    @GetMapping("/of/name")
    public Result queryOutdoorPlaceByName(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "current", defaultValue = "1") Integer current
    ) {
        Page<OutdoorPlace> page = outdoorPlaceService.query()
                .like(StrUtil.isNotBlank(name), "name", name)
                .page(new Page<>(current, SystemConstants.MAX_PAGE_SIZE));
        return Result.ok(page.getRecords());
    }
}
