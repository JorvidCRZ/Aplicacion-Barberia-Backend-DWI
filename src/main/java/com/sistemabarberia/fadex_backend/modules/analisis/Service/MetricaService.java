package com.sistemabarberia.fadex_backend.modules.analisis.Service;

import com.sistemabarberia.fadex_backend.modules.analisis.dto.response.*;

import java.time.LocalDate;
import java.util.List;

public interface MetricaService {
    ResumenMetricasDTO getResumen(LocalDate desde, LocalDate hasta);
    List<IngresoDiarioDTO> getIngresosDiarios(LocalDate desde, LocalDate hasta);
    List<ReservasDiaDTO> getReservasPorDia(LocalDate desde, LocalDate hasta);
    List<RendimientoBarberoDTO> getRendimientoBarberos(LocalDate desde, LocalDate hasta);
    List<ServicioSolicitadoDTO> getServiciosMasSolicitados(LocalDate desde, LocalDate hasta);
}
