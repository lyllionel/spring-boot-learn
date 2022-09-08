package com.lionel.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.TimeUnit;

/**
 * 采用SpringBoot2 spring-boot-starter-data-redis setIfAbsent()原子性操作
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    private static int stock = 100;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @GetMapping("/create")
    @ResponseBody
    public String create(Integer currStock) {
        if (stock > 0) {
            Boolean isOk;
            if (currStock != null) {
                // 保证原子性操作
                isOk = redisTemplate.opsForValue().setIfAbsent("lock_product_" + currStock, currStock+"", 60, TimeUnit.SECONDS);
            } else {
                isOk = redisTemplate.opsForValue().setIfAbsent("lock_product_" + stock, stock+"", 60, TimeUnit.SECONDS);
            }
            if (isOk) {
                stock--;
                System.out.println("当前剩余库存: " + OrderController.stock);
                return "当前剩余库存: " + stock;
            } else {
                //按理来说 我们应该抛出一个自定义的 CacheLockException 异常;
                return "请勿重复请求";
            }
        }
        return "库存不足";
    }

}
