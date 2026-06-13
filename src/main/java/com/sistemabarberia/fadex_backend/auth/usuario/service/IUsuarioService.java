package com.sistemabarberia.fadex_backend.auth.usuario.service;

import com.sistemabarberia.fadex_backend.auth.usuario.dto.request.*;
import com.sistemabarberia.fadex_backend.auth.usuario.dto.response.PermisoResponse;
import com.sistemabarberia.fadex_backend.auth.usuario.dto.response.RolResponse;
import com.sistemabarberia.fadex_backend.auth.usuario.dto.response.UsuarioResponse;
import com.sistemabarberia.fadex_backend.auth.usuario.dto.response.UsuarioTablaResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUsuarioService {

    List<UsuarioResponse> getAll();
    UsuarioResponse getById(Integer id);
    UsuarioResponse update(Integer id, RegisterRequest request);
    UsuarioResponse crearAdmin(CreateUsuarioRequest request);
    UsuarioResponse crearBarbero(CreateBarberoRequest request);
    UsuarioResponse crearCliente(CreateClienteRequest request);
    void resetPassword(Integer idUsuario, ResetPasswordRequest request);
    void updateUsername(Integer id, UpdateUsernameRequest request);
    Page<UsuarioTablaResponse> listarUsuariosTabla(Pageable pageable);
    Page<UsuarioTablaResponse> filtrarUsuarios(
            String rol,
            Boolean tieneQr,
            Boolean multiplesRoles,
            Pageable pageable
    );


    Page<UsuarioTablaResponse> buscarUsuarios(
            String texto,
            Pageable pageable
    );

    byte[] generarQr(Integer idUsuario);

    byte[] regenerarQr(Integer idUsuario);

    void asignarRoles(
            Integer idUsuario,
            AssignRolesRequest request
    );

    List<RolResponse> listarRoles();

    List<RolResponse> obtenerRolesUsuario(Integer idUsuario);

    Page<PermisoResponse> obtenerPermisosUsuario(Integer idUsuario, Pageable pageable);

    void quitarRol(Integer idUsuario, Integer idRol);

    void asignarPin(Integer idUsuario, AsignarPinRequest request);
}
