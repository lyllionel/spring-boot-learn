package com.lionel.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.concurrent.TimeUnit;

@Slf4j
public class RedisTemplateDistributeLock {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final int RETRY_COUNT = 3;

    private static final String LOCK_PREFIX = "DISTRIBUTE_LOCK_";

    /**
     * 尝试获取锁，超时会自动释放锁
     * @param key
     * @param value
     * @param timeOut  默认超时时长10秒
     * @param timeUnit 默认单位为秒
     * @return
     */
    public boolean tryLock(String key, String value, Long timeOut, TimeUnit timeUnit) {
        Boolean isOk = false;
        int retryCount = RETRY_COUNT;
        if (timeOut == null) {
            timeOut = 10L;
        }
        if (timeUnit == null) {
            timeUnit = TimeUnit.SECONDS;
        }

        while (!isOk && retryCount > 0) {
            isOk = redisTemplate.opsForValue().setIfAbsent(LOCK_PREFIX + key, value, timeOut, timeUnit);
            retryCount--;
        }

        log.error(LOCK_PREFIX + key + " 锁获取失败！");
        return isOk;
    }

    public boolean tryLock(String key, String value, Long timeOut) {
        return tryLock(key, value, timeOut, null);
    }

    public boolean tryLock(String key, String value) {
        return tryLock(key, value, null, null);
    }

}
