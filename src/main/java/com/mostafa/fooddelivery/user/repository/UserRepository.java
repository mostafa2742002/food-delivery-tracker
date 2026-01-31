package com.mostafa.fooddelivery.user.repository;

import com.mostafa.fooddelivery.user.entity.Role;
import com.mostafa.fooddelivery.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find user by email (for login)
    Optional<User> findByEmail(String email);
    
    // Check if email exists (for registration)
    boolean existsByEmail(String email);
    
    // Find all users by role
    List<User> findByRole(Role role);
    
    // Find active users by role
    List<User> findByRoleAndIsActiveTrue(Role role);
}