package com.sistemabarberia.fadex_backend.auth.authentication.dto.response;

import com.sistemabarberia.fadex_backend.auth.refreshToken.entity.RefreshToken;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
@AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String tokenType = "Bearer";
    private long expiresIn;
    private String username;
    private String rol;

    private List<String> permisos;



}