package com.sistemabarberia.fadex_backend.modules.persona.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistemabarberia.fadex_backend.modules.cliente.controller.ClienteController;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.request.ClienteRequestDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ClienteResponseDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.service.IClienteService;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(value = ClienteController.class,
        excludeFilters =@ComponentScan.Filter(
                type = FilterType.ASSIGNABLE_TYPE,
                classes = JwtFilter.class
        )
)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = "spring.flyway.enabled=false")
class ClienteControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IClienteService clienteService;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void deberiaListarClientes() throws Exception {
        ClienteResponseDTO dto= ClienteResponseDTO.builder()
                .clienteId(1).fechaRegistro(LocalDate.now()).build();

        when(clienteService.listarClientes(any()))
                .thenReturn(new PageImpl<>(List.of(dto), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/clientes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content[0].clienteId").value(1));
    }


    @Test
    void deberiaBuscarClientesPorId() throws  Exception{
        ClienteResponseDTO dto= ClienteResponseDTO.builder()
                        .clienteId(1).build();
        when(clienteService.buscarCliente(1)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/clientes/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.clienteId").value(1));
    }

    @Test
    void deberiaCrearCliente() throws Exception{
        ClienteRequestDTO request = new ClienteRequestDTO();
        request.setPersonaId(1);


        ClienteResponseDTO response= ClienteResponseDTO.builder()
                .clienteId(1).fechaRegistro(LocalDate.now()).build();

        when(clienteService.crearCliente(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.clienteId").value(1));
    }

    @Test
    void deberiaEliminarClientePorId() throws Exception{
        ClienteResponseDTO dto= ClienteResponseDTO.builder()
                .clienteId(1).build();
        when(clienteService.eliminar(1)).thenReturn(dto);

        mockMvc.perform(delete("/api/v1/clientes/eliminar/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.clienteId").value(1));
    }
}
