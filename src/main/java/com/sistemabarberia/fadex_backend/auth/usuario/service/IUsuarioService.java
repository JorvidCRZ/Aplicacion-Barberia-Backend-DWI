package com.sistemabarberia.fadex_backend.auth.usuario.service;

import com.sistemabarberia.fadex_backend.auth.usuario.dto.request.*;
import com.sistemabarberia.fadex_backend.auth.usuario.dto.response.UsuarioResponse;

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
}
