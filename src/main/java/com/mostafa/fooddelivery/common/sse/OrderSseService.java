package com.mostafa.fooddelivery.common.sse;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class OrderSseService {

    private final Map<Long, List<SseEmitter>> emittersByOrderId = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long orderId) {
        SseEmitter emitter = new SseEmitter(0L); // never timeout by default

        emittersByOrderId
                .computeIfAbsent(orderId, id -> new CopyOnWriteArrayList<>())
                .add(emitter);

        emitter.onCompletion(() -> remove(orderId, emitter));
        emitter.onTimeout(() -> remove(orderId, emitter));
        emitter.onError(e -> remove(orderId, emitter));

        // Send initial event so client knows connection is open
        try {
            emitter.send(SseEmitter.event()
                    .name("connected")
                    .data("Connected to order stream: " + orderId));
        } catch (IOException e) {
            log.warn("Failed to send initial SSE event for order {}", orderId);
        }

        return emitter;
    }

    public void sendEvent(Long orderId, String eventName, Object data) {
        List<SseEmitter> emitters = emittersByOrderId.get(orderId);
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name(eventName)
                        .data(data));
            } catch (IOException e) {
                remove(orderId, emitter);
            }
        });
    }

    private void remove(Long orderId, SseEmitter emitter) {
        List<SseEmitter> emitters = emittersByOrderId.get(orderId);
        if (emitters != null) {
            emitters.remove(emitter);
            if (emitters.isEmpty()) {
                emittersByOrderId.remove(orderId);
            }
        }
    }
}
