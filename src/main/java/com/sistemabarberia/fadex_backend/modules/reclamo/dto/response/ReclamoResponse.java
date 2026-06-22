package com.sistemabarberia.fadex_backend.modules.reclamo.dto.response;

import com.sistemabarberia.fadex_backend.modules.reclamo.entity.enums.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReclamoResponse {
    private Long idReclamo;
    private String numeroReclamo;
    private String nombreCliente;
    private String correoCliente;
    private String telefonoCliente;
    private TipoReclamacion tipoReclamacion;
    private TipoProblema tipoProblema;
    private CausaReclamo causaReclamo;
    private EstadoReclamo estadoReclamo;
    private SolucionReclamo solucionReclamo;
    private String descripcion;
    private String notasInternas;
    private BigDecimal montoReclamado;
    private BigDecimal montoCompensado;
    private LocalDateTime fechaOcurrencia;
    private LocalDateTime fechaReclamo;
    private LocalDateTime fechaResolucion;
    private Boolean esPublico;
    private List<ReclamoAdjuntoResponse> adjuntos;
}