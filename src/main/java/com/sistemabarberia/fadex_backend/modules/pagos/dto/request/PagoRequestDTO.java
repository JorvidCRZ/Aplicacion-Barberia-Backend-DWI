package com.sistemabarberia.fadex_backend.modules.pagos.dto.request;


import com.sistemabarberia.fadex_backend.modules.pagos.entity.MetodoPago;
import com.sistemabarberia.fadex_backend.modules.pagos.entity.TipoPago;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoRequestDTO {

    @NotNull(message = "Cliente obligatorio")
    private Integer clienteId;

    @NotNull(message = "Barbero obligatorio")
    private Integer barberoId;

    private Long reservaId;

    private Integer ventaId;


    @NotNull(message = "Monto obligatorio")
    //@DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
    @DecimalMin(value = "0.00", message = "El monto no puede ser negativo")
    private BigDecimal monto;

    @NotNull(message = "Método de pago obligatorio")
    private MetodoPago metodo;

    @NotNull(message = "Tipo de pago obligatorio")
    private TipoPago tipo;
}