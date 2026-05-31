package com.sistemabarberia.fadex_backend.modules.pagos.dto.response;

import com.sistemabarberia.fadex_backend.modules.pagos.entity.MetodoPago;
import com.sistemabarberia.fadex_backend.modules.pagos.entity.TipoPago;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoResponseDTO {

    private Long id;
    private Integer clienteId;
    private String clienteNombre;
    private Integer barberoId;
    private String barberoNombre;
    private Long reservaId;
    private Integer ventaId;
    private BigDecimal monto;
    private MetodoPago metodo;
    private TipoPago tipo;
    private LocalDateTime fecha;
}