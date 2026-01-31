package com.mostafa.fooddelivery.driver.entity;

import com.mostafa.fooddelivery.common.entity.BaseEntity;
import com.mostafa.fooddelivery.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "drivers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type", nullable = false)
    private VehicleType vehicleType;

    @Column(name = "license_plate")
    private String licensePlate;

    @Builder.Default
    @Column(name = "is_available")
    private boolean isAvailable = true;

    // Current location (for tracking)
    private Double latitude;
    private Double longitude;

    @Builder.Default
    private Double rating = 0.0;

    @Builder.Default
    @Column(name = "total_deliveries")
    private Integer totalDeliveries = 0;

    // ===== RELATIONSHIP =====
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}