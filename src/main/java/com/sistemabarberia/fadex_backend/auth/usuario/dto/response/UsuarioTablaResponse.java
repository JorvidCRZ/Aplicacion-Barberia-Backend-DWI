package com.sistemabarberia.fadex_backend.auth.usuario.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UsuarioTablaResponse {

    private Integer idUsuario;

    private String usuario;

    private String nombre;

    private String apellido;

    private Boolean tieneQr;

    private List<String> roles;
}