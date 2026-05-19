package com.sistemabarberia.fadex_backend.modules.venta.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleVentaRequestDTO {

    @NotNull(message = "Producto obligatorio")
    private Integer productoId;

    @NotNull(message = "Cantidad obligatoria")
    @Min(value = 1, message = "Cantidad mínima 1")
    private Integer cantidad;

    @NotNull(message = "Precio obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "Precio mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "Formato inválido")
    private BigDecimal precioUnitario;
}