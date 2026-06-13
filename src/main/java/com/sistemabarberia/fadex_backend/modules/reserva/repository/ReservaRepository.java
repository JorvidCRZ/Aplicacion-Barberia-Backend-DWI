package com.sistemabarberia.fadex_backend.modules.reserva.repository;

import com.sistemabarberia.fadex_backend.modules.analisis.dto.response.IngresoDiarioDTO;
import com.sistemabarberia.fadex_backend.modules.analisis.dto.response.RendimientoBarberoDTO;
import com.sistemabarberia.fadex_backend.modules.analisis.dto.response.ReservasDiaDTO;
import com.sistemabarberia.fadex_backend.modules.analisis.dto.response.ServicioSolicitadoDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.EstadoReserva;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    @Query("""
    SELECT COUNT(r) > 0 FROM Reserva r
    WHERE r.barbero.barberoId = :barberoId
    AND r.fecha = :fecha
    AND (r.horaInicio < :horaFin AND r.horaFin > :horaInicio)
""")
    boolean existeConflicto(
            @Param("barberoId") Integer barberoId,
            @Param("fecha") LocalDate fecha,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFin") LocalTime horaFin
    );


    Page<Reserva> findByCliente_ClienteId (Integer clienteId, Pageable  pageable);
    List<Reserva> findByBarbero_BarberoId (Integer barberoId);


    @Query("""
        SELECT r FROM Reserva r
        LEFT JOIN FETCH r.cliente c
        LEFT JOIN FETCH c.persona
        WHERE r.barbero.barberoId = :barberoId
          AND r.fecha BETWEEN :desde AND :hasta
        ORDER BY r.fecha ASC
    """)
    List<Reserva> findReservasDiarias(
            @Param("barberoId") Integer barberoId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );

    /*********************/
    @Query("""
        SELECT r FROM Reserva r
        WHERE r.barbero.barberoId = :barberoId
          AND r.fecha BETWEEN :desde AND :hasta
    """)
    List<Reserva> findByBarberoIdAndFechaBetween(
            @Param("barberoId") Integer barberoId,
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta
    );


    //Resumen-Cliente
    @Query(value = """
       SELECT COUNT(DISTINCT id_cliente)
       FROM reservas
       WHERE EXTRACT(MONTH FROM fecha) = EXTRACT(MONTH FROM CURRENT_DATE)
       AND EXTRACT(YEAR FROM fecha) = EXTRACT(YEAR FROM CURRENT_DATE)
       """, nativeQuery = true)
    Long clientesActivosMes();

    @Query(value = """
       SELECT 
       (
           COUNT(*) * 100.0 /
           (SELECT COUNT(*) FROM cliente)
       )
       FROM (
           SELECT id_cliente
           FROM reservas
           GROUP BY id_cliente
           HAVING COUNT(*) > 1
       ) clientes_fieles
       """, nativeQuery = true)
    Double calcularRetencion();

    @Query(value = """
   SELECT COUNT(DISTINCT id_cliente)
   FROM reservas
   WHERE EXTRACT(MONTH FROM fecha) =
         EXTRACT(MONTH FROM CURRENT_DATE - INTERVAL '1 month')
   AND EXTRACT(YEAR FROM fecha) =
         EXTRACT(YEAR FROM CURRENT_DATE - INTERVAL '1 month')
   """, nativeQuery = true)
    Long clientesActivosMesAnterior();

    @Query(value = """
   SELECT 
   (
       COUNT(*) * 100.0 /
       (SELECT COUNT(*) FROM cliente)
   )
   FROM (
       SELECT id_cliente
       FROM reservas
       WHERE EXTRACT(MONTH FROM fecha) =
             EXTRACT(MONTH FROM CURRENT_DATE - INTERVAL '1 month')
       AND EXTRACT(YEAR FROM fecha) =
             EXTRACT(YEAR FROM CURRENT_DATE - INTERVAL '1 month')
       GROUP BY id_cliente
       HAVING COUNT(*) > 1
   ) clientes_fieles
   """, nativeQuery = true)
    Double calcularRetencionMesAnterior();


    /*Resumen Cliente*/

    @Query(value = """
    SELECT COUNT(*)
    FROM reservas
    WHERE id_cliente = :clienteId
    """, nativeQuery = true)
    Long contarReservasCliente(
            @Param("clienteId") Integer clienteId
    );

    @Query(value = """
    SELECT COUNT(dr.id_detalle_reserva)
    FROM detalle_reservas dr
    INNER JOIN reservas r
        ON dr.id_reservas = r.id_reservas
    WHERE r.id_cliente = :clienteId
    """, nativeQuery = true)
    Long contarCortesCliente(
            @Param("clienteId") Integer clienteId
    );

    @Query(value = """
    SELECT MAX(fecha)
    FROM reservas
    WHERE id_cliente = :clienteId
    """, nativeQuery = true)
    java.sql.Date ultimaVisitaCliente(
            @Param("clienteId") Integer clienteId
    );
    @Query("""
        SELECT r FROM Reserva r
        JOIN FETCH r.barbero b
        JOIN FETCH b.persona p
        JOIN FETCH p.usuario u
        WHERE u.user = :username
          AND r.fecha = :fecha
        ORDER BY r.horaInicio ASC
        """)
    List<Reserva> findByBarberoUsernameAndFecha(
            @Param("username") String username,
            @Param("fecha") LocalDate fecha
    );

    @Query("""
        SELECT r FROM Reserva r
        JOIN r.barbero b
        JOIN b.persona p
        JOIN p.usuario u
        WHERE r.id = :idReserva
          AND u.user = :username
        """)
    Optional<Reserva> findByIdAndBarberoUsername(
            @Param("idReserva") Long idReserva,
            @Param("username") String username
    );
    List<Reserva> findByFechaOrderByHoraInicioAsc(LocalDate fecha);

    @Query("SELECT r FROM Reserva r WHERE r.estadoReserva != 'CANCELADA' AND r.id NOT IN (SELECT p.reserva.id FROM Pago p WHERE p.reserva IS NOT NULL)")
    List<Reserva> findReservasSinPago();
    @Query(value = """
    SELECT
        TRIM(TO_CHAR(r.fecha, 'Day')) AS dia,
        COUNT(*) AS total,
        SUM(CASE WHEN r.estado_reserva = 'FINALIZADA' THEN 1 ELSE 0 END) AS finalizadas,
        SUM(CASE WHEN r.estado_reserva = 'CANCELADA' THEN 1 ELSE 0 END) AS canceladas,
        SUM(CASE WHEN r.estado_reserva = 'FINALIZADA' THEN r.total ELSE 0 END) AS ingresos
    FROM reservas r
    WHERE r.fecha BETWEEN :desde AND :hasta
    GROUP BY TRIM(TO_CHAR(r.fecha, 'Day')), EXTRACT(DOW FROM r.fecha)
    ORDER BY EXTRACT(DOW FROM r.fecha)
    """, nativeQuery = true)
    List<Object[]> resumenSemanal(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);
    @Query("SELECT r FROM Reserva r WHERE r.fecha BETWEEN :desde AND :hasta")
    List<Reserva> findReservasPorPeriodo(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query("SELECT SUM(r.total) FROM Reserva r WHERE r.fecha BETWEEN :desde AND :hasta AND r.estadoReserva = 'FINALIZADA'")
    BigDecimal calcularIngresosPorPeriodo(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);
    // Queries JPQL con constructor DTO
    @Query("SELECT new com.sistemabarberia.fadex_backend.modules.analisis.dto.response.IngresoDiarioDTO(r.fecha, SUM(r.total)) FROM Reserva r WHERE r.estadoReserva = :estado AND r.fecha BETWEEN :desde AND :hasta GROUP BY r.fecha ORDER BY r.fecha")
    List<IngresoDiarioDTO> ingresosDiarios(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta, @Param("estado") EstadoReserva estado);

    @Query(value = "SELECT TO_CHAR(r.fecha, 'Day') as dia, " +
            "SUM(CASE WHEN r.estado_reserva = 'FINALIZADA' THEN 1 ELSE 0 END) as completadas, " +
            "SUM(CASE WHEN r.estado_reserva = 'CANCELADA' THEN 1 ELSE 0 END) as canceladas " +
            "FROM reservas r WHERE r.fecha BETWEEN :desde AND :hasta " +
            "GROUP BY TO_CHAR(r.fecha, 'Day'), EXTRACT(DOW FROM r.fecha) " +
            "ORDER BY EXTRACT(DOW FROM r.fecha)", nativeQuery = true)
    List<Object[]> reservasPorDia(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);


    @Query("SELECT new com.sistemabarberia.fadex_backend.modules.analisis.dto.response.RendimientoBarberoDTO(r.barbero.persona.nombre, COUNT(r)) FROM Reserva r WHERE r.fecha BETWEEN :desde AND :hasta GROUP BY r.barbero.persona.nombre ORDER BY COUNT(r) DESC")
    List<RendimientoBarberoDTO> rendimientoBarberos(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query("SELECT new com.sistemabarberia.fadex_backend.modules.analisis.dto.response.ServicioSolicitadoDTO(r.servicio.nombre, COUNT(r)) FROM Reserva r WHERE r.estadoReserva != 'CANCELADA' AND r.fecha BETWEEN :desde AND :hasta GROUP BY r.servicio.nombre ORDER BY COUNT(r) DESC")
    List<ServicioSolicitadoDTO> serviciosMasSolicitados(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query("SELECT COUNT(DISTINCT r.cliente.clienteId) FROM Reserva r WHERE r.fecha BETWEEN :desde AND :hasta")
    Long clientesActivos(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query("SELECT COUNT(DISTINCT r.cliente.clienteId) FROM Reserva r WHERE r.fecha BETWEEN :desde AND :hasta AND (SELECT COUNT(r2) FROM Reserva r2 WHERE r2.cliente = r.cliente AND r2.fecha < :desde) = 0")
    Long clientesNuevos(@Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    // Derived queries (sin @Query)
    Long countByFechaBetween(LocalDate desde, LocalDate hasta);

    @Query("SELECT COUNT(r) FROM Reserva r WHERE r.estadoReserva = :estado AND r.fecha BETWEEN :desde AND :hasta")
    Long countByEstadoReservaAndFechaBetween(@Param("estado") EstadoReserva estado, @Param("desde") LocalDate desde, @Param("hasta") LocalDate hasta);

    @Query(value = """
SELECT r.* FROM reservas r
LEFT JOIN pago p ON p.id_reservas = r.id_reservas
WHERE r.fecha BETWEEN :desde AND :hasta
AND (:barberoId IS NULL OR r.id_barbero = :barberoId)
AND (:servicioId IS NULL OR r.id_servicio = :servicioId)
AND (:estado IS NULL OR r.estado_reserva = :estado)
AND (:metodoPago IS NULL OR p.metodo::TEXT = :metodoPago)
""", nativeQuery = true)
    List<Reserva> findReservasFiltradas(
            @Param("desde") LocalDate desde,
            @Param("hasta") LocalDate hasta,
            @Param("barberoId") Long barberoId,
            @Param("servicioId") Long servicioId,
            @Param("estado") String estado,
            @Param("metodoPago") String metodoPago
    );
}
