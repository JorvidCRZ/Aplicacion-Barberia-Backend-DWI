package com.sistemabarberia.fadex_backend.modules.reserva.controller;

import com.sistemabarberia.fadex_backend.auth.security.service.CustomUserDetails;
import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.ReservaDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.ResumenDiarioDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.ResumenSemanalDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/barbero/reservas")
public class ReservaControllerBarbero {

    private final ReservaService  reservaService;

    @PreAuthorize("hasAuthority('RESERVA_READ_ASSIGNED')")
    @GetMapping
    public ResponseEntity<List<ReservaDTO>> ListarReservasBarbero(@AuthenticationPrincipal CustomUserDetails details) {



        return ResponseEntity.ok(reservaService.ListarReservasBarbero(details.getUsuario()));
    }

    @GetMapping("/barbero/{barberoId}/hoy")
    public ResponseEntity<ApiResponse<ResumenDiarioDTO>> resumenDiario(
            @PathVariable Integer barberoId) {
        return ResponseEntity.ok(ApiResponse.ok("Resumen diario obtenido correctamente",
                        reservaService.obtenerResumenDiario(barberoId)
                )
        );
    }


    @PutMapping("/{id}/iniciar")
    public ResponseEntity<ApiResponse<ReservaDTO>> iniciar(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Atención iniciada correctamente",
                        reservaService.iniciarAtencion(id)
                )
        );
    }

    // EN_PROCESO → COMPLETADO
    @PutMapping("/{id}/finalizar")
    public ResponseEntity<ApiResponse<ReservaDTO>> finalizar(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Atención finalizada correctamente",
                        reservaService.finalizarAtencion(id)
                )
        );
    }

    // Resumen semanal para gráficas
    @GetMapping("/barbero/{barberoId}/semanal")
    public ResponseEntity<ApiResponse<ResumenSemanalDTO>> resumenSemanal(
            @PathVariable Integer barberoId) {
        return ResponseEntity.ok( ApiResponse.ok("Resumen semanal obtenido correctamente",
                        reservaService.obtenerResumenSemanal(barberoId)
                )
        );
    }

    @PutMapping("/{id}/cancelar")
    public ResponseEntity<ApiResponse<ReservaDTO>> cancelar(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok("Reserva cancelada correctamente",
                        reservaService.cancelarReserva(id)
                )
        );
    }
}
