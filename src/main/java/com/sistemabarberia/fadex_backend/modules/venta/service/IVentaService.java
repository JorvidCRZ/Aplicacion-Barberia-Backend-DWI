package com.sistemabarberia.fadex_backend.modules.venta.service;

import com.sistemabarberia.fadex_backend.modules.venta.dto.request.VentaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.venta.dto.response.*;

import java.util.List;

public interface IVentaService {

    VentaResponseDTO crear(VentaRequestDTO dto);

    List<VentaResponseDTO> listar();

    List<VentaResponseDTO> listar(String cliente);

    VentaResponseDTO obtenerPorId(Integer id);

    VentaResponseDTO actualizar(Integer id, VentaRequestDTO dto);

    void eliminar(Integer id);

    List<DetalleVentaResponseDTO> listarDetalles(Integer ventaId);

    List<HistorialVentaResponseDTO> listarHistorial(Integer ventaId);
}