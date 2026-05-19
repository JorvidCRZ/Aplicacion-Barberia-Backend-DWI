package com.sistemabarberia.fadex_backend.modules.reserva.dto.reponse;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ResumenDiarioDTO {
    private long enEspera;
    private long enProceso;
    private long completados;
    private long totalDia;
    private BigDecimal totalIngresos;
    private List<ReservaDTO> reservas;
}
