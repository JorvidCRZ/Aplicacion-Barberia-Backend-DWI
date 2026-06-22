package com.sistemabarberia.fadex_backend.modules.venta.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.modules.venta.dto.request.VentaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.venta.dto.response.DetalleVentaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.venta.dto.response.HistorialVentaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.venta.dto.response.VentaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.venta.service.IVentaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ventas")
@RequiredArgsConstructor
public class VentaController {

    private final IVentaService ventaService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<VentaResponseDTO>>> listar(
            @RequestParam(required = false) String cliente,
            @RequestParam(required = false) String numeroCorrelativo,
            @RequestParam(required = false) String tipoComprobante,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin
    ) {

        return ResponseEntity.ok(ApiResponse.ok("Ventas listadas correctamente",
                ventaService.buscarConFiltros(cliente, numeroCorrelativo, tipoComprobante, fechaInicio, fechaFin)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VentaResponseDTO>> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Venta obtenida correctamente", ventaService.obtenerPorId(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VentaResponseDTO>> crear(@Valid @RequestBody VentaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Venta creada correctamente", ventaService.crear(dto)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<VentaResponseDTO>> actualizar(@PathVariable Integer id, @Valid @RequestBody VentaRequestDTO dto) {
        return ResponseEntity.ok(ApiResponse.ok("Venta actualizada correctamente", ventaService.actualizar(id, dto)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Integer id) {
        ventaService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Venta eliminada correctamente"));
    }

    @GetMapping("/{id}/detalles")
    public ResponseEntity<ApiResponse<List<DetalleVentaResponseDTO>>> listarDetalles(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Detalles obtenidos correctamente", ventaService.listarDetalles(id)));
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<ApiResponse<List<HistorialVentaResponseDTO>>> listarHistorial(@PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Historial obtenido correctamente", ventaService.listarHistorial(id)));
    }
}