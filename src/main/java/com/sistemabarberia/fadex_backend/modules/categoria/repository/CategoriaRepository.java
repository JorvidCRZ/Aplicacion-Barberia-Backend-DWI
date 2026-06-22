package com.sistemabarberia.fadex_backend.modules.categoria.repository;

import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import com.sistemabarberia.fadex_backend.modules.categoria.entity.CategoriaEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByEstadoTrue();
    List<Categoria> findByPadreId(Long padreId);
    List<Categoria> findByPadreIsNullAndEstadoTrue();
    List<Categoria> findByPadreIsNullAndTipoAndEstadoTrue(CategoriaEnum tipo);
    Page<Categoria> findAll(Specification<Categoria> spec, Pageable pageable);
    boolean existsByPadreId(Long padreId);
    boolean existsByNombreIgnoreCaseAndPadreIsNullAndEstadoTrue(String nombre);
    boolean existsByNombreIgnoreCaseAndPadreIdAndEstadoTrueAndTipo(String nombre, Long padreId, CategoriaEnum tipo);
}
