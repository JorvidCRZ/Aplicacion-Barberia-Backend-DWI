package com.sistemabarberia.fadex_backend.auth.authentication.dto.request;

public record LoginRequest(
        String username,
        String password
) {}