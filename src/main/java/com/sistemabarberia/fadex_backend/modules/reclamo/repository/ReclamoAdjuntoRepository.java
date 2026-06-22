package com.sistemabarberia.fadex_backend.modules.reclamo.repository;

import com.sistemabarberia.fadex_backend.modules.reclamo.entity.ReclamoAdjunto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReclamoAdjuntoRepository extends JpaRepository<ReclamoAdjunto, Long> {
        List<ReclamoAdjunto> findByReclamoIdReclamo(Long idReclamo);
}