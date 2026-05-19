package com.sistemabarberia.fadex_backend.auth.usuario.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateUsernameRequest {

    @NotBlank(message = "El usuario es obligatorio")
    @Size(min = 4, max = 50)
    private String username;
}