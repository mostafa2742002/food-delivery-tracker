package com.mostafa.fooddelivery.order.dto;

import com.mostafa.fooddelivery.order.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class OrderSnapshotEvent {
    private Long orderId;
    private OrderStatus status;
    private BigDecimal totalPrice;
    private BigDecimal deliveryFee;
    private String deliveryAddress;
    private String restaurantName;
    private String driverName;
    private String driverPhone;
    private LocalDateTime estimatedDeliveryTime;
    private LocalDateTime createdAt;
}
