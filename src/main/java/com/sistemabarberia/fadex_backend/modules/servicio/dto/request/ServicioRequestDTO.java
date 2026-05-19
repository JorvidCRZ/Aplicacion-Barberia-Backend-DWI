package com.sistemabarberia.fadex_backend.modules.servicio.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServicioRequestDTO {

    @NotBlank(message = "Nombre obligatorio")
    @Size(max = 100, message = "Máx 100 caracteres")
    private String nombre;

    @NotNull(message = "Precio obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "Precio mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "Formato inválido")
    private BigDecimal precio;

    @NotNull(message = "Categoría obligatoria")
    private Long categoriaId;
    @NotNull(message = "la duracion del servicio es obligatorio")
    private Integer duracion;
}