package com.sistemabarberia.fadex_backend.modules.reclamo.dto.request;

import com.sistemabarberia.fadex_backend.modules.reclamo.entity.enums.EstadoReclamo;
import com.sistemabarberia.fadex_backend.modules.reclamo.entity.enums.SolucionReclamo;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReclamoSolucionRequest {
    @NotNull
    private EstadoReclamo estadoReclamo;
    private SolucionReclamo solucionReclamo;
    private String notasInternas;
    private BigDecimal montoCompensado;
}