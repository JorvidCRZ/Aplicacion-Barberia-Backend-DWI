package com.sistemabarberia.fadex_backend.modules.venta.mapper;

import com.sistemabarberia.fadex_backend.modules.venta.dto.response.HistorialVentaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.venta.entity.HistorialVenta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HistorialVentaMapper {

    @Mapping(source = "venta.ventaId", target = "ventaId")
    HistorialVentaResponseDTO toResponse(HistorialVenta historial);

    List<HistorialVentaResponseDTO> toResponseList(List<HistorialVenta> historialList);
}