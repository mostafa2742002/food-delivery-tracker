package com.mostafa.fooddelivery.auth.controller;

import com.mostafa.fooddelivery.auth.dto.AuthResponse;
import com.mostafa.fooddelivery.auth.dto.LoginRequest;
import com.mostafa.fooddelivery.auth.dto.RegisterRequest;
import com.mostafa.fooddelivery.auth.service.AuthService;
import com.mostafa.fooddelivery.user.entity.User;
import com.mostafa.fooddelivery.user.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;


    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "phone", user.getPhone(),
                "role", user.getRole(),
                "message", "You are authenticated! ✅"
        ));
    }
}