package com.mostafa.fooddelivery.order.repository;

import com.mostafa.fooddelivery.order.entity.Order;
import com.mostafa.fooddelivery.order.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Find orders by customer
    List<Order> findByCustomerIdOrderByCreatedAtDesc(Long customerId);
    
    // Find orders by restaurant
    List<Order> findByRestaurantIdOrderByCreatedAtDesc(Long restaurantId);
    
    // Find orders by driver
    List<Order> findByDriverIdOrderByCreatedAtDesc(Long driverId);
    
    // Find orders by status
    List<Order> findByStatus(OrderStatus status);
    
    // Find orders for restaurant by status (e.g., pending orders)
    List<Order> findByRestaurantIdAndStatus(Long restaurantId, OrderStatus status);
    
    // Find active orders for a driver
    List<Order> findByDriverIdAndStatusIn(Long driverId, List<OrderStatus> statuses);
    
    // Find orders waiting for driver assignment
    List<Order> findByStatusAndDriverIsNull(OrderStatus status);
}