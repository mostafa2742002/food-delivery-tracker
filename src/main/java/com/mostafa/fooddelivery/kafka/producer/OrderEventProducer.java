package com.mostafa.fooddelivery.kafka.producer;

import com.mostafa.fooddelivery.kafka.event.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderEventProducer {

    private final KafkaTemplate<String, OrderEvent> kafkaTemplate;

    private static final String TOPIC = "order-status-events";

    /**
     * Publish order event to Kafka
     */
    public void sendOrderEvent(OrderEvent event) {
        log.info("📤 Publishing event to Kafka: {} for order {}", event.getEventType(), event.getOrderId());
        
        // Use orderId as key (ensures same order events go to same partition)
        String key = String.valueOf(event.getOrderId());
        
        kafkaTemplate.send(TOPIC, key, event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("✅ Event sent successfully: topic={}, partition={}, offset={}",
                                result.getRecordMetadata().topic(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("❌ Failed to send event: {}", ex.getMessage());
                    }
                });
    }
}