package com.mostafa.fooddelivery.kafka.event;

import com.mostafa.fooddelivery.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderEvent {

    private String eventType;  // ORDER_PLACED, ORDER_ACCEPTED, etc.
    private Long orderId;
    private OrderStatus status;
    
    // Customer info
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    
    // Restaurant info
    private Long restaurantId;
    private String restaurantName;
    
    // Driver info (if assigned)
    private Long driverId;
    private String driverName;
    private String driverPhone;
    
    // Order details
    private BigDecimal totalPrice;
    private String deliveryAddress;
    
    // Timestamps
    private LocalDateTime eventTimestamp;
    private LocalDateTime estimatedDeliveryTime;
}