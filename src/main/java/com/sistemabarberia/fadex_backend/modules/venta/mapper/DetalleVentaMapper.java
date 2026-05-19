package com.sistemabarberia.fadex_backend.modules.venta.mapper;

import com.sistemabarberia.fadex_backend.modules.producto.entity.Producto;
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
    @Mapping(target = "producto", expression = "java(mapProducto(dto.getProductoId()))")
    DetalleVenta toEntity(DetalleVentaRequestDTO dto);

    @Mapping(source = "venta.ventaId", target = "ventaId")
    @Mapping(source = "producto.id", target = "productoId")
    @Mapping(source = "producto.nombre", target = "productoNombre")
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

    default BigDecimal calcularSubtotal(DetalleVenta detalle) {

        if (detalle.getPrecioUnitario() == null || detalle.getCantidad() == null) {
            return BigDecimal.ZERO;
        }

        return detalle.getPrecioUnitario()
                .multiply(BigDecimal.valueOf(detalle.getCantidad()));
    }
}