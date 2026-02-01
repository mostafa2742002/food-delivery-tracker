package com.mostafa.fooddelivery.order.controller;

import com.mostafa.fooddelivery.order.dto.CreateOrderRequest;
import com.mostafa.fooddelivery.order.dto.OrderResponse;
import com.mostafa.fooddelivery.order.dto.UpdateOrderStatusRequest;
import com.mostafa.fooddelivery.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<OrderResponse> createOrder(
            Authentication authentication,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.createOrder(email, request));
    }

    @GetMapping
    @PreAuthorize("hasRole('CUSTOMER')")
    public ResponseEntity<List<OrderResponse>> getMyOrders(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.getCustomerOrders(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.getOrderById(id, email));
    }

    @GetMapping("/restaurant")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<List<OrderResponse>> getRestaurantOrders(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.getRestaurantOrders(email));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('RESTAURANT_OWNER')")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateOrderStatusRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.updateOrderStatus(id, email, request.getStatus()));
    }
}