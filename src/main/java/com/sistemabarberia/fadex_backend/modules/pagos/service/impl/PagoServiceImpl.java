package com.sistemabarberia.fadex_backend.modules.pagos.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.exception.ResourceNotFoundException;
import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;
import com.sistemabarberia.fadex_backend.modules.barbero.repository.BarberoRepository;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.pagos.dto.request.PagoRequestDTO;
import com.sistemabarberia.fadex_backend.modules.pagos.dto.response.HistorialPagoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.pagos.dto.response.PagoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.pagos.entity.HistorialPago;
import com.sistemabarberia.fadex_backend.modules.pagos.entity.Pago;
import com.sistemabarberia.fadex_backend.modules.pagos.mapper.HistorialPagoMapper;
import com.sistemabarberia.fadex_backend.modules.pagos.mapper.PagoMapper;
import com.sistemabarberia.fadex_backend.modules.pagos.repository.HistorialPagoRepository;
import com.sistemabarberia.fadex_backend.modules.pagos.repository.PagoRepository;
import com.sistemabarberia.fadex_backend.modules.pagos.service.IPagoService;
import com.sistemabarberia.fadex_backend.modules.recompensa.service.IRecompensaService;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.EstadoReserva;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.TipoReserva;
import com.sistemabarberia.fadex_backend.modules.reserva.repository.ReservaRepository;
import com.sistemabarberia.fadex_backend.modules.venta.entity.Venta;
import com.sistemabarberia.fadex_backend.modules.venta.repository.VentaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PagoServiceImpl implements IPagoService {

    private final PagoRepository pagoRepository;
    private final HistorialPagoRepository historialPagoRepository;

    private final ClienteRepository clienteRepository;
    private final BarberoRepository barberoRepository;
    private final ReservaRepository reservaRepository;
    private final VentaRepository ventaRepository;

    private final PagoMapper pagoMapper;
    private final HistorialPagoMapper historialPagoMapper;

    // ── NUEVO ──
    private final IRecompensaService recompensaService;

    @Override
    @Transactional
    public PagoResponseDTO crear(PagoRequestDTO dto) {

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        Barbero barbero = barberoRepository.findById(dto.getBarberoId())
                .orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado"));

        Reserva reserva = null;
        if (dto.getReservaId() != null) {
            reserva = reservaRepository.findById(dto.getReservaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
            if (!reserva.getCliente().getClienteId().equals(cliente.getClienteId())) {
                throw new BusinessException("La reserva no pertenece al cliente", HttpStatus.BAD_REQUEST);
            }
            if (!reserva.getBarbero().getBarberoId().equals(barbero.getBarberoId())) {
                throw new BusinessException("La reserva no pertenece al barbero", HttpStatus.BAD_REQUEST);
            }
            if (reserva.getEstadoReserva() == EstadoReserva.CANCELADA) {
                throw new BusinessException("No se puede pagar una reserva cancelada", HttpStatus.BAD_REQUEST);
            }
            if (pagoRepository.existsByReserva_Id(dto.getReservaId())) {
                throw new BusinessException("Esta reserva ya fue pagada", HttpStatus.BAD_REQUEST);
            }
        }

        Venta venta = null;

        if (dto.getMonto().compareTo(BigDecimal.ZERO) == 0) {
            if (reserva == null || reserva.getTipoReserva() != TipoReserva.RESERVA_GRATIS) {
                throw new BusinessException(
                        "Solo se puede registrar monto 0 en reservas de tipo GRATIS",
                        HttpStatus.BAD_REQUEST);
            }
        }

        if (dto.getVentaId() != null) {
            venta = ventaRepository.findById(dto.getVentaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));
        }

        Pago pago = new Pago();
        pago.setCliente(cliente);
        pago.setBarbero(barbero);
        pago.setReserva(reserva);
        pago.setVenta(venta);
        pago.setMonto(dto.getMonto());
        pago.setMetodo(dto.getMetodo());
        pago.setTipo(dto.getTipo());

        Pago pagoGuardado = pagoRepository.save(pago);

        HistorialPago historial = new HistorialPago();
        historial.setPago(pagoGuardado);
        historial.setCliente(cliente);
        historial.setFecha(LocalDateTime.now());
        historialPagoRepository.save(historial);

        // ── NUEVO: acumular corte si es reserva normal (no gratis) ──
        if (reserva != null && reserva.getTipoReserva() != TipoReserva.RESERVA_GRATIS) {
            recompensaService.acumularCorte(cliente.getClienteId());
        }

        return pagoMapper.toResponse(pagoGuardado);
    }

    @Override
    public List<PagoResponseDTO> listar() {
        return pagoMapper.toResponseList(pagoRepository.findAll());
    }

    @Override
    public List<PagoResponseDTO> listar(String cliente) {
        if (cliente != null && !cliente.isBlank()) {
            return pagoMapper.toResponseList(
                    pagoRepository.findByCliente_Persona_NombreContainingIgnoreCase(cliente)
            );
        }
        return listar();
    }

    @Override
    public PagoResponseDTO obtenerPorId(Long id) {
        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));
        return pagoMapper.toResponse(pago);
    }

    @Override
    @Transactional
    public PagoResponseDTO actualizar(Long id, PagoRequestDTO dto) {

        Pago pago = pagoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado"));

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        Barbero barbero = barberoRepository.findById(dto.getBarberoId())
                .orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado"));

        Reserva reserva = null;
        if (dto.getReservaId() != null) {
            reserva = reservaRepository.findById(dto.getReservaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));
        }

        Venta venta = null;
        if (dto.getVentaId() != null) {
            venta = ventaRepository.findById(dto.getVentaId())
                    .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));
        }

        pago.setCliente(cliente);
        pago.setBarbero(barbero);
        pago.setReserva(reserva);
        pago.setVenta(venta);
        pago.setMonto(dto.getMonto());
        pago.setMetodo(dto.getMetodo());
        pago.setTipo(dto.getTipo());

        Pago actualizado = pagoRepository.save(pago);

        HistorialPago historial = new HistorialPago();
        historial.setPago(actualizado);
        historial.setCliente(cliente);
        historial.setFecha(LocalDateTime.now());
        historialPagoRepository.save(historial);

        return pagoMapper.toResponse(actualizado);
    }

    @Override
    public void eliminar(Long id) {
        if (!pagoRepository.existsById(id)) {
            throw new ResourceNotFoundException("Pago no encontrado");
        }
        pagoRepository.deleteById(id);
    }

    @Override
    public List<HistorialPagoResponseDTO> listarHistorial(Long pagoId) {
        return historialPagoMapper.toResponseList(
                historialPagoRepository.findByPagoId(pagoId)
        );
    }
}