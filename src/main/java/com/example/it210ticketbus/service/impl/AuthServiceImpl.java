// === AuthServiceImpl.java ===
package com.example.it210ticketbus.service.impl;

import com.example.it210ticketbus.dto.request.LoginRequest;
import com.example.it210ticketbus.dto.request.RegisterRequest;
import com.example.it210ticketbus.dto.response.UserProfileResponse;
import com.example.it210ticketbus.exception.*;
import com.example.it210ticketbus.model.User;
import com.example.it210ticketbus.model.UserProfile;
import com.example.it210ticketbus.repository.UserRepository;
import com.example.it210ticketbus.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of AuthService
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public UserProfileResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByProfileEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }
        
        // Check if phone already exists
        if (userRepository.existsByProfilePhone(request.getPhone())) {
            throw new DuplicateFieldException("Số điện thoại", request.getPhone());
        }
        
        // Check if passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordMismatchException();
        }
        
        // Hash password
        String hashedPassword = passwordEncoder.encode(request.getPassword());
        
        // Generate username from email (part before @)
        String generatedUsername = request.getEmail().split("@")[0];
        
        // Ensure username is unique
        String finalUsername = generatedUsername;
        int counter = 1;
        while (userRepository.existsByUsername(finalUsername)) {
            finalUsername = generatedUsername + counter;
            counter++;
        }
        
        // Create User entity
        User user = User.builder()
                .username(finalUsername)
                .passwordHash(hashedPassword)
                .role(request.getRole())
                .build();
        
        // Create UserProfile entity
        UserProfile userProfile = UserProfile.builder()
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .user(user)
                .build();
        
        // Set bidirectional relationship
        user.setProfile(userProfile);
        
        // Save both entities
        User savedUser = userRepository.save(user);
        
        // Return response
        return UserProfileResponse.builder()
                .id(savedUser.getId())
                .username(savedUser.getUsername())
                .role(savedUser.getRole())
                .fullName(savedUser.getProfile().getFullName())
                .phone(savedUser.getProfile().getPhone())
                .email(savedUser.getProfile().getEmail())
                .build();
    }
    
    @Override
    public UserProfileResponse login(LoginRequest request) {
        String emailOrPhone = request.getEmailOrPhone();
        User user = null;

        // Try to find user by username, email, or phone
        if (emailOrPhone.contains("@")) {
            // Input looks like email
            user = userRepository.findByProfileEmail(emailOrPhone)
                    .orElseThrow(() -> new UserNotFoundException(emailOrPhone));
        } else if (emailOrPhone.matches("\\d+")) {
            // Input looks like phone (all digits)
            user = userRepository.findByProfilePhone(emailOrPhone)
                    .orElseThrow(() -> new UserNotFoundException(emailOrPhone));
        } else {
            // Input looks like username
            user = userRepository.findByUsername(emailOrPhone)
                    .orElseThrow(() -> new UserNotFoundException(emailOrPhone));
        }
        
        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        
        // Create user profile response
        return UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .fullName(user.getProfile().getFullName())
                .phone(user.getProfile().getPhone())
                .email(user.getProfile().getEmail())
                .build();
    }
}
