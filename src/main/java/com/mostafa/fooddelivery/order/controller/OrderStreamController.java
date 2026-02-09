package com.mostafa.fooddelivery.order.controller;

import com.mostafa.fooddelivery.common.sse.OrderSseService;
import com.mostafa.fooddelivery.order.dto.OrderSnapshotEvent;
import com.mostafa.fooddelivery.order.entity.Order;
import com.mostafa.fooddelivery.order.repository.OrderRepository;
import com.mostafa.fooddelivery.user.entity.User;
import com.mostafa.fooddelivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderStreamController {

    private final OrderSseService orderSseService;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @GetMapping(value = "/{id}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamOrder(
            @PathVariable Long id,
            Authentication authentication
    ) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        boolean isCustomer = order.getCustomer().getId().equals(user.getId());
        boolean isOwner = order.getRestaurant().getOwner().getId().equals(user.getId());
        boolean isDriver = order.getDriver() != null && order.getDriver().getUser().getId().equals(user.getId());

        if (!isCustomer && !isOwner && !isDriver) {
            throw new AccessDeniedException("You are not allowed to access this order stream");
        }

        SseEmitter emitter = orderSseService.subscribe(id);

        orderSseService.sendEvent(
                id,
                "order_snapshot",
                OrderSnapshotEvent.builder()
                        .orderId(order.getId())
                        .status(order.getStatus())
                        .totalPrice(order.getTotalPrice())
                        .deliveryFee(order.getDeliveryFee())
                        .deliveryAddress(order.getDeliveryAddress())
                        .restaurantName(order.getRestaurant().getName())
                        .driverName(order.getDriver() != null ? order.getDriver().getUser().getName() : null)
                        .driverPhone(order.getDriver() != null ? order.getDriver().getUser().getPhone() : null)
                        .estimatedDeliveryTime(order.getEstimatedDeliveryTime())
                        .createdAt(order.getCreatedAt())
                        .build()
        );

        return emitter;

    }
}
