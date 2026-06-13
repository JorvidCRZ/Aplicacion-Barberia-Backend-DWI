package com.sistemabarberia.fadex_backend.modules.reserva.controller;

import com.sistemabarberia.fadex_backend.auth.security.service.CustomUserDetails;
import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Request.ReservaRequest;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.ReservaDTO;

import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.ReservaPendienteDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.service.ReservaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/reservas")
public class ReservaControllerCliente {

    private final ReservaService reservaService;
    private final ClienteRepository clienteRepository;



    @PreAuthorize("hasAuthority('RESERVA_CREATE')")
    @PostMapping
    public ResponseEntity<ReservaDTO> crearReserva(@RequestBody @Valid ReservaRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaService.crearReserva(request));
    }

    @PreAuthorize("hasAuthority('RESERVA_READ_SELF')")
    @GetMapping("/mis-reservas")
    public ResponseEntity<ApiResponse<PageResponse<ReservaDTO>>> obtenerReservasCliente(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReservaDTO> result = reservaService.listarReservasPorCliente(
                userDetails.getUsuario(),
                pageable
        );
        return ResponseEntity.ok(
                ApiResponse.ok("Reservas obtenidas correctamente", result)
        );
    }

    @GetMapping("/cliente/pendientes-pago")
    public ResponseEntity<ApiResponse<List<ReservaPendienteDTO>>> obtenerPendientesPago() {
        List<ReservaPendienteDTO> pendientes = reservaService.obtenerReservasPendientesDePago();
        return ResponseEntity.ok(
                ApiResponse.ok("Reservas pendientes obtenidas correctamente", pendientes)
        );
    }
}