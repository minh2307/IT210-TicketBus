package com.example.it210ticketbus.service.impl;

import com.example.it210ticketbus.dto.request.ProfileUpdateRequest;
import com.example.it210ticketbus.dto.response.ProfileDTO;
import com.example.it210ticketbus.exception.DuplicateFieldException;
import com.example.it210ticketbus.exception.PasswordMismatchException;
import com.example.it210ticketbus.exception.ProfileNotFoundException;
import com.example.it210ticketbus.exception.UserNotFoundException;
import com.example.it210ticketbus.model.User;
import com.example.it210ticketbus.model.UserProfile;
import com.example.it210ticketbus.repository.UserProfileRepository;
import com.example.it210ticketbus.repository.UserRepository;
import com.example.it210ticketbus.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of ProfileService (CORE-03)
 * Handles profile viewing and updating for all user roles
 */
@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {
    
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    
    @Override
    @Transactional(readOnly = true)
    public ProfileDTO getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(userId)));
        
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));
        
        return mapToDTO(user, profile);
    }
    
    @Override
    @Transactional
    public ProfileDTO updateProfile(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(userId)));
        
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ProfileNotFoundException(userId));
        
        // Validate email uniqueness (exclude current user)
        if (userProfileRepository.existsByEmailAndUserIdNot(request.getEmail(), userId)) {
            throw new DuplicateFieldException("Email", request.getEmail());
        }
        
        // Validate phone uniqueness (exclude current user)
        if (userProfileRepository.existsByPhoneAndUserIdNot(request.getPhone(), userId)) {
            throw new DuplicateFieldException("Số điện thoại", request.getPhone());
        }
        
        // Update profile fields
        profile.setFullName(request.getFullName());
        profile.setPhone(request.getPhone());
        profile.setEmail(request.getEmail());
        profile.setAddress(request.getAddress());
        
        // Save updated profile
        UserProfile savedProfile = userProfileRepository.save(profile);
        
        return mapToDTO(user, savedProfile);
    }
    
    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(String.valueOf(userId)));
        
        // Validate old password
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new PasswordMismatchException("Mật khẩu hiện tại không đúng");
        }
        
        // Hash new password and update
        String hashedNewPassword = passwordEncoder.encode(newPassword);
        user.setPasswordHash(hashedNewPassword);
        
        userRepository.save(user);
    }
    
    /**
     * Helper: Map User + UserProfile to ProfileDTO
     */
    private ProfileDTO mapToDTO(User user, UserProfile profile) {
        return ProfileDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .role(user.getRole())
                .fullName(profile.getFullName())
                .phone(profile.getPhone())
                .email(profile.getEmail())
                .address(profile.getAddress())
                .build();
    }
}
