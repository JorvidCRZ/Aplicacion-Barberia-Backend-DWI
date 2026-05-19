package com.sistemabarberia.fadex_backend.modules.barbero.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BarberoRequestDTO {

    @NotNull(message = "El personaId es obligatorio")
    @Positive(message = "El personaId debe ser un número positivo")
    private Integer personaId;

    @NotNull(message = "La experiencia es obligatoria")
    @Min(value = 0, message = "La experiencia no puede ser negativa")
    @Max(value = 60, message = "La experiencia no puede superar 60 años")
    private Integer experiencia;

    private boolean ocupado;

    @NotNull(message = "El sueldo es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El sueldo debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El sueldo tiene formato inválido")
    private BigDecimal sueldo;

    @NotNull(message = "La comisión es obligatoria")
    @DecimalMin(value = "0.0", inclusive = true, message = "La comisión no puede ser negativa")
    @DecimalMax(value = "100.0", message = "La comisión no puede superar el 100%")
    @Digits(integer = 3, fraction = 2, message = "La comisión tiene formato inválido")
    private BigDecimal comision;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String descripcion;

    @Size(max = 255, message = "La URL de la foto no puede superar los 255 caracteres")
    private String fotoUrl;
}