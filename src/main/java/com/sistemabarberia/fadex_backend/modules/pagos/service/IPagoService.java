package com.sistemabarberia.fadex_backend.modules.pagos.service;

import com.sistemabarberia.fadex_backend.modules.pagos.dto.request.PagoRequestDTO;
import com.sistemabarberia.fadex_backend.modules.pagos.dto.response.HistorialPagoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.pagos.dto.response.PagoResponseDTO;

import java.util.List;

public interface IPagoService {

    PagoResponseDTO crear(PagoRequestDTO dto);

    List<PagoResponseDTO> listar();

    List<PagoResponseDTO> listar(String cliente);

    PagoResponseDTO obtenerPorId(Long id);

    PagoResponseDTO actualizar(Long id, PagoRequestDTO dto);

    void eliminar(Long id);

    List<HistorialPagoResponseDTO> listarHistorial(Long pagoId);
}