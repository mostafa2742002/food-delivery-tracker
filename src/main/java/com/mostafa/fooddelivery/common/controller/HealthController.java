package com.mostafa.fooddelivery.common.controller;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/health")
public class HealthController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;



    @GetMapping
    public ResponseEntity<Map<String, Object>> checkHealth() {
        Map<String, Object> health = new HashMap<>();

        health.put("postgres", checkPostgres());

        health.put("redis", checkRedis());

        health.put("kafka", checkKafka());

        // Overall status
        boolean allHealthy = health.values().stream()
                .allMatch(v -> v instanceof Map && "UP".equals(((Map<?, ?>) v).get("status")));
        
        health.put("status", allHealthy ? "ALL SERVICES UP ✅" : "SOME SERVICES DOWN ❌");
        
        return ResponseEntity.ok(health);
    }


    private Map<String, String> checkPostgres() { 
        Map<String, String> result = new HashMap<>();

        try (Connection conn = dataSource.getConnection()) {
            result.put("status", "UP");
            result.put("database", conn.getCatalog());

        }
        catch (Exception e) {
            result.put("status", "DOWN");
            result.put("error", e.getMessage());
        }

        return result;
    }

        private Map<String, String> checkRedis() {
        Map<String, String> result = new HashMap<>();
        try {
            redisTemplate.opsForValue().set("health-check", "OK");
            String value = redisTemplate.opsForValue().get("health-check");
            if ("OK".equals(value)) {
                result.put("status", "UP");
            } else {
                result.put("status", "DOWN");
                result.put("error", "Could not read value");
            }
        } catch (Exception e) {
            result.put("status", "DOWN");
            result.put("error", e.getMessage());
        }
        return result;
    }

    private Map<String, String> checkKafka() {
        Map<String, String> result = new HashMap<>();
        try {
            kafkaTemplate.send("health-check-topic", "health-check", "OK");
            result.put("status", "UP");
        } catch (Exception e) {
            result.put("status", "DOWN");
            result.put("error", e.getMessage());
        }
        return result;
    }
}
