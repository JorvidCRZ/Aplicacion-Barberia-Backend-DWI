package com.sistemabarberia.fadex_backend.modules.ia.service;

import com.sistemabarberia.fadex_backend.modules.ia.dto.HaircutFeaturesRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ia.dto.HaircutFeaturesResponseDTO;

public interface IHaircutFeaturesService {
    HaircutFeaturesResponseDTO obtener(Integer idCorte);
    HaircutFeaturesResponseDTO actualizar(Integer idCorte, HaircutFeaturesRequestDTO dto);
}