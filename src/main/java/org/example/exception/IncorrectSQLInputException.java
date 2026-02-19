package org.example.exception;

public class IncorrectSQLInputException extends RuntimeException {
    public IncorrectSQLInputException(String reason, Throwable cause) {
        super(reason, cause);
    }
}
