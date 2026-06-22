package com.sistemabarberia.fadex_backend.modules.servicio.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicioResponseDTO {

    private Long servicioId;
    private String nombre;
    private BigDecimal precio;
    private Long categoriaId;
    private String descripcion;
    private String categoriaNombre;
    private Integer duracion;
    private boolean estado;
    private boolean publicado;
    private List<String> urlsMultimedia;
}