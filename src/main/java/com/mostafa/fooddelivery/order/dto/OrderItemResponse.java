package com.mostafa.fooddelivery.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderItemResponse {

    private Long id;
    private String menuItemName;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal subtotal;  // price × quantity
    private String specialInstructions;
}