package com.sistemabarberia.fadex_backend.modules.producto.controller;

import com.sistemabarberia.fadex_backend.commons.response.ApiResponse;
import com.sistemabarberia.fadex_backend.commons.response.PageResponse;
import com.sistemabarberia.fadex_backend.modules.producto.dto.ProductoFiltro;
import com.sistemabarberia.fadex_backend.modules.producto.dto.request.ProductoRequest;
import com.sistemabarberia.fadex_backend.modules.producto.dto.response.ProductoResponse;
import com.sistemabarberia.fadex_backend.modules.producto.service.IProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final IProductoService productoService;

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<ProductoResponse>>> obtenerProductos(@Valid @ModelAttribute ProductoFiltro filtro, @PageableDefault(size = 10, page = 0) Pageable pageable) {
        PageResponse<ProductoResponse> productos = productoService.listarProductoFiltros(filtro, pageable);
        return ResponseEntity.ok(ApiResponse.ok("Productos  obtenidos correctamente", productos));
    }

    @PreAuthorize("hasAuthority('PRODUCTO_READ')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductoResponse>> obtenerPorId(@PathVariable Long id) {
        ProductoResponse producto = productoService.obtenerProductoPorId(id);
        return ResponseEntity.ok(ApiResponse.ok("Producto obtenido correctamente", producto));
    }

    @PreAuthorize("hasAuthority('PRODUCTO_CREATE')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductoResponse>> crear(@RequestPart("producto") ProductoRequest request, @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos) {
        ProductoResponse producto = productoService.crearProducto(request, archivos);
        return ResponseEntity.ok(ApiResponse.ok("Producto creado correctamente", producto));
    }

    @PreAuthorize("hasAuthority('PRODUCTO_UPDATE_ALL')")
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ProductoResponse>> actualizar(@PathVariable Long id, @RequestPart("producto") ProductoRequest request, @RequestPart(value = "archivos", required = false) List<MultipartFile> archivos) {
        ProductoResponse producto = productoService.actualizarProducto(id, request, archivos);
        return ResponseEntity.ok(ApiResponse.ok("Producto actualizado correctamente", producto));
    }

    @PreAuthorize("hasAuthority('PRODUCTO_UPDATE_ALL')")
    @PatchMapping("/{id}/estado")
    public ResponseEntity<ApiResponse<ProductoResponse>> cambiarEstadoProducto(@PathVariable Long id, @RequestParam boolean estado) {
        ProductoResponse producto = productoService.cambiarEstadoProducto(id, estado);
        return ResponseEntity.ok(ApiResponse.ok("Estado actualizado correctamente", producto));
    }

    @PreAuthorize("hasAuthority('PRODUCTO_UPDATE_ALL')")
    @PatchMapping("/{id}/publicacion")
    public ResponseEntity<ApiResponse<ProductoResponse>> cambiarPublicacion(@PathVariable Long id, @RequestParam boolean publicado) {
        ProductoResponse producto = productoService.cambiarPublicacion(id, publicado);
        return ResponseEntity.ok(ApiResponse.ok("Publicación actualizada correctamente", producto));
    }

    @PreAuthorize("hasAuthority('PRODUCTO_DELETE_ALL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        productoService.eliminarProducto(id);
        return ResponseEntity.ok(ApiResponse.ok("Producto eliminado correctamente"));
    }
}