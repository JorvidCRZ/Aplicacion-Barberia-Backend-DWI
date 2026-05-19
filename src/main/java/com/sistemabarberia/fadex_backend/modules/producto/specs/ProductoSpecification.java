package com.sistemabarberia.fadex_backend.modules.producto.specs;

import com.sistemabarberia.fadex_backend.modules.producto.dto.ProductoFiltro;
import com.sistemabarberia.fadex_backend.modules.producto.entity.Producto;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

public class ProductoSpecification {
    public static Specification<Producto> filtrar(ProductoFiltro filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filtro == null) {
                return cb.conjunction();
            }

            if (filtro.getNombre() != null && !filtro.getNombre().isBlank()) {
                String nombre = filtro.getNombre().trim().toLowerCase();
                predicates.add(cb.like(cb.lower(root.get("nombre")), "%" + nombre + "%"));
            }

            if (filtro.getIdCategoria() != null) {
                predicates.add(cb.equal(root.get("categoria").get("id"), filtro.getIdCategoria()));
            }

            if (filtro.getPrecioMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("precio"), filtro.getPrecioMin()));
            }

            if (filtro.getPrecioMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("precio"), filtro.getPrecioMax()));
            }

            if (filtro.getPublicado() != null) {
                predicates.add(cb.equal(root.get("publicado"), filtro.getPublicado()));
            }

            if (filtro.getEstado() != null) {
                predicates.add(cb.equal(root.get("estado"), filtro.getEstado()));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}