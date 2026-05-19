package com.sistemabarberia.fadex_backend.modules.producto.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductoFiltro {

    private String nombre;

    @Positive(message = "idCategoria debe ser mayor a 0")
    private Long idCategoria;

    private Boolean estado;
    private Boolean publicado;

    @DecimalMin(value = "0.0", inclusive = true, message = "precioMax no puede ser negativo")
    private BigDecimal precioMax;

    @DecimalMin(value = "0.0", inclusive = true, message = "precioMin no puede ser negativo")
    private BigDecimal precioMin;

    @AssertTrue(message = "precioMin no puede ser mayor que precioMax")
    public boolean isRangoPrecioValido() {
        if (precioMin == null || precioMax == null) {
            return true;
        }
        return precioMin.compareTo(precioMax) <= 0;
    }
}