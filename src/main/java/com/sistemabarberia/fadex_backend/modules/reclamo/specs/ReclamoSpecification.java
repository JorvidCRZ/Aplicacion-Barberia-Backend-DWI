package com.sistemabarberia.fadex_backend.modules.reclamo.specs;

import com.sistemabarberia.fadex_backend.modules.reclamo.dto.ReclamoFiltro;
import com.sistemabarberia.fadex_backend.modules.reclamo.entity.Reclamo;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ReclamoSpecification {
    public static Specification<Reclamo> filtrar(ReclamoFiltro filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (StringUtils.hasText(filtro.getNumeroReclamo())) {
                predicates.add(cb.like(cb.upper(root.get("numeroReclamo")), "%" + filtro.getNumeroReclamo().toUpperCase() + "%"));
            }

            if (filtro.getEstado() != null) {
                predicates.add(cb.equal(root.get("estadoReclamo"), filtro.getEstado()));
            }

            if (filtro.getTipoProblema() != null) {
                predicates.add(cb.equal(root.get("tipoProblema"), filtro.getTipoProblema()));
            }

            if (filtro.getEsPublico() != null) {
                predicates.add(cb.equal(root.get("esPublico"), filtro.getEsPublico()));
            }

            if (filtro.getIdResponsable() != null) {
                predicates.add(cb.equal(root.get("usuarioResponsable").get("idUsuario"), filtro.getIdResponsable())
                );
            }
            if(filtro.getTipoReclamacion() != null){
                predicates.add(cb.equal(root.get("tipoReclamacion"), filtro.getTipoReclamacion()));
            }

            if(filtro.getCausaReclamo() != null){
                predicates.add(cb.equal(root.get("causaReclamo"), filtro.getCausaReclamo()));
            }

            if (StringUtils.hasText(filtro.getNumeroDocumentoCliente())) {
                predicates.add(cb.like(root.get("numeroDocumentoCliente"), "%" + filtro.getNumeroDocumentoCliente() + "%"));
            }

            if (filtro.getFechaInicio() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("fechaReclamo"), filtro.getFechaInicio().atStartOfDay()));
            }

            if (filtro.getFechaFin() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("fechaReclamo"), filtro.getFechaFin().atTime(23,59,59)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}