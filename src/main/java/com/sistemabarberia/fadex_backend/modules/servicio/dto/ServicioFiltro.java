package com.sistemabarberia.fadex_backend.modules.servicio.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ServicioFiltro {

    private Long id;

    private String nombre;

    private Long categoriaId;

    private BigDecimal precioMin;

    private BigDecimal precioMax;

    private Boolean estado;

    private Boolean publicado;
}
