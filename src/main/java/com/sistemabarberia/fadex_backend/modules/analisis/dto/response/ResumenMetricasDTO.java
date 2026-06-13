package com.sistemabarberia.fadex_backend.modules.analisis.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResumenMetricasDTO {
    private BigDecimal ingresosTotales;
    private Long reservasTotales;
    private Long completadas;
    private Long clientesActivos;
    private Long clientesNuevos;
    private BigDecimal ticketPromedio;
}
