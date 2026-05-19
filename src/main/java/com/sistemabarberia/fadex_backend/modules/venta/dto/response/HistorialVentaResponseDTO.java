package com.sistemabarberia.fadex_backend.modules.venta.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialVentaResponseDTO {

    private Integer historialVentaId;

    private Integer ventaId;

    private LocalDateTime fecha;
}