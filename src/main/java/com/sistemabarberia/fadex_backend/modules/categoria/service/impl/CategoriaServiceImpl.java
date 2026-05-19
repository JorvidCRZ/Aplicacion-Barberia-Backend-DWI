package com.sistemabarberia.fadex_backend.modules.categoria.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.categoria.dto.CategoriaFiltro;
import com.sistemabarberia.fadex_backend.modules.categoria.dto.request.CategoriaRequestDTO;
import com.sistemabarberia.fadex_backend.modules.categoria.dto.response.CategoriaResponseDTO;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.CategoriaEnum;
import com.sistemabarberia.fadex_backend.modules.categoria.mapper.CategoriaMapper;
import com.sistemabarberia.fadex_backend.modules.categoria.repository.CategoriaRepository;
import com.sistemabarberia.fadex_backend.modules.categoria.service.ICategoriaService;
import com.sistemabarberia.fadex_backend.modules.categoria.specs.CategoriaSpecification;
import com.sistemabarberia.fadex_backend.modules.producto.repository.ProductoRepository;
import com.sistemabarberia.fadex_backend.modules.servicio.repository.ServicioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements ICategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final ProductoRepository productoRepository;
    private final ServicioRepository servicioRepository;
    private final CategoriaMapper categoriaMapper;

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CategoriaResponseDTO> listarConFiltro(CategoriaFiltro filtro, Pageable pageable) {
        Page<Categoria> page = categoriaRepository.findAll(CategoriaSpecification.conFiltros(filtro), pageable);
        List<CategoriaResponseDTO> data = page.getContent().stream().map(this::mapearConHijos).toList();
        data = filtrarArbol(data, filtro);
        return PageResponse.<CategoriaResponseDTO>builder()
                .content(data)
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Override
    public CategoriaResponseDTO obtenerPorId(Long id) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new BusinessException("Categoría no encontrada", HttpStatus.NOT_FOUND));
        Map<Long, CategoriaResponseDTO> mapa = construirArbol(true);
        if (categoria.getPadre() != null) {
            return mapa.get(categoria.getPadre().getId());
        }
        return mapa.get(id);
    }

    @Override
    public CategoriaResponseDTO crear(CategoriaRequestDTO dto) {
        if (dto.getPadreId() == null) {
            if (categoriaRepository.existsByNombreIgnoreCaseAndPadreIsNullAndEstadoTrue(dto.getNombre().trim())) {
                throw new BusinessException("La categoría ya existe en nivel raíz", HttpStatus.BAD_REQUEST);
            }
        } else {
            if (categoriaRepository.existsByNombreIgnoreCaseAndPadreIdAndEstadoTrueAndTipo(dto.getNombre().trim(), dto.getPadreId(), dto.getTipo())) {
                throw new BusinessException("La categoría ya existe en esta categoría padre", HttpStatus.BAD_REQUEST);
            }
        }
        Categoria categoria = new Categoria();
        categoria.setNombre(dto.getNombre().trim());
        categoria.setDescripcion(dto.getDescripcion() != null ? dto.getDescripcion().trim() : null);
        categoria.setEstado(dto.getEstado() != null ? dto.getEstado() : true);
        categoria.setTipo(dto.getTipo());

        if (dto.getPadreId() != null) {
            Categoria padre = categoriaRepository.findById(dto.getPadreId()).orElseThrow(() -> new BusinessException("La categoría padre no existe", HttpStatus.NOT_FOUND));
            if (!padre.isEstado()) {
                throw new BusinessException("La categoría padre está inactiva", HttpStatus.BAD_REQUEST);
            }
            if (!padre.getTipo().equals(dto.getTipo())) {
                throw new BusinessException("El tipo debe coincidir con la categoría padre", HttpStatus.BAD_REQUEST);
            }
            categoria.setPadre(padre);
        }
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Override
    public CategoriaResponseDTO actualizar(Long id, CategoriaRequestDTO dto) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new BusinessException("Categoría no encontrada", HttpStatus.NOT_FOUND));
        boolean cambioNombre = !categoria.getNombre().equalsIgnoreCase(dto.getNombre().trim());
        boolean cambioTipo = !categoria.getTipo().equals(dto.getTipo());

        if (cambioNombre) {
            if (dto.getPadreId() == null) {
                if (categoriaRepository.existsByNombreIgnoreCaseAndPadreIsNullAndEstadoTrue(dto.getNombre().trim())) {
                    throw new BusinessException("La categoría ya existe en nivel raíz", HttpStatus.BAD_REQUEST);
                }
            } else {
                if (categoriaRepository.existsByNombreIgnoreCaseAndPadreIdAndEstadoTrueAndTipo(dto.getNombre().trim(), dto.getPadreId(), dto.getTipo())) {
                    throw new BusinessException("La categoría ya existe en esta categoría padre", HttpStatus.BAD_REQUEST);
                }
            }
        }
        if (dto.getPadreId() != null) {
            Categoria padre = categoriaRepository.findById(dto.getPadreId()).orElseThrow(() -> new BusinessException("La categoría padre no existe", HttpStatus.NOT_FOUND));
            if (!padre.isEstado()) {
                throw new BusinessException("La categoría padre está inactiva", HttpStatus.BAD_REQUEST);
            }
            if (dto.getPadreId().equals(id)) {
                throw new BusinessException("Una categoría no puede ser su propia padre", HttpStatus.BAD_REQUEST);
            }
            if (!padre.getTipo().equals(dto.getTipo())) {
                throw new BusinessException("El tipo debe coincidir con la categoría padre", HttpStatus.BAD_REQUEST);
            }
            categoria.setPadre(padre);
        } else {
            categoria.setPadre(null);
        }
        if (cambioTipo) {
            boolean tieneHijos = categoriaRepository.existsByPadreId(categoria.getId());
            if (tieneHijos) {
                throw new BusinessException("No se puede cambiar el tipo porque tiene subcategorías asociadas", HttpStatus.BAD_REQUEST);
            }
            if (categoria.getTipo() == CategoriaEnum.PRODUCTO) {
                boolean tieneProductos = productoRepository.existsByCategoriaId(id);
                if (tieneProductos) {
                    throw new BusinessException("No se puede cambiar el tipo porque tiene productos asociados", HttpStatus.BAD_REQUEST);
                }
            } else {
                boolean tieneServicios = servicioRepository.existsByCategoriaId(id);
                if (tieneServicios) {
                    throw new BusinessException("No se puede cambiar el tipo porque tiene servicios asociados", HttpStatus.BAD_REQUEST);
                }
            }
        }
        categoria.setNombre(dto.getNombre().trim());
        categoria.setDescripcion(dto.getDescripcion() != null ? dto.getDescripcion().trim() : null);
        categoria.setEstado(dto.getEstado() != null ? dto.getEstado() : categoria.isEstado());
        categoria.setTipo(dto.getTipo());
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Override
    public CategoriaResponseDTO cambiarEstado(Long id, Boolean estado) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new BusinessException("Categoría no encontrada", HttpStatus.NOT_FOUND));
        if (Boolean.TRUE.equals(estado) && categoria.getPadre() != null) {
            if (!categoria.getPadre().isEstado()) {
                throw new BusinessException("No se puede activar una categoría cuyo padre está inactivo", HttpStatus.BAD_REQUEST);
            }
        }
        if (Boolean.FALSE.equals(estado)) {
            boolean tieneSubcategorias = categoriaRepository.existsByPadreId(id);
            if (tieneSubcategorias) {
                throw new BusinessException("No se puede desactivar la categoría porque tiene subcategorías asociadas", HttpStatus.BAD_REQUEST);
            }
            if (categoria.getTipo() == CategoriaEnum.PRODUCTO) {
                boolean tieneProductos = productoRepository.existsByCategoriaId(id);
                if (tieneProductos) {
                    throw new BusinessException("No se puede desactivar la categoría porque tiene productos asociados", HttpStatus.BAD_REQUEST);
                }

            } else {
                boolean tieneServicios = servicioRepository.existsByCategoriaId(id);
                if (tieneServicios) {
                    throw new BusinessException("No se puede desactivar la categoría porque tiene servicios asociados", HttpStatus.BAD_REQUEST);
                }
            }
        }
        categoria.setEstado(estado);
        return categoriaMapper.toResponse(categoriaRepository.save(categoria));
    }

    @Override
    public void eliminar(Long id) {
        Categoria categoria = categoriaRepository.findById(id).orElseThrow(() -> new BusinessException("Categoría no encontrada", HttpStatus.NOT_FOUND));
        boolean tieneSubcategorias = categoriaRepository.existsByPadreId(id);
        if (tieneSubcategorias) {
            throw new BusinessException("No se puede eliminar la categoría porque tiene subcategorías asociadas", HttpStatus.BAD_REQUEST);
        }
        if (categoria.getTipo() == CategoriaEnum.PRODUCTO) {
            boolean tieneProductos = productoRepository.existsByCategoriaId(id);
            if (tieneProductos) {
                throw new BusinessException("No se puede eliminar la categoría porque tiene productos asociados", HttpStatus.BAD_REQUEST);
            }
        } else {
            boolean tieneServicios = servicioRepository.existsByCategoriaId(id);
            if (tieneServicios) {
                throw new BusinessException("No se puede eliminar la categoría porque tiene servicios asociados", HttpStatus.BAD_REQUEST);
            }
        }
        categoriaRepository.delete(categoria);
    }

    private Map<Long, CategoriaResponseDTO> construirArbol(boolean incluirInactivas) {
        List<Categoria> categorias = incluirInactivas ? categoriaRepository.findAll() : categoriaRepository.findByEstadoTrue();
        List<CategoriaResponseDTO> dtos = categoriaMapper.toResponseList(categorias);
        Map<Long, CategoriaResponseDTO> mapa = new HashMap<>();
        for (CategoriaResponseDTO dto : dtos) {
            dto.setSubcategorias(new ArrayList<>());
            mapa.put(dto.getId(), dto);
        }
        for (CategoriaResponseDTO dto : dtos) {
            if (dto.getPadreId() != null) {
                CategoriaResponseDTO padre = mapa.get(dto.getPadreId());
                if (padre != null) {
                    padre.getSubcategorias().add(dto);
                }
            }
        }
        return mapa;
    }

    private List<CategoriaResponseDTO> filtrarArbol(List<CategoriaResponseDTO> lista, CategoriaFiltro filtro) {
        List<CategoriaResponseDTO> resultado = new ArrayList<>();
        for (CategoriaResponseDTO cat : lista) {
            boolean cumple = true;
            if (filtro.getNombre() != null && !cat.getNombre().toLowerCase().contains(filtro.getNombre().toLowerCase().trim())) {cumple = false;}
            if (filtro.getEstado() != null && cat.isEstado() != filtro.getEstado()) {cumple = false;}
            if (filtro.getTipo() != null &&  !cat.getTipo().equals(filtro.getTipo())) {cumple = false;}
            List<CategoriaResponseDTO> hijosFiltrados = filtrarArbol(cat.getSubcategorias(), filtro);
            if (cumple || !hijosFiltrados.isEmpty()) {cat.setSubcategorias(hijosFiltrados);resultado.add(cat);}
        }

        return resultado;
    }

    private CategoriaResponseDTO mapearConHijos(Categoria categoria) {
        CategoriaResponseDTO dto = categoriaMapper.toResponse(categoria);
        List<CategoriaResponseDTO> hijos = new ArrayList<>();
        if (categoria.getHijos() != null && !categoria.getHijos().isEmpty()) {
            hijos = categoria.getHijos().stream().sorted((a, b) -> b.getId().compareTo(a.getId())).map(this::mapearConHijos).toList();
        }
        dto.setSubcategorias(hijos);
        return dto;
    }
}