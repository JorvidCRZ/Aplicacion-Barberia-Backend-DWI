package com.sistemabarberia.fadex_backend.modules.categoria.service;

import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.categoria.dto.CategoriaFiltro;
import com.sistemabarberia.fadex_backend.modules.categoria.dto.request.CategoriaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.categoria.dto.response.CategoriaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.CategoriaEnum;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICategoriaService {
    List<Long> obtenerIdsCategoriaYRamas(Long categoriaId);
    List<CategoriaResponseDTO> listarCategoriasPadre(CategoriaEnum tipo);
    PageResponse<CategoriaResponseDTO> listarConFiltro(CategoriaFiltro filtro, Pageable pageable);
    CategoriaResponseDTO obtenerPorId(Long id);
    CategoriaResponseDTO crear(CategoriaRequestDTO dto);
    CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO dto);
    CategoriaResponseDTO cambiarEstado(Long id, Boolean estado);
    void eliminar(Long id);
}