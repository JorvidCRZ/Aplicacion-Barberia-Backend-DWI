package com.sistemabarberia.fadex_backend.auth.refreshToken.service;

public interface PasswordResetService {
    void forgotPassword(String correo);
    void resetPassword(String token, String nuevaPassword);
}
