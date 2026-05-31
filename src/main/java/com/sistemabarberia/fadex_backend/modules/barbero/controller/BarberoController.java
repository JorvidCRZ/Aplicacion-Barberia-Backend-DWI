package com.sistemabarberia.fadex_backend.modules.barbero.controller;

import com.sistemabarberia.fadex_backend.auth.security.service.CustomUserDetails;
import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.request.BarberoRequestDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.request.BarberoUpdateRequestDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.response.BarberoDetalleResponseDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.response.BarberoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.response.ResumenBarberoDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.response.ResumenIndividualBarberoDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.service.IBarberoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/barberos")
public class BarberoController {

    @Autowired
    private IBarberoService barberoService;

    // ─── CRUD BÁSICO ──────────────────────────────────────────────────────────

    // GET /api/v1/barberos?page=0&size=10
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<BarberoResponseDTO>>> listarBarberos(
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BarberoResponseDTO> result = barberoService.listarBarberos(pageable);
        return ResponseEntity.ok(ApiResponse.ok("Barberos obtenidos correctamente", result));
    }

    @GetMapping("/inhabilitados")
    public ResponseEntity<ApiResponse<PageResponse<BarberoResponseDTO>>> listarBarberosInhabilitados(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        Page<BarberoResponseDTO> result =
                barberoService.listarBarberosInhabilitados(pageable);

        return ResponseEntity.ok(
                ApiResponse.ok("Barberos inhabilitados obtenidos", result)
        );
    }

    @PatchMapping("/{id}/deshabilitar")
    public ResponseEntity<ApiResponse<String>> deshabilitarBarbero(
            @PathVariable Integer id
    ) {

        barberoService.deshabilitarBarbero(id);

        return ResponseEntity.ok(
                ApiResponse.ok("Barbero deshabilitado correctamente", "OK")
        );
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<ApiResponse<String>> reactivarBarbero(
            @PathVariable Integer id
    ) {

        barberoService.reactivarBarbero(id);

        return ResponseEntity.ok(
                ApiResponse.ok("Barbero reactivado correctamente", "OK")
        );
    }

    // ⚠️ Rutas estáticas SIEMPRE antes de /{id}

    // GET /api/v1/barberos/resumen
    @GetMapping("/resumen-general")
    public ResponseEntity<ApiResponse<ResumenBarberoDTO>> obtenerResumen() {
        ResumenBarberoDTO resumen = barberoService.obtenerResumen();
        return ResponseEntity.ok(ApiResponse.ok("Resumen obtenido correctamente", resumen));
    }

    // GET /api/v1/barberos/buscar?estado=disponible&ordenarPor=sueldo&direccion=desc
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<PageResponse<BarberoResponseDTO>>> buscar(
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) String ordenarPor,
            @RequestParam(required = false) String direccion,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BarberoResponseDTO> result = barberoService.buscar(estado, ordenarPor, direccion, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Barberos obtenidos correctamente", result));
    }

    // GET /api/v1/barberos/{id}  ← SIEMPRE al final de los GETs
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BarberoResponseDTO>> buscarBarbero(@PathVariable Integer id) {
        BarberoResponseDTO barbero = barberoService.buscarBarbero(id);
        return ResponseEntity.ok(ApiResponse.ok("Barbero obtenido correctamente", barbero));
    }

    // POST /api/v1/barberos
    @PostMapping
    public ResponseEntity<ApiResponse<BarberoResponseDTO>> crearBarbero(
            @Valid @RequestBody BarberoRequestDTO requestDTO) {
        BarberoResponseDTO creado = barberoService.crearBarbero(requestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Barbero creado correctamente", creado));
    }

    // DELETE /api/v1/barberos/eliminar/{id}
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<ApiResponse<BarberoResponseDTO>> eliminar(@PathVariable Integer id) {
        BarberoResponseDTO eliminado = barberoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok("Barbero eliminado correctamente", eliminado));
    }

    // PATCH /api/v1/barberos/actualizar/{id}
    @PatchMapping("/actualizar/{id}")
    public ResponseEntity<ApiResponse<BarberoResponseDTO>> actualizarBarbero(
            @PathVariable Integer id,
            @Valid @RequestBody BarberoUpdateRequestDTO requestDTO) {
        BarberoResponseDTO actualizado = barberoService.actualizarBarbero(id, requestDTO);
        return ResponseEntity.ok(ApiResponse.ok("Barbero actualizado correctamente", actualizado));
    }

    // GET /api/v1/barberos/buscar-nombre?q=luis&page=0&size=10
    @GetMapping("/buscar-nombre")
    public ResponseEntity<ApiResponse<PageResponse<BarberoResponseDTO>>> buscarPorNombre(
            @RequestParam(required = false, defaultValue = "") String q,
            @RequestParam(defaultValue = "0")  int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BarberoResponseDTO> result = barberoService.buscarPorNombre(q, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Búsqueda completada", result));
    }

    // GET /api/v1/barberos/{id}/resumen
    @GetMapping("/{id}/resumen")
    public ResponseEntity<ApiResponse<ResumenIndividualBarberoDTO>> obtenerResumenIndividual(
            @PathVariable Integer id) {
        ResumenIndividualBarberoDTO resumen = barberoService.obtenerResumenIndividual(id);
        return ResponseEntity.ok(ApiResponse.ok("Resumen del barbero obtenido", resumen));
    }
    @GetMapping("/perfil-propio")
    public ResponseEntity<ApiResponse<BarberoDetalleResponseDTO>> perfilPropio(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.ok("Perfil obtenido",
                barberoService.obtenerPerfilPropio(userDetails.getUsuario().getIdUsuario())));
    }

    @PatchMapping("/{id}/ocupado")
    public ResponseEntity<ApiResponse<BarberoResponseDTO>> toggleOcupado(
            @PathVariable Integer id) {
        return ResponseEntity.ok(ApiResponse.ok("Estado actualizado",
                barberoService.toggleOcupado(id)));
    }
}