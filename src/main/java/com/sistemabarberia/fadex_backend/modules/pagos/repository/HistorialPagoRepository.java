package com.sistemabarberia.fadex_backend.modules.pagos.repository;

import com.sistemabarberia.fadex_backend.modules.pagos.entity.HistorialPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistorialPagoRepository extends JpaRepository<HistorialPago, Long> {

    List<HistorialPago> findByCliente_ClienteId(Integer clienteId);

    List<HistorialPago> findByPagoId(Long pagoId);
}