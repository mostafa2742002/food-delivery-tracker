package com.mostafa.fooddelivery.restaurant.repository;

import com.mostafa.fooddelivery.restaurant.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    
    // Find all items for a restaurant
    List<MenuItem> findByRestaurantId(Long restaurantId);
    
    // Find available items for a restaurant
    List<MenuItem> findByRestaurantIdAndIsAvailableTrue(Long restaurantId);
    
    // Find by category
    List<MenuItem> findByRestaurantIdAndCategoryIgnoreCase(Long restaurantId, String category);
    
    // Search by name
    List<MenuItem> findByRestaurantIdAndNameContainingIgnoreCase(Long restaurantId, String name);
}