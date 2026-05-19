package com.sistemabarberia.fadex_backend.modules.venta.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaRequestDTO {

    @NotNull(message = "Cliente obligatorio")
    private Integer clienteId;

    @NotNull(message = "Barbero obligatorio")
    private Integer barberoId;

    @NotNull(message = "Fecha obligatoria")
    private LocalDateTime fecha;

    @NotEmpty(message = "Debe tener al menos un detalle")
    private List<DetalleVentaRequestDTO> detalles;
}