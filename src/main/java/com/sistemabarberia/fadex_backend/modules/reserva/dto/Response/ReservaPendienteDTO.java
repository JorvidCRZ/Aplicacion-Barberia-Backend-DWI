package com.sistemabarberia.fadex_backend.modules.reserva.dto.Response;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservaPendienteDTO {
    private Long id;
    private Integer clienteId;
    private String clienteNombre;
    private String clienteApellido;
    private Integer barberoId;
    private String barberoNombre;
    private BigDecimal montoTotal;
    private List<String> servicios;
}