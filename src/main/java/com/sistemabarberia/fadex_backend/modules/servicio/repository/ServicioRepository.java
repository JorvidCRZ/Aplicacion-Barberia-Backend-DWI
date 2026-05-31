package com.sistemabarberia.fadex_backend.modules.servicio.repository;

import com.sistemabarberia.fadex_backend.modules.servicio.entity.Servicio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServicioRepository extends
        JpaRepository<Servicio, Long>,
        JpaSpecificationExecutor<Servicio> {

    @EntityGraph(attributePaths = {"urlsMultimedia", "categoria"})
    Page<Servicio> findByPublicadoAndEstado(
            Boolean publicado,
            Boolean estado,
            Pageable pageable
    );

    @Override
    @EntityGraph(attributePaths = {"urlsMultimedia", "categoria"})
    Page<Servicio> findAll(
            Specification<Servicio> spec,
            Pageable pageable
    );

    Optional<Servicio> findByNombre(String nombre);

    boolean existsByNombre(String nombre);

    boolean existsByCategoriaId(Long categoriaId);

    boolean existsByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCaseAndServicioIdNot(
            String nombre,
            Long servicioId
    );
}