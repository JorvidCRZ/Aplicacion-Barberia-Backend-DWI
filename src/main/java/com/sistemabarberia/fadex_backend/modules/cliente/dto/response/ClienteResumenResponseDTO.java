package com.sistemabarberia.fadex_backend.modules.cliente.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClienteResumenResponseDTO {

    private Long totalClientes;
    private String deltaTotalClientes;

    private Long clientesActivosMes;
    private String deltaClientesActivos;

    private Long nuevosClientes;
    private String deltaNuevosClientes;

    private Double retencion;
    private String deltaRetencion;

}