package com.quye.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.quye.dto.Result;
import com.quye.entity.PassOrder;
import com.quye.mapper.PassOrderMapper;
import com.quye.service.IFlashPassService;
import com.quye.service.IPassOrderService;
import com.quye.utils.RedisIdWorker;
import com.quye.utils.UserHolder;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class PassOrderServiceImpl extends ServiceImpl<PassOrderMapper, PassOrder> implements IPassOrderService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private IFlashPassService flashPassService;
    @Resource
    private RedisIdWorker redisIdWorker;
    @Resource
    private RedissonClient redissonClient;
    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    @Lazy
    private IPassOrderService self;

    private static final DefaultRedisScript<Long> FLASH_PASS_SCRIPT;

    static {
        FLASH_PASS_SCRIPT = new DefaultRedisScript<>();
        FLASH_PASS_SCRIPT.setLocation(new ClassPathResource("claim-pass.lua"));
        FLASH_PASS_SCRIPT.setResultType(Long.class);
    }

    @RabbitListener(queues = "quye.pass.order.queue")
    public void handlePassOrderMessage(PassOrder passOrder, Channel channel, Message message) throws IOException {
        try {
            handlePassOrder(passOrder);
        } catch (Exception e) {
            log.error("处理体验券订单失败，回滚 Redis 预扣库存，orderId={}", passOrder.getId(), e);
            rollbackReservation(passOrder.getPassId(), passOrder.getUserId());
            channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
            return;
        }
        channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
    }

    private void handlePassOrder(PassOrder passOrder) {
        Long userId = passOrder.getUserId();
        RLock lock = redissonClient.getLock("quye:lock:pass-order:" + userId);
        lock.lock(10, TimeUnit.SECONDS);
        try {
            self.createPassOrder(passOrder);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }

    @Override
    public Result claimFlashPass(Long passId) {
        Long userId = UserHolder.getUser().getId();
        long orderId = redisIdWorker.nextId("order");
        Long result = stringRedisTemplate.execute(
                FLASH_PASS_SCRIPT,
                Collections.emptyList(),
                passId.toString(), userId.toString(), String.valueOf(orderId)
        );
        if (result == null) {
            return Result.fail("系统繁忙，请重试");
        }
        int code = result.intValue();
        if (code != 0) {
            return Result.fail(code == 1 ? "库存不足" : "不能重复领取");
        }

        PassOrder passOrder = new PassOrder();
        passOrder.setId(orderId);
        passOrder.setUserId(userId);
        passOrder.setPassId(passId);
        try {
            rabbitTemplate.convertAndSend("quye.pass.order.exchange", "quye.pass.order.create", passOrder);
        } catch (Exception e) {
            log.error("发送体验券订单消息失败，回滚 Redis 预扣库存，orderId={}", orderId, e);
            rollbackReservation(passId, userId);
            return Result.fail("系统繁忙，请重试");
        }
        return Result.ok(orderId);
    }

    @Override
    @Transactional
    public void createPassOrder(PassOrder passOrder) {
        long count = query()
                .eq("user_id", passOrder.getUserId())
                .eq("pass_id", passOrder.getPassId())
                .count();
        if (count > 0) {
            return;
        }

        boolean stockUpdated = flashPassService.update()
                .setSql("stock = stock - 1")
                .eq("pass_id", passOrder.getPassId())
                .gt("stock", 0)
                .update();
        if (!stockUpdated) {
            throw new IllegalStateException("体验券库存不足");
        }
        if (!save(passOrder)) {
            throw new IllegalStateException("体验券订单保存失败");
        }
    }

    private void rollbackReservation(Long passId, Long userId) {
        stringRedisTemplate.opsForValue().increment("quye:flash:stock:" + passId, 1);
        stringRedisTemplate.opsForSet().remove("quye:pass:order:" + passId, userId.toString());
    }
}
