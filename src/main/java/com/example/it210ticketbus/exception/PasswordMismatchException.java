// === PasswordMismatchException.java ===
package com.example.it210ticketbus.exception;

/**
 * Exception thrown when password and confirm password do not match
 */
public class PasswordMismatchException extends RuntimeException {
    
    public PasswordMismatchException() {
        super("Mật khẩu và xác nhận mật khẩu không khớp");
    }
    
    public PasswordMismatchException(String message) {
        super(message);
    }
}
