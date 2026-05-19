package com.sistemabarberia.fadex_backend.modules.reserva.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetalleReservaDTO {

    private Long idDetalle;
    private String nombreCorte;
    private Double precio;
}
