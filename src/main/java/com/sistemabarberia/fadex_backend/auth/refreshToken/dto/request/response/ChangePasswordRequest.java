package com.sistemabarberia.fadex_backend.auth.refreshToken.dto.request.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ChangePasswordRequest {
    @NotBlank
    private String passwordActual;

    @NotBlank
    @Size(min = 8)
    private String passwordNueva;
}
