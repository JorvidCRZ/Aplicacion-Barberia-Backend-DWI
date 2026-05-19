package com.sistemabarberia.fadex_backend.modules.cliente.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class ClienteRequestDTO {

    @NotNull(message = "El personaId es obligatorio")
    @Positive(message = "El personaId debe ser un número positivo")
    private Integer personaId;
}