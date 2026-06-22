package com.sistemabarberia.fadex_backend.modules.reclamo.dto.request;

import com.sistemabarberia.fadex_backend.modules.reclamo.entity.enums.CausaReclamo;
import com.sistemabarberia.fadex_backend.modules.reclamo.entity.enums.TipoProblema;
import com.sistemabarberia.fadex_backend.modules.reclamo.entity.enums.TipoReclamacion;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReclamoRequest {
    private Integer idCliente;
    private Integer idVenta;
    private Integer idReserva;

    @NotBlank
    private String nombreCliente;
    private String correoCliente;
    private String telefonoCliente;
    private String tipoDocumentoCliente;
    private String numeroDocumentoCliente;

    @NotNull
    private TipoReclamacion tipoReclamacion;

    @NotNull
    private TipoProblema tipoProblema;

    private CausaReclamo causaReclamo;

    @NotBlank
    private String descripcion;

    private BigDecimal montoReclamado;
    private LocalDateTime fechaOcurrencia;
    private String notasInternas;
    private Integer idUsuarioResponsable;
}
