package com.sistemabarberia.fadex_backend.modules.venta.repository;

import com.sistemabarberia.fadex_backend.modules.venta.entity.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    List<Venta> findByCliente_Persona_NombreContainingIgnoreCase(String nombre);

    List<Venta> findByCliente_ClienteId(Integer clienteId);

    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

    List<Venta> findByCliente_ClienteIdAndFechaBetween(
            Integer clienteId,
            LocalDateTime inicio,
            LocalDateTime fin
    );



    @Query(value = """
    SELECT COUNT(*)
    FROM venta
    WHERE id_cliente = :clienteId
    """, nativeQuery = true)
    Long contarComprasCliente(
            @Param("clienteId") Integer clienteId
    );

    @Query(value = """
    SELECT COALESCE(SUM(monto), 0)
    FROM pago
    WHERE id_cliente = :clienteId
    """, nativeQuery = true)
    Double totalGastadoCliente(
            @Param("clienteId") Integer clienteId
    );

    @Query("""
    SELECT v
    FROM Venta v
    LEFT JOIN FETCH v.detalles d
    LEFT JOIN FETCH d.producto
    WHERE v.ventaId = :id
""")
    Venta findByIdWithDetalles(@Param("id") Integer id);
}