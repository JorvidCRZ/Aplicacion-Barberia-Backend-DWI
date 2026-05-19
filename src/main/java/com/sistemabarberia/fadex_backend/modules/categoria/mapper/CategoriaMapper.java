package com.sistemabarberia.fadex_backend.modules.categoria.mapper;

import com.sistemabarberia.fadex_backend.modules.categoria.dto.request.CategoriaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.categoria.dto.response.CategoriaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoriaMapper {

    @Mapping(target = "id", ignore = true)
    Categoria toEntity(CategoriaRequestDTO dto);

    @Mapping(target = "padreId", source = "padre.id")
    @Mapping(target = "padreNombre", source = "padre.nombre")
    @Mapping(target = "tipo", source = "tipo")
    CategoriaResponseDTO toResponse(Categoria categoria);
    List<CategoriaResponseDTO> toResponseList(List<Categoria> categorias);
}