package com.example.it210ticketbus.exception;

/**
 * Exception thrown when a unique field (email, phone) already exists for another user
 */
public class DuplicateFieldException extends RuntimeException {
    
    public DuplicateFieldException(String fieldName, String value) {
        super(fieldName + " '" + value + "' đã được sử dụng bởi người dùng khác.");
    }
}
