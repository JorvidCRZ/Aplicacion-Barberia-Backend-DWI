package com.sistemabarberia.fadex_backend.modules.reserva.dto.Response;

import com.sistemabarberia.fadex_backend.modules.reserva.entity.TipoReserva;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record ReservaDTO(
        Long reservaId,
        String clienteNombre,
        String barberoNombre,
        String servicio,
        LocalDate fecha,
        LocalTime horaInicio,
        LocalTime horaFin,
        TipoReserva tipoReserva,
        BigDecimal total
) {}
