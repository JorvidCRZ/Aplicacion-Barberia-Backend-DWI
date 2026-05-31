package com.sistemabarberia.fadex_backend.auth.usuario.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermisoResponse {

    private Integer idPermiso;
    private String nombre;
    private String descripcion;
}