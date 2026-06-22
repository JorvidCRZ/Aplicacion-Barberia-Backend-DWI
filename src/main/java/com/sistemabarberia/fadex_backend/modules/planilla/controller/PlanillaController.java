package com.sistemabarberia.fadex_backend.modules.planilla.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.planilla.dto.*;
import com.sistemabarberia.fadex_backend.modules.planilla.service.IPlanillaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
@RequestMapping("/api/v1/planillas")
@RequiredArgsConstructor
public class PlanillaController {

    private final IPlanillaService planillaService;

    @GetMapping("/detalle")
    public ResponseEntity<ApiResponse<PageResponse<PlanillaBarberoDTO>>> detalle(
            @RequestParam Integer mes,
            @RequestParam Integer anio,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<PlanillaBarberoDTO> result =
                planillaService.obtenerDetalle(mes, anio, PageRequest.of(page, size));

        return ResponseEntity.ok(ApiResponse.ok(
                "Detalle de planilla obtenido correctamente",
                PageResponse.of(result)
        ));
    }

    @GetMapping("/resumen")
    public ResponseEntity<ApiResponse<PlanillaResumenDTO>> resumen(
            @RequestParam Integer mes,
            @RequestParam Integer anio
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Resumen de planilla obtenido correctamente",
                planillaService.obtenerResumen(mes, anio)
        ));
    }

    @GetMapping("/anios")
    public ResponseEntity<ApiResponse<List<Integer>>> obtenerAnios() {
        return ResponseEntity.ok(ApiResponse.ok(
                "Años obtenidos correctamente",
                planillaService.obtenerAniosDisponibles()
        ));
    }

    @GetMapping("/barberos/{idBarbero}/resumen")
    public ResponseEntity<ApiResponse<DetalleBarberoResumenDTO>> resumenBarbero(
                                                                                  @PathVariable Integer idBarbero,
                                                                                  @RequestParam Integer mes,
                                                                                  @RequestParam Integer anio
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Resumen obtenido correctamente",
                planillaService.obtenerResumenBarbero(idBarbero, mes, anio)
        ));
    }

    @GetMapping("/barberos/{idBarbero}/ventas")
    public ResponseEntity<ApiResponse<PageResponse<VentaBarberoDTO>>> ventasBarbero(
            @PathVariable Integer idBarbero,
            @RequestParam Integer mes,
            @RequestParam Integer anio,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(ApiResponse.ok(
                "Ventas obtenidas correctamente",
                PageResponse.of(
                        planillaService.obtenerVentasBarbero(
                                idBarbero, mes, anio, PageRequest.of(page, size)
                        )
                )
        ));
    }
}