package com.sistemabarberia.fadex_backend.modules.producto.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    private String descripcion;
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal precio;
    @Min(value = 0)
    private Integer stock;
    private boolean estado;
    private boolean publicado;
    @NotNull
    private Long idCategoria;
}