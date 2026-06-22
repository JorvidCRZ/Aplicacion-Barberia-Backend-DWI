package com.sistemabarberia.fadex_backend.modules.servicio.specs;

import com.sistemabarberia.fadex_backend.modules.servicio.dto.ServicioFiltro;
import com.sistemabarberia.fadex_backend.modules.servicio.entity.Servicio;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ServicioSpecification {
    public static Specification<Servicio> filtrar(ServicioFiltro filtro, List<Long> categoriasIds) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (filtro == null) {
                return cb.conjunction();
            }

            if (filtro.getId() != null) {
                predicates.add(cb.equal(root.get("servicioId"), filtro.getId()));
            }

            if (filtro.getNombre() != null && !filtro.getNombre().isBlank()) {
                predicates.add(cb.like(cb.lower(root.get("nombre")), "%" + filtro.getNombre().toLowerCase() + "%"));
            }

            if (categoriasIds != null && !categoriasIds.isEmpty()) {
                predicates.add(root.get("categoria").get("id").in(categoriasIds));
            }

            if (filtro.getPrecioMin() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("precio"), filtro.getPrecioMin()));
            }

            if (filtro.getPrecioMax() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("precio"), filtro.getPrecioMax()));
            }

            if (filtro.getEstado() != null) {
                predicates.add(cb.equal(root.get("estado"), filtro.getEstado()));
            }

            if (filtro.getPublicado() != null) {
                predicates.add(cb.equal(root.get("publicado"), filtro.getPublicado()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}