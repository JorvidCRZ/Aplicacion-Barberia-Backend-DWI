package com.sistemabarberia.fadex_backend.modules.analisis.Service;


import com.sistemabarberia.fadex_backend.modules.analisis.dto.ResumenDiaDTO;
import com.sistemabarberia.fadex_backend.modules.pagos.entity.MetodoPago;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.EstadoReserva;

import java.time.LocalDate;
import java.util.List;

public interface ReporteService {
    List<ResumenDiaDTO> getResumenSemanal(LocalDate desde, LocalDate hasta);
    byte[] generarReservasPdf(LocalDate desde, LocalDate hasta, Long barberoId, Long servicioId, EstadoReserva estado, MetodoPago metodoPago);
    byte[] generarReservasExcel(LocalDate desde, LocalDate hasta, Long barberoId, Long servicioId, EstadoReserva estado, MetodoPago metodoPago);
    byte[] generarVentasPdf(LocalDate desde, LocalDate hasta);
    byte[] generarVentasExcel(LocalDate desde, LocalDate hasta);
    byte[] generarClientesPdf(LocalDate desde, LocalDate hasta);
    byte[] generarClientesExcel(LocalDate desde, LocalDate hasta);
    byte[] generarBarberosPdf(LocalDate desde, LocalDate hasta);
    byte[] generarBarberosExcel(LocalDate desde, LocalDate hasta);
}
