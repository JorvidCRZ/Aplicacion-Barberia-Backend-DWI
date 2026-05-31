package com.sistemabarberia.fadex_backend.modules.venta.dto.response;

import com.sistemabarberia.fadex_backend.modules.venta.entity.TipoComprobante;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaResponseDTO {

    private Integer ventaId;

    private Integer clienteId;
    private String clienteNombre;

//    private Integer barberoId;
//    private String barberoNombre;

    private LocalDateTime fecha;

    private TipoComprobante tipoComprobante;

    private List<DetalleVentaResponseDTO> detalles;
}