package com.sistemabarberia.fadex_backend.modules.barbero.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ResumenIndividualBarberoDTO {
    private String nombreBarbero;
    private long cortesEsteMes;
    private BigDecimal ingresosGenerados;
    private BigDecimal comisionGanada;
    private long reservasHoy;
}