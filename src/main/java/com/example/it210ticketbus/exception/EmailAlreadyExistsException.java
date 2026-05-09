// === EmailAlreadyExistsException.java ===
package com.example.it210ticketbus.exception;

/**
 * Exception thrown when attempting to register with an already existing email
 */
public class EmailAlreadyExistsException extends RuntimeException {
    
    public EmailAlreadyExistsException(String email) {
        super("Email '" + email + "' đã tồn tại");
    }
}
