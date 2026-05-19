package com.sistemabarberia.fadex_backend.modules.cliente.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteDetalleResumenDTO {

    private Long totalReservas;
    private Long totalCortes;
    private Long totalCompras;
    private Double totalGastado;
    private String ultimaVisita;
}