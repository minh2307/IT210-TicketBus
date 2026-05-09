// === UserNotFoundException.java ===
package com.example.it210ticketbus.exception;

/**
 * Exception thrown when user is not found
 */
public class UserNotFoundException extends RuntimeException {
    
    public UserNotFoundException(String username) {
        super("Không tìm thấy người dùng với tên đăng nhập '" + username + "'");
    }
}
