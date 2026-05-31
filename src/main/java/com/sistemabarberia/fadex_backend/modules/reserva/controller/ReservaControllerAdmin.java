package com.sistemabarberia.fadex_backend.modules.reserva.controller;


import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.response.BarberoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.ReservaDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/reservas")
@RequiredArgsConstructor
public class ReservaControllerAdmin {
    private final ReservaService reservaService;

    @PreAuthorize("hasAuthority('RESERVA_READ_ALL')")
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ReservaDTO>>> listarReservas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<ReservaDTO> result = reservaService.listarReservasAdmin(pageable);

        return ResponseEntity.ok(
                ApiResponse.ok("Reservas obtenidas correctamente", result)
        );
    }
}
