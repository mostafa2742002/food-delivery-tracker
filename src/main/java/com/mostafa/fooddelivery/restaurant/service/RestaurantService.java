package com.mostafa.fooddelivery.restaurant.service;

import com.mostafa.fooddelivery.restaurant.dto.MenuItemResponse;
import com.mostafa.fooddelivery.restaurant.dto.RestaurantResponse;
import com.mostafa.fooddelivery.restaurant.entity.MenuItem;
import com.mostafa.fooddelivery.restaurant.entity.Restaurant;
import com.mostafa.fooddelivery.restaurant.repository.MenuItemRepository;
import com.mostafa.fooddelivery.restaurant.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantRepository.findByIsOpenTrue()
                .stream()
                .map(this::mapToRestaurantResponse)
                .collect(Collectors.toList());
    }

    public RestaurantResponse getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurant not found with id: " + id));
        return mapToRestaurantResponse(restaurant);
    }

    public List<MenuItemResponse> getMenuByRestaurantId(Long restaurantId) {
        
        if (!restaurantRepository.existsById(restaurantId)) {
            throw new RuntimeException("Restaurant not found with id: " + restaurantId);
        }

        return menuItemRepository.findByRestaurantIdAndIsAvailableTrue(restaurantId)
                .stream()
                .map(this::mapToMenuItemResponse)
                .collect(Collectors.toList());
    }

    public List<RestaurantResponse> searchRestaurants(String name) {
        return restaurantRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(this::mapToRestaurantResponse)
                .collect(Collectors.toList());
    }

    public List<RestaurantResponse> getRestaurantsByCuisine(String cuisineType) {
        return restaurantRepository.findByCuisineTypeIgnoreCase(cuisineType)
                .stream()
                .map(this::mapToRestaurantResponse)
                .collect(Collectors.toList());
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