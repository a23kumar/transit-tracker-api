package com.transittracker.exception;

/**
 * Exception thrown when data fetching fails
 */
public class DataFetchException extends RuntimeException {
    public DataFetchException(String message) {
        super(message);
    }

    public DataFetchException(String message, Throwable cause) {
        super(message, cause);
    }
}

