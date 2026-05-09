package com.example.it210ticketbus.exception;

/**
 * Exception thrown when a bus is not found
 */
public class BusNotFoundException extends RuntimeException {
    
    public BusNotFoundException(Long id) {
        super("Không tìm thấy xe với ID: " + id);
    }
}
