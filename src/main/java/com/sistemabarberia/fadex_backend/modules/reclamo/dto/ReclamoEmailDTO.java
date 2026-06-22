package com.sistemabarberia.fadex_backend.modules.reclamo.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ReclamoEmailDTO(
        String nombreCliente,
        String numeroReclamo,
        String tipoReclamacion,
        String tipoProblema,
        String estado,
        String solucionReclamo,
        LocalDateTime fechaReclamo
) {}
