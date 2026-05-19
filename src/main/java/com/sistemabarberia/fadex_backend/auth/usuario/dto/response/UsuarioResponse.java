package com.sistemabarberia.fadex_backend.auth.usuario.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioResponse {
    private Integer idUsuario;
    private String username;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;
    private String rol;
}
