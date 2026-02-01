package com.mostafa.fooddelivery.restaurant.controller;

import com.mostafa.fooddelivery.restaurant.dto.MenuItemResponse;
import com.mostafa.fooddelivery.restaurant.dto.RestaurantResponse;
import com.mostafa.fooddelivery.restaurant.service.RestaurantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService restaurantService;

    @GetMapping
    public ResponseEntity<List<RestaurantResponse>> getAllRestaurants() {
        return ResponseEntity.ok(restaurantService.getAllRestaurants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurantById(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getRestaurantById(id));
    }

    @GetMapping("/{id}/menu")
    public ResponseEntity<List<MenuItemResponse>> getRestaurantMenu(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getMenuByRestaurantId(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<RestaurantResponse>> searchRestaurants(@RequestParam String name) {
        return ResponseEntity.ok(restaurantService.searchRestaurants(name));
    }

    @GetMapping("/cuisine/{type}")
    public ResponseEntity<List<RestaurantResponse>> getRestaurantsByCuisine(@PathVariable String type) {
        return ResponseEntity.ok(restaurantService.getRestaurantsByCuisine(type));
    }
}