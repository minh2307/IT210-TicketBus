package com.example.it210ticketbus.exception;

/**
 * Exception thrown when user profile is not found
 */
public class ProfileNotFoundException extends RuntimeException {
    
    public ProfileNotFoundException(Long userId) {
        super("Không tìm thấy hồ sơ cá nhân cho người dùng có ID: " + userId);
    }
}
