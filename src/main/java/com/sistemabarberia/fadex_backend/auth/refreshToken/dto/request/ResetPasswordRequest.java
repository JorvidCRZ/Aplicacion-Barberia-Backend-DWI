package com.sistemabarberia.fadex_backend.auth.refreshToken.dto.request;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
    private String token;
    private String nuevaPassword;
}
