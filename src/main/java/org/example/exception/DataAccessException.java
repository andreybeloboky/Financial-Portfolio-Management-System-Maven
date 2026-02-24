package org.example.exception;

public class DataAccessException extends RuntimeException {
    public DataAccessException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
