package com.sistemabarberia.fadex_backend.modules.categoria.dto.request;

import com.sistemabarberia.fadex_backend.modules.categoria.entity.CategoriaEnum;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaRequestDTO {
    @NotBlank(message = "Nombre obligatorio")
    @Size(max = 100, message = "Máx 100 caracteres")
    private String nombre;
    private String descripcion;
    @NotNull(message = "Estado obligatorio")
    private Boolean estado;
    private Long padreId;
    @NotNull(message = "El tipo es obligatorio")
    private CategoriaEnum tipo;
}