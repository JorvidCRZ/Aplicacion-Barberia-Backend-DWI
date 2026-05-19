package com.sistemabarberia.fadex_backend.modules.categoria.dto;

import com.sistemabarberia.fadex_backend.modules.categoria.entity.CategoriaEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoriaFiltro {

    private String nombre;
    private Boolean estado;
    private Long padreId;
    private CategoriaEnum tipo;
}