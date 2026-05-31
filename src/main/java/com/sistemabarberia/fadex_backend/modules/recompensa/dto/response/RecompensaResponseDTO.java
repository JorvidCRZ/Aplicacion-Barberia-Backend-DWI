package com.sistemabarberia.fadex_backend.modules.recompensa.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RecompensaResponseDTO {

    private Integer recompensaId;
    private Integer clienteId;
    private String  nombreCliente;

    // Tarjeta virtual
    private Integer cortesAcumulados;   // 0–9 (casillas marcadas)
    private Integer cortesGratis;       // cortes gratis disponibles
    private Integer cortesParaProximo;  // cuántos faltan para el siguiente gratis
    private boolean tieneCorteGratis;   // atajo para el front

    private LocalDateTime fechaActualizacion;
}