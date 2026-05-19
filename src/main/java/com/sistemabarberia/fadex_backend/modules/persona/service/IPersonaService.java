package com.sistemabarberia.fadex_backend.modules.persona.service;



import com.sistemabarberia.fadex_backend.modules.persona.dto.request.PersonaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.persona.dto.request.PersonaUpdateRequestDTO;
import com.sistemabarberia.fadex_backend.modules.persona.dto.response.PersonaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPersonaService {

    //Paginacion
    Page<PersonaResponseDTO> listarPersonas(Pageable pageable);

    //Crear
    PersonaResponseDTO crearPersona(PersonaRequestDTO dto);

    //Eliminar
    PersonaResponseDTO eliminar(Integer id);

    //Actualzar
    PersonaResponseDTO actualizarPersona(Integer id, PersonaUpdateRequestDTO dto);

    //Buscar
    PersonaResponseDTO buscarPersona(Integer id);

}
