package com.sistemabarberia.fadex_backend.modules.reclamo.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.ReclamoFiltro;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.ReclamoResumen;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.request.ReclamoPublicoRequest;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.request.ReclamoRequest;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.request.ReclamoSolucionRequest;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.response.ReclamoResponse;
import com.sistemabarberia.fadex_backend.modules.reclamo.service.IReclamoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/reclamos")
@RequiredArgsConstructor
public class ReclamoController {

    private final IReclamoService reclamoService;

   @PreAuthorize("hasAuthority('RECLAMO_CREATE')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ReclamoResponse>> crear(@RequestPart("reclamo") @Valid ReclamoRequest request, @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos) {
        ReclamoResponse reclamo = reclamoService.crearReclamo(request, archivos);
        return ResponseEntity.ok(ApiResponse.ok("Reclamo creado correctamente", reclamo));
    }

    @PostMapping(value = "/publico", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ReclamoResponse>> crearPublico(@RequestPart("reclamo") @Valid ReclamoPublicoRequest request, @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos) {
        ReclamoResponse reclamo = reclamoService.crearReclamoPublico(request, archivos);
        return ResponseEntity.ok(ApiResponse.ok("Reclamo registrado correctamente", reclamo));
    }

    @PreAuthorize("hasAuthority('RECLAMO_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReclamoResponse>> obtener(@PathVariable Long id) {
        ReclamoResponse reclamo = reclamoService.obtenerReclamoPorId(id);
        return ResponseEntity.ok(ApiResponse.ok("Reclamo obtenido correctamente", reclamo));
    }

    @PreAuthorize("hasAuthority('RECLAMO_READ')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ReclamoResponse>>> listar(@Valid @ModelAttribute ReclamoFiltro filtro, @PageableDefault(size = 10, page = 0, sort = "fechaReclamo", direction = Sort.Direction.DESC) Pageable pageable) {
        PageResponse<ReclamoResponse> reclamos = reclamoService.listarReclamoFiltros(filtro, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Reclamos obtenidos correctamente", reclamos));
    }

    @PreAuthorize("hasAuthority('RECLAMO_UPDATE_ALL')")
    @PutMapping("/{id}/solucion")
    public ResponseEntity<ApiResponse<ReclamoResponse>> actualizar(@PathVariable Long id, @Valid @RequestBody ReclamoSolucionRequest request) {
        ReclamoResponse reclamo = reclamoService.actualizarReclamoSolucion(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Reclamo actualizado correctamente", reclamo));
    }

    @PreAuthorize("hasAuthority('RECLAMO_READ')")
    @GetMapping("/resumen")
    public ResponseEntity<ApiResponse<ReclamoResumen>> resumen() {
        ReclamoResumen resumen = reclamoService.obtenerReclamoResumen();
        return ResponseEntity.ok(ApiResponse.ok("Resumen obtenido correctamente", resumen));
    }

    @PreAuthorize("hasAuthority('RECLAMO_DELETE_ALL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        reclamoService.eliminarReclamo(id);
        return ResponseEntity.ok(ApiResponse.ok("Reclamo eliminado correctamente"));
    }
}