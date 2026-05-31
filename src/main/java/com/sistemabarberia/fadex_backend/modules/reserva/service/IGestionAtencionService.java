package com.sistemabarberia.fadex_backend.modules.reserva.service;

import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.ReservaDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.ResumenDiarioDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.ResumenSemanalDTO;

public interface IGestionAtencionService {
    ResumenDiarioDTO obtenerResumenDiario(Integer barberoId);

    ReservaDTO iniciarAtencion(Integer reservaId);

    ReservaDTO finalizarAtencion(Integer reservaId);

    ResumenSemanalDTO obtenerResumenSemanal(Integer barberoId);

    ReservaDTO cancelarReserva(Integer reservaId);
}
