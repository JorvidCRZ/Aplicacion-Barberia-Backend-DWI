package com.sistemabarberia.fadex_backend.modules.servicio.mapper;

import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;

import com.sistemabarberia.fadex_backend.modules.servicio.dto.request.ServicioRequestDTO;

import com.sistemabarberia.fadex_backend.modules.servicio.dto.response.ServicioResponseDTO;

import com.sistemabarberia.fadex_backend.modules.servicio.entity.Servicio;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ServicioMapper {

    @Mapping(target = "servicioId", ignore = true)
    @Mapping(target = "categoria", expression = "java(mapCategoria(dto.getCategoriaId()))")
    @Mapping(target = "urlsMultimedia", ignore = true)
    Servicio toEntity(ServicioRequestDTO dto);

    @Mapping(source = "categoria.id", target = "categoriaId")
    @Mapping(source = "categoria.nombre", target = "categoriaNombre")
    ServicioResponseDTO toResponse(Servicio servicio);

    List<ServicioResponseDTO> toResponseList(List<Servicio> cortes);

    default Categoria mapCategoria(Long id) {
        if (id == null) return null;
        Categoria categoria = new Categoria();
        categoria.setId(id);
        return categoria;
    }

    @BeanMapping(nullValuePropertyMappingStrategy= NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "servicioId", ignore = true)
    @Mapping(target = "urlsMultimedia", ignore = true)
    void updateEntityFromDto(ServicioRequestDTO dto, @MappingTarget Servicio servicio);
}