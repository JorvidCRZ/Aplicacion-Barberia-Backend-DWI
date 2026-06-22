package com.sistemabarberia.fadex_backend.modules.cliente.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.exception.ResourceNotFoundException;
import com.sistemabarberia.fadex_backend.modules.barbero.repository.BarberoRepository;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.request.ClienteRequestDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ActividadRecienteResponse;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ClienteDetalleResumenDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ClienteResponseDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.dto.response.ClienteResumenResponseDTO;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.mapper.ClienteMapper;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.cliente.service.IClienteService;
import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import com.sistemabarberia.fadex_backend.modules.persona.repository.PersonaRepository;
import com.sistemabarberia.fadex_backend.modules.recompensa.entity.Recompensa;
import com.sistemabarberia.fadex_backend.modules.recompensa.repository.RecompensaRepository;
import com.sistemabarberia.fadex_backend.modules.reserva.repository.ReservaRepository;
import com.sistemabarberia.fadex_backend.modules.venta.repository.VentaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClienteServiceImpl implements IClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private BarberoRepository barberoRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private RecompensaRepository recompensaRepository;

    @Autowired
    private ClienteMapper mapper;

    //CRUD básico

    @Override
    public Page<ClienteResponseDTO> listarClientes(Pageable pageable) {
        return clienteRepository.findByActivoTrue(pageable)
                .map(mapper::toResponseDTO);
    }

    @Override
    public Page<ClienteResponseDTO> listarClientesInhabilitados(Pageable pageable) {
        return clienteRepository.findByActivoFalse(pageable)
                .map(mapper::toResponseDTO);
    }


    @Transactional
    @Override
    public ClienteResponseDTO crearCliente(ClienteRequestDTO dto) {

        Persona persona = personaRepository.findById(dto.getPersonaId())
                .orElseThrow(() -> new BusinessException(
                        "Persona no encontrada con id: " + dto.getPersonaId(),
                        HttpStatus.NOT_FOUND));

        if (clienteRepository.existsByPersona_PersonaId(dto.getPersonaId())) {
            throw new BusinessException(
                    "Esta persona ya está registrada como Cliente",
                    HttpStatus.CONFLICT);
        }

        if (barberoRepository.existsByPersona_PersonaId(dto.getPersonaId())) {
            throw new BusinessException(
                    "Esta persona ya está registrada como Barbero",
                    HttpStatus.CONFLICT);
        }

        Cliente cliente = mapper.toEntity(dto, persona);
        Cliente guardado = clienteRepository.save(cliente);

        // Se crea la tarjeta de recompensas automáticamente
        Recompensa recompensa = Recompensa.builder()
                .cliente(guardado)
                .cortesAcumulados(0)
                .cortesGratis(0)
                .fechaActualizacion(LocalDateTime.now())
                .build();
        recompensaRepository.save(recompensa);

        return mapper.toResponseDTO(guardado);
    }

    @Override
    public ClienteResponseDTO eliminar(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        ClienteResponseDTO dto = mapper.toResponseDTO(cliente);

        Integer idPersona = cliente.getPersona().getPersonaId();
        // Borrar la persona elimina el cliente por CASCADE
        personaRepository.deleteById(idPersona);
        return dto;
    }

    @Override
    public ClienteResponseDTO buscarCliente(Integer id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Cliente no encontrado con id: " + id,
                        HttpStatus.NOT_FOUND));
        return mapper.toResponseDTO(cliente);
    }

    // Buscador por nombre

    @Override
    public Page<ClienteResponseDTO> buscarPorNombre(String nombre, Pageable pageable) {
        return clienteRepository.buscarPorNombre(nombre, pageable)
                .map(mapper::toResponseDTO);
    }

    // Filtros por fecha

    @Override
    public Page<ClienteResponseDTO> filtrarTodos(Pageable pageable) {
        // Delega en listarClientes para no duplicar lógica
        return listarClientes(pageable);
    }

    @Override
    public Page<ClienteResponseDTO> filtrarPorMesActual(Pageable pageable) {
        return clienteRepository.filtrarPorMesActual(pageable)
                .map(mapper::toResponseDTO);
    }

    @Override
    public Page<ClienteResponseDTO> filtrarPorAnioActual(Pageable pageable) {
        return clienteRepository.filtrarPorAnioActual(pageable)
                .map(mapper::toResponseDTO);
    }

    @Override
    public Page<ClienteResponseDTO> filtrarRecientes(Pageable pageable) {
        return clienteRepository.filtrarRecientes(pageable)
                .map(mapper::toResponseDTO);
    }

    @Override
    public Page<ClienteResponseDTO> filtrarPorRangoFechas(
            LocalDate fechaInicio,
            LocalDate fechaFin,
            Pageable pageable) {

        if (fechaInicio.isAfter(fechaFin)) {
            throw new BusinessException(
                    "La fecha de inicio no puede ser posterior a la fecha fin",
                    HttpStatus.BAD_REQUEST);
        }

        return clienteRepository.filtrarPorRangoFechas(fechaInicio, fechaFin, pageable)
                .map(mapper::toResponseDTO);
    }

    //  Resúmenes y actividad

    @Override
    public ClienteResumenResponseDTO obtenerResumen() {

        Long totalClientes    = clienteRepository.contarClientes();
        Long nuevosClientes   = clienteRepository.contarClientesNuevosMes();
        Long clientesActivos  = reservaRepository.clientesActivosMes();
        Double retencion      = reservaRepository.calcularRetencion();
        Double retencionAnterior = reservaRepository.calcularRetencionMesAnterior();

        if (retencionAnterior == null) retencionAnterior = 0.0;

        Double diferenciaRetencion = retencion - retencionAnterior;
        String deltaRetencion = (diferenciaRetencion >= 0 ? "+" : "")
                + String.format("%.1f", diferenciaRetencion) + "% vs anterior";

        Long nuevosAnterior    = clienteRepository.contarClientesNuevosMesAnterior();
        Long diferenciaNuevos  = nuevosClientes - nuevosAnterior;
        String deltaNuevos     = (diferenciaNuevos >= 0 ? "+" : "") + diferenciaNuevos + " vs anterior";

        Long totalAnterior     = clienteRepository.contarClientesHastaMesAnterior();
        Long diferenciaTotal   = totalClientes - totalAnterior;
        String deltaTotal      = (diferenciaTotal >= 0 ? "+" : "") + diferenciaTotal + " este mes";

        Long activosAnterior   = reservaRepository.clientesActivosMesAnterior();
        Long diferenciaActivos = clientesActivos - activosAnterior;
        String deltaActivos    = (diferenciaActivos >= 0 ? "+" : "") + diferenciaActivos + " vs anterior";

        return ClienteResumenResponseDTO.builder()
                .totalClientes(totalClientes)
                .deltaTotalClientes(deltaTotal)
                .clientesActivosMes(clientesActivos)
                .deltaClientesActivos(deltaActivos)
                .nuevosClientes(nuevosClientes)
                .deltaNuevosClientes(deltaNuevos)
                .retencion(retencion)
                .deltaRetencion(deltaRetencion)
                .build();
    }

    @Override
    public ClienteDetalleResumenDTO obtenerResumenCliente(Integer clienteId) {

        System.out.println(">>> clienteId para cortes: " + clienteId);
        Long totalReservas = reservaRepository.contarReservasCliente(clienteId.longValue());
        Long totalCortes   = reservaRepository.contarCortesCliente(clienteId.longValue());
        System.out.println(">>> totalCortes resultado: " + totalCortes);
        Long totalCompras  = ventaRepository.contarComprasCliente(clienteId);
        Double totalGastado = ventaRepository.totalGastadoCliente(clienteId);
        java.sql.Date ultimaVisita = reservaRepository.ultimaVisitaCliente(clienteId.longValue());

        return ClienteDetalleResumenDTO.builder()
                .totalReservas(totalReservas)
                .totalCortes(totalCortes)
                .totalCompras(totalCompras)
                .totalGastado(totalGastado)
                .ultimaVisita(ultimaVisita != null
                        ? ultimaVisita.toLocalDate().toString()
                        : "Sin visitas")
                .build();
    }
    @Override
    public List<ActividadRecienteResponse> obtenerActividadReciente(Integer idCliente) {

        List<Object[]> rows = clienteRepository.obtenerActividadReciente(idCliente);

        return rows.stream().map(row ->
                ActividadRecienteResponse.builder()
                        .tipo((String) row[0])
                        .titulo((String) row[1])
                        .descripcion((String) row[2])
                        .fecha(((Timestamp) row[3]).toLocalDateTime())
                        .color((String) row[4])
                        .build()
        ).toList();
    }

    @Override
    public void deshabilitarCliente(Integer id) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cliente no encontrado"));

        cliente.setActivo(false);

        clienteRepository.save(cliente);
    }

    @Override
    public void reactivarCliente(Integer id) {

        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cliente no encontrado"));

        cliente.setActivo(true);

        clienteRepository.save(cliente);
    }
    @Override
    public ClienteResponseDTO obtenerPerfilPropio(Integer usuarioId) {
        Cliente cliente = clienteRepository.findByPersona_Usuario_IdUsuario(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado para el usuario autenticado"));
        return mapper.toResponseDTO(cliente);
    }

    @Override
    public ClienteDetalleResumenDTO obtenerResumenPropio(Integer usuarioId) {
        Cliente cliente = clienteRepository.findByPersona_Usuario_IdUsuario(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cliente no encontrado para el usuario autenticado"));
        return obtenerResumenCliente(cliente.getClienteId());
    }

    // ClienteServiceImpl.java — implementación
    @Override
    public Integer obtenerIdClientePorUsuario(Integer idUsuario) {
        return clienteRepository.findByUsuarioId(idUsuario)
                .map(Cliente::getClienteId)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado para el usuario: " + idUsuario));
    }
}