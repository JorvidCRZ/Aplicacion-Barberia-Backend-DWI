package com.sistemabarberia.fadex_backend.modules.barbero.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.exception.ResourceNotFoundException;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.request.BarberoRequestDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.request.BarberoUpdateRequestDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.response.BarberoDetalleResponseDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.response.BarberoResponseDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.response.ResumenBarberoDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.dto.response.ResumenIndividualBarberoDTO;
import com.sistemabarberia.fadex_backend.modules.barbero.entity.Barbero;
import com.sistemabarberia.fadex_backend.modules.barbero.mapper.BarberoMapper;
import com.sistemabarberia.fadex_backend.modules.barbero.repository.BarberoRepository;
import com.sistemabarberia.fadex_backend.modules.barbero.service.IBarberoService;
import com.sistemabarberia.fadex_backend.modules.cliente.repository.ClienteRepository;
import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import com.sistemabarberia.fadex_backend.modules.persona.repository.PersonaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class BarberoServiceImpl implements IBarberoService {

    private static final Set<String> CAMPOS_ORDENABLES = new HashSet<>(
            Arrays.asList("fechaIngreso", "experiencia", "sueldo", "comision")
    );

    @Autowired
    private BarberoRepository barberoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private BarberoMapper mapper;

    // ─── CRUD BÁSICO ──────────────────────────────────────────────────────────

    @Override
    public Page<BarberoResponseDTO> listarBarberos(Pageable pageable) {
        return barberoRepository.findByActivoTrue(pageable)
                .map(mapper::toResponseDTO);
    }

    @Override
    public Page<BarberoResponseDTO> listarBarberosInhabilitados(Pageable pageable) {

        return barberoRepository.findByActivoFalse(pageable)
                .map(mapper::toResponseDTO);

    }

    @Override
    public BarberoResponseDTO crearBarbero(BarberoRequestDTO dto) {
        Persona persona = personaRepository.findById(dto.getPersonaId())
                .orElseThrow(() -> new BusinessException(
                        "Persona no encontrada con id: " + dto.getPersonaId(),
                        HttpStatus.NOT_FOUND
                ));

        if (barberoRepository.existsByPersona_PersonaId(dto.getPersonaId())) {
            throw new BusinessException(
                    "Esta persona ya está registrada como barbero",
                    HttpStatus.CONFLICT
            );
        }

        if (clienteRepository.existsByPersona_PersonaId(dto.getPersonaId())) {
            throw new BusinessException(
                    "Esta persona ya está registrada como Cliente",
                    HttpStatus.CONFLICT
            );
        }

        Barbero barbero = mapper.toEntity(dto, persona);
        barbero.setActivo(true); // ── NUEVO ──
        return mapper.toResponseDTO(barberoRepository.save(barbero));
    }

    @Override
    public BarberoResponseDTO eliminar(Integer id) {
        Barbero barbero = barberoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Barbero no encontrado con id: " + id,
                        HttpStatus.NOT_FOUND
                ));
        BarberoResponseDTO dto = mapper.toResponseDTO(barbero);
        personaRepository.deleteById(barbero.getPersona().getPersonaId());
        return dto;
    }

    @Override
    public BarberoResponseDTO actualizarBarbero(Integer id, BarberoUpdateRequestDTO dto) {
        Barbero barbero = barberoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Barbero no encontrado con id: " + id,
                        HttpStatus.NOT_FOUND
                ));

        if (dto.getExperiencia() != null) barbero.setExperiencia(dto.getExperiencia());
        if (dto.getSueldo() != null)      barbero.setSueldo(dto.getSueldo());
        if (dto.getComision() != null)    barbero.setComision(dto.getComision());
        if (dto.getDescripcion() != null) barbero.setDescripcion(dto.getDescripcion());
        if (dto.getFotoUrl() != null)     barbero.setFotoUrl(dto.getFotoUrl());
        barbero.setOcupado(dto.isOcupado());

        return mapper.toResponseDTO(barberoRepository.save(barbero));
    }

    @Override
    public BarberoResponseDTO buscarBarbero(Integer id) {
        return mapper.toResponseDTO(
                barberoRepository.findById(id)
                        .orElseThrow(() -> new BusinessException(
                                "Barbero no encontrado con id: " + id,
                                HttpStatus.NOT_FOUND
                        ))
        );
    }

    // ─── BÚSQUEDA COMBINADA ───────────────────────────────────────────────────

    @Override
    public Page<BarberoResponseDTO> buscar(String estado, String ordenarPor, String direccion, Pageable pageable) {
        Pageable pageableFinal = construirPageable(pageable, ordenarPor, direccion);

        boolean filtrarEstado = estado != null
                && !estado.isBlank()
                && !estado.equalsIgnoreCase("todos");

        if (filtrarEstado) {
            boolean ocupado = estado.equalsIgnoreCase("ocupado");
            return barberoRepository
                    .findByActivoTrueAndOcupado(ocupado, pageableFinal)
                    .map(mapper::toResponseDTO);
        }

        return barberoRepository.findByActivoTrue(pageableFinal)
                .map(mapper::toResponseDTO);
    }

    // ─── RESUMEN DASHBOARD ────────────────────────────────────────────────────

    @Override
    public ResumenBarberoDTO obtenerResumen() {

        // 1. Conteos de barberos
        long total       = barberoRepository.count();
        long disponibles = barberoRepository.countByOcupado(false);
        long ocupados    = barberoRepository.countByOcupado(true);

        // 2. Ventas hoy y ayer
        BigDecimal ventasHoy  = barberoRepository.getTotalVentasHoy();
        BigDecimal ventasAyer = barberoRepository.getTotalVentasAyer();

        // 3. Calcular porcentaje vs ayer: "+12% vs ayer" o "-5% vs ayer"
        String porcentajeVsAyer = calcularPorcentaje(ventasHoy, ventasAyer);

        // 4. Mejor barbero del mes
        String mejorNombre        = "Sin datos";
        BigDecimal mejorTotal     = BigDecimal.ZERO;

        Optional<Object[]> mejor = barberoRepository.findMejorBarberoDelMes();
        if (mejor.isPresent()) {
            Object raw = mejor.get();

            // Hibernate a veces anida: Object[] → Object[] → [nombre, total]
            if (raw instanceof Object[] outer && outer.length > 0 && outer[0] instanceof Object[]) {
                raw = outer[0]; // desenvuelve el nivel extra
            }

            Object[] row = (raw instanceof Object[]) ? (Object[]) raw : new Object[]{raw};
            mejorNombre  = row.length > 0 && row[0] != null ? row[0].toString() : "Sin datos";
            mejorTotal   = row.length > 1 && row[1] != null
                    ? new BigDecimal(row[1].toString())
                    : BigDecimal.ZERO;
        }

        return ResumenBarberoDTO.builder()
                .totalBarberos(total)
                .disponibles(disponibles)
                .ocupados(ocupados)
                .ventasHoy(ventasHoy)
                .porcentajeVsAyer(porcentajeVsAyer)
                .mejorDelMes(mejorNombre)
                .totalGeneradoMejor(mejorTotal)
                .build();
    }

    // ─── HELPERS PRIVADOS ─────────────────────────────────────────────────────

    private String calcularPorcentaje(BigDecimal hoy, BigDecimal ayer) {
        if (ayer == null || ayer.compareTo(BigDecimal.ZERO) == 0) {
            return hoy != null && hoy.compareTo(BigDecimal.ZERO) > 0
                    ? "+100% vs ayer"
                    : "0% vs ayer";
        }
        if (hoy == null) hoy = BigDecimal.ZERO;

        BigDecimal diferencia = hoy.subtract(ayer);
        BigDecimal porcentaje = diferencia
                .divide(ayer, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(0, RoundingMode.HALF_UP);

        return (porcentaje.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "")
                + porcentaje + "% vs ayer";
    }

    private Pageable construirPageable(Pageable pageable, String ordenarPor, String direccion) {
        boolean hayOrden = ordenarPor != null && !ordenarPor.isBlank();

        if (!hayOrden) return pageable;

        if (!CAMPOS_ORDENABLES.contains(ordenarPor)) {
            throw new BusinessException(
                    "Campo de ordenamiento inválido: '" + ordenarPor +
                            "'. Valores permitidos: fechaIngreso, experiencia, sueldo, comision",
                    HttpStatus.BAD_REQUEST
            );
        }

        Sort.Direction dir = "desc".equalsIgnoreCase(direccion)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(dir, ordenarPor)
        );
    }


    @Override
    public Page<BarberoResponseDTO> buscarPorNombre(String termino, Pageable pageable) {
        if (termino == null || termino.isBlank()) {
            return barberoRepository.findAll(pageable)
                    .map(mapper::toResponseDTO);
        }
        return barberoRepository.buscarPorNombreOApellido(termino.trim(), pageable)
                .map(mapper::toResponseDTO);
    }

    @Override
    public ResumenIndividualBarberoDTO obtenerResumenIndividual(Integer id) {
        Barbero barbero = barberoRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        "Barbero no encontrado con id: " + id,
                        HttpStatus.NOT_FOUND
                ));

        long cortesEsteMes       = barberoRepository.countCortesEsteMesByBarbero(id);
        BigDecimal ingresos      = barberoRepository.getIngresosEsteMesByBarbero(id);
        long reservasHoy         = barberoRepository.countReservasHoyByBarbero(id);

        // comisión = ingresos * (comision% / 100)
        BigDecimal comisionGanada = BigDecimal.ZERO;
        if (barbero.getComision() != null && ingresos.compareTo(BigDecimal.ZERO) > 0) {
            comisionGanada = ingresos
                    .multiply(barbero.getComision())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        }

        String nombre = barbero.getPersona().getNombre() + " " + barbero.getPersona().getApellido();

        return ResumenIndividualBarberoDTO.builder()
                .nombreBarbero(nombre)
                .cortesEsteMes(cortesEsteMes)
                .ingresosGenerados(ingresos)
                .comisionGanada(comisionGanada)
                .reservasHoy(reservasHoy)
                .build();
    }

    @Override
    public void deshabilitarBarbero(Integer id) {

        Barbero barbero = barberoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Barbero no encontrado"));

        barbero.setActivo(false);

        barberoRepository.save(barbero);
    }

    @Override
    public void reactivarBarbero(Integer id) {

        Barbero barbero = barberoRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Barbero no encontrado"));

        barbero.setActivo(true);

        barberoRepository.save(barbero);
    }
    @Override
    public BarberoDetalleResponseDTO obtenerPerfilPropio(Integer usuarioId) {
        Barbero barbero = barberoRepository.findByPersona_Usuario_IdUsuario(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado"));
        return mapper.toDetalleResponseDTO(barbero);
    }

    @Override
    public BarberoResponseDTO toggleOcupado(Integer id) {
        Barbero barbero = barberoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Barbero no encontrado"));
        barbero.setOcupado(!barbero.isOcupado());
        return mapper.toResponseDTO(barberoRepository.save(barbero));
    }
}