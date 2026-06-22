package com.sistemabarberia.fadex_backend.modules.venta.mapper;

import com.sistemabarberia.fadex_backend.modules.producto.entity.Producto;
import com.sistemabarberia.fadex_backend.modules.servicio.entity.Servicio;
import com.sistemabarberia.fadex_backend.modules.venta.dto.request.DetalleVentaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.venta.dto.response.DetalleVentaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.venta.entity.DetalleVenta;
import com.sistemabarberia.fadex_backend.modules.venta.entity.Venta;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DetalleVentaMapper {

    @Mapping(target = "detalleVentaId", ignore = true)
    @Mapping(target = "venta", ignore = true)
    @Mapping(source = "productoId", target = "producto")
    @Mapping(source = "servicioId", target = "servicio")
    DetalleVenta toEntity(DetalleVentaRequestDTO dto);

    @Mapping(source = "venta.ventaId", target = "ventaId")
    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "producto.nombre", target = "productoNombre")
    @Mapping(source = "servicio.servicioId", target = "servicioId")
    @Mapping(source = "servicio.nombre", target = "servicioNombre")
    @Mapping(target = "subtotal", expression = "java(calcularSubtotal(detalle))")
    DetalleVentaResponseDTO toResponse(DetalleVenta detalle);

    List<DetalleVentaResponseDTO> toResponseList(List<DetalleVenta> detalles);
    default Venta mapVenta(Integer id) {
        if (id == null) return null;
        Venta venta = new Venta();
        venta.setVentaId(id);
        return venta;
    }

    default Producto mapProducto(Integer id) {
        if (id == null) return null;
        Producto producto = new Producto();
        producto.setId(Long.valueOf(id));
        return producto;
    }

    default Servicio mapServicio(Integer id) {
        if (id == null) return null;
        Servicio servicio = new Servicio();
        servicio.setServicioId(Long.valueOf(id));
        return servicio;
    }

    default BigDecimal calcularSubtotal(DetalleVenta detalle) {
        if (detalle.getPrecioUnitario() == null || detalle.getCantidad() == null) {
            return BigDecimal.ZERO;
        }
        return detalle.getPrecioUnitario()
                .multiply(BigDecimal.valueOf(detalle.getCantidad()));
    }
}