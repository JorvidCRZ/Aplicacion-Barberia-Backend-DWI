package com.sistemabarberia.fadex_backend.modules.planilla.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VentaBarberoDTO {

    private Integer ventaId;

    private LocalDateTime fecha;

    private String nombreCliente;

    private BigDecimal total;
}