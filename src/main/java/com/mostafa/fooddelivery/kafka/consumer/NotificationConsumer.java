package com.mostafa.fooddelivery.kafka.consumer;

import com.mostafa.fooddelivery.kafka.event.OrderEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationConsumer {

    /**
     * Listen for order events and send notifications
     */
    @KafkaListener(
            topics = "order-status-events",
            groupId = "notification-service-group"
    )
    public void handleOrderEvent(OrderEvent event) {
        log.info("📥 Received order event: {} for order {}", event.getEventType(), event.getOrderId());

        // Simulate sending notification based on event type
        switch (event.getEventType()) {
            case "ORDER_PLACED" -> notifyRestaurant(event);
            case "ORDER_ACCEPTED" -> notifyCustomerAccepted(event);
            case "ORDER_REJECTED" -> notifyCustomerRejected(event);
            case "ORDER_PREPARING" -> notifyCustomerPreparing(event);
            case "ORDER_READY_FOR_PICKUP" -> notifyDriverAndCustomer(event);
            case "ORDER_PICKED_UP" -> notifyCustomerPickedUp(event);
            case "ORDER_DELIVERED" -> notifyCustomerDelivered(event);
            default -> log.warn("Unknown event type: {}", event.getEventType());
        }
    }

    private void notifyRestaurant(OrderEvent event) {
        log.info("🔔 NOTIFICATION → Restaurant '{}': New order #{} received! Total: {}",
                event.getRestaurantName(),
                event.getOrderId(),
                event.getTotalPrice());
    }

    private void notifyCustomerAccepted(OrderEvent event) {
        log.info("🔔 NOTIFICATION → Customer '{}' ({}): Your order #{} has been ACCEPTED by {}!",
                event.getCustomerName(),
                event.getCustomerEmail(),
                event.getOrderId(),
                event.getRestaurantName());
    }

    private void notifyCustomerRejected(OrderEvent event) {
        log.info("🔔 NOTIFICATION → Customer '{}' ({}): Sorry, your order #{} was REJECTED by {}.",
                event.getCustomerName(),
                event.getCustomerEmail(),
                event.getOrderId(),
                event.getRestaurantName());
    }

    private void notifyCustomerPreparing(OrderEvent event) {
        log.info("🔔 NOTIFICATION → Customer '{}': Your order #{} is being PREPARED! 👨‍🍳",
                event.getCustomerName(),
                event.getOrderId());
    }

    private void notifyDriverAndCustomer(OrderEvent event) {
        log.info("🔔 NOTIFICATION → Driver '{}': New pickup! Order #{} at {} is READY!",
                event.getDriverName(),
                event.getOrderId(),
                event.getRestaurantName());
        
        log.info("🔔 NOTIFICATION → Customer '{}': Your order #{} is READY! Driver {} is on the way!",
                event.getCustomerName(),
                event.getOrderId(),
                event.getDriverName());
    }

    private void notifyCustomerPickedUp(OrderEvent event) {
        log.info("🔔 NOTIFICATION → Customer '{}': Driver {} has PICKED UP your order #{}! 🚗",
                event.getCustomerName(),
                event.getDriverName(),
                event.getOrderId());
    }

    private void notifyCustomerDelivered(OrderEvent event) {
        log.info("🔔 NOTIFICATION → Customer '{}': Your order #{} has been DELIVERED! Enjoy your meal! 🎉",
                event.getCustomerName(),
                event.getOrderId());
    }
}