package com.sistemabarberia.fadex_backend.modules.venta.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.ResourceNotFoundException;
import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;
import com.sistemabarberia.fadex_backend.modules.barbero.repository.BarberoRepository;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.venta.dto.request.VentaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.venta.dto.request.DetalleVentaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.venta.dto.response.*;
import com.sistemabarberia.fadex_backend.modules.venta.entity.*;
import com.sistemabarberia.fadex_backend.modules.venta.mapper.VentaMapper;
import com.sistemabarberia.fadex_backend.modules.venta.mapper.DetalleVentaMapper;
import com.sistemabarberia.fadex_backend.modules.venta.mapper.HistorialVentaMapper;
import com.sistemabarberia.fadex_backend.modules.venta.repository.*;
import com.sistemabarberia.fadex_backend.modules.venta.service.IVentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements IVentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final HistorialVentaRepository historialVentaRepository;
    private final BarberoRepository barberoRepository;
    private final ClienteRepository clienteRepository;

    private final VentaMapper ventaMapper;
    private final DetalleVentaMapper detalleVentaMapper;
    private final HistorialVentaMapper historialVentaMapper;

    @Override
    @Transactional
    public VentaResponseDTO crear(VentaRequestDTO dto) {

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        Barbero barbero = null;

        if(dto.getBarberoId() != null){
            barbero = barberoRepository.findById(dto.getBarberoId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Barbero no encontrado"));
        }
        Venta venta = new Venta();
        venta.setBarbero(barbero);
        venta.setCliente(cliente);
        venta.setFecha(dto.getFecha());
        venta.setTipoComprobante(dto.getTipoComprobante());

        String nuevoCorrelativo = generarCorrelativo(venta.getFecha());
        venta.setNumeroCorrelativo(nuevoCorrelativo);

        List<DetalleVenta> detalles = dto.getDetalles()
                .stream()
                .map(detDto -> {
                    DetalleVenta detalle = detalleVentaMapper.toEntity(detDto);
                    detalle.setVenta(venta);
                    return detalle;
                }).toList();
        venta.setDetalles(detalles);

        Venta ventaGuardada = ventaRepository.save(venta);

        ventaGuardada = ventaRepository.findByIdWithDetalles(ventaGuardada.getVentaId());

        HistorialVenta historial = new HistorialVenta();
        historial.setVenta(ventaGuardada);
        historial.setFecha(LocalDateTime.now());
        historialVentaRepository.save(historial);

        return ventaMapper.toResponse(ventaGuardada);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> buscarConFiltros(String cliente, String numeroCorrelativo, String tipoComprobanteStr, String fechaInicioStr, String fechaFinStr) {

        LocalDateTime fechaInicio = null;
        LocalDateTime fechaFin = null;
        TipoComprobante comprobanteEnum = null;

        if (fechaInicioStr != null && !fechaInicioStr.isEmpty()) {
            fechaInicio = LocalDate.parse(fechaInicioStr.substring(0, 10)).atStartOfDay();
        }
        if (fechaFinStr != null && !fechaFinStr.isEmpty()) {
            fechaFin = LocalDate.parse(fechaFinStr.substring(0, 10)).atTime(23, 59, 59);
        }

        if (tipoComprobanteStr != null && !tipoComprobanteStr.isEmpty()) {
            try {
                comprobanteEnum = TipoComprobante.valueOf(tipoComprobanteStr.toUpperCase());
            } catch (IllegalArgumentException e) {
                comprobanteEnum = null;
            }
        }

        String clienteFiltro = (cliente != null && !cliente.trim().isEmpty()) ? cliente.trim() : null;
        String correlativoFiltro = (numeroCorrelativo != null && !numeroCorrelativo.trim().isEmpty()) ? numeroCorrelativo.trim() : null;

        boolean hasCliente = clienteFiltro != null;
        String valCliente = hasCliente ? clienteFiltro : "";

        boolean hasCorrelativo = correlativoFiltro != null;
        String valCorrelativo = hasCorrelativo ? correlativoFiltro : "";

        boolean hasComprobante = comprobanteEnum != null;
        TipoComprobante valComprobante = hasComprobante ? comprobanteEnum : TipoComprobante.BOLETA;

        boolean hasFechaInicio = fechaInicio != null;
        LocalDateTime valFechaInicio = hasFechaInicio ? fechaInicio : LocalDateTime.now();

        boolean hasFechaFin = fechaFin != null;
        LocalDateTime valFechaFin = hasFechaFin ? fechaFin : LocalDateTime.now();

        List<Venta> ventas = ventaRepository.buscarConFiltrosAvanzados(
                hasCliente, valCliente,
                hasCorrelativo, valCorrelativo,
                hasComprobante, valComprobante,
                hasFechaInicio, valFechaInicio,
                hasFechaFin, valFechaFin
        );

        return ventaMapper.toResponseList(ventas);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> listar() {
        return buscarConFiltros(null, null, null, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaResponseDTO> listar(String cliente) {
        return buscarConFiltros(cliente, null, null, null, null);
    }

    @Override
    @Transactional(readOnly = true)
    public VentaResponseDTO obtenerPorId(Integer id) {
        Venta venta = Optional.ofNullable(
                ventaRepository.findByIdWithDetalles(id)
        ).orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));
        return ventaMapper.toResponse(venta);
    }

    @Override
    @Transactional
    public VentaResponseDTO actualizar(Integer id, VentaRequestDTO dto) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venta no encontrada"));
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        venta.setCliente(cliente);
        venta.setFecha(dto.getFecha());
        venta.setTipoComprobante(dto.getTipoComprobante());

        detalleVentaRepository.deleteAll(venta.getDetalles());
        venta.getDetalles().clear();
        for (DetalleVentaRequestDTO detDto : dto.getDetalles()) {
            DetalleVenta detalle = detalleVentaMapper.toEntity(detDto);
            detalle.setVenta(venta);
            venta.getDetalles().add(detalle);
        }

        Venta ventaActualizada = ventaRepository.save(venta);

        ventaActualizada = ventaRepository.findByIdWithDetalles(ventaActualizada.getVentaId());

        HistorialVenta historial = new HistorialVenta();
        historial.setVenta(ventaActualizada);
        historial.setFecha(LocalDateTime.now());
        historialVentaRepository.save(historial);

        return ventaMapper.toResponse(ventaActualizada);
    }

    @Override
    public void eliminar(Integer id) {
        if (!ventaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Venta no encontrada");
        }
        ventaRepository.deleteById(id);
    }

    @Override
    public List<DetalleVentaResponseDTO> listarDetalles(Integer ventaId) {
        return detalleVentaMapper.toResponseList(
                detalleVentaRepository.findByVenta_VentaId(ventaId)
        );
    }

    @Override
    public List<HistorialVentaResponseDTO> listarHistorial(Integer ventaId) {
        return historialVentaMapper.toResponseList(
                historialVentaRepository.findByVenta_VentaId(ventaId)
        );
    }

    private String generarCorrelativo(LocalDateTime fecha) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMyyyy");
        String mesAnio = fecha.format(formatter);
        String prefijo = "VEN-" + mesAnio + "-";
        String ultimoCorrelativo = ventaRepository.findUltimoCorrelativoPorMesAnio(prefijo);
        int siguienteNumero = 1;
        if (ultimoCorrelativo != null && !ultimoCorrelativo.isEmpty()) {
            try {
                String strNumero = ultimoCorrelativo.substring(prefijo.length());
                siguienteNumero = Integer.parseInt(strNumero) + 1;
            } catch (NumberFormatException e) {
                siguienteNumero = 1;
            }
        }
        return prefijo + String.format("%04d", siguienteNumero);
    }
}