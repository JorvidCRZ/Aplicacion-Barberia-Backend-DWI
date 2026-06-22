package com.sistemabarberia.fadex_backend.modules.planilla.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class PlanillaRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Object[]> obtenerVentasPorBarbero(
            LocalDateTime inicio,
            LocalDateTime fin
    ) {

        String jpql = """
    SELECT
        b.barberoId,
        p.nombre,
        p.apellido,
        b.sueldo,
        b.comision,
        COUNT(DISTINCT v.ventaId),
        COALESCE(SUM(dv.cantidad * dv.precioUnitario), 0)
    FROM Barbero b
    JOIN b.persona p
    LEFT JOIN Venta v
        ON v.barbero = b
        AND v.fecha BETWEEN :inicio AND :fin
    LEFT JOIN v.detalles dv
    WHERE b.activo = true
    GROUP BY
        b.barberoId,
        p.nombre,
        p.apellido,
        b.sueldo,
        b.comision
    ORDER BY
        (COALESCE(SUM(dv.cantidad * dv.precioUnitario), 0) * b.comision) DESC
    """;

        return entityManager.createQuery(jpql, Object[].class)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getResultList();
    }

    public Long contarBarberosActivos() {

        String jpql = """
            SELECT COUNT(b)
            FROM Barbero b
            WHERE b.activo = true
            """;

        return entityManager.createQuery(jpql, Long.class)
                .getSingleResult();
    }

    public BigDecimal obtenerTotalSueldosActivos() {

        String jpql = """
            SELECT COALESCE(SUM(b.sueldo), 0)
            FROM Barbero b
            WHERE b.activo = true
            """;

        return entityManager.createQuery(jpql, BigDecimal.class)
                .getSingleResult();
    }


    public List<Integer> obtenerAniosDisponibles() {

        String jpql = """
        SELECT DISTINCT YEAR(v.fecha)
        FROM Venta v
        ORDER BY YEAR(v.fecha) DESC
        """;

        return entityManager
                .createQuery(jpql, Integer.class)
                .getResultList();
    }





    public Object[] obtenerResumenBarbero(
            Integer barberoId,
            LocalDateTime inicio,
            LocalDateTime fin
    ) {

        String jpql = """
        SELECT
            b.barberoId,
            p.nombre,
            p.apellido,
            b.sueldo,
            b.comision,
            COUNT(DISTINCT v.ventaId),
            COALESCE(SUM(dv.cantidad * dv.precioUnitario),0)
        FROM Barbero b
        JOIN b.persona p
        LEFT JOIN Venta v
            ON v.barbero = b
            AND v.fecha BETWEEN :inicio AND :fin
        LEFT JOIN v.detalles dv
        WHERE b.barberoId = :barberoId
        GROUP BY
            b.barberoId,
            p.nombre,
            p.apellido,
            b.sueldo,
            b.comision
        """;

        return entityManager
                .createQuery(jpql, Object[].class)
                .setParameter("barberoId", barberoId)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getSingleResult();
    }


    public List<Object[]> obtenerVentasBarbero(
            Integer barberoId,
            LocalDateTime inicio,
            LocalDateTime fin
    ) {
        String jpql = """
        SELECT
            v.ventaId,
            v.fecha,
            p.nombre,
            p.apellido,
            COALESCE(SUM(dv.cantidad * dv.precioUnitario), 0)
        FROM Venta v
        LEFT JOIN v.cliente c
        LEFT JOIN c.persona p
        LEFT JOIN v.detalles dv
        WHERE
            v.barbero.barberoId = :barberoId
            AND v.fecha BETWEEN :inicio AND :fin
        GROUP BY
            v.ventaId,
            v.fecha,
            p.nombre,
            p.apellido
        ORDER BY v.fecha DESC
        """;

        return entityManager
                .createQuery(jpql, Object[].class)
                .setParameter("barberoId", barberoId)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin)
                .getResultList();
    }


}