package com.mostafa.fooddelivery.restaurant.service;

import com.mostafa.fooddelivery.restaurant.dto.MenuItemResponse;
import com.mostafa.fooddelivery.restaurant.dto.RestaurantResponse;
import com.mostafa.fooddelivery.restaurant.entity.MenuItem;
import com.mostafa.fooddelivery.restaurant.entity.Restaurant;
import com.mostafa.fooddelivery.restaurant.repository.MenuItemRepository;
import com.mostafa.fooddelivery.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    /**
     * Get all open restaurants (CACHED)
     */
    @Cacheable(value = "restaurants", key = "'all'")
    public List<RestaurantResponse> getAllRestaurants() {
        log.info("🔍 Fetching all restaurants from DATABASE");
        return restaurantRepository.findByIsOpenTrue()
                .stream()
                .map(this::mapToRestaurantResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get restaurant by ID (CACHED)
     */
    @Cacheable(value = "restaurants", key = "#id")
    public RestaurantResponse getRestaurantById(Long id) {
        log.info("🔍 Fetching restaurant {} from DATABASE", id);
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
        return mapToRestaurantResponse(restaurant);
    }

    /**
     * Get menu items for a restaurant (CACHED)
     */
    @Cacheable(value = "menus", key = "#restaurantId")
    public List<MenuItemResponse> getMenuByRestaurantId(Long restaurantId) {
        log.info("🔍 Fetching menu for restaurant {} from DATABASE", restaurantId);
        
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new RuntimeException("Restaurant not found with id: " + restaurantId);
        }

        return menuItemRepository.findByRestaurantIdAndIsAvailableTrue(restaurantId)
                .stream()
                .map(this::mapToMenuItemResponse)
                .collect(Collectors.toList());
    }

    /**
     * Search restaurants by name (NOT CACHED - dynamic query)
     */
    public List<RestaurantResponse> searchRestaurants(String name) {
        log.info("🔍 Searching restaurants by name: {}", name);
        return restaurantRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToRestaurantResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get restaurants by cuisine type (CACHED)
     */
    @Cacheable(value = "restaurants", key = "'cuisine:' + #cuisineType")
    public List<RestaurantResponse> getRestaurantsByCuisine(String cuisineType) {
        log.info("🔍 Fetching restaurants for cuisine {} from DATABASE", cuisineType);
        return restaurantRepository.findByCuisineTypeIgnoreCase(cuisineType)
                .stream()
                .map(this::mapToRestaurantResponse)
                .collect(Collectors.toList());
    }

    /**
     * Clear all restaurant caches (useful for admin operations)
     */
    @CacheEvict(value = {"restaurants", "menus"}, allEntries = true)
    public void clearCache() {
        log.info("🗑️ Clearing all restaurant and menu caches");
    }

    // ==================== MAPPERS ====================

    private RestaurantResponse mapToRestaurantResponse(Restaurant restaurant) {
        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .address(restaurant.getAddress())
                .phone(restaurant.getPhone())
                .description(restaurant.getDescription())
                .cuisineType(restaurant.getCuisineType())
                .isOpen(restaurant.isOpen())
                .rating(restaurant.getRating())
                .build();
    }

    private MenuItemResponse mapToMenuItemResponse(MenuItem menuItem) {
        return MenuItemResponse.builder()
                .id(menuItem.getId())
                .name(menuItem.getName())
                .description(menuItem.getDescription())
                .price(menuItem.getPrice())
                .category(menuItem.getCategory())
                .isAvailable(menuItem.isAvailable())
                .imageUrl(menuItem.getImageUrl())
                .build();
    }
}