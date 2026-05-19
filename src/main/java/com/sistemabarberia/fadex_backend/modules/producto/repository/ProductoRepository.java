package com.sistemabarberia.fadex_backend.modules.producto.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.sistemabarberia.fadex_backend.modules.producto.entity.Producto;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long>,JpaSpecificationExecutor<Producto> {
    boolean existsByCategoriaId(Long categoriaId);
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
}