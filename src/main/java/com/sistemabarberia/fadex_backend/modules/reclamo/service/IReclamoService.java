package com.sistemabarberia.fadex_backend.modules.reclamo.service;

import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.ReclamoFiltro;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.ReclamoResumen;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.request.ReclamoPublicoRequest;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.request.ReclamoRequest;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.request.ReclamoSolucionRequest;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.response.ReclamoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IReclamoService {
    ReclamoResponse crearReclamo(ReclamoRequest request, List<MultipartFile> archivos);
    ReclamoResponse crearReclamoPublico(ReclamoPublicoRequest request, List<MultipartFile> archivos);
    ReclamoResponse obtenerReclamoPorId(Long id);
    PageResponse<ReclamoResponse> listarReclamoFiltros(ReclamoFiltro filtro, Pageable pageable);
    ReclamoResponse actualizarReclamoSolucion(Long id, ReclamoSolucionRequest request);
    ReclamoResumen obtenerReclamoResumen();
    void eliminarReclamo(Long id);
}
