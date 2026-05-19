package com.sistemabarberia.fadex_backend.commons.exception;

public class ResourceNotFoundException extends ApiError {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
