package com.sistemabarberia.fadex_backend.modules.pagos.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.modules.pagos.dto.request.PagoRequestDTO;
import com.sistemabarberia.fadex_backend.modules.pagos.dto.response.HistorialPagoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.pagos.dto.response.PagoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.pagos.service.IPagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final IPagoService pagoService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PagoResponseDTO>>> listar(
            @RequestParam(required = false) String cliente
    ) {

        List<PagoResponseDTO> pagos = pagoService.listar(cliente);

        return ResponseEntity.ok(
                ApiResponse.ok("Pagos obtenidos correctamente", pagos)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PagoResponseDTO>> obtenerPorId(
            @PathVariable Long id
    ) {

        PagoResponseDTO pago = pagoService.obtenerPorId(id);

        return ResponseEntity.ok(
                ApiResponse.ok("Pago obtenido correctamente", pago)
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PagoResponseDTO>> crear(
            @Valid @RequestBody PagoRequestDTO dto
    ) {

        PagoResponseDTO creado = pagoService.crear(dto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Pago registrado correctamente", creado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PagoResponseDTO>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody PagoRequestDTO dto
    ) {

        PagoResponseDTO actualizado = pagoService.actualizar(id, dto);

        return ResponseEntity.ok(
                ApiResponse.ok("Pago actualizado correctamente", actualizado)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(
            @PathVariable Long id
    ) {

        pagoService.eliminar(id);

        return ResponseEntity.ok(
                ApiResponse.ok("Pago eliminado correctamente")
        );
    }

    @GetMapping("/{id}/historial")
    public ResponseEntity<ApiResponse<List<HistorialPagoResponseDTO>>> listarHistorial(
            @PathVariable Long id
    ) {

        List<HistorialPagoResponseDTO> historial =
                pagoService.listarHistorial(id);

        return ResponseEntity.ok(
                ApiResponse.ok("Historial de pago obtenido correctamente", historial)
        );
    }
}