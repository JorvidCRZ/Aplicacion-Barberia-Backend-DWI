package com.sistemabarberia.fadex_backend.modules.reserva.mapper;

import com.sistemabarberia.fadex_backend.modules.reserva.dto.Request.ReservaRequest;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.ReservaDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReservaMapper {

    Reserva toEntity(ReservaRequest request);
    @Mapping(target = "reservaId", source = "id")
    @Mapping(target = "clienteNombre", source = "reserva.cliente.persona.nombre")
    @Mapping(target = "barberoNombre", source = "reserva.barbero.persona.nombre")
    @Mapping(target = "servicio", source = "reserva.servicio.nombre")
    @Mapping(target = "tipoReserva", source = "reserva.tipoReserva")
    ReservaDTO toDto(Reserva reserva);

    List<ReservaDTO> toDtoLista(List<Reserva> reservas);
}
