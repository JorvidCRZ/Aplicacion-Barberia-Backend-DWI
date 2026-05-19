package com.sistemabarberia.fadex_backend.modules.barbero.service;

import com.sistemabarberia.fadex_backend.modules.barbero.dto.request.BarberoRequestDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.request.BarberoUpdateRequestDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.response.BarberoResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBarberoService {

    //Paginacion
    Page<BarberoResponseDTO> listarBarberos(Pageable pageable);


    //Crear
    BarberoResponseDTO crearBarbero(BarberoRequestDTO dto);

    //Eliminar
    BarberoResponseDTO eliminar(Integer id);

    //Actualizar
    BarberoResponseDTO actualizarBarbero(Integer id, BarberoUpdateRequestDTO dto);

    //Buscar
    BarberoResponseDTO buscarBarbero(Integer id);
}