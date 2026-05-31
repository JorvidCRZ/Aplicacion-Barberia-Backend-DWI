package com.sistemabarberia.fadex_backend.modules.reserva.dto.Response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResumenSemanalDTO {

    private List<DiaSemana> dias;
    private BigDecimal sueldoBase;
    private BigDecimal comisionSemanal;
    private BigDecimal totalSemana;

    @Getter
    @Setter
    @AllArgsConstructor
    public static class DiaSemana {
        private String fecha;
        private long atendidos;
        private long cancelados;
        private BigDecimal totalIngresos;

    }
}