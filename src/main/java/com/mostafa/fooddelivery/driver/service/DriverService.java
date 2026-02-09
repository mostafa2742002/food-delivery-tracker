package com.mostafa.fooddelivery.driver.service;

import com.mostafa.fooddelivery.common.sse.OrderSseService;
import com.mostafa.fooddelivery.driver.dto.DriverLocationEvent;
import com.mostafa.fooddelivery.driver.dto.DriverLocationResponse;
import com.mostafa.fooddelivery.driver.dto.UpdateLocationRequest;
import com.mostafa.fooddelivery.driver.entity.Driver;
import com.mostafa.fooddelivery.driver.repository.DriverRepository;
import com.mostafa.fooddelivery.order.entity.Order;
import com.mostafa.fooddelivery.order.entity.OrderStatus;
import com.mostafa.fooddelivery.order.repository.OrderRepository;
import com.mostafa.fooddelivery.user.entity.User;
import com.mostafa.fooddelivery.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriverService {

    private final DriverRepository driverRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderSseService orderSseService;
    private final DriverLocationCache driverLocationCache;


    public void updateLocation(String driverEmail, UpdateLocationRequest request) {
        User user = userRepository.findByEmail(driverEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Driver driver = driverRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));

        driver.setLatitude(request.getLatitude());
        driver.setLongitude(request.getLongitude());
        driverRepository.save(driver);

        driverLocationCache.saveLocation(driver.getId(), driver.getLatitude(), driver.getLongitude());

        // Push live update to all active orders assigned to this driver
        List<Order> activeOrders = orderRepository.findByDriverIdAndStatusNot(
                driver.getId(),
                OrderStatus.DELIVERED
        );

        DriverLocationEvent event = DriverLocationEvent.builder()
                .driverId(driver.getId())
                .latitude(driver.getLatitude())
                .longitude(driver.getLongitude())
                .timestamp(LocalDateTime.now())
                .build();

        activeOrders.forEach(order ->
                orderSseService.sendEvent(order.getId(), "driver_location", event)
        );
    }

    public DriverLocationResponse getDriverLocation(Long driverId) {
    DriverLocationResponse cached = driverLocationCache.getLocation(driverId);
    if (cached != null) {
        return cached;
    }

    Driver driver = driverRepository.findById(driverId)
            .orElseThrow(() -> new RuntimeException("Driver not found"));

    return DriverLocationResponse.builder()
            .latitude(driver.getLatitude())
            .longitude(driver.getLongitude())
            .build();
    }

}
