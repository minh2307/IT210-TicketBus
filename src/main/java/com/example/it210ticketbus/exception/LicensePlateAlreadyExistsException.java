package com.example.it210ticketbus.exception;

/**
 * Exception thrown when a license plate already exists
 */
public class LicensePlateAlreadyExistsException extends RuntimeException {
    
    public LicensePlateAlreadyExistsException(String licensePlate) {
        super("Biển số xe '" + licensePlate + "' đã tồn tại trong hệ thống.");
    }
}
