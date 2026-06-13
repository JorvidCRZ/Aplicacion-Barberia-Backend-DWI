package com.sistemabarberia.fadex_backend.auth.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class QrLoginRequest {
    @NotBlank
    private String qrToken;

    @NotBlank
    @Size(min = 4, max = 6, message = "El PIN debe tener entre 4 y 6 dígitos")
    private String pin;
}