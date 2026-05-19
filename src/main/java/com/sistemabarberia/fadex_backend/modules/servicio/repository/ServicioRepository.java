package com.sistemabarberia.fadex_backend.modules.servicio.repository;


import com.sistemabarberia.fadex_backend.modules.servicio.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long> {
    Optional<Servicio> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
    List<Servicio> findByCategoriaId_Id(Long categoriaIdId);
    boolean existsByCategoriaId(Long id);
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndServicioIdNot(String nombre, Long servicioId);
}
