package com.sistemabarberia.fadex_backend.modules.venta.repository;

import com.sistemabarberia.fadex_backend.modules.venta.entity.HistorialVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HistorialVentaRepository extends JpaRepository<HistorialVenta, Integer> {

    List<HistorialVenta> findByVenta_VentaId(Integer ventaId);

    List<HistorialVenta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    List<HistorialVenta> findByVenta_VentaIdAndFechaBetween(
            Integer ventaId,
            LocalDateTime inicio,
            LocalDateTime fin
    );
}