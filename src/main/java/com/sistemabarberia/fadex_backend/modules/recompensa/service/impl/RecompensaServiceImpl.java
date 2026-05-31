package com.sistemabarberia.fadex_backend.modules.recompensa.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.exception.ResourceNotFoundException;
import com.sistemabarberia.fadex_backend.modules.recompensa.dto.response.RecompensaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.recompensa.entity.Recompensa;
import com.sistemabarberia.fadex_backend.modules.recompensa.mapper.RecompensaMapper;
import com.sistemabarberia.fadex_backend.modules.recompensa.repository.RecompensaRepository;
import com.sistemabarberia.fadex_backend.modules.recompensa.service.IRecompensaService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class RecompensaServiceImpl implements IRecompensaService {

    @Autowired
    private RecompensaRepository recompensaRepository;

    @Autowired
    private RecompensaMapper mapper;

    // ── Consultas ────────────────────────────────────────────────────────────

    @Override
    public RecompensaResponseDTO obtenerTarjeta(Integer clienteId) {
        Recompensa recompensa = recompensaRepository.findByCliente_ClienteId(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tarjeta de recompensa no encontrada para el cliente: " + clienteId));
        return mapper.toResponseDTO(recompensa);
    }

    @Override
    public RecompensaResponseDTO obtenerTarjetaPropia(Integer usuarioId) {
        Recompensa recompensa = recompensaRepository
                .findByCliente_Persona_Usuario_IdUsuario(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tarjeta de recompensa no encontrada para el usuario autenticado"));
        return mapper.toResponseDTO(recompensa);
    }

    // ── Lógica de negocio ────────────────────────────────────────────────────

    @Transactional
    @Override
    public void acumularCorte(Integer clienteId) {
        // Verifica que exista la tarjeta
        recompensaRepository.findByCliente_ClienteId(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tarjeta de recompensa no encontrada para el cliente: " + clienteId));

        // La query maneja sola el reset a 10 y el +1 de gratis
        recompensaRepository.acumularCorte(clienteId);
    }

    @Transactional
    @Override
    public void canjearCorteGratis(Integer clienteId) {
        Recompensa recompensa = recompensaRepository.findByCliente_ClienteId(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Tarjeta de recompensa no encontrada para el cliente: " + clienteId));

        if (recompensa.getCortesGratis() <= 0) {
            throw new BusinessException(
                    "El cliente no tiene cortes gratis disponibles",
                    HttpStatus.BAD_REQUEST);
        }

        int filasAfectadas = recompensaRepository.canjearCorteGratis(clienteId);
        if (filasAfectadas == 0) {
            throw new BusinessException(
                    "No se pudo canjear el corte gratis",
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}