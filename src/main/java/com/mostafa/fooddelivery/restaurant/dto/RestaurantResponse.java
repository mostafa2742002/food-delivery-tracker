package com.mostafa.fooddelivery.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RestaurantResponse {
    
    private Long id;
    private String name;
    private String address;
    private String phone;
    private String description;
    private String cuisineType;
    private boolean isOpen;
    private Double rating;
}