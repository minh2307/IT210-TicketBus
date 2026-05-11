// === GlobalExceptionHandler.java ===
package com.example.it210ticketbus.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for the application
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(UsernameAlreadyExistsException.class)
    public String handleUsernameAlreadyExists(UsernameAlreadyExistsException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "auth/register";
    }
    
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public String handleEmailAlreadyExists(EmailAlreadyExistsException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "auth/register";
    }
    
    @ExceptionHandler(PasswordMismatchException.class)
    public String handlePasswordMismatch(PasswordMismatchException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "auth/register";
    }
    
    @ExceptionHandler(UserNotFoundException.class)
    public String handleUserNotFound(UserNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "auth/login";
    }
    
    @ExceptionHandler(InvalidCredentialsException.class)
    public String handleInvalidCredentials(InvalidCredentialsException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "auth/login";
    }
    
    @ExceptionHandler(ProfileNotFoundException.class)
    public String handleProfileNotFound(ProfileNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "profile/view";
    }
    
    @ExceptionHandler(DuplicateFieldException.class)
    public String handleDuplicateField(DuplicateFieldException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "profile/edit";
    }
    
    @ExceptionHandler(BusNotFoundException.class)
    public String handleBusNotFound(BusNotFoundException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "redirect:/admin/buses";
    }
    
    @ExceptionHandler(LicensePlateAlreadyExistsException.class)
    public String handleLicensePlateExists(LicensePlateAlreadyExistsException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "admin/buses/form";
    }
    
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        model.addAttribute("exception", ex.getClass().getName());
        return "error/error";
    }
}
