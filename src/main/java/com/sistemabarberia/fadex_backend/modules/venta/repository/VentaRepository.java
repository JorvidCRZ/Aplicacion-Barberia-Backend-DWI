package com.sistemabarberia.fadex_backend.modules.venta.repository;

import com.sistemabarberia.fadex_backend.modules.venta.entity.TipoComprobante;
import com.sistemabarberia.fadex_backend.modules.venta.entity.Venta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {

    // --- CONSULTA A PRUEBA DE FALLOS DE POSTGRESQL ---
    @EntityGraph(attributePaths = {"detalles", "detalles.producto", "detalles.servicio"})
    @Query("""
        SELECT v FROM Venta v
        WHERE (:hasCliente = false OR LOWER(v.cliente.persona.nombre) LIKE LOWER(CONCAT('%', :cliente, '%')) 
               OR LOWER(v.cliente.persona.apellido) LIKE LOWER(CONCAT('%', :cliente, '%')))
        AND (:hasCorrelativo = false OR v.numeroCorrelativo LIKE CONCAT('%', :numeroCorrelativo, '%'))
        AND (:hasComprobante = false OR v.tipoComprobante = :tipoComprobante)
        AND (:hasFechaInicio = false OR v.fecha >= :fechaInicio)
        AND (:hasFechaFin = false OR v.fecha <= :fechaFin)
        ORDER BY v.fecha DESC
    """)
    List<Venta> buscarConFiltrosAvanzados(
            @Param("hasCliente") boolean hasCliente, @Param("cliente") String cliente,
            @Param("hasCorrelativo") boolean hasCorrelativo, @Param("numeroCorrelativo") String numeroCorrelativo,
            @Param("hasComprobante") boolean hasComprobante, @Param("tipoComprobante") TipoComprobante tipoComprobante,
            @Param("hasFechaInicio") boolean hasFechaInicio, @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("hasFechaFin") boolean hasFechaFin, @Param("fechaFin") LocalDateTime fechaFin
    );

    @EntityGraph(attributePaths = {"detalles", "detalles.producto", "detalles.servicio"})
    List<Venta> findAllByOrderByFechaDesc();

    @EntityGraph(attributePaths = {"detalles", "detalles.producto", "detalles.servicio"})
    List<Venta> findByCliente_Persona_NombreContainingIgnoreCaseOrderByFechaDesc(String nombre);

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
    LEFT JOIN FETCH d.servicio
    WHERE v.ventaId = :id
    """)
    Venta findByIdWithDetalles(@Param("id") Integer id);

    @Query("SELECT v.numeroCorrelativo FROM Venta v WHERE v.numeroCorrelativo LIKE :prefijo% ORDER BY v.numeroCorrelativo DESC LIMIT 1")
    String findUltimoCorrelativoPorMesAnio(@Param("prefijo") String prefijo);
}