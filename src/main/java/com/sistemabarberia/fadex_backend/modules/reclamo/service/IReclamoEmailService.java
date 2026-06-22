package com.sistemabarberia.fadex_backend.modules.reclamo.service;

import com.sistemabarberia.fadex_backend.modules.reclamo.dto.ReclamoEmailDTO;

public interface IReclamoEmailService {
    void enviarConfirmacionCliente(String email, ReclamoEmailDTO reclamo);
    void enviarCambioEstado(String email, ReclamoEmailDTO reclamo);
}
