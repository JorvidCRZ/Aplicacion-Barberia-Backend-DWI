package com.sistemabarberia.fadex_backend.auth.refreshToken.service;

import com.sistemabarberia.fadex_backend.auth.security.service.CustomUserDetails;

public interface PasswordResetService {
    void forgotPassword(String correo);
    void resetPassword(String token, String nuevaPassword);
    void changePassword(String passwordActual, String passwordNueva, CustomUserDetails userDetails);
}
