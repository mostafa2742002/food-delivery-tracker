package com.mostafa.fooddelivery.driver.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DriverLocationResponse {
    private Double latitude;
    private Double longitude;
}
