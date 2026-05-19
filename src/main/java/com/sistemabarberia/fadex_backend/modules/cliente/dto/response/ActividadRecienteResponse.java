package com.sistemabarberia.fadex_backend.modules.cliente.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActividadRecienteResponse {

    private String tipo;

    private String titulo;

    private String descripcion;

    private LocalDateTime fecha;

    private String color;
}