package com.ner.logistics.tracking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisTrackingService {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String REDIS_KEY_PREFIX = "vehicle:last-location:";
    private static final Duration TTL = Duration.ofHours(24);

    public void saveLatestLocation(GpsLocationDto dto) {
        try {
            String key = REDIS_KEY_PREFIX + dto.getVehicleCode();
            String value = objectMapper.writeValueAsString(dto);
            redisTemplate.opsForValue().set(key, value, TTL);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize GPS location DTO for Redis", e);
        }
    }

    public Optional<GpsLocationDto> getLatestLocation(String vehicleCode) {
        String key = REDIS_KEY_PREFIX + vehicleCode;
        String value = redisTemplate.opsForValue().get(key);
        if (value == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(objectMapper.readValue(value, GpsLocationDto.class));
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize GPS location DTO from Redis", e);
            return Optional.empty();
        }
    }
}
