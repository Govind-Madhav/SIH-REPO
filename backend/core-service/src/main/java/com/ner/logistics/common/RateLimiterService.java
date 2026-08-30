package com.ner.logistics.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimiterService {

    private final StringRedisTemplate redisTemplate;

    public boolean isAllowed(String key, int maxRequests, int windowSeconds) {
        try {
            String redisKey = "rate_limit:" + key;
            Long currentCount = redisTemplate.opsForValue().increment(redisKey);
            if (currentCount != null && currentCount == 1) {
                redisTemplate.expire(redisKey, Duration.ofSeconds(windowSeconds));
            }
            return currentCount != null && currentCount <= maxRequests;
        } catch (Exception e) {
            log.warn("RateLimiter fallback: Redis unavailable for key {}, allowing request", key);
            return true; // Graceful fallback if Redis is unreachable
        }
    }
}
