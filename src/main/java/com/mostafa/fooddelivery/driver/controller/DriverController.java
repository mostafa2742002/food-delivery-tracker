package com.mostafa.fooddelivery.driver.controller;

import com.mostafa.fooddelivery.driver.dto.UpdateLocationRequest;
import com.mostafa.fooddelivery.driver.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
}
