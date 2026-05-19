package com.sistemabarberia.fadex_backend.modules.persona.service.impl;

import com.sistemabarberia.fadex_backend.modules.persona.dto.request.PersonaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.persona.dto.response.PersonaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import com.sistemabarberia.fadex_backend.modules.persona.mapper.PersonaMapper;
import com.sistemabarberia.fadex_backend.modules.persona.repository.PersonaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonaServiceImplTest {

    @Mock
    private PersonaRepository personaRepository;

    @Mock
    private PersonaMapper mapper;

    @InjectMocks
    private PersonaServiceImpl personaService;
    @Test
    void deberiaCrearPersonaCorrectamente() {
        PersonaRequestDTO dto = new PersonaRequestDTO();
        dto.setNombre("Juan");
        dto.setApellido("Pérez");
        dto.setTelefono("987654321");
        dto.setEmail("juan@gmail.com");

        Persona entidad = Persona.builder().nombre("Juan").build();
        Persona guardada = Persona.builder().personaId(1).nombre("Juan").build();
        PersonaResponseDTO responseDTO = PersonaResponseDTO.builder()
                .personaId(1).nombre("Juan").build();

        when(mapper.toEntity(dto)).thenReturn(entidad);
        when(personaRepository.save(entidad)).thenReturn(guardada);
        when(mapper.toResponseDTO(guardada)).thenReturn(responseDTO);

        PersonaResponseDTO resultado = personaService.crearPersona(dto);

        assertThat(resultado.getPersonaId()).isEqualTo(1);
        assertThat(resultado.getNombre()).isEqualTo("Juan");
        verify(personaRepository, times(1)).save(entidad);
    }

    @Test
    void deberiaBuscarPersonaPorId() {
        Persona persona = Persona.builder().personaId(1).nombre("Juan").build();
        PersonaResponseDTO responseDTO = PersonaResponseDTO.builder()
                .personaId(1).nombre("Juan").build();

        when(personaRepository.findById(1)).thenReturn(Optional.of(persona));
        when(mapper.toResponseDTO(persona)).thenReturn(responseDTO);

        PersonaResponseDTO resultado = personaService.buscarPersona(1);

        assertThat(resultado.getNombre()).isEqualTo("Juan");
    }

    @Test
    void deberiLanzarExcepcionSiPersonaNoExiste() {
        when(personaRepository.findById(99)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> personaService.buscarPersona(99))
                .isInstanceOf(RuntimeException.class);
    }
}