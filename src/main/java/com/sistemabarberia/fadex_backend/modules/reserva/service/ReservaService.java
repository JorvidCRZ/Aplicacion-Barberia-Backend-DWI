package com.sistemabarberia.fadex_backend.modules.reserva.service;

import com.sistemabarberia.fadex_backend.auth.security.service.CustomUserDetails;
import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.auth.usuario.service.UsuarioSecurityService;
import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.exception.ResourceNotFoundException;
import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;

import com.sistemabarberia.fadex_backend.modules.barbero.repository.BarberoRepository;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.recompensa.service.IRecompensaService;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Request.ReservaRequest;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Response.*;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.EstadoReserva;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.TipoReserva;
import com.sistemabarberia.fadex_backend.modules.reserva.mapper.ReservaMapper;
import com.sistemabarberia.fadex_backend.modules.reserva.repository.ReservaRepository;
import com.sistemabarberia.fadex_backend.modules.reserva.dto.Request.ActualizarEstadoReservaDTO;
import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import com.sistemabarberia.fadex_backend.modules.servicio.entity.Servicio;

import com.sistemabarberia.fadex_backend.modules.servicio.repository.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final BarberoRepository barberoRepository;
    private final ClienteRepository clienteRepository;
    private final ServicioRepository servicioRepository;
    private final ReservaMapper reservaMapper;
    private final UsuarioSecurityService securityService;
    private final IRecompensaService recompensaService;

    @Transactional
    public ReservaDTO crearReserva(ReservaRequest request) {

        Usuario usuario = securityService.getUsuarioLogueado();
        String rol = securityService.getRolePrincipal();
        System.out.println("ROL DEL USUARIO: " + rol);
        Barbero barbero;
        Cliente cliente;
        Servicio servicio = servicioRepository.findById(request.servicioId()).orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));

        switch (rol) {
            case "ROLE_admin" -> {
                cliente = clienteRepository.findById(request.clienteId()).orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
                barbero = barberoRepository.findById(request.barberoId()).orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado"));
            }
            case "ROLE_barbero" -> {
                barbero = barberoRepository.findByPersona_Usuario_IdUsuario(usuario.getIdUsuario()).orElseThrow(
                        () -> new ResourceNotFoundException("Barbero no encontrado")
                );
                cliente = clienteRepository.findById(request.clienteId()).orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
            }
            case "ROLE_cliente" -> {
                cliente = clienteRepository.findByPersona_Usuario_IdUsuario(usuario.getIdUsuario()).orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
                barbero = barberoRepository.findById(request.barberoId()).orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado"));
            }
            default -> {
                throw new BusinessException("no existe otro rol", HttpStatus.FORBIDDEN);
            }
        }
        LocalTime horaFin = request.horaInicio().plusMinutes(servicio.getDuracion());
        validarConflicto(barbero, request.fecha(), request.horaInicio(), horaFin);

        TipoReserva tipo = request.esGratis() ? TipoReserva.RESERVA_GRATIS : TipoReserva.RESERVA_VIRTUAL;
        if (request.esGratis()) {
            recompensaService.canjearCorteGratis(cliente.getClienteId());
        }
        Reserva reserva = crearReservaInterna(cliente, servicio, barbero, request.fecha(), request.horaInicio(), tipo, EstadoReserva.CONFIRMADA);
        //Reserva reserva = crearReservaInterna(cliente, servicio, barbero, request.fecha(), request.horaInicio(), TipoReserva.RESERVA_VIRTUAL, EstadoReserva.CONFIRMADA);
        return reservaMapper.toDto(reserva);
    }

    private Reserva crearReservaInterna(Cliente cliente,
                                        Servicio servicio,
                                        Barbero barbero,
                                        LocalDate fecha,
                                        LocalTime horaInicio,
                                        TipoReserva tipo,
                                        EstadoReserva estado
    ) {

        LocalTime horaFin = horaInicio.plusMinutes(servicio.getDuracion());
        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setBarbero(barbero);
        reserva.setFecha(fecha);
        reserva.setServicio(servicio);
        reserva.setHoraInicio(horaInicio);
        reserva.setTipoReserva(tipo);
        reserva.setHoraFin(horaFin);
        reserva.setTotal(tipo == TipoReserva.RESERVA_GRATIS ? BigDecimal.ZERO : servicio.getPrecio());
        reserva.setEstadoReserva(estado);
        return reservaRepository.save(reserva);
    }

    private void validarConflicto(Barbero barbero, LocalDate fecha,
                                  LocalTime horaInicio, LocalTime horaFin) {

        if (reservaRepository.existeConflicto(barbero.getBarberoId(), fecha, horaInicio, horaFin)) {
            throw new BusinessException("hay cruce de horario: el barbero " + barbero.getPersona().getNombre() + " esta ocupado", HttpStatus.BAD_REQUEST);
        }
    }


    public Page<ReservaDTO> listarReservasPorCliente(Usuario usuario, Pageable pageable) {
        System.out.println("USUARIO ID: " + usuario.getIdUsuario());

        Cliente cliente = clienteRepository
                .findByPersona_Usuario_IdUsuario(usuario.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        System.out.println("CLIENTE ID: " + cliente.getClienteId());


        Page<Reserva> reservasPage = reservaRepository.findByCliente_ClienteId(
                cliente.getClienteId(),
                pageable
        );




        return reservasPage.map(reservaMapper::toDto);
    }

    public Page<ReservaDTO> listarReservasAdmin(Pageable pageable) {

        return reservaRepository.findAll(pageable)
                .map(reservaMapper::toDto);
    }

    public List<ReservaDTO> ListarReservasBarbero(Usuario usuario) {
        Barbero barbero = barberoRepository.findByPersonaUsuario(usuario).orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado"));

        return reservaRepository.findByBarbero_BarberoId(barbero.getBarberoId()).stream().map(reservaMapper::toDto).toList();
    }



    public ResumenDiarioDTO obtenerResumenDiario(Integer barberoId) {

        LocalDate inicio = LocalDate.now();
        LocalDate fin = inicio;

        List<Reserva> reservas = reservaRepository
                .findReservasDiarias(barberoId, inicio, fin);

        List<ReservaDTO> dtos = reservas.stream()
                .map(reservaMapper::toDto)
                .collect(Collectors.toList());

        return ResumenDiarioDTO.builder()
                .totalDia(dtos.size())
                .enEspera(contar(reservas, EstadoReserva.PENDIENTE_PAGO))
                .enProceso(contar(reservas, EstadoReserva.EN_PROCESO))
                .completados(contar(reservas, EstadoReserva.FINALIZADA))
                .reservas(dtos)
                .build();
    }


    @Transactional
    public ReservaDTO iniciarAtencion(Long reservaId) {
        Reserva reserva = buscarOFallar(reservaId);

        if (reserva.getEstadoReserva() != EstadoReserva.PENDIENTE_PAGO) {
            throw new IllegalStateException(
                    "La reserva debe estar en PENDIENTE. Estado actual: "
                            + reserva.getEstadoReserva()
            );
        }

        reserva.setEstadoReserva(EstadoReserva.EN_PROCESO);
        reserva.setHoraInicio(LocalTime.now());
        return reservaMapper.toDto(reservaRepository.save(reserva));
    }


    @Transactional
    public ReservaDTO finalizarAtencion(Long reservaId) {
        Reserva reserva = buscarOFallar(reservaId);

        if (reserva.getEstadoReserva() != EstadoReserva.EN_PROCESO) {
            throw new IllegalStateException(
                    "La reserva debe estar EN_PROCESO. Estado actual: "
                            + reserva.getEstadoReserva()
            );
        }

        reserva.setEstadoReserva(EstadoReserva.FINALIZADA);
        reserva.setHoraFin(LocalTime.now());
        Reserva guardada = reservaRepository.save(reserva);

        return reservaMapper.toDto(guardada);
    }


    public ResumenSemanalDTO obtenerResumenSemanal(Integer barberoId) {

        LocalDate hoy = LocalDate.now();

        Barbero barbero = barberoRepository.findById(barberoId)
                .orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado"));

        List<ResumenSemanalDTO.DiaSemana> dias = hoy.minusDays(6)
                .datesUntil(hoy.plusDays(1))
                .map(fecha -> {

                    List<Reserva> reservasDia = reservaRepository
                            .findByBarberoIdAndFechaBetween(
                                    barberoId,
                                    fecha,
                                    fecha
                            );

                    long atendidos = contar(reservasDia, EstadoReserva.FINALIZADA);

                    long cancelados = contar(reservasDia, EstadoReserva.CANCELADA);

                    BigDecimal totalIngresos = reservasDia.stream()
                            .filter(r -> r.getEstadoReserva() == EstadoReserva.FINALIZADA)
                            .map(Reserva::getTotal)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new ResumenSemanalDTO.DiaSemana(
                            fecha.toString(),
                            atendidos,
                            cancelados,
                            totalIngresos
                    );
                })
                .collect(Collectors.toList());

        BigDecimal ingresosSemana = dias.stream()
                .map(ResumenSemanalDTO.DiaSemana::getTotalIngresos)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal sueldoBase = barbero.getSueldo();

        BigDecimal comisionSemanal = ingresosSemana.multiply(
                barbero.getComision().divide(BigDecimal.valueOf(100))
        );

        BigDecimal totalSemana = sueldoBase.add(comisionSemanal);

        return ResumenSemanalDTO.builder()
                .dias(dias)
                .sueldoBase(sueldoBase)
                .comisionSemanal(comisionSemanal)
                .totalSemana(totalSemana)
                .build();
    }

    private Reserva buscarOFallar(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Reserva no encontrada: " + id)
                );
    }

    private long contar(List<Reserva> lista, EstadoReserva estado) {
        return lista.stream()
                .filter(r -> r.getEstadoReserva() == estado)
                .count();
    }

    @Transactional
    public ReservaDTO cancelarReserva(Long reservaId) {
        Reserva reserva = buscarOFallar(reservaId);

        if (reserva.getEstadoReserva() == EstadoReserva.FINALIZADA) {
            throw new IllegalStateException(
                    "No se puede cancelar una reserva COMPLETADA."
            );
        }

        if (reserva.getEstadoReserva() == EstadoReserva.FINALIZADA) {
            throw new IllegalStateException(
                    "La reserva ya está CANCELADA."
            );
        }

        reserva.setEstadoReserva(EstadoReserva.CANCELADA);
        return reservaMapper.toDto(reservaRepository.save(reserva));
    }

    @Transactional(readOnly = true)
    public List<CitaBarberoResponseDTO> obtenerCitasHoy() {

        Usuario usuario = securityService.getUsuarioLogueado();
        String rol = securityService.getRolePrincipal();

        List<Reserva> reservas;

        if ("ROLE_admin".equals(rol)) {

            reservas = reservaRepository.findByFechaOrderByHoraInicioAsc(LocalDate.now());

        } else if ("ROLE_barbero".equals(rol)) {

            reservas = reservaRepository
                    .findByBarberoUsernameAndFecha(usuario.getUser(), LocalDate.now());

        } else {
            throw new BusinessException(
                    "Acceso denegado",
                    HttpStatus.FORBIDDEN
            );
        }

        return reservas.stream()
                .map(this::mapToCitaBarberoDTO)
                .toList();
    }

    @Transactional
    public CitaBarberoResponseDTO actualizarEstadoReserva(Long idReserva,
                                                          ActualizarEstadoReservaDTO dto) {
        if (!securityService.isBarbero()) {
            throw new BusinessException("Acceso denegado: se requiere rol barbero", HttpStatus.FORBIDDEN);
        }

        Usuario usuario = securityService.getUsuarioLogueado();
        Reserva reserva = reservaRepository
                .findByIdAndBarberoUsername(idReserva, usuario.getUser())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Reserva no encontrada o no pertenece a este barbero"));

        reserva.setEstadoReserva(dto.getEstado());
        return mapToCitaBarberoDTO(reservaRepository.save(reserva));
    }


    private CitaBarberoResponseDTO mapToCitaBarberoDTO(Reserva reserva) {
        String nombre = "", apellido = "", telefono = "";

        if (reserva.getCliente() != null && reserva.getCliente().getPersona() != null) {
            Persona p = reserva.getCliente().getPersona();
            nombre   = p.getNombre();
            apellido = p.getApellido();
            telefono = p.getTelefono();
        }

        String nombreServicio = "";
        Double precioServicio = 0.0;
        if (reserva.getServicio() != null) {
            nombreServicio = reserva.getServicio().getNombre();
            precioServicio = reserva.getServicio().getPrecio() != null
                    ? reserva.getServicio().getPrecio().doubleValue()
                    : 0.0;
        }

        List<DetalleReservaDTO> servicios = List.of(
                DetalleReservaDTO.builder()
                        .nombreCorte(nombreServicio)
                        .precio(precioServicio)
                        .build()
        );

        return CitaBarberoResponseDTO.builder()
                .idReserva(reserva.getId())
                .nombreCliente(nombre)
                .apellidoCliente(apellido)
                .telefonoCliente(telefono)
                .fecha(reserva.getFecha())
                .horaInicio(reserva.getHoraInicio())
                .estado(reserva.getEstadoReserva())
                .tipoReserva(reserva.getTipoReserva())
                .servicios(servicios)
                .build();
    }

    public List<ReservaPendienteDTO> obtenerReservasPendientesDePago() {
        List<Reserva> reservasSinPago = reservaRepository.findReservasSinPago();

        return reservasSinPago.stream().map(r -> {
            String nombreServicio = r.getServicio() != null ? r.getServicio().getNombre() : "Servicio general";

            return ReservaPendienteDTO.builder()
                    .id(r.getId())
                    .clienteId(r.getCliente().getClienteId())
                    .clienteNombre(r.getCliente().getPersona().getNombre())
                    .clienteApellido(r.getCliente().getPersona().getApellido())
                    .barberoId(r.getBarbero().getBarberoId())
                    .barberoNombre(r.getBarbero().getPersona().getNombre())
                    .montoTotal(r.getTotal())
                    .servicios(List.of(nombreServicio))
                    .build();
        }).collect(Collectors.toList());
    }

}
