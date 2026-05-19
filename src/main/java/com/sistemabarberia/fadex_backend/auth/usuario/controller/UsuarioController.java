package com.sistemabarberia.fadex_backend.auth.usuario.controller;


import com.sistemabarberia.fadex_backend.auth.usuario.dto.request.*;
import com.sistemabarberia.fadex_backend.auth.usuario.dto.response.UsuarioResponse;
import com.sistemabarberia.fadex_backend.auth.usuario.service.IUsuarioService;
import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService usuarioService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioResponse>>> listar() {
        return ResponseEntity.ok( ApiResponse.ok("Usuarios obtenidos correctamente", usuarioService.getAll()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> buscarPorId(
            @PathVariable Integer id) {
        return ResponseEntity.ok( ApiResponse.ok("Usuario encontrado", usuarioService.getById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> update( @PathVariable Integer id, @RequestBody RegisterRequest request) {
        return ResponseEntity.ok( ApiResponse.ok( "Usuario actualizado correctamente", usuarioService.update(id, request)));
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ApiResponse<UsuarioResponse>> crearAdmin(
            @Valid @RequestBody CreateUsuarioRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Admin creado correctamente", usuarioService.crearAdmin(request)));
    }

    @PostMapping("/barbero")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ApiResponse<UsuarioResponse>> crearBarbero(
            @Valid @RequestBody CreateBarberoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Barbero creado correctamente", usuarioService.crearBarbero(request)));
    }

    @PostMapping("/cliente")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ApiResponse<UsuarioResponse>> crearCliente(
            @Valid @RequestBody CreateClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Cliente creado correctamente", usuarioService.crearCliente(request)));
    }



    @PutMapping("/{id}/reset-password")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ApiResponse<Void>> resetPassword(
            @PathVariable Integer id,
            @Valid @RequestBody ResetPasswordRequest request
    ) {

        usuarioService.resetPassword(id, request);

        return ResponseEntity.ok(
                ApiResponse.ok("Contraseña reseteada correctamente")
        );
    }


    @PatchMapping("/{id}/username-update")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ApiResponse<Void>> updateUsername(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateUsernameRequest request
    ) {

        usuarioService.updateUsername(id, request);

        return ResponseEntity.ok(
                ApiResponse.ok("Usuario actualizado correctamente")
        );
    }
}
