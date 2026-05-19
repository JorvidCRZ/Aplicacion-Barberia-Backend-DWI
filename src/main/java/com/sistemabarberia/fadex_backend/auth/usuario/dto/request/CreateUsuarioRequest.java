package com.sistemabarberia.fadex_backend.auth.usuario.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUsuarioRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotNull
    private Integer idRol;
    @NotBlank
    private String nombre;
    @NotBlank
    private String apellido;
    private String telefono;
    @Email
    private String email;
}
