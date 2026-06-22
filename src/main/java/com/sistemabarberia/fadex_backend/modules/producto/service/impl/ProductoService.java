package com.sistemabarberia.fadex_backend.modules.producto.service.impl;

import com.sistemabarberia.fadex_backend.commons.exception.BusinessException;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.commons.storage.FileStorageService;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.CategoriaEnum;
import com.sistemabarberia.fadex_backend.modules.categoria.repository.CategoriaRepository;
import com.sistemabarberia.fadex_backend.modules.producto.dto.ProductoFiltro;
import com.sistemabarberia.fadex_backend.modules.producto.dto.request.ProductoRequest;
import com.sistemabarberia.fadex_backend.modules.producto.dto.response.ProductoResponse;
import com.sistemabarberia.fadex_backend.modules.producto.entity.Producto;
import com.sistemabarberia.fadex_backend.modules.producto.mapper.ProductoMapper;
import com.sistemabarberia.fadex_backend.modules.producto.repository.ProductoRepository;
import com.sistemabarberia.fadex_backend.modules.producto.service.IProductoService;
import com.sistemabarberia.fadex_backend.modules.producto.specs.ProductoSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProductoService implements IProductoService {
    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final ProductoMapper productoMapper;
    private final FileStorageService fileStorageService;
    private static final List<String> TIPOS_IMAGEN = List.of("image/jpeg", "image/png", "image/webp");

    @Override
    public ProductoResponse obtenerProductoPublicadoPorId(Long id) {
        Producto producto = productoRepository.findById(id).orElseThrow(() -> new BusinessException("Producto no encontrado", HttpStatus.NOT_FOUND));
        if (!producto.isEstado() || !producto.isPublicado()) {
            throw new BusinessException("Producto no disponible", HttpStatus.NOT_FOUND);
        }
        return productoMapper.toResponse(producto);
    }

    @Override
    public PageResponse<ProductoResponse> listarProductosPublicos(ProductoFiltro filtro, Pageable pageable) {
        filtro.setEstado(true);
        filtro.setPublicado(true);
        List<Long> categoriasIds = null;
        if (filtro != null && filtro.getIdCategoria() != null) {
            categoriasIds = obtenerIdsCategorias(filtro.getIdCategoria());
        }
        Page<Producto> pagina = productoRepository.findAll(ProductoSpecification.filtrar(filtro, categoriasIds), pageable);
        return PageResponse.of(pagina.map(productoMapper::toResponse));
    }

    @Override
    public PageResponse<ProductoResponse> listarProductoFiltros(ProductoFiltro filtro, Pageable pageable) {
        List<Long> categoriasIds = null;
        if (filtro != null && filtro.getIdCategoria() != null) {
            categoriasIds = obtenerIdsCategorias(filtro.getIdCategoria());
        }
        Page<Producto> pagina = productoRepository.findAll(ProductoSpecification.filtrar(filtro, categoriasIds), pageable);
        return PageResponse.of(pagina.map(productoMapper::toResponse));
    }

    @Override
    public ProductoResponse obtenerProductoPorId(Long id) {
        Producto producto = productoRepository.findById(id).orElseThrow(() -> new BusinessException("Producto no encontrado", HttpStatus.NOT_FOUND));
        return productoMapper.toResponse(producto);
    }

    @Override
    public ProductoResponse crearProducto(ProductoRequest request, List<MultipartFile> archivos)  {
        Categoria categoria = obtenerCategoriaProducto(request.getIdCategoria());
        String nombre = validarNombreProducto(request.getNombre());
        request.setNombre(nombre);
        if (productoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new BusinessException("Ya existe un producto con ese nombre", HttpStatus.BAD_REQUEST);
        }

        Producto producto = productoMapper.toEntity(request);
        producto.setCategoria(categoria);
        List<String> urls = new ArrayList<>();
        List<MultipartFile> archivosValidos = filtrarArchivosNoVacios(archivos);

        if (!archivosValidos.isEmpty()) {
            for (MultipartFile file : archivosValidos) {
                validarArchivoImagen(file);
                String url = fileStorageService.guardarArchivo(file, "productos", TIPOS_IMAGEN);
                urls.add(url);
            }
        }
        producto.setUrlsMultimedia(urls);
        Producto guardado = productoRepository.save(producto);
        return productoMapper.toResponse(guardado);
    }

    @Override
    public ProductoResponse actualizarProducto(Long id, ProductoRequest request, List<MultipartFile> archivos) {
        Producto producto = productoRepository.findById(id).orElseThrow(() ->new BusinessException("Producto no encontrado",HttpStatus.NOT_FOUND));
        Categoria categoria = obtenerCategoriaProducto(request.getIdCategoria());
        String nombre = validarNombreProducto(request.getNombre());
        request.setNombre(nombre);
        boolean existe = productoRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id);
        if (existe) {
            throw new BusinessException("Ya existe un producto con ese nombre", HttpStatus.BAD_REQUEST);
        }
        productoMapper.updateFromRequest(request, producto);
        producto.setCategoria(categoria);
        List<MultipartFile> archivosValidos = filtrarArchivosNoVacios(archivos);

        if (!archivosValidos.isEmpty()) {
            if (producto.getUrlsMultimedia() != null) {
                for (String url : producto.getUrlsMultimedia()) {
                    fileStorageService.eliminarArchivo(url);
                }
            }
            List<String> nuevasUrls = new ArrayList<>();
            for (MultipartFile file : archivosValidos) {
                validarArchivoImagen(file);
                String url = fileStorageService.guardarArchivo(file, "productos", TIPOS_IMAGEN);
                nuevasUrls.add(url);
            }
            producto.setUrlsMultimedia(nuevasUrls);
        }
        return productoMapper.toResponse(productoRepository.save(producto));
    }

    @Override
    public ProductoResponse  cambiarEstadoProducto(Long id, boolean nuevoEstado) {
        Producto producto = productoRepository.findById(id).orElseThrow(() -> new BusinessException("Producto no encontrado", HttpStatus.NOT_FOUND));
        producto.setEstado(nuevoEstado);
        Producto actualizado = productoRepository.save(producto);
        return productoMapper.toResponse(actualizado);
    }

    @Override
    public ProductoResponse cambiarPublicacion(Long id, boolean publicado) {
        Producto producto = productoRepository.findById(id).orElseThrow(() -> new BusinessException("Producto no encontrado", HttpStatus.NOT_FOUND));
        producto.setPublicado(publicado);
        Producto actualizado = productoRepository.save(producto);
        return productoMapper.toResponse(actualizado);
    }

    @Override
    public void eliminarProducto(Long id) {
        Producto producto = productoRepository.findById(id).orElseThrow(() -> new BusinessException("Producto no encontrado", HttpStatus.NOT_FOUND));
        if (producto.getUrlsMultimedia() != null) {
            for (String url : producto.getUrlsMultimedia()) {
                fileStorageService.eliminarArchivo(url);
            }
        }
        productoRepository.delete(producto);
    }

    private void validarArchivoImagen(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("Archivo vacío", HttpStatus.BAD_REQUEST);
        }
        if (!TIPOS_IMAGEN.contains(file.getContentType())) {
            throw new BusinessException("Formato no permitido: " + file.getContentType(), HttpStatus.BAD_REQUEST);
        }
    }

    private List<MultipartFile> filtrarArchivosNoVacios(List<MultipartFile> archivos) {
        if (archivos == null || archivos.isEmpty()) {
            return List.of();
        }
        List<MultipartFile> archivosValidos = new ArrayList<>();
        for (MultipartFile archivo : archivos) {
            if (archivo != null && !archivo.isEmpty()) {
                archivosValidos.add(archivo);
            }
        }
        return archivosValidos;
    }
    private String validarNombreProducto(String nombre) {
        if (nombre == null) {
            throw new BusinessException("El nombre del producto es obligatorio", HttpStatus.BAD_REQUEST);
        }
        nombre = nombre.trim();
        if (nombre.isBlank()) {
            throw new BusinessException("El nombre del producto es obligatorio", HttpStatus.BAD_REQUEST);
        }
        return nombre;
    }

    private Categoria obtenerCategoriaProducto(Long idCategoria) {
        Categoria categoria = categoriaRepository.findById(idCategoria).orElseThrow(() -> new BusinessException("Categoría no encontrada", HttpStatus.BAD_REQUEST));
        if (categoria.getTipo() != CategoriaEnum.PRODUCTO) {
            throw new BusinessException("La categoría seleccionada no pertenece a productos", HttpStatus.BAD_REQUEST);
        }
        return categoria;
    }

    private List<Long> obtenerIdsCategorias(Long categoriaId) {
        Set<Long> ids = new HashSet<>();
        recolectarCategoriasHijas(categoriaId, ids);
        return new ArrayList<>(ids);
    }

    private void recolectarCategoriasHijas(Long categoriaId, Set<Long> ids) {
        if (ids.contains(categoriaId)) {return;}
        ids.add(categoriaId);
        List<Categoria> hijos = categoriaRepository.findByPadreId(categoriaId);
        for (Categoria hijo : hijos) {
            recolectarCategoriasHijas(hijo.getId(), ids);
        }
    }
}