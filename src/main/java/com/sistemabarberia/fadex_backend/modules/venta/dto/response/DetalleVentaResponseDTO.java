package com.sistemabarberia.fadex_backend.modules.venta.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetalleVentaResponseDTO {

    private Integer detalleVentaId;

    private Integer ventaId;

    private Integer productoId;
    private String productoNombre;

    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
}