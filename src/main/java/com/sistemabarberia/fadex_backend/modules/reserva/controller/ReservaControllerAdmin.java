package com.sistemabarberia.fadex_backend.modules.reserva.controller;


import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.ReservaDTO;
import com.sistemabarberia.fadex_backend.modules.reserva.service.ReservaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/reservas")
@RequiredArgsConstructor
public class ReservaControllerAdmin {
    private final ReservaService reservaService;

    @PreAuthorize("hasAuthority('RESERVA_READ_ALL')")
    @GetMapping
    public ResponseEntity<List<ReservaDTO>> listarReservas() {

         return ResponseEntity.ok(reservaService.ListarReservasAdmin());
    }
}
