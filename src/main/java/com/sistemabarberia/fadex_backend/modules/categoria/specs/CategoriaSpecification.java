package com.sistemabarberia.fadex_backend.modules.categoria.specs;

import com.sistemabarberia.fadex_backend.modules.categoria.dto.CategoriaFiltro;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
public class CategoriaSpecification {
    public static Specification<Categoria> conFiltros(CategoriaFiltro filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filtro.getPadreId() != null) {
                predicates.add(cb.equal(root.get("padre").get("id"), filtro.getPadreId()));
            }

            if (filtro.getEstado() != null) {
                predicates.add(cb.equal(root.get("estado"), filtro.getEstado()));
            }

            if (filtro.getTipo() != null) {
                predicates.add(cb.equal(root.get("tipo"), filtro.getTipo()));
            }

            if (filtro.getNombre() != null &&
                    !filtro.getNombre().trim().isEmpty()) {
                String nombre = "%" + filtro.getNombre().trim().toLowerCase() + "%";
                Predicate nombreActual = cb.like(cb.lower(root.get("nombre")), nombre);
                if (filtro.getPadreId() != null) {
                    predicates.add(nombreActual);
                } else {
                    Join<Categoria, Categoria> hijos = root.join("hijos", JoinType.LEFT);
                    Predicate nombreHijo = cb.like(cb.lower(hijos.get("nombre")), nombre);
                    predicates.add(cb.or(nombreActual, nombreHijo)
                    );
                }
            }
            query.distinct(true);
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}