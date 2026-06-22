package com.sistemabarberia.fadex_backend.modules.planilla.service;

import com.sistemabarberia.fadex_backend.modules.planilla.dto.DetalleBarberoResumenDTO;
import com.sistemabarberia.fadex_backend.modules.planilla.dto.PlanillaBarberoDTO;
import com.sistemabarberia.fadex_backend.modules.planilla.dto.PlanillaResumenDTO;
import com.sistemabarberia.fadex_backend.modules.planilla.dto.VentaBarberoDTO;
import com.sistemabarberia.fadex_backend.modules.planilla.repository.PlanillaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PlanillaServiceImpl implements IPlanillaService {

    private final PlanillaRepository planillaRepository;

    // ─── Resumen general ──────────────────────────────────────────────────────

    @Override
    public PlanillaResumenDTO obtenerResumen(Integer mes, Integer anio) {

        List<PlanillaBarberoDTO> detalle = construirDetalle(mes, anio);

        BigDecimal totalPlanilla = detalle.stream()
                .map(PlanillaBarberoDTO::getSueldoBase)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalComisiones = detalle.stream()
                .map(PlanillaBarberoDTO::getMontoComision)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal sueldoFinalTotal = detalle.stream()
                .map(PlanillaBarberoDTO::getSueldoFinal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Long ventasPeriodo = detalle.stream()
                .mapToLong(PlanillaBarberoDTO::getCantidadVentas)
                .sum();

        Long barberosActivos = (long) detalle.size();

        return PlanillaResumenDTO.builder()
                .totalPlanilla(totalPlanilla)
                .totalComisiones(totalComisiones)
                .sueldoFinalTotal(sueldoFinalTotal)
                .ventasPeriodo(ventasPeriodo)
                .barberosActivos(barberosActivos)
                .build();
    }

    // ─── Detalle paginado ─────────────────────────────────────────────────────

    @Override
    public Page<PlanillaBarberoDTO> obtenerDetalle(
            Integer mes,
            Integer anio,
            Pageable pageable
    ) {
        List<PlanillaBarberoDTO> detalle = construirDetalle(mes, anio);

        int start = (int) pageable.getOffset();
        int end   = Math.min(start + pageable.getPageSize(), detalle.size());

        List<PlanillaBarberoDTO> contenido = start >= detalle.size()
                ? List.of()
                : detalle.subList(start, end);

        return new PageImpl<>(contenido, pageable, detalle.size());
    }

    // ─── Años disponibles ─────────────────────────────────────────────────────

    @Override
    public List<Integer> obtenerAniosDisponibles() {
        return planillaRepository.obtenerAniosDisponibles();
    }

    // ─── Resumen individual por barbero ───────────────────────────────────────

    @Override
    public DetalleBarberoResumenDTO obtenerResumenBarbero(
            Integer barberoId,
            Integer mes,
            Integer anio
    ) {
        LocalDateTime[] rango = obtenerRango(mes, anio);

        Object[] r = planillaRepository.obtenerResumenBarbero(
                barberoId,
                rango[0],
                rango[1]
        );

        BigDecimal sueldo     = r[3] != null ? (BigDecimal) r[3] : BigDecimal.ZERO;
        BigDecimal porcentaje = r[4] != null ? (BigDecimal) r[4] : BigDecimal.ZERO;
        Long       cantidad   = r[5] != null ? ((Number) r[5]).longValue() : 0L;
        BigDecimal totalVentas = r[6] != null ? (BigDecimal) r[6] : BigDecimal.ZERO;

        BigDecimal montoComision = totalVentas
                .multiply(porcentaje)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        BigDecimal sueldoFinal = sueldo.add(montoComision);

        String nombre   = r[1] != null ? r[1].toString() : "";
        String apellido = r[2] != null ? r[2].toString() : "";

        return DetalleBarberoResumenDTO.builder()
                .barberoId((Integer) r[0])
                .nombreBarbero((nombre + " " + apellido).trim())
                .sueldoBase(sueldo)
                .porcentajeComision(porcentaje)
                .cantidadVentas(cantidad)
                .totalVentas(totalVentas)
                .montoComision(montoComision)
                .sueldoFinal(sueldoFinal)
                .build();
    }

    // ─── Ventas individuales por barbero ──────────────────────────────────────

    @Override
    public Page<VentaBarberoDTO> obtenerVentasBarbero(
            Integer barberoId,
            Integer mes,
            Integer anio,
            Pageable pageable
    ) {
        LocalDateTime[] rango = obtenerRango(mes, anio);

        List<Object[]> resultados = planillaRepository.obtenerVentasBarbero(
                barberoId, rango[0], rango[1]
        );

        List<VentaBarberoDTO> lista = resultados.stream()
                .map(r -> VentaBarberoDTO.builder()
                        .ventaId((Integer) r[0])
                        .fecha((LocalDateTime) r[1])
                        .nombreCliente(construirNombre(r[2], r[3]))
                        .total(r[4] != null ? (BigDecimal) r[4] : BigDecimal.ZERO)
                        .build()
                )
                .toList();

        int start = (int) pageable.getOffset();
        int end   = Math.min(start + pageable.getPageSize(), lista.size());

        return new PageImpl<>(
                start >= lista.size() ? List.of() : lista.subList(start, end),
                pageable,
                lista.size()
        );
    }

    // ─── Métodos privados ─────────────────────────────────────────────────────

    private List<PlanillaBarberoDTO> construirDetalle(Integer mes, Integer anio) {

        LocalDateTime[] rango = obtenerRango(mes, anio);

        List<Object[]> resultados = planillaRepository.obtenerVentasPorBarbero(
                rango[0],
                rango[1]
        );

        return resultados.stream()
                .map(r -> {
                    BigDecimal sueldo      = r[3] != null ? (BigDecimal) r[3] : BigDecimal.ZERO;
                    BigDecimal porcentaje  = r[4] != null ? (BigDecimal) r[4] : BigDecimal.ZERO;
                    Long       cantidad    = r[5] != null ? ((Number) r[5]).longValue() : 0L;
                    BigDecimal totalVentas = r[6] != null ? (BigDecimal) r[6] : BigDecimal.ZERO;

                    BigDecimal montoComision = totalVentas
                            .multiply(porcentaje)
                            .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

                    return PlanillaBarberoDTO.builder()
                            .barberoId((Integer) r[0])
                            .nombreBarbero(construirNombre(r[1], r[2]))
                            .sueldoBase(sueldo)
                            .cantidadVentas(cantidad)
                            .totalVentas(totalVentas)
                            .porcentajeComision(porcentaje)
                            .montoComision(montoComision)
                            .sueldoFinal(sueldo.add(montoComision))
                            .build();
                })
                .toList();
    }

    private LocalDateTime[] obtenerRango(Integer mes, Integer anio) {
        LocalDate fechaBase = LocalDate.of(anio, mes, 1);
        return new LocalDateTime[]{
                fechaBase.atStartOfDay(),
                fechaBase.withDayOfMonth(fechaBase.lengthOfMonth()).atTime(23, 59, 59)
        };
    }

    private String construirNombre(Object nombre, Object apellido) {
        String n = nombre   != null ? nombre.toString()   : "";
        String a = apellido != null ? apellido.toString() : "";
        return (n + " " + a).trim();
    }
}