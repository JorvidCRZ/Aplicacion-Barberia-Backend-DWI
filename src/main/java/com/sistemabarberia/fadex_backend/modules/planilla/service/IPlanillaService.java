package com.sistemabarberia.fadex_backend.modules.planilla.service;

import com.sistemabarberia.fadex_backend.modules.planilla.dto.DetalleBarberoResumenDTO;
import com.sistemabarberia.fadex_backend.modules.planilla.dto.PlanillaBarberoDTO;
import com.sistemabarberia.fadex_backend.modules.planilla.dto.PlanillaResumenDTO;
import com.sistemabarberia.fadex_backend.modules.planilla.dto.VentaBarberoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IPlanillaService {

    PlanillaResumenDTO obtenerResumen(
            Integer mes,
            Integer anio
    );

    Page<PlanillaBarberoDTO> obtenerDetalle(
            Integer mes,
            Integer anio,
            Pageable pageable
    );

    public List<Integer> obtenerAniosDisponibles() ;


    DetalleBarberoResumenDTO obtenerResumenBarbero(
            Integer barberoId,
            Integer mes,
            Integer anio
    );

    Page<VentaBarberoDTO> obtenerVentasBarbero(
            Integer barberoId,
            Integer mes,
            Integer anio,
            Pageable pageable
    );

}