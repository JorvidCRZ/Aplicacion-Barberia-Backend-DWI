package com.sistemabarberia.fadex_backend.modules.producto.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductoResponse {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private Integer stock;
    private boolean estado;
    private boolean publicado;
    private Long idCategoria;
    private String nombreCategoria;
    private List<String> urlsMultimedia;
}