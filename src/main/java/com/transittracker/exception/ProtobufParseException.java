package com.transittracker.exception;

/**
 * Exception thrown when protobuf parsing fails
 */
public class ProtobufParseException extends RuntimeException {
    public ProtobufParseException(String message) {
        super(message);
    }

    public ProtobufParseException(String message, Throwable cause) {
        super(message, cause);
    }
}

