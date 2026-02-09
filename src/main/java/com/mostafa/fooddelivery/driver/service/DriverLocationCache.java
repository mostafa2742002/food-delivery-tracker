package com.mostafa.fooddelivery.driver.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.mostafa.fooddelivery.driver.dto.DriverLocationResponse;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DriverLocationCache {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String KEY_PREFIX = "driver:location:";
    private static final Duration TTL = Duration.ofMinutes(10);

    public void saveLocation(Long driverId, Double lat, Double lon) {
        String key = KEY_PREFIX + driverId;

        Map<String, Object> data = new HashMap<>();
        data.put("latitude", lat);
        data.put("longitude", lon);

        redisTemplate.opsForHash().putAll(key, data);
        redisTemplate.expire(key, TTL);
    }

    public DriverLocationResponse getLocation(Long driverId) {
    String key = KEY_PREFIX + driverId;
    Map<Object, Object> data = redisTemplate.opsForHash().entries(key);

    if (data == null || data.isEmpty()) {
        return null;
    }

    return DriverLocationResponse.builder()
            .latitude(Double.valueOf(data.get("latitude").toString()))
            .longitude(Double.valueOf(data.get("longitude").toString()))
            .build();
    }

}
