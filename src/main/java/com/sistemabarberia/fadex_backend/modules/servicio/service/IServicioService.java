package com.sistemabarberia.fadex_backend.modules.servicio.service;


import com.sistemabarberia.fadex_backend.modules.servicio.dto.request.ServicioRequestDTO;

import com.sistemabarberia.fadex_backend.modules.servicio.dto.response.ServicioResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IServicioService {

    ServicioResponseDTO crear(ServicioRequestDTO dto, List<MultipartFile> archivos);

    List<ServicioResponseDTO> listar();

    List<ServicioResponseDTO> listarPorCategoria(Long categoriaId);

    ServicioResponseDTO obtenerPorId(Long id);

    ServicioResponseDTO actualizar(Long id, ServicioRequestDTO dto);

    void eliminar(Long id);
}