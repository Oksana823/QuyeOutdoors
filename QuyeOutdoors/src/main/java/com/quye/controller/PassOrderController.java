package com.quye.controller;


import com.quye.dto.Result;
import com.quye.service.IPassOrderService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pass-orders")
public class PassOrderController {
    @Resource
    private IPassOrderService passOrderService;
    @PostMapping("flash/{id}")
    public Result claimFlashPass(@PathVariable("id") Long passId) {

        return passOrderService.claimFlashPass(passId);
    }
}
