package com.sistemabarberia.fadex_backend.modules.pagos.repository;

import com.sistemabarberia.fadex_backend.modules.pagos.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByCliente_ClienteId(Integer clienteId);

    List<Pago> findByBarbero_BarberoId(Integer barberoId);

    boolean existsByReserva_Id(Long reservaId);

    boolean existsByVenta_VentaId(Integer ventaId);

    List<Pago> findByCliente_Persona_NombreContainingIgnoreCase(String nombre);
}