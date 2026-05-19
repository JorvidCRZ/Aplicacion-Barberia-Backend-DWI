package com.sistemabarberia.fadex_backend.modules.venta.repository;

import com.sistemabarberia.fadex_backend.modules.venta.entity.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Integer> {

    List<DetalleVenta> findByVenta_VentaId(Integer ventaId);

    List<DetalleVenta> findByProductoId(Integer productoId);
}