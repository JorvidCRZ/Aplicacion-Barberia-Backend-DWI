package com.sistemabarberia.fadex_backend.auth.usuario.dto.request;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    private String username;
    private String password;
    private Integer idRol;
    private Integer idPersona;
}
