package com.transittracker.exception;

public class ProtobufParseException extends RuntimeException {
    public ProtobufParseException(String message) {
        super(message);
    }

    public ProtobufParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
