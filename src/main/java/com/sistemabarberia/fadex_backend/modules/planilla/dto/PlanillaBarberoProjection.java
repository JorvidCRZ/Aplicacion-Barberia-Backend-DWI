package com.sistemabarberia.fadex_backend.modules.planilla.dto;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlanillaBarberoProjection {

    private Integer barberoId;

    private String nombre;

    private String apellido;

    private BigDecimal sueldo;

    private BigDecimal porcentajeComision;

    private Long cantidadVentas;

    private BigDecimal totalVentas;
}