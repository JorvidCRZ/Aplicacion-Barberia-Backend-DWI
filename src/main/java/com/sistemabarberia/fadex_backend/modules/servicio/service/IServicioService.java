package com.sistemabarberia.fadex_backend.modules.servicio.service;

import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.servicio.dto.ServicioFiltro;
import com.sistemabarberia.fadex_backend.modules.servicio.dto.request.ServicioRequestDTO;
import com.sistemabarberia.fadex_backend.modules.servicio.dto.response.ServicioResponseDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IServicioService {

    ServicioResponseDTO obtenerServicioPublicadoPorId(Long id);

    PageResponse<ServicioResponseDTO> listarServiciosPublicos(
            ServicioFiltro filtro,
            Pageable pageable
    );

    PageResponse<ServicioResponseDTO> listarServicioFiltros(
            ServicioFiltro filtro,
            Pageable pageable
    );

    ServicioResponseDTO obtenerPorId(Long id);

    ServicioResponseDTO crear(
            ServicioRequestDTO dto,
            List<MultipartFile> archivos
    );

    ServicioResponseDTO actualizar(
            Long id,
            ServicioRequestDTO dto,
            List<MultipartFile> archivos
    );

    ServicioResponseDTO cambiarEstadoServicio(
            Long id,
            boolean estado
    );

    ServicioResponseDTO cambiarPublicacion(
            Long id,
            boolean publicado
    );

    void eliminar(Long id);
}