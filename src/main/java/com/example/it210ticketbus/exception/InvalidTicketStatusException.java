package com.example.it210ticketbus.exception;

public class InvalidTicketStatusException extends RuntimeException {
    public InvalidTicketStatusException(String message) {
        super(message);
    }
}
