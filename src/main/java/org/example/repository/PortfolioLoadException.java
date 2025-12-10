package org.example.repository;

public class PortfolioLoadException extends RuntimeException {

    public PortfolioLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
