package com.sistemabarberia.fadex_backend.modules.persona.mapper;


import com.sistemabarberia.fadex_backend.modules.persona.dto.request.PersonaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.persona.dto.response.PersonaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface PersonaMapper {

    @Mapping(source = "usuario.idUsuario", target = "usuarioId")
    PersonaResponseDTO toResponseDTO(Persona persona);

    @Mapping(target = "personaId", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    Persona toEntity(PersonaRequestDTO dto);
}