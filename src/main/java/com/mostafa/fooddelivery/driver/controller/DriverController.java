package com.mostafa.fooddelivery.driver.controller;

import com.mostafa.fooddelivery.driver.dto.DriverLocationResponse;
import com.mostafa.fooddelivery.driver.dto.UpdateLocationRequest;
import com.mostafa.fooddelivery.driver.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.mostafa.fooddelivery.driver.dto.DriverLocationResponse;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
public class DriverController {

    private final DriverService driverService;

    @PatchMapping("/me/location")
    @PreAuthorize("hasRole('DRIVER')")
    public ResponseEntity<Void> updateLocation(
            Authentication authentication,
            @Valid @RequestBody UpdateLocationRequest request
    ) {
        String email = authentication.getName();
        driverService.updateLocation(email, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/location")
    @PreAuthorize("hasRole('RESTAURANT_OWNER') or hasRole('CUSTOMER')")
    public ResponseEntity<DriverLocationResponse> getDriverLocation(@PathVariable Long id) {
        DriverLocationResponse location = driverService.getDriverLocation(id);
        return ResponseEntity.ok(location);
    }

}
