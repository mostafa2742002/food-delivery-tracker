package com.mostafa.fooddelivery.restaurant.repository;

import com.mostafa.fooddelivery.restaurant.entity.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    
    // Find restaurant by owner
    Optional<Restaurant> findByOwnerId(Long ownerId);
    
    // Find all open restaurants
    List<Restaurant> findByIsOpenTrue();
    
    // Find by cuisine type
    List<Restaurant> findByCuisineTypeIgnoreCase(String cuisineType);
    
    // Search by name (partial match)
    List<Restaurant> findByNameContainingIgnoreCase(String name);
    
    // Find top rated restaurants
    List<Restaurant> findByIsOpenTrueOrderByRatingDesc();
}