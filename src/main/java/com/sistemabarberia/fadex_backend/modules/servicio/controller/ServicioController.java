package com.sistemabarberia.fadex_backend.modules.servicio.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.servicio.dto.ServicioFiltro;
import com.sistemabarberia.fadex_backend.modules.servicio.dto.request.ServicioRequestDTO;
import com.sistemabarberia.fadex_backend.modules.servicio.dto.response.ServicioResponseDTO;
import com.sistemabarberia.fadex_backend.modules.servicio.service.IServicioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/servicios")
@RequiredArgsConstructor
public class ServicioController {

    private final IServicioService servicioService;

    @GetMapping("/publicados/{id}")
    public ResponseEntity<ApiResponse<ServicioResponseDTO>> obtenerServicioPublicado(@PathVariable Long id) {
        ServicioResponseDTO servicio = servicioService.obtenerServicioPublicadoPorId(id);
        return ResponseEntity.ok(ApiResponse.ok("Servicio obtenido correctamente", servicio));
    }

    @GetMapping("/publicados")
    public ResponseEntity<ApiResponse<PageResponse<ServicioResponseDTO>>> obtenerServiciosPublicos(@Valid @ModelAttribute ServicioFiltro filtro, @PageableDefault(size = 10, page = 0) Pageable pageable) {
        PageResponse<ServicioResponseDTO> servicios = servicioService.listarServiciosPublicos(filtro, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Servicios obtenidos correctamente", servicios));
    }

    @PreAuthorize("hasAuthority('SERVICIO_READ')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ServicioResponseDTO>>> obtenerServicios(@Valid @ModelAttribute ServicioFiltro filtro, @PageableDefault(size = 10, page = 0) Pageable pageable) {
        PageResponse<ServicioResponseDTO> servicios = servicioService.listarServicioFiltros(filtro, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Servicios obtenidos correctamente", servicios));
    }

    @PreAuthorize("hasAuthority('SERVICIO_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServicioResponseDTO>> obtenerPorId(@PathVariable Long id) {
        ServicioResponseDTO servicio = servicioService.obtenerPorId(id);
        return ResponseEntity.ok(ApiResponse.ok("Servicio obtenido correctamente", servicio));
    }

    @PreAuthorize("hasAuthority('SERVICIO_CREATE')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ServicioResponseDTO>> crear(@RequestPart("servicio") ServicioRequestDTO request, @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos) {
        ServicioResponseDTO servicio = servicioService.crear(request, archivos);
        return ResponseEntity.ok(ApiResponse.ok("Servicio creado correctamente", servicio));
    }

    @PreAuthorize("hasAuthority('SERVICIO_UPDATE_ALL')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ServicioResponseDTO>> actualizar(@PathVariable Long id, @RequestPart("servicio") ServicioRequestDTO request, @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos) {
        ServicioResponseDTO servicio = servicioService.actualizar(id, request, archivos);
        return ResponseEntity.ok(ApiResponse.ok("Servicio actualizado correctamente", servicio));
    }

    @PreAuthorize("hasAuthority('SERVICIO_UPDATE_ALL')")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<ServicioResponseDTO>> cambiarEstadoServicio(@PathVariable Long id, @RequestParam boolean estado) {
        ServicioResponseDTO servicio = servicioService.cambiarEstadoServicio(id, estado);
        return ResponseEntity.ok(ApiResponse.ok("Estado actualizado correctamente", servicio));
    }

    @PreAuthorize("hasAuthority('SERVICIO_UPDATE_ALL')")
    @PatchMapping("/{id}/publicacion")
    public ResponseEntity<ApiResponse<ServicioResponseDTO>> cambiarPublicacion(@PathVariable Long id, @RequestParam boolean publicado) {
        ServicioResponseDTO servicio = servicioService.cambiarPublicacion(id, publicado);
        return ResponseEntity.ok(ApiResponse.ok("Publicación actualizada correctamente", servicio));
    }

    @PreAuthorize("hasAuthority('SERVICIO_DELETE_ALL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        servicioService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Servicio eliminado correctamente"));
    }

    @GetMapping("/lista")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> lista() {
        return ResponseEntity.ok(ApiResponse.success(servicioService.getLista()));
    }
}
