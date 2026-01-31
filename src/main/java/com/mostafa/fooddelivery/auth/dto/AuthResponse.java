package com.mostafa.fooddelivery.auth.dto;

import com.mostafa.fooddelivery.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    
    private String token;
    private String email;
    private String name;
    private Role role;
    private String message;
}