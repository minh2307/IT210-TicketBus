// === UsernameAlreadyExistsException.java ===
package com.example.it210ticketbus.exception;

/**
 * Exception thrown when attempting to register with an already existing username
 */
public class UsernameAlreadyExistsException extends RuntimeException {
    
    public UsernameAlreadyExistsException(String username) {
        super("Tên đăng nhập '" + username + "' đã tồn tại");
    }
}
