package org.example.repository;

public class IncorrectSaveException extends RuntimeException {

    public IncorrectSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}
