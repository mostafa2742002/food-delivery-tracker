package com.mostafa.fooddelivery.order.dto;

import com.mostafa.fooddelivery.order.entity.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class OrderStatusEvent {
    private Long orderId;
    private OrderStatus status;
    private LocalDateTime timestamp;
}
