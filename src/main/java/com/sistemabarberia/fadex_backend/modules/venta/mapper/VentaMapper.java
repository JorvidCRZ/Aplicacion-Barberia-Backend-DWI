package com.sistemabarberia.fadex_backend.modules.venta.mapper;

import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.venta.dto.request.VentaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.venta.dto.response.VentaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.venta.dto.response.DetalleVentaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.venta.entity.Venta;
import com.sistemabarberia.fadex_backend.modules.venta.entity.DetalleVenta;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VentaMapper {

    @Mapping(target = "ventaId", ignore = true)
    @Mapping(target = "cliente", expression = "java(mapCliente(dto.getClienteId()))")
    @Mapping(target = "barbero", expression = "java(mapBarbero(dto.getBarberoId()))")
    Venta toEntity(VentaRequestDTO dto);

    @Mapping(source = "cliente.clienteId", target = "clienteId")
    @Mapping(source = "cliente.persona.nombre", target = "clienteNombre")
    @Mapping(source = "barbero.barberoId", target = "barberoId")
    @Mapping(source = "barbero.persona.nombre", target = "barberoNombre")
    @Mapping(target = "detalles", expression = "java(mapDetalles(venta.getDetalles()))")
    VentaResponseDTO toResponse(Venta venta);

    List<VentaResponseDTO> toResponseList(List<Venta> ventas);

    default Cliente mapCliente(Integer id) {
        if (id == null) return null;
        Cliente cliente = new Cliente();
        cliente.setClienteId(id);
        return cliente;
    }

    default Barbero mapBarbero(Integer id) {
        if (id == null) return null;
        Barbero barbero = new Barbero();
        barbero.setBarberoId(id);
        return barbero;
    }

    default List<DetalleVentaResponseDTO> mapDetalles(List<DetalleVenta> detalles) {

        if (detalles == null) return List.of();

        return detalles.stream()
                .map(this::mapDetalle)
                .toList();
    }

    default DetalleVentaResponseDTO mapDetalle(DetalleVenta detalle) {

        if (detalle == null) return null;

        DetalleVentaResponseDTO dto = new DetalleVentaResponseDTO();

        dto.setDetalleVentaId(detalle.getDetalleVentaId());
        dto.setVentaId(detalle.getVenta().getVentaId());

        if (detalle.getProducto() != null) {
            dto.setProductoId(detalle.getProducto().getId().intValue());
            dto.setProductoNombre(detalle.getProducto().getNombre());
        }

        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());

        if (detalle.getPrecioUnitario() != null && detalle.getCantidad() != null) {
            dto.setSubtotal(
                    detalle.getPrecioUnitario()
                            .multiply(java.math.BigDecimal.valueOf(detalle.getCantidad()))
            );
        }

        return dto;
    }
}