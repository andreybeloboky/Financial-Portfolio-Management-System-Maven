package org.example.exception;

public class InvestmentAlreadyExistsException extends RuntimeException {
    public InvestmentAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}

