package com.sistemabarberia.fadex_backend.modules.cliente.dto.response;

import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ClienteResponseDTO {
    private Integer clienteId;
    private Persona persona;
    private LocalDate fechaRegistro;
}
