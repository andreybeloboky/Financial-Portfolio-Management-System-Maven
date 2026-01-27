package org.example.repository;

public class IncorrectLoadException extends RuntimeException {

    public IncorrectLoadException(String message, Throwable cause) {
        super(message, cause);
    }
}
