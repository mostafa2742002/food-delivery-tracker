package com.mostafa.fooddelivery.order.entity;

import com.mostafa.fooddelivery.common.entity.BaseEntity;
import com.mostafa.fooddelivery.restaurant.entity.MenuItem;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem extends BaseEntity {

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;  // Price at time of order (menu price can change)

    @Column(length = 255)
    private String specialInstructions;  // "No onions", "Extra cheese"

    // ===== RELATIONSHIPS =====
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;
}