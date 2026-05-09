// === AuthService.java ===
package com.example.it210ticketbus.service;

import com.example.it210ticketbus.dto.request.LoginRequest;
import com.example.it210ticketbus.dto.request.RegisterRequest;
import com.example.it210ticketbus.dto.response.UserProfileResponse;

/**
 * Service interface for authentication operations
 */
public interface AuthService {
    
    /**
     * Register a new user
     */
    UserProfileResponse register(RegisterRequest request);
    
    /**
     * Login user
     */
    UserProfileResponse login(LoginRequest request);
}
