package com.sistemabarberia.fadex_backend.modules.barbero.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ResumenBarberoDTO {

    // Tarjetas de barberos
    private long totalBarberos;
    private long disponibles;
    private long ocupados;

    // Ventas hoy
    private BigDecimal ventasHoy;
    private String porcentajeVsAyer;

    // Mejor del mes
    private String mejorDelMes;
    private BigDecimal totalGeneradoMejor;
}