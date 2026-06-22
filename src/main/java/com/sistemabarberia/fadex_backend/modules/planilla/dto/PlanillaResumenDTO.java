package com.sistemabarberia.fadex_backend.modules.planilla.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlanillaResumenDTO {

    private BigDecimal totalPlanilla;
    private BigDecimal totalComisiones;
    private BigDecimal sueldoFinalTotal;

    private Long ventasPeriodo;

    private Long barberosActivos;
}