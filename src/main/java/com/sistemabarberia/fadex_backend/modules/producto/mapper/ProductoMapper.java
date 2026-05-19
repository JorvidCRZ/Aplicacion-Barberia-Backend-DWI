package com.sistemabarberia.fadex_backend.modules.producto.mapper;

import com.sistemabarberia.fadex_backend.modules.producto.dto.request.ProductoRequest;
import com.sistemabarberia.fadex_backend.modules.producto.dto.response.ProductoResponse;
import com.sistemabarberia.fadex_backend.modules.producto.entity.Producto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ProductoMapper {

    @Mapping(target = "idCategoria", source = "categoria.id")
    @Mapping(target = "nombreCategoria", source = "categoria.nombre")
    ProductoResponse toResponse(Producto producto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "urlsMultimedia", ignore = true)
    Producto toEntity(ProductoRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "categoria", ignore = true)
    @Mapping(target = "urlsMultimedia", ignore = true)
    void updateFromRequest(ProductoRequest request, @MappingTarget Producto producto);
}