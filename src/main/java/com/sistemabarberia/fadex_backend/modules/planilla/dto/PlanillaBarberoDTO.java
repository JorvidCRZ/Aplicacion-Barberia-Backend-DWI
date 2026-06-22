package com.sistemabarberia.fadex_backend.modules.planilla.dto;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanillaBarberoDTO {

    private Integer barberoId;

    private String nombreBarbero;

    private BigDecimal sueldoBase;

    private Long cantidadVentas;

    private BigDecimal totalVentas;

    private BigDecimal porcentajeComision;

    private BigDecimal montoComision;

    private BigDecimal sueldoFinal;
}