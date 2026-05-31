package com.sistemabarberia.fadex_backend.modules.barbero.service;

import com.sistemabarberia.fadex_backend.modules.barbero.dto.request.BarberoRequestDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.request.BarberoUpdateRequestDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.response.BarberoDetalleResponseDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.response.BarberoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.response.ResumenBarberoDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.response.ResumenIndividualBarberoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBarberoService {

    // Listar con paginación
    Page<BarberoResponseDTO> listarBarberos(Pageable pageable);

    Page<BarberoResponseDTO> listarBarberosInhabilitados(Pageable pageable);

    // Crear
    BarberoResponseDTO crearBarbero(BarberoRequestDTO dto);

    // Eliminar
    BarberoResponseDTO eliminar(Integer id);

    // Actualizar
    BarberoResponseDTO actualizarBarbero(Integer id, BarberoUpdateRequestDTO dto);

    // Buscar por ID
    BarberoResponseDTO buscarBarbero(Integer id);

    // Búsqueda combinada (filtro + orden)
    Page<BarberoResponseDTO> buscar(String estado, String ordenarPor, String direccion, Pageable pageable);

    // Resumen del dashboard
    ResumenBarberoDTO obtenerResumen();

    BarberoDetalleResponseDTO obtenerPerfilPropio(Integer usuarioId);
    BarberoResponseDTO toggleOcupado(Integer id);

    Page<BarberoResponseDTO> buscarPorNombre(String termino, Pageable pageable);

    ResumenIndividualBarberoDTO obtenerResumenIndividual(Integer id);

    void deshabilitarBarbero(Integer id);

    void reactivarBarbero(Integer id);
}