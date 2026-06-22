package com.sistemabarberia.fadex_backend.modules.planilla.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetalleBarberoResumenDTO {

    private Integer barberoId;

    private String nombreBarbero;

    private BigDecimal sueldoBase;

    private BigDecimal porcentajeComision;

    private Long cantidadVentas;

    private BigDecimal totalVentas;
    private BigDecimal montoComision;

    private BigDecimal sueldoFinal;
}