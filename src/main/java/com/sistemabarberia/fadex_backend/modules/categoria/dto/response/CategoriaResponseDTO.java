package com.sistemabarberia.fadex_backend.modules.categoria.dto.response;

import com.sistemabarberia.fadex_backend.modules.categoria.entity.CategoriaEnum;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private boolean estado;
    private Long padreId;
    private String padreNombre;
    private CategoriaEnum tipo;
    private List<CategoriaResponseDTO> subcategorias;
}