package com.sistemabarberia.fadex_backend.modules.reclamo.service.impl;

import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.auth.usuario.Repository.UsuarioRepository;
import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.exception.ResourceNotFoundException;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.commons.storage.FileStorageService;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.ReclamoEmailDTO;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.ReclamoFiltro;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.ReclamoResumen;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.request.ReclamoPublicoRequest;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.request.ReclamoRequest;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.request.ReclamoSolucionRequest;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.response.ReclamoAdjuntoResponse;
import com.sistemabarberia.fadex_backend.modules.reclamo.dto.response.ReclamoResponse;
import com.sistemabarberia.fadex_backend.modules.reclamo.entity.Reclamo;
import com.sistemabarberia.fadex_backend.modules.reclamo.entity.ReclamoAdjunto;
import com.sistemabarberia.fadex_backend.modules.reclamo.entity.enums.EstadoReclamo;
import com.sistemabarberia.fadex_backend.modules.reclamo.mapper.ReclamoMapper;
import com.sistemabarberia.fadex_backend.modules.reclamo.repository.ReclamoAdjuntoRepository;
import com.sistemabarberia.fadex_backend.modules.reclamo.repository.ReclamoRepository;
import com.sistemabarberia.fadex_backend.modules.reclamo.service.IReclamoEmailService;
import com.sistemabarberia.fadex_backend.modules.reclamo.service.IReclamoService;
import com.sistemabarberia.fadex_backend.modules.reclamo.specs.ReclamoSpecification;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.reserva.repository.ReservaRepository;
import com.sistemabarberia.fadex_backend.modules.venta.entity.Venta;
import com.sistemabarberia.fadex_backend.modules.venta.repository.VentaRepository;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReclamoService implements IReclamoService {
    private final ReclamoRepository reclamoRepository;
    private final ReclamoAdjuntoRepository reclamoAdjuntoRepository;
    private final ClienteRepository clienteRepository;
    private final UsuarioRepository usuarioRepository;
    private final ReservaRepository reservaRepository;
    private final VentaRepository ventaRepository;
    private final IReclamoEmailService emailService;
    private final ReclamoMapper reclamoMapper;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public ReclamoResponse crearReclamo(ReclamoRequest request, List<MultipartFile> archivos) {
        if (request.getIdVenta() != null && request.getIdReserva() != null) {
            throw new BusinessException("Debe seleccionar una venta o una reserva.", HttpStatus.BAD_REQUEST);
        }
        Cliente cliente = obtenerCliente(request.getIdCliente());
        Venta venta = obtenerVenta(request.getIdVenta());
        Reserva reserva = obtenerReserva(request.getIdReserva());
        if (cliente != null && venta != null && !venta.getCliente().getClienteId().equals(cliente.getClienteId())) {
            throw new BusinessException("La venta no pertenece al cliente.", HttpStatus.BAD_REQUEST);
        }
        if (cliente != null && reserva != null && !reserva.getCliente().getClienteId().equals(cliente.getClienteId())) {
            throw new BusinessException("La reserva no pertenece al cliente.", HttpStatus.BAD_REQUEST);
        }
        Usuario responsable = obtenerUsuario(request.getIdUsuarioResponsable());
        Reclamo reclamo = Reclamo.builder()
                .numeroReclamo(generarNumeroReclamo()).cliente(cliente).venta(venta).reserva(reserva).usuarioResponsable(responsable)
                .esPublico(false).nombreCliente(request.getNombreCliente()).correoCliente(request.getCorreoCliente()).telefonoCliente(request.getTelefonoCliente())
                .tipoDocumentoCliente(request.getTipoDocumentoCliente()).numeroDocumentoCliente(request.getNumeroDocumentoCliente())
                .tipoReclamacion(request.getTipoReclamacion()).tipoProblema(request.getTipoProblema()).causaReclamo(request.getCausaReclamo())
                .descripcion(request.getDescripcion()).notasInternas(request.getNotasInternas()).montoReclamado(request.getMontoReclamado())
                .fechaOcurrencia(request.getFechaOcurrencia()).build();
        reclamo = reclamoRepository.save(reclamo);
        guardarAdjuntos(reclamo, archivos);
        enviarCorreoConfirmacion(reclamo);
        return reclamoMapper.toResponse(reclamo);
    }

    @Override
    @Transactional
    public ReclamoResponse crearReclamoPublico(ReclamoPublicoRequest request, List<MultipartFile> archivos) {
        if (request.getIdVenta() != null && request.getIdReserva() != null) {
            throw new BusinessException("Debe seleccionar una venta o una reserva.", HttpStatus.BAD_REQUEST);
        }
        Venta venta = obtenerVenta(request.getIdVenta());
        Reserva reserva = obtenerReserva(request.getIdReserva());
        Reclamo reclamo = Reclamo.builder()
                .numeroReclamo(generarNumeroReclamo()).esPublico(true).usuarioResponsable(null).cliente(null).venta(venta).reserva(reserva)
                .nombreCliente(request.getNombres().trim() + " " + request.getApellidos().trim()).correoCliente(request.getEmail()).telefonoCliente(request.getTelefono())
                .tipoDocumentoCliente(request.getTipoDocumento()).numeroDocumentoCliente(request.getNumeroDocumento()).tipoReclamacion(request.getTipoReclamacion())
                .tipoProblema(request.getTipoProblema()).descripcion(request.getDescripcion()).montoReclamado(request.getMontoReclamado())
                .fechaOcurrencia(request.getFechaOcurrencia()).build();
        reclamo = reclamoRepository.save(reclamo);
        guardarAdjuntos(reclamo, archivos);
        enviarCorreoConfirmacion(reclamo);
        return reclamoMapper.toResponse(reclamo);
    }

    @Override
    @Transactional(readOnly = true)
    public ReclamoResponse obtenerReclamoPorId(Long id) {
        Reclamo reclamo = reclamoRepository.findByIdReclamo(id).orElseThrow(() -> new ResourceNotFoundException("Reclamo no encontrado"));
        return reclamoMapper.toDetalleResponse(reclamo);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ReclamoResponse> listarReclamoFiltros(ReclamoFiltro filtro, Pageable pageable) {
        Page<ReclamoResponse> page = reclamoRepository.findAll(ReclamoSpecification.filtrar(filtro), pageable).map(reclamoMapper::toResponse);
        return PageResponse.of(page);
    }

    @Override
    @Transactional
    public ReclamoResponse actualizarReclamoSolucion(Long id, ReclamoSolucionRequest request) {
        Reclamo reclamo = reclamoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Reclamo no encontrado"));
        if (reclamo.getEstadoReclamo() == EstadoReclamo.CERRADO || reclamo.getEstadoReclamo() == EstadoReclamo.ANULADO) {
            throw new BusinessException("El reclamo ya no puede ser modificado.", HttpStatus.BAD_REQUEST);
        }
        if ((request.getEstadoReclamo() == EstadoReclamo.RESUELTO || request.getEstadoReclamo() == EstadoReclamo.CERRADO) && request.getSolucionReclamo() == null) {
            throw new BusinessException("Debe indicar una solución para finalizar el reclamo.", HttpStatus.BAD_REQUEST);
        }
        reclamo.setEstadoReclamo(request.getEstadoReclamo());
        reclamo.setSolucionReclamo(request.getSolucionReclamo());
        if (request.getNotasInternas() != null) {
            reclamo.setNotasInternas(request.getNotasInternas());
        }
        if (request.getMontoCompensado() != null) {
            if (request.getMontoCompensado().signum() < 0) {
                throw new BusinessException("El monto compensado no puede ser negativo.", HttpStatus.BAD_REQUEST);
            }
            reclamo.setMontoCompensado(request.getMontoCompensado());
        }
        if (request.getEstadoReclamo() == EstadoReclamo.RESUELTO || request.getEstadoReclamo() == EstadoReclamo.CERRADO) {
            reclamo.setFechaResolucion(LocalDateTime.now());
        }
        reclamo = reclamoRepository.save(reclamo);

        if (reclamo.getCorreoCliente() != null && !reclamo.getCorreoCliente().isBlank()) {
            ReclamoEmailDTO dto = ReclamoEmailDTO.builder()
                    .nombreCliente(reclamo.getNombreCliente())
                    .numeroReclamo(reclamo.getNumeroReclamo())
                    .tipoReclamacion(reclamo.getTipoReclamacion() != null ? reclamo.getTipoReclamacion().name() : null)
                    .tipoProblema(reclamo.getTipoProblema() != null ? reclamo.getTipoProblema().name() : null)
                    .estado(reclamo.getEstadoReclamo().name())
                    .fechaReclamo(reclamo.getFechaReclamo())
                    .build();

            emailService.enviarCambioEstado(reclamo.getCorreoCliente(), dto);
        }
        return reclamoMapper.toResponse(reclamo);
    }

    @Override
    @Transactional
    public ReclamoResumen obtenerReclamoResumen() {
        return ReclamoResumen.builder()
                .abiertos(reclamoRepository.countByEstadoReclamo(EstadoReclamo.ABIERTO))
                .enProceso(reclamoRepository.countByEstadoReclamo(EstadoReclamo.EN_PROCESO))
                .resueltos(reclamoRepository.countByEstadoReclamo(EstadoReclamo.RESUELTO))
                .cerrados(reclamoRepository.countByEstadoReclamo(EstadoReclamo.CERRADO))
                .anulados(reclamoRepository.countByEstadoReclamo(EstadoReclamo.ANULADO))
                .total(reclamoRepository.count())
                .build();
    }

    @Override
    public void eliminarReclamo(Long id) {
        Reclamo reclamo = reclamoRepository.findByIdReclamo(id).orElseThrow(() -> new ResourceNotFoundException("Reclamo no encontrado"));
        for (ReclamoAdjunto adjunto : reclamo.getAdjuntos()) {
            fileStorageService.eliminarArchivo(adjunto.getNombreArchivo());
        }
        reclamoRepository.delete(reclamo);
    }

    private String generarNumeroReclamo() {
        LocalDate fecha = LocalDate.now();
        long correlativo = reclamoRepository.nextSecuenciaReclamo();
        return "REC-%s-%04d".formatted(fecha.format(DateTimeFormatter.BASIC_ISO_DATE), correlativo);
    }

    private ReclamoResponse mapToResponse(Reclamo reclamo) {
        return ReclamoResponse.builder()
                .idReclamo(reclamo.getIdReclamo()).numeroReclamo(reclamo.getNumeroReclamo()).nombreCliente(reclamo.getNombreCliente())
                .correoCliente(reclamo.getCorreoCliente()).telefonoCliente(reclamo.getTelefonoCliente()).tipoReclamacion(reclamo.getTipoReclamacion())
                .tipoProblema(reclamo.getTipoProblema()).causaReclamo(reclamo.getCausaReclamo()).estadoReclamo(reclamo.getEstadoReclamo())
                .solucionReclamo(reclamo.getSolucionReclamo()).descripcion(reclamo.getDescripcion()).notasInternas(reclamo.getNotasInternas())
                .montoReclamado(reclamo.getMontoReclamado()).montoCompensado(reclamo.getMontoCompensado()).fechaOcurrencia(reclamo.getFechaOcurrencia())
                .fechaReclamo(reclamo.getFechaReclamo()).fechaResolucion(reclamo.getFechaResolucion()).esPublico(reclamo.isEsPublico()).adjuntos(null).build();
    }
    private void guardarAdjuntos(Reclamo reclamo, List<MultipartFile> archivos) {
        if (archivos == null || archivos.isEmpty()) { return;}
        for (MultipartFile archivo : archivos) {
            String ruta = fileStorageService.guardarArchivo(archivo, "reclamos", List.of("image/jpeg", "image/png", "application/pdf"));
            ReclamoAdjunto adjunto = ReclamoAdjunto.builder().reclamo(reclamo).nombreOriginal(archivo.getOriginalFilename()).nombreArchivo(ruta)
                    .urlArchivo(ruta).mimeType(archivo.getContentType()).pesoBytes(archivo.getSize()).build();
            reclamo.getAdjuntos().add(adjunto);
            reclamoAdjuntoRepository.save(adjunto);
        }
    }

    //validaciones
    private Cliente obtenerCliente(Integer idCliente) {
        if (idCliente == null) {return null;}
        return clienteRepository.findById(idCliente).orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
    }
    private Venta obtenerVenta(Integer idVenta) {
        if (idVenta == null) {return null;}
        return ventaRepository.findById(idVenta).orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));
    }
    private Reserva obtenerReserva(Integer idReserva) {
        if (idReserva == null) {return null;}
        return reservaRepository.findById(idReserva.longValue()).orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
    }
    private Usuario obtenerUsuario(Integer idUsuario) {
        if (idUsuario == null) {return null;}
        return usuarioRepository.findById(idUsuario).orElseThrow(() -> new ResourceNotFoundException("Usuario responsable no encontrado"));
    }
    private void enviarCorreoConfirmacion(Reclamo reclamo) {
        if (reclamo.getCorreoCliente() == null || reclamo.getCorreoCliente().isBlank()) return;
        ReclamoEmailDTO dto = ReclamoEmailDTO.builder().nombreCliente(reclamo.getNombreCliente()).numeroReclamo(reclamo.getNumeroReclamo())
                .tipoReclamacion(reclamo.getTipoReclamacion() != null ? reclamo.getTipoReclamacion().name() : null)
                .tipoProblema(reclamo.getTipoProblema() != null ? reclamo.getTipoProblema().name() : null)
                .estado(reclamo.getEstadoReclamo().name()).fechaReclamo(reclamo.getFechaReclamo()).build();
        emailService.enviarConfirmacionCliente(reclamo.getCorreoCliente(), dto);
    }
}
