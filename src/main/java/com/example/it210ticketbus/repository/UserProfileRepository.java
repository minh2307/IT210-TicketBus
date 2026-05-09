package com.example.it210ticketbus.repository;

import com.example.it210ticketbus.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for UserProfile entity operations (CORE-03)
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
    /**
     * Find profile by user ID
     */
    Optional<UserProfile> findByUserId(Long userId);
    
    /**
     * Check if email exists for another user (excluding given user ID)
     */
    boolean existsByEmailAndUserIdNot(String email, Long userId);
    
    /**
     * Check if phone exists for another user (excluding given user ID)
     */
    boolean existsByPhoneAndUserIdNot(String phone, Long userId);
}
