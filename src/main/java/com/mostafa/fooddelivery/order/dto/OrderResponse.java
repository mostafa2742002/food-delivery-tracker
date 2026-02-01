package com.mostafa.fooddelivery.order.dto;

import com.mostafa.fooddelivery.order.entity.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponse {

    private Long id;
    private OrderStatus status;
    private String customerName;
    private String customerPhone;
    private String restaurantName;
    private String restaurantAddress;
    private String deliveryAddress;
    private List<OrderItemResponse> items;
    private BigDecimal subtotal;
    private BigDecimal deliveryFee;
    private BigDecimal totalPrice;
    private String notes;
    private String driverName;
    private String driverPhone;
    private LocalDateTime createdAt;
    private LocalDateTime estimatedDeliveryTime;
}