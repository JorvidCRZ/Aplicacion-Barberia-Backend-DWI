package com.sistemabarberia.fadex_backend.modules.persona.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;
import com.sistemabarberia.fadex_backend.modules.persona.dto.request.PersonaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.persona.dto.request.PersonaUpdateRequestDTO;
import com.sistemabarberia.fadex_backend.modules.persona.dto.response.PersonaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import com.sistemabarberia.fadex_backend.modules.persona.mapper.PersonaMapper;
import com.sistemabarberia.fadex_backend.modules.persona.repository.PersonaRepository;
import com.sistemabarberia.fadex_backend.modules.persona.service.IPersonaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PersonaServiceImpl implements IPersonaService {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private PersonaMapper mapper;


    @Override
    public Page<PersonaResponseDTO> listarPersonas(Pageable pageable) {
        return personaRepository.findAll(pageable)
                .map(mapper::toResponseDTO);
    }

    @Override
    public PersonaResponseDTO crearPersona(PersonaRequestDTO dto) {
        Persona persona = mapper.toEntity(dto);
        Persona guardado = personaRepository.save(persona);
        return mapper.toResponseDTO(guardado);
    }

    //Eliminar
    @Override
    public PersonaResponseDTO eliminar(Integer id) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrado"));
        PersonaResponseDTO dto = mapper.toResponseDTO(persona);
        personaRepository.delete(persona);

        return dto;
    }

    //Actualizar
    @Override
    public PersonaResponseDTO actualizarPersona(Integer id, PersonaUpdateRequestDTO dto) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Persona no encontrada con id: " + id,
                        HttpStatus.NOT_FOUND
                ));

        if (dto.getNombre() != null) persona.setNombre(dto.getNombre());
        if (dto.getApellido() != null) persona.setApellido(dto.getApellido());
        if (dto.getTelefono() != null) persona.setTelefono(dto.getTelefono());
        if (dto.getEmail() != null) persona.setEmail(dto.getEmail());

        Persona actualizado = personaRepository.save(persona);
        return mapper.toResponseDTO(actualizado);
    }

    //Buscar
    @Override
    public PersonaResponseDTO buscarPersona(Integer id) {
        Persona persona = personaRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Persona no encontrado con id: " + id,
                        HttpStatus.NOT_FOUND
                ));

        return mapper.toResponseDTO(persona);
    }
}
