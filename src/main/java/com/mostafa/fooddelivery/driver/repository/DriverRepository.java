package com.mostafa.fooddelivery.driver.repository;

import com.mostafa.fooddelivery.driver.entity.Driver;
import com.mostafa.fooddelivery.driver.entity.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    
    // Find driver by user ID
    Optional<Driver> findByUserId(Long userId);
    
    // Find available drivers
    List<Driver> findByIsAvailableTrue();
    
    // Find available drivers by vehicle type
    List<Driver> findByIsAvailableTrueAndVehicleType(VehicleType vehicleType);
    
    // Find top rated available drivers
    List<Driver> findByIsAvailableTrueOrderByRatingDesc();
}