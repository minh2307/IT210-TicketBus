package com.example.it210ticketbus.service;

import com.example.it210ticketbus.dto.request.ProfileUpdateRequest;
import com.example.it210ticketbus.dto.response.ProfileDTO;

/**
 * Service interface for profile management (CORE-03)
 * Available for all roles: PASSENGER, STAFF, ADMIN
 */
public interface ProfileService {
    
    /**
     * Get profile of a user by user ID
     * @param userId the ID of the user
     * @return ProfileDTO containing user profile data
     */
    ProfileDTO getProfile(Long userId);
    
    /**
     * Update profile of a user
     * Validates email and phone uniqueness (must not duplicate with other users)
     * @param userId the ID of the user
     * @param request the profile update data
     * @return updated ProfileDTO
     */
    ProfileDTO updateProfile(Long userId, ProfileUpdateRequest request);
    
    /**
     * Change password for a user
     * Validates old password and updates to new password
     * @param userId the ID of the user
     * @param oldPassword the current password
     * @param newPassword the new password
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
}
