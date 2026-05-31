package com.sistemabarberia.fadex_backend.auth.usuario.controller;

import com.sistemabarberia.fadex_backend.auth.usuario.dto.request.*;
import com.sistemabarberia.fadex_backend.auth.usuario.dto.response.PermisoResponse;
import com.sistemabarberia.fadex_backend.auth.usuario.dto.response.RolResponse;
import com.sistemabarberia.fadex_backend.auth.usuario.dto.response.UsuarioResponse;
import com.sistemabarberia.fadex_backend.auth.usuario.dto.response.UsuarioTablaResponse;
import com.sistemabarberia.fadex_backend.auth.usuario.service.IUsuarioService;
import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.persona.dto.request.PersonaUpdateRequestDTO;
import com.sistemabarberia.fadex_backend.modules.persona.service.IPersonaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService usuarioService;
    private final IPersonaService personaService;


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

    @GetMapping("/tabla")
    public ResponseEntity<ApiResponse<PageResponse<UsuarioTablaResponse>>> listarUsuariosTabla(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        Page<UsuarioTablaResponse> result =
                usuarioService.listarUsuariosTabla(pageable);

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Usuarios obtenidos correctamente",
                        result
                )
        );
    }

    @GetMapping("/filtrar")
    public ResponseEntity<ApiResponse<PageResponse<UsuarioTablaResponse>>> filtrarUsuarios(

            @RequestParam(required = false) String rol,

            @RequestParam(required = false) Boolean tieneQr,

            @RequestParam(required = false) Boolean multiplesRoles,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        Page<UsuarioTablaResponse> result =
                usuarioService.filtrarUsuarios(
                        rol,
                        tieneQr,
                        multiplesRoles,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Usuarios filtrados correctamente",
                        result
                )
        );
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<PageResponse<UsuarioTablaResponse>>> buscarUsuarios(

            @RequestParam String texto,

            @RequestParam(defaultValue = "0") int page,

            @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);

        Page<UsuarioTablaResponse> result =
                usuarioService.buscarUsuarios(
                        texto,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Usuarios encontrados correctamente",
                        result
                )
        );
    }

    @GetMapping(value = "/{id}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generarQr(
            @PathVariable Integer id
    ) {

        byte[] qr = usuarioService.generarQr(id);

        return ResponseEntity.ok(qr);
    }


    @PatchMapping("/{id}/roles")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ApiResponse<Void>> asignarRoles(

            @PathVariable Integer id,

            @Valid
            @RequestBody AssignRolesRequest request
    ) {

        usuarioService.asignarRoles(id, request);

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Roles asignados correctamente"
                )
        );
    }

    @GetMapping("/roles")
    public ResponseEntity<ApiResponse<List<RolResponse>>> listarRoles() {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Roles obtenidos correctamente",
                        usuarioService.listarRoles()
                )
        );
    }

    @GetMapping("/{id}/roles")
    public ResponseEntity<ApiResponse<List<RolResponse>>> obtenerRolesUsuario(
            @PathVariable Integer id
    ) {

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Roles del usuario obtenidos correctamente",
                        usuarioService.obtenerRolesUsuario(id)
                )
        );
    }

    @GetMapping("/{id}/permisos")
    public ResponseEntity<ApiResponse<PageResponse<PermisoResponse>>> obtenerPermisosUsuario(
            @PathVariable Integer id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        return ResponseEntity.ok(
                ApiResponse.ok(
                        "Permisos obtenidos correctamente",
                        usuarioService.obtenerPermisosUsuario(id, pageable)
                )
        );
    }

    @DeleteMapping("/{id}/roles/{idRol}")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<ApiResponse<Void>> quitarRol(
            @PathVariable Integer id,
            @PathVariable Integer idRol
    ) {
        usuarioService.quitarRol(id, idRol);
        return ResponseEntity.ok(ApiResponse.ok("Rol eliminado correctamente"));
    }



}
