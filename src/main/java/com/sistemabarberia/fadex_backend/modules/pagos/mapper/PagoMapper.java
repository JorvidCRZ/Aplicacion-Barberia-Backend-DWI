package com.sistemabarberia.fadex_backend.modules.pagos.mapper;

import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.pagos.dto.request.PagoRequestDTO;
import com.sistemabarberia.fadex_backend.modules.pagos.dto.response.PagoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.pagos.entity.Pago;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.venta.entity.Venta;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PagoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cliente", expression = "java(mapCliente(dto.getClienteId()))")
    @Mapping(target = "barbero", expression = "java(mapBarbero(dto.getBarberoId()))")
    @Mapping(target = "reserva", expression = "java(mapReserva(dto.getReservaId()))")
    @Mapping(target = "venta", expression = "java(mapVenta(dto.getVentaId()))")
    Pago toEntity(PagoRequestDTO dto);

    @Mapping(source = "cliente.clienteId", target = "clienteId")
    @Mapping(source = "cliente.persona.nombre", target = "clienteNombre")

    @Mapping(source = "barbero.barberoId", target = "barberoId")
    @Mapping(source = "barbero.persona.nombre", target = "barberoNombre")

    @Mapping(source = "reserva.id", target = "reservaId")
    @Mapping(source = "venta.ventaId", target = "ventaId")
    PagoResponseDTO toResponse(Pago pago);

    List<PagoResponseDTO> toResponseList(List<Pago> pagos);

    default Cliente mapCliente(Integer id) {
        if (id == null) {
            return null;
        }
        Cliente cliente = new Cliente();
        cliente.setClienteId(id);
        return cliente;
    }

    default Barbero mapBarbero(Integer id) {
        if (id == null) {
            return null;
        }
        Barbero barbero = new Barbero();
        barbero.setBarberoId(id);
        return barbero;
    }

    default Reserva mapReserva(Long id) {
        if (id == null) {
            return null;
        }
        Reserva reserva = new Reserva();
        reserva.setId(id);
        return reserva;
    }

    default Venta mapVenta(Integer id) {
        if (id == null) {
            return null;
        }
        Venta venta = new Venta();
        venta.setVentaId(id);
        return venta;
    }
}