package com.mostafa.fooddelivery.order.service;

import com.mostafa.fooddelivery.driver.entity.Driver;
import com.mostafa.fooddelivery.driver.repository.DriverRepository;
import com.mostafa.fooddelivery.kafka.event.OrderEvent;
import com.mostafa.fooddelivery.kafka.producer.OrderEventProducer;
import com.mostafa.fooddelivery.order.dto.*;
import com.mostafa.fooddelivery.order.entity.Order;
import com.mostafa.fooddelivery.order.entity.OrderItem;
import com.mostafa.fooddelivery.order.entity.OrderStatus;
import com.mostafa.fooddelivery.order.repository.OrderRepository;
import com.mostafa.fooddelivery.restaurant.entity.MenuItem;
import com.mostafa.fooddelivery.restaurant.entity.Restaurant;
import com.mostafa.fooddelivery.restaurant.repository.MenuItemRepository;
import com.mostafa.fooddelivery.restaurant.repository.RestaurantRepository;
import com.mostafa.fooddelivery.user.entity.User;
import com.mostafa.fooddelivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.mostafa.fooddelivery.common.sse.OrderSseService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;
    private final DriverRepository driverRepository;
    private final OrderEventProducer orderEventProducer;  
    private final OrderSseService orderSseService;


    private static final BigDecimal DELIVERY_FEE = new BigDecimal("15.00");

    @Transactional
    public OrderResponse createOrder(String customerEmail, CreateOrderRequest request) {
        log.info("Creating order for customer: {}", customerEmail);

        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        Restaurant restaurant = restaurantRepository.findById(request.getRestaurantId())
                .orElseThrow(() -> new RuntimeException("Restaurant not found"));

        if (!restaurant.isOpen()) {
            throw new RuntimeException("Restaurant is currently closed");
        }

        Order order = Order.builder()
                .customer(customer)
                .restaurant(restaurant)
                .deliveryAddress(request.getDeliveryAddress())
                .notes(request.getNotes())
                .status(OrderStatus.PLACED)
                .deliveryFee(DELIVERY_FEE)
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;

        for (OrderItemRequest itemRequest : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(itemRequest.getMenuItemId())
                    .orElseThrow(() -> new RuntimeException("Menu item not found: " + itemRequest.getMenuItemId()));

            if (!menuItem.getRestaurant().getId().equals(restaurant.getId())) {
                throw new RuntimeException("Menu item does not belong to this restaurant");
            }

            if (!menuItem.isAvailable()) {
                throw new RuntimeException("Menu item is not available: " + menuItem.getName());
            }

            OrderItem orderItem = OrderItem.builder()
                    .menuItem(menuItem)
                    .quantity(itemRequest.getQuantity())
                    .price(menuItem.getPrice())
                    .specialInstructions(itemRequest.getSpecialInstructions())
                    .build();

            order.addItem(orderItem);
            subtotal = subtotal.add(menuItem.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity())));
        }

        order.setTotalPrice(subtotal.add(DELIVERY_FEE));
        order.setEstimatedDeliveryTime(LocalDateTime.now().plusMinutes(45));

        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully with ID: {}", savedOrder.getId());

        // 🔥 PUBLISH KAFKA EVENT
        publishOrderEvent(savedOrder, "ORDER_PLACED");

        // 🔥 SEND SSE UPDAT
        orderSseService.sendEvent(
                savedOrder.getId(),
                "order_status",
                OrderStatusEvent.builder()
                        .orderId(savedOrder.getId())
                        .status(savedOrder.getStatus())
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        return mapToOrderResponse(savedOrder);
    }

    public List<OrderResponse> getCustomerOrders(String customerEmail) {
        User customer = userRepository.findByEmail(customerEmail)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        return orderRepository.findByCustomerIdOrderByCreatedAtDesc(customer.getId())
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long orderId, String userEmail) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getCustomer().getEmail().equals(userEmail) &&
            !order.getRestaurant().getOwner().getEmail().equals(userEmail)) {
            throw new RuntimeException("Access denied to this order");
        }

        return mapToOrderResponse(order);
    }

    public List<OrderResponse> getRestaurantOrders(String ownerEmail) {
        Restaurant restaurant = restaurantRepository.findByOwnerId(
                userRepository.findByEmail(ownerEmail)
                        .orElseThrow(() -> new RuntimeException("User not found"))
                        .getId()
        ).orElseThrow(() -> new RuntimeException("Restaurant not found for this owner"));

        return orderRepository.findByRestaurantIdOrderByCreatedAtDesc(restaurant.getId())
                .stream()
                .map(this::mapToOrderResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse updateOrderStatus(Long orderId, String ownerEmail, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getRestaurant().getOwner().getEmail().equals(ownerEmail)) {
            throw new RuntimeException("Access denied: Not the restaurant owner");
        }

        validateStatusTransition(order.getStatus(), newStatus);

        order.setStatus(newStatus);

        if (newStatus == OrderStatus.READY_FOR_PICKUP) {
            assignDriver(order);
        }

        Order updatedOrder = orderRepository.save(order);
        log.info("Order {} status updated to {}", orderId, newStatus);

        // 🔥 PUBLISH KAFKA EVENT
        String eventType = "ORDER_" + newStatus.name();
        publishOrderEvent(updatedOrder, eventType);

        // 🔥 SEND SSE UPDATE
        orderSseService.sendEvent(
                updatedOrder.getId(),
                "order_status",
                OrderStatusEvent.builder()
                        .orderId(updatedOrder.getId())
                        .status(updatedOrder.getStatus())
                        .timestamp(LocalDateTime.now())
                        .build()
        );

        return mapToOrderResponse(updatedOrder);
    }

    private void assignDriver(Order order) {
        List<Driver> availableDrivers = driverRepository.findByIsAvailableTrue();

        if (!availableDrivers.isEmpty()) {
            Driver driver = availableDrivers.get(0);
            order.setDriver(driver);
            driver.setAvailable(false);
            driverRepository.save(driver);
            log.info("Driver {} assigned to order {}", driver.getUser().getName(), order.getId());
        } else {
            log.warn("No available drivers for order {}", order.getId());
        }
    }

    private void validateStatusTransition(OrderStatus current, OrderStatus next) {
        boolean valid = switch (current) {
            case PLACED -> next == OrderStatus.ACCEPTED || next == OrderStatus.REJECTED;
            case ACCEPTED -> next == OrderStatus.PREPARING;
            case PREPARING -> next == OrderStatus.READY_FOR_PICKUP;
            case READY_FOR_PICKUP -> next == OrderStatus.PICKED_UP;
            case PICKED_UP -> next == OrderStatus.DELIVERED;
            default -> false;
        };

        if (!valid) {
            throw new RuntimeException("Invalid status transition from " + current + " to " + next);
        }
    }

    // ==================== KAFKA EVENT PUBLISHER ====================

    private void publishOrderEvent(Order order, String eventType) {
        OrderEvent event = OrderEvent.builder()
                .eventType(eventType)
                .orderId(order.getId())
                .status(order.getStatus())
                .customerId(order.getCustomer().getId())
                .customerName(order.getCustomer().getName())
                .customerEmail(order.getCustomer().getEmail())
                .customerPhone(order.getCustomer().getPhone())
                .restaurantId(order.getRestaurant().getId())
                .restaurantName(order.getRestaurant().getName())
                .driverId(order.getDriver() != null ? order.getDriver().getId() : null)
                .driverName(order.getDriver() != null ? order.getDriver().getUser().getName() : null)
                .driverPhone(order.getDriver() != null ? order.getDriver().getUser().getPhone() : null)
                .totalPrice(order.getTotalPrice())
                .deliveryAddress(order.getDeliveryAddress())
                .eventTimestamp(LocalDateTime.now())
                .estimatedDeliveryTime(order.getEstimatedDeliveryTime())
                .build();

        orderEventProducer.sendOrderEvent(event);
    }

    // ==================== MAPPER ====================

    private OrderResponse mapToOrderResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .menuItemName(item.getMenuItem().getName())
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .subtotal(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .specialInstructions(item.getSpecialInstructions())
                        .build())
                .collect(Collectors.toList());

        BigDecimal subtotal = order.getTotalPrice().subtract(order.getDeliveryFee());

        return OrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .customerName(order.getCustomer().getName())
                .customerPhone(order.getCustomer().getPhone())
                .restaurantName(order.getRestaurant().getName())
                .restaurantAddress(order.getRestaurant().getAddress())
                .deliveryAddress(order.getDeliveryAddress())
                .items(itemResponses)
                .subtotal(subtotal)
                .deliveryFee(order.getDeliveryFee())
                .totalPrice(order.getTotalPrice())
                .notes(order.getNotes())
                .driverName(order.getDriver() != null ? order.getDriver().getUser().getName() : null)
                .driverPhone(order.getDriver() != null ? order.getDriver().getUser().getPhone() : null)
                .createdAt(order.getCreatedAt())
                .estimatedDeliveryTime(order.getEstimatedDeliveryTime())
                .build();
    }
}