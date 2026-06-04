package company.restaurant.util;

import company.restaurant.constant.CacheConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Random;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

//Redis缓存工具类（解决缓存三大问题，要用的时候调用方法即可）
@Slf4j
@Component
@RequiredArgsConstructor
public class CacheTool {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final Random random = new Random();
    private static final String NULL_MARK = "NULL";

    /**
     * 获取缓存，带本地锁防击穿
     */
    public <T> T getWithLock(String key, String lockKey,
                             Supplier<T> supplier, Class<T> clazz) {
        // 1. 尝试从 Redis 中拿缓存
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value != null) {
                // 检查是否是空值标记
                if (isNullValue(value)) {
                    log.debug("缓存命中（空值标记），key={}", key);
                    return null;
                }
                log.debug("缓存命中，key={}", key);
                return clazz.cast(value);
            }
        } catch (Exception e) {
            // 反序列化失败，删除该缓存，重新查询
            log.warn("缓存反序列化失败，删除缓存重新查询，key={}", key, e);
            redisTemplate.delete(key);
        }
        // 2. 本地锁，防止击穿
        synchronized (lockKey.intern()) {
            // 双重检查
            try {
                Object value = redisTemplate.opsForValue().get(key);
                if (value != null) {
                    if (isNullValue(value)) {
                        log.debug("缓存命中（空值标记），双重检查，key={}", key);
                        return null;
                    }
                    log.debug("缓存命中，双重检查，key={}", key);
                    return clazz.cast(value);
                }
            } catch (Exception e) {
                log.warn("缓存反序列化失败（双重检查），key={}", key, e);
                redisTemplate.delete(key);
            }

            // 3. 执行查询逻辑
            T result = supplier.get();

            // 4. 写缓存
            try {
                if (result != null) {
                    writeCache(key, result);
                } else {
                    writeNullValue(key, CacheConstants.DISH_CACHE_NULL_TTL);
                    log.info("查询结果为空，已缓存空值防止穿透，key={}", key);
                }
            } catch (Exception e) {
                log.error("写入缓存失败，key={}", key, e);
                // 写缓存失败不影响业务逻辑，继续返回结果
            }

            return result;
        }
    }

    /**
     * 写缓存，基础过期时间 + 随机额外时间（防雪崩）
     */
    public void writeCache(String key, Object value,
                           int baseTtl, int randomMax) {
        int actualTtl = baseTtl + random.nextInt(randomMax + 1);
        redisTemplate.opsForValue().set(key, value, actualTtl, TimeUnit.MINUTES);
    }

    public void writeCache(String key, Object value) {
        writeCache(key, value,
                CacheConstants.DISH_CACHE_BASE_TTL,
                CacheConstants.DISH_CACHE_RANDOM_MAX);
    }

    /**
     * 缓存空结果（防穿透）
     */
    public void writeNullValue(String key, long ttl) {
        redisTemplate.opsForValue().set(key, NULL_MARK, ttl, TimeUnit.MINUTES);
    }

    /**
     * 判断缓存是否是空结果
     */
    public boolean isNullValue(Object value) {
        return NULL_MARK.equals(value);
    }

    /**
     * 删除缓存
     */
    public void evict(String key) {
        redisTemplate.delete(key);
        log.info("清除缓存，key={}", key);
    }

    /**
     * 批量删除以指定前缀开头的所有 key大量key时候要用SCAN
     */
    public void evictByPrefix(String prefix) {
        Set<String> keys = redisTemplate.keys(prefix + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("批量清缓存，前缀={}, 删除了{}个key", prefix, keys.size());
        }
    }
}