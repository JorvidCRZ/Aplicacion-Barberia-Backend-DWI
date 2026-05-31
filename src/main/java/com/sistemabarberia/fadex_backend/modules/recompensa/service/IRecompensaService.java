package com.sistemabarberia.fadex_backend.modules.recompensa.service;

import com.sistemabarberia.fadex_backend.modules.recompensa.dto.response.RecompensaResponseDTO;

public interface IRecompensaService {

    // Para admins / barberos — busca por clienteId
    RecompensaResponseDTO obtenerTarjeta(Integer clienteId);

    // Para el cliente autenticado — busca por usuarioId del JWT
    RecompensaResponseDTO obtenerTarjetaPropia(Integer usuarioId);

    // Llamado internamente al completar un pago de corte (no gratis)
    void acumularCorte(Integer clienteId);

    // Llamado al crear una reserva con corte gratis
    void canjearCorteGratis(Integer clienteId);
}