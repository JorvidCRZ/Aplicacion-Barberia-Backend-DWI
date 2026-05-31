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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements IVentaService {

    private final VentaRepository ventaRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final HistorialVentaRepository historialVentaRepository;

    private final ClienteRepository clienteRepository;
    private final BarberoRepository barberoRepository;

    private final VentaMapper ventaMapper;
    private final DetalleVentaMapper detalleVentaMapper;
    private final HistorialVentaMapper historialVentaMapper;

    @Override
    @Transactional
    public VentaResponseDTO crear(VentaRequestDTO dto) {

        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

//        Barbero barbero = barberoRepository.findById(dto.getBarberoId())
//                .orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado"));

        Venta venta = new Venta();

        venta.setCliente(cliente);
//        venta.setBarbero(barbero);
        venta.setFecha(dto.getFecha());
        venta.setTipoComprobante(dto.getTipoComprobante());

        List<DetalleVenta> detalles = dto.getDetalles()
                .stream()
                .map(detDto -> {

                    DetalleVenta detalle = detalleVentaMapper.toEntity(detDto);

                    detalle.setVenta(venta);

                    return detalle;

                }).toList();

        venta.setDetalles(detalles);

        Venta ventaGuardada = ventaRepository.save(venta);

        ventaGuardada = ventaRepository.findById(
                ventaGuardada.getVentaId()
        ).orElseThrow();

        // HISTORIAL
        HistorialVenta historial = new HistorialVenta();

        historial.setVenta(ventaGuardada);
        historial.setFecha(LocalDateTime.now());

        historialVentaRepository.save(historial);

        return ventaMapper.toResponse(ventaGuardada);

    }

    @Override
    public List<VentaResponseDTO> listar() {
        return ventaMapper.toResponseList(ventaRepository.findAll());
    }

    @Override
    public List<VentaResponseDTO> listar(String cliente) {
        // Si viene texto, filtramos
        if (cliente != null && !cliente.isEmpty()) {
            return ventaMapper.toResponseList(
                    ventaRepository.findByCliente_Persona_NombreContainingIgnoreCase(cliente)
            );
        }
        return listar();
    }

    @Override
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

//        Barbero barbero = barberoRepository.findById(dto.getBarberoId())
//                .orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado"));

        // ACTUALIZAR VENTA
        venta.setCliente(cliente);
//        venta.setBarbero(barbero);
        venta.setFecha(dto.getFecha());
        venta.setTipoComprobante(dto.getTipoComprobante());

        detalleVentaRepository.deleteAll(venta.getDetalles());

        // LIMPIAR
        venta.getDetalles().clear();

        for (DetalleVentaRequestDTO detDto : dto.getDetalles()) {

            DetalleVenta detalle = detalleVentaMapper.toEntity(detDto);

            detalle.setVenta(venta);

            venta.getDetalles().add(detalle);
        }

        Venta ventaActualizada = ventaRepository.save(venta);

        // GUARDAR
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
}