package com.quye.service;

import com.quye.dto.Result;
import com.quye.entity.PassOrder;
import com.baomidou.mybatisplus.extension.service.IService;

public interface IPassOrderService extends IService<PassOrder> {

    Result claimFlashPass(Long passId);

    void createPassOrder(PassOrder passOrder);
}
