package com.sistemabarberia.fadex_backend.commons.exception;

public class ApiError extends RuntimeException {
    public ApiError(String message) {
        super(message);
    }
}
