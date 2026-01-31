package com.mostafa.fooddelivery.restaurant.entity;

import com.mostafa.fooddelivery.common.entity.BaseEntity;
import com.mostafa.fooddelivery.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "restaurants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Restaurant extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String phone;

    @Column(length = 500)
    private String description;

    @Column(name = "cuisine_type")
    private String cuisineType;  // Italian, Chinese, Egyptian, etc.

    @Builder.Default
    @Column(name = "is_open")
    private boolean isOpen = true;

    @Builder.Default
    private Double rating = 0.0;

    // ===== RELATIONSHIP =====
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}