package com.sistemabarberia.fadex_backend.modules.reserva.controller;

import com.sistemabarberia.fadex_backend.modules.reserva.dto.Request.ActualizarEstadoReservaDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.CitaBarberoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/barbero/citas")
@RequiredArgsConstructor
public class CitasBarberoController {

    private final ReservaService reservaService;

    @GetMapping("/hoy")
    @PreAuthorize("hasAuthority('ROLE_barbero')")
    public ResponseEntity<List<CitaBarberoResponseDTO>> obtenerCitasHoy() {
        // LOG TEMPORAL
        SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .forEach(a -> System.out.println(">>> AUTHORITY EN CONTROLLER: " + a.getAuthority()));

        return ResponseEntity.ok(reservaService.obtenerCitasHoy());
    }

    @PatchMapping("/{idReserva}/estado")
    @PreAuthorize("hasAuthority('ROLE_barbero')")
    public ResponseEntity<CitaBarberoResponseDTO> actualizarEstado(
            @PathVariable Long idReserva,
            @Valid @RequestBody ActualizarEstadoReservaDTO dto
    ) {
        return ResponseEntity.ok(reservaService.actualizarEstadoReserva(idReserva, dto));
    }
}
