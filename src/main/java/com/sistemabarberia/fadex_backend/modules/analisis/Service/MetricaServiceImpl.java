package com.sistemabarberia.fadex_backend.modules.analisis.Service;


import com.sistemabarberia.fadex_backend.modules.analisis.dto.response.*;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.EstadoReserva;
import com.sistemabarberia.fadex_backend.modules.reserva.repository.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MetricaServiceImpl implements MetricaService {
    private final ReservaRepository reservaRepository;

    @Override
    public ResumenMetricasDTO getResumen(LocalDate desde, LocalDate hasta) {
        BigDecimal ingresos = reservaRepository.calcularIngresosPorPeriodo(desde, hasta);
        if (ingresos == null) ingresos = BigDecimal.ZERO;

        Long reservasTotales = reservaRepository.countByFechaBetween(desde, hasta);
        Long completadas = reservaRepository.countByEstadoReservaAndFechaBetween(EstadoReserva.FINALIZADA, desde, hasta);
        Long clientesActivos = reservaRepository.clientesActivos(desde, hasta);
        Long clientesNuevos = reservaRepository.clientesNuevos(desde, hasta);
        BigDecimal ticket = completadas != null && completadas > 0
                ? ingresos.divide(BigDecimal.valueOf(completadas), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return ResumenMetricasDTO.builder()
                .ingresosTotales(ingresos)
                .reservasTotales(reservasTotales != null ? reservasTotales : 0L)
                .completadas(completadas != null ? completadas : 0L)
                .clientesActivos(clientesActivos != null ? clientesActivos : 0L)
                .clientesNuevos(clientesNuevos != null ? clientesNuevos : 0L)
                .ticketPromedio(ticket)
                .build();
    }

    @Override
    public List<IngresoDiarioDTO> getIngresosDiarios(LocalDate desde, LocalDate hasta) {
        return reservaRepository.ingresosDiarios(desde, hasta, EstadoReserva.FINALIZADA);
    }

    @Override
    public List<ReservasDiaDTO> getReservasPorDia(LocalDate desde, LocalDate hasta) {
        return reservaRepository.reservasPorDia(desde, hasta).stream()
                .map(row -> new ReservasDiaDTO(
                        (String) row[0],
                        ((Number) row[1]).longValue(),
                        ((Number) row[2]).longValue()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public List<RendimientoBarberoDTO> getRendimientoBarberos(LocalDate desde, LocalDate hasta) {
        return reservaRepository.rendimientoBarberos(desde, hasta);
    }

    @Override
    public List<ServicioSolicitadoDTO> getServiciosMasSolicitados(LocalDate desde, LocalDate hasta) {
        return reservaRepository.serviciosMasSolicitados(desde, hasta);
    }
}
