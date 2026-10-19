package com.aseel.mini_wsa.exception;

public class InvalidIngestionRequestException extends RuntimeException {
    public InvalidIngestionRequestException(String message) {
        super(message);
    }
}
