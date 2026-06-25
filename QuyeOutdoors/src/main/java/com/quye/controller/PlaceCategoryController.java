package com.quye.controller;


import com.quye.dto.Result;
import com.quye.entity.PlaceCategory;
import com.quye.service.IPlaceCategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/place-categories")
public class PlaceCategoryController {
    @Resource
    private IPlaceCategoryService typeService;

    @GetMapping("list")
    public Result queryTypeList() {
        List<PlaceCategory> categories = typeService.queryPlaceCategory();
        return Result.ok(categories);
    }
}
