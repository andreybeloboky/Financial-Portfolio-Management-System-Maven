package org.example.exception;

public class InvestmentCloneException extends RuntimeException {
    public InvestmentCloneException(String message) {
        super(message);
    }

    public InvestmentCloneException(String message, Throwable cause) {
        super(message, cause);
    }
}
