package com.sistemabarberia.fadex_backend.modules.servicio.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.modules.servicio.dto.request.ServicioRequestDTO;

import com.sistemabarberia.fadex_backend.modules.servicio.dto.response.ServicioResponseDTO;


import com.sistemabarberia.fadex_backend.modules.servicio.service.IServicioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/servicio")
@RequiredArgsConstructor
public class ServicioController {

    private final IServicioService servicioService;

    @GetMapping
    public ResponseEntity<List<ServicioResponseDTO>> listar() {
        return ResponseEntity.ok(servicioService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(servicioService.obtenerPorId(id));
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ServicioResponseDTO>> listarPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(servicioService.listarPorCategoria(categoriaId));
    }

    @PreAuthorize("hasAuthority('SERVICIO_CREATE')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ServicioResponseDTO>> crear(
            @RequestPart("servicio") String servicio,
            @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos
    ) throws Exception {

        ObjectMapper objectMapper = new ObjectMapper();

        ServicioRequestDTO request =
                objectMapper.readValue(servicio, ServicioRequestDTO.class);

        ServicioResponseDTO responseDTO =
                servicioService.crear(request, archivos);

        return ResponseEntity.ok(
                ApiResponse.ok("Servicio creado correctamente", responseDTO)
        );
    }

    @PreAuthorize("hasAuthority('SERVICIO_UPDATE_ALL')")
    @PutMapping("/{id}")
    public ResponseEntity<ServicioResponseDTO> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ServicioRequestDTO dto) {
        return ResponseEntity.ok(servicioService.actualizar(id, dto));
    }

    @PreAuthorize("hasAuthority('SERVICIO_DELETE_ALL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        servicioService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}