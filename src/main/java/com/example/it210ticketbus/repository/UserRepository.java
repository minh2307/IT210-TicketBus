// === UserRepository.java ===
package com.example.it210ticketbus.repository;

import com.example.it210ticketbus.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity operations
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Find user by email through UserProfile
     */
    Optional<User> findByProfileEmail(String email);
    
    /**
     * Find user by phone through UserProfile
     */
    Optional<User> findByProfilePhone(String phone);
    
    /**
     * Check if email exists in UserProfile
     */
    boolean existsByProfileEmail(String email);
    
    /**
     * Check if phone exists in UserProfile
     */
    boolean existsByProfilePhone(String phone);
}
