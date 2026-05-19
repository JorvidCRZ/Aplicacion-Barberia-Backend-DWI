package com.sistemabarberia.fadex_backend.modules.reserva.dto.Request;

import com.sistemabarberia.fadex_backend.modules.reserva.entity.EstadoReserva;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActualizarEstadoReservaDTO {

    @NotNull(message = "El estado no puede ser nulo")
    private EstadoReserva estado;
}