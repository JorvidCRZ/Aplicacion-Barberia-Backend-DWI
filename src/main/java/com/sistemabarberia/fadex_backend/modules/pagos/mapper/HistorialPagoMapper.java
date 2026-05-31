package com.sistemabarberia.fadex_backend.modules.pagos.mapper;

import com.sistemabarberia.fadex_backend.modules.pagos.dto.response.HistorialPagoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.pagos.entity.HistorialPago;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HistorialPagoMapper {

    @Mapping(source = "id", target = "id")
    @Mapping(source = "pago.id", target = "pagoId")
    @Mapping(source = "cliente.clienteId", target = "clienteId")
    @Mapping(source = "cliente.persona.nombre", target = "clienteNombre")
    HistorialPagoResponseDTO toResponse(HistorialPago historialPago);

    List<HistorialPagoResponseDTO> toResponseList(List<HistorialPago> historialPagos);

}