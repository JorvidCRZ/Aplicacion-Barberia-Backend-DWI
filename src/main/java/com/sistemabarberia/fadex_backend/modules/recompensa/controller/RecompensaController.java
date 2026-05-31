package com.sistemabarberia.fadex_backend.modules.recompensa.controller;

import com.sistemabarberia.fadex_backend.auth.security.service.CustomUserDetails;
import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.modules.recompensa.dto.response.RecompensaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.recompensa.service.IRecompensaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/recompensas")
public class RecompensaController {

    @Autowired
    private IRecompensaService recompensaService;

    // ─────────────────────────────────────────────────────────────────────────
    // CLIENTE AUTENTICADO — tarjeta propia
    // ─────────────────────────────────────────────────────────────────────────

    @PreAuthorize("hasAuthority('ROLE_cliente')")
    @GetMapping("/mi-tarjeta")
    public ResponseEntity<ApiResponse<RecompensaResponseDTO>> obtenerTarjetaPropia(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Integer usuarioId = userDetails.getUsuario().getIdUsuario();
        RecompensaResponseDTO tarjeta = recompensaService.obtenerTarjetaPropia(usuarioId);
        return ResponseEntity.ok(
                ApiResponse.ok("Tarjeta de recompensas obtenida correctamente", tarjeta));
    }

    // ─────────────────────────────────────────────────────────────────────────
    // ADMIN / BARBERO — consulta por clienteId
    // ─────────────────────────────────────────────────────────────────────────

    @PreAuthorize("hasAnyAuthority('ROLE_admin', 'ROLE_barbero')")
    @GetMapping("/{clienteId}")
    public ResponseEntity<ApiResponse<RecompensaResponseDTO>> obtenerTarjeta(
            @PathVariable Integer clienteId
    ) {
        RecompensaResponseDTO tarjeta = recompensaService.obtenerTarjeta(clienteId);
        return ResponseEntity.ok(
                ApiResponse.ok("Tarjeta de recompensas obtenida correctamente", tarjeta));
    }
}