package com.sistemabarberia.fadex_backend.modules.producto.service;

import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.producto.dto.ProductoFiltro;
import com.sistemabarberia.fadex_backend.modules.producto.dto.request.ProductoRequest;
import com.sistemabarberia.fadex_backend.modules.producto.dto.response.ProductoResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IProductoService {
    PageResponse<ProductoResponse> listarProductoFiltros(ProductoFiltro filtro, Pageable pageable);
    ProductoResponse obtenerProductoPorId(Long id);
    ProductoResponse crearProducto(ProductoRequest request, List<MultipartFile> archivos);
    ProductoResponse actualizarProducto(Long id, ProductoRequest request, List<MultipartFile> archivos);
    ProductoResponse cambiarEstadoProducto(Long id, boolean nuevoEstado);
    ProductoResponse cambiarPublicacion(Long id, boolean nuevoEstado);
    void eliminarProducto(Long id);
}