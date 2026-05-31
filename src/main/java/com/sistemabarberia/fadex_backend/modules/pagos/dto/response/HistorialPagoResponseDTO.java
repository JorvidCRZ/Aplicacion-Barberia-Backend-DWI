package com.sistemabarberia.fadex_backend.modules.pagos.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HistorialPagoResponseDTO {
    private Long id;
    private Long pagoId;
    private Integer clienteId;
    private String clienteNombre;
    private LocalDateTime fecha;
}