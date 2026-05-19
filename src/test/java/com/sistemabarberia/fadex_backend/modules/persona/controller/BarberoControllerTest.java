package com.sistemabarberia.fadex_backend.modules.persona.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistemabarberia.fadex_backend.modules.barbero.controller.BarberoController;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.request.BarberoRequestDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.response.BarberoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.service.IBarberoService;
import com.sistemabarberia.fadex_backend.modules.seguridad.security.JwtFilter;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(value = BarberoController.class,
                excludeFilters =@ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = JwtFilter.class
                )
)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = "spring.flyway.enabled=false")
class BarberoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IBarberoService barberoService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void deberiaListarBarberos() throws Exception {
        BarberoResponseDTO dto= BarberoResponseDTO.builder()
                .barberoId(1).experiencia(1)
                .ocupado(true).sueldo(BigDecimal.valueOf(1500.00))
                .comision(BigDecimal.valueOf(0.20)).descripcion("Prueba")
                .fotoUrl("Prueba").fechaIngreso(LocalDate.now()).build();

        when(barberoService.listarBarberos(any()))
                .thenReturn(new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/barberos"))
                .andExpect(status().isOk()) //verifica la respuesta
                .andExpect(jsonPath("$.data.content[0].barberoId").value(1));
    }


    @Test
    void deberiaBuscarBarberoPorId() throws  Exception{
        BarberoResponseDTO dto= BarberoResponseDTO.builder()
                .barberoId(1).build();
        when(barberoService.buscarBarbero(1)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/barberos/1")) //simula peticion http
                .andExpect(status().isOk()) //Verifica que la respuesta HTTP tenga código 200 OK
                .andExpect(jsonPath("$.data.barberoId").value(1));
    }

    @Test
    void deberiaCrearBarbero() throws Exception{
        BarberoRequestDTO request = new BarberoRequestDTO();
        request.setPersonaId(1);
        request.setComision(BigDecimal.valueOf(0.13));
        request.setDescripcion("Prueba");
        request.setOcupado(true);
        request.setExperiencia(12);
        request.setSueldo(BigDecimal.valueOf(2000));
        request.setFotoUrl("http...");

        BarberoResponseDTO response= BarberoResponseDTO.builder()
                .barberoId(1).ocupado(true).build();

        when(barberoService.crearBarbero(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/barberos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.barberoId").value(1));
    }

    @Test
    void deberiaEliminarBarberoPorId() throws Exception{
        BarberoResponseDTO dto= BarberoResponseDTO.builder()
                .barberoId(1).build();
        when(barberoService.eliminar(1)).thenReturn(dto);

        mockMvc.perform(delete("/api/v1/barberos/eliminar/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.barberoId").value(1));
    }
}
