package com.sistemabarberia.fadex_backend.modules.barbero.repository;

import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface BarberoRepository extends JpaRepository<Barbero, Integer> {

    Page<Barbero> findByActivoTrue(Pageable pageable);
    Page<Barbero> findByActivoFalse(Pageable pageable);
    boolean existsByPersona_PersonaId(Integer personaId);
    Optional<Barbero> findByPersona_Usuario_IdUsuario(Integer usuarioId);
    Optional<Barbero> findByPersonaUsuario(Usuario usuario);

    // Filtrar por estado ocupado
    Page<Barbero> findByActivoTrueAndOcupado(
            boolean ocupado,
            Pageable pageable
    );

    // Contar disponibles u ocupados
    long countByOcupado(boolean ocupado);

    // Total de ventas (monto) del día de hoy
    @Query(value = """
        SELECT COALESCE(SUM(p.monto), 0)
        FROM pago p
        WHERE DATE(p.fecha) = CURRENT_DATE
    """, nativeQuery = true)
    BigDecimal getTotalVentasHoy();

    // Total de ventas (monto) de ayer
    @Query(value = """
        SELECT COALESCE(SUM(p.monto), 0)
        FROM pago p
        WHERE DATE(p.fecha) = CURRENT_DATE - INTERVAL '1 day'
    """, nativeQuery = true)
    BigDecimal getTotalVentasAyer();

    // Barbero con más ingresos en el mes actual → devuelve [nombre, total]
    @Query(value = """
        SELECT CONCAT(pe.nombre, ' ', LEFT(pe.apellido, 1), '.') AS nombre,
               COALESCE(SUM(p.monto), 0)                         AS total
        FROM barbero b
        JOIN persona pe ON pe.id_persona = b.id_persona
        LEFT JOIN pago p ON p.id_barbero = b.id_barbero
            AND EXTRACT(MONTH FROM p.fecha) = EXTRACT(MONTH FROM CURRENT_DATE)
            AND EXTRACT(YEAR  FROM p.fecha) = EXTRACT(YEAR  FROM CURRENT_DATE)
        GROUP BY b.id_barbero, pe.nombre, pe.apellido
        ORDER BY total DESC
        LIMIT 1
    """, nativeQuery = true)
    Optional<Object[]> findMejorBarberoDelMes();


    // Buscar por nombre o apellido (parcial, case-insensitive)
    @Query(value = """
    SELECT b.* FROM barbero b
    JOIN persona pe ON pe.id_persona = b.id_persona
    WHERE b.activo = true
      AND (
           LOWER(pe.nombre)   LIKE LOWER(CONCAT('%', :termino, '%'))
        OR LOWER(pe.apellido) LIKE LOWER(CONCAT('%', :termino, '%'))
      )
""", nativeQuery = true)
    Page<Barbero> buscarPorNombreOApellido(
            @Param("termino") String termino,
            Pageable pageable
    );

    // Cortes atendidos este mes por el barbero
    @Query(value = """
    SELECT COUNT(hc.id_historial)
    FROM historial_cortes hc
    JOIN detalle_reservas dr ON dr.id_detalle_reserva = hc.id_detalle_reserva
    JOIN reservas r ON r.id_reservas = dr.id_reservas
    WHERE r.id_barbero = :idBarbero
      AND EXTRACT(MONTH FROM hc.fecha) = EXTRACT(MONTH FROM CURRENT_DATE)
      AND EXTRACT(YEAR  FROM hc.fecha) = EXTRACT(YEAR  FROM CURRENT_DATE)
""", nativeQuery = true)
    long countCortesEsteMesByBarbero(@Param("idBarbero") Integer idBarbero);

    // Ingresos generados este mes por el barbero
    @Query(value = """
    SELECT COALESCE(SUM(p.monto), 0)
    FROM pago p
    WHERE p.id_barbero = :idBarbero
      AND EXTRACT(MONTH FROM p.fecha) = EXTRACT(MONTH FROM CURRENT_DATE)
      AND EXTRACT(YEAR  FROM p.fecha) = EXTRACT(YEAR  FROM CURRENT_DATE)
""", nativeQuery = true)
    BigDecimal getIngresosEsteMesByBarbero(@Param("idBarbero") Integer idBarbero);

    // Reservas de hoy del barbero
    @Query(value = """
    SELECT COUNT(r.id_reservas)
    FROM reservas r
    WHERE r.id_barbero = :idBarbero
      AND DATE(r.fecha) = CURRENT_DATE
""", nativeQuery = true)
    long countReservasHoyByBarbero(@Param("idBarbero") Integer idBarbero);
}