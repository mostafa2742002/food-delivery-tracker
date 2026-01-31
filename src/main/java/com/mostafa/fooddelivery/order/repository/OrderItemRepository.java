package com.mostafa.fooddelivery.order.repository;

import com.mostafa.fooddelivery.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    // Find items for an order
    List<OrderItem> findByOrderId(Long orderId);
}