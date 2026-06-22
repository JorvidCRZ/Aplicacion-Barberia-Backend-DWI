package com.sistemabarberia.fadex_backend.auth.authentication.controller;

import com.sistemabarberia.fadex_backend.auth.authentication.dto.request.GoogleLoginRequest;
import com.sistemabarberia.fadex_backend.auth.authentication.dto.request.LoginRequest;
import com.sistemabarberia.fadex_backend.auth.authentication.dto.request.QrLoginRequest;
import com.sistemabarberia.fadex_backend.auth.authentication.dto.request.RefreshRequest;
import com.sistemabarberia.fadex_backend.auth.authentication.dto.response.TokenResponse;
import com.sistemabarberia.fadex_backend.auth.authentication.service.AuthService;
import com.sistemabarberia.fadex_backend.auth.refreshToken.dto.request.ForgotPasswordRequest;
import com.sistemabarberia.fadex_backend.auth.refreshToken.dto.request.ResetPasswordRequest;
import com.sistemabarberia.fadex_backend.auth.refreshToken.dto.request.response.ChangePasswordRequest;
import com.sistemabarberia.fadex_backend.auth.refreshToken.service.PasswordResetService;
import com.sistemabarberia.fadex_backend.auth.security.service.CustomUserDetails;
import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<TokenResponse>> login( @RequestBody @Valid LoginRequest loginRequest){
        TokenResponse response = authService.login(loginRequest);
        return ResponseEntity.ok(ApiResponse.ok("Login correctamente", response));
    }

    @PostMapping("/refresh")
    public  ResponseEntity<TokenResponse> refresh(@RequestBody RefreshRequest refreshRequest) {
        return ResponseEntity.ok(authService.refresh(refreshRequest.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout ( @RequestBody RefreshRequest refreshRequest) {
        authService.logout(refreshRequest.refreshToken());
        return ResponseEntity.ok().build();
    }
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword( @RequestBody ForgotPasswordRequest request) {
        passwordResetService.forgotPassword(request.getEmail());
        return ResponseEntity.ok( ApiResponse.ok("Si el correo está registrado, recibirás un enlace") );
    }
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody ResetPasswordRequest request) {
        passwordResetService.resetPassword(request.getToken(), request.getNuevaPassword());
        return ResponseEntity.ok(ApiResponse.ok("Contraseña actualizada correctamente"));
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<TokenResponse>> loginWithGoogle(@RequestBody @Valid GoogleLoginRequest request) {
        TokenResponse response = authService.loginWithGoogle(request.getIdToken());
        return ResponseEntity.ok(ApiResponse.ok("Login con Google correctamente", response));
    }

    @PostMapping("/qr-login")
    public ResponseEntity<ApiResponse<TokenResponse>> loginWithQr(
            @RequestBody @Valid QrLoginRequest request) {
        TokenResponse response = authService.loginWithQr(request.getQrToken(), request.getPin());
        return ResponseEntity.ok(ApiResponse.ok("Login con QR correctamente", response));
    }

    @PatchMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @RequestBody @Valid ChangePasswordRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        passwordResetService.changePassword(
                request.getPasswordActual(),
                request.getPasswordNueva(),
                userDetails
        );
        return ResponseEntity.ok(ApiResponse.ok("Contraseña cambiada correctamente"));
    }
}
