package com.mostafa.fooddelivery.driver.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class DriverLocationEvent {
    private Long driverId;
    private Double latitude;
    private Double longitude;
    private LocalDateTime timestamp;
}
