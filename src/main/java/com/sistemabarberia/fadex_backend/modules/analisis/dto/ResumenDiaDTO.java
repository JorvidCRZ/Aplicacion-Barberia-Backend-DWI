package com.sistemabarberia.fadex_backend.modules.analisis.dto;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResumenDiaDTO {
    private String dia;
    private Long reservas;
    private Long completadas;
    private Long canceladas;
    private BigDecimal ingresos;
}
