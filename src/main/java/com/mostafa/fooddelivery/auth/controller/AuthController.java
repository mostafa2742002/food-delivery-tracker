package com.mostafa.fooddelivery.auth.controller;

import com.mostafa.fooddelivery.auth.dto.AuthResponse;
import com.mostafa.fooddelivery.auth.dto.LoginRequest;
import com.mostafa.fooddelivery.auth.dto.RegisterRequest;
import com.mostafa.fooddelivery.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}