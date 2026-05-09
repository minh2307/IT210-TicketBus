// === InvalidCredentialsException.java ===
package com.example.it210ticketbus.exception;

/**
 * Exception thrown when login credentials are invalid
 */
public class InvalidCredentialsException extends RuntimeException {
    
    public InvalidCredentialsException() {
        super("Tên đăng nhập hoặc mật khẩu không đúng");
    }
}
