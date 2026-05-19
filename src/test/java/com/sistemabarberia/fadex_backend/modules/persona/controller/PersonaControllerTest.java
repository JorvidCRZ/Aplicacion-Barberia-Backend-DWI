package com.sistemabarberia.fadex_backend.modules.persona.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistemabarberia.fadex_backend.auth.security.filter.JwtAuthenticationFilter;
import com.sistemabarberia.fadex_backend.modules.persona.dto.request.PersonaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.persona.dto.response.PersonaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.persona.service.IPersonaService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = PersonaController.class,
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtAuthenticationFilter.class
            )
)

@AutoConfigureMockMvc(addFilters = false)


@TestPropertySource(properties = {
        "spring.flyway.enabled=false"

})

class PersonaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IPersonaService personaService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void deberiaListarPersonas() throws Exception {
        // GIVEN
        PersonaResponseDTO dto = PersonaResponseDTO.builder()
                .personaId(1).usuarioId(1).nombre("Juan").apellido("Pérez")
                .telefono("987654321").email("juan@gmail.com")
                .build();

        when(personaService.listarPersonas(any()))
                .thenReturn(new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1));

        // WHEN + THEN
        mockMvc.perform(get("/api/v1/personas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].nombre").value("Juan"));
    }

    @Test
    void deberiaBuscarPersonaPorId() throws Exception {
        // GIVEN
        PersonaResponseDTO dto = PersonaResponseDTO.builder()
                .personaId(1).nombre("Juan").build();

        when(personaService.buscarPersona(1)).thenReturn(dto);
        mockMvc.perform(get("/api/v1/personas/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nombre").value("Juan"));
    }

    @Test
    void deberiaCrearPersona() throws Exception {
        // GIVEN
        PersonaRequestDTO request = new PersonaRequestDTO();
        request.setUsuarioId(1);
        request.setNombre("Juan");
        request.setApellido("Pérez");
        request.setTelefono("987654321");
        request.setEmail("juan@gmail.com");

        PersonaResponseDTO response = PersonaResponseDTO.builder()
                .personaId(1).nombre("Juan").build();

        when(personaService.crearPersona(any())).thenReturn(response);

        // WHEN + THEN
        mockMvc.perform(post("/api/v1/personas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.personaId").value(1));
    }

    @Test
    void deberiaEliminarPersona() throws Exception {
        // GIVEN
        PersonaResponseDTO response = PersonaResponseDTO.builder()
                .personaId(1).nombre("Juan").build();

        when(personaService.eliminar(1)).thenReturn(response);

        // WHEN + THEN
        mockMvc.perform(delete("/api/v1/personas/eliminar/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.personaId").value(1));
    }
}