package com.sistemabarberia.fadex_backend.modules.cliente.service;

import com.sistemabarberia.fadex_backend.modules.cliente.dto.request.ClienteRequestDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ActividadRecienteResponse;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ClienteDetalleResumenDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ClienteResponseDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ClienteResumenResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IClienteService {

    // ── CRUD básico ──────────────────────────────────────────────────────────

    Page<ClienteResponseDTO> listarClientes(Pageable pageable);

    Page<ClienteResponseDTO> listarClientesInhabilitados(Pageable pageable);

    void deshabilitarCliente(Integer id);

    void reactivarCliente(Integer id);

    ClienteResponseDTO crearCliente(ClienteRequestDTO dto);

    ClienteResponseDTO eliminar(Integer id);

    ClienteResponseDTO buscarCliente(Integer id);

    // ── Buscador por nombre

    Page<ClienteResponseDTO> buscarPorNombre(String nombre, Pageable pageable);



    Page<ClienteResponseDTO> filtrarTodos(Pageable pageable);


    Page<ClienteResponseDTO> filtrarPorMesActual(Pageable pageable);

    Page<ClienteResponseDTO> filtrarPorAnioActual(Pageable pageable);


    Page<ClienteResponseDTO> filtrarRecientes(Pageable pageable);


    Page<ClienteResponseDTO> filtrarPorRangoFechas(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Pageable pageable);


    ClienteResumenResponseDTO obtenerResumen();

    ClienteDetalleResumenDTO obtenerResumenCliente(Integer clienteId);

    List<ActividadRecienteResponse> obtenerActividadReciente(Integer idCliente);
    ClienteResponseDTO obtenerPerfilPropio(Integer usuarioId);

    ClienteDetalleResumenDTO obtenerResumenPropio(Integer usuarioId);

    // IClienteService.java — agrega
    Integer obtenerIdClientePorUsuario(Integer idUsuario);
}