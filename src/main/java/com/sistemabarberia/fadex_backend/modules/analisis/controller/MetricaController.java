package com.sistemabarberia.fadex_backend.modules.analisis.controller;


import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.modules.analisis.Service.MetricaService;
import com.sistemabarberia.fadex_backend.modules.analisis.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/metricas")
@RequiredArgsConstructor
public class MetricaController {

    private final MetricaService metricaService;

    @GetMapping("/resumen")
    public ResponseEntity<ApiResponse<ResumenMetricasDTO>> resumen(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(ApiResponse.success(metricaService.getResumen(desde, hasta)));
    }

    @GetMapping("/ingresos-diarios")
    public ResponseEntity<ApiResponse<List<IngresoDiarioDTO>>> ingresosDiarios(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(ApiResponse.success(metricaService.getIngresosDiarios(desde, hasta)));
    }

    @GetMapping("/reservas-por-dia")
    public ResponseEntity<ApiResponse<List<ReservasDiaDTO>>> reservasPorDia(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(ApiResponse.success(metricaService.getReservasPorDia(desde, hasta)));
    }

    @GetMapping("/rendimiento-barberos")
    public ResponseEntity<ApiResponse<List<RendimientoBarberoDTO>>> rendimientoBarberos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(ApiResponse.success(metricaService.getRendimientoBarberos(desde, hasta)));
    }

    @GetMapping("/servicios-solicitados")
    public ResponseEntity<ApiResponse<List<ServicioSolicitadoDTO>>> serviciosSolicitados(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return ResponseEntity.ok(ApiResponse.success(metricaService.getServiciosMasSolicitados(desde, hasta)));
    }
}
