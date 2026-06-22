package com.sistemabarberia.fadex_backend.modules.ia.service;



import com.sistemabarberia.fadex_backend.commons.exception.ResourceNotFoundException;
import com.sistemabarberia.fadex_backend.modules.ia.dto.HaircutFeaturesRequestDTO;
import com.sistemabarberia.fadex_backend.modules.ia.dto.HaircutFeaturesResponseDTO;
import com.sistemabarberia.fadex_backend.modules.ia.entity.HaircutFeatures;
import com.sistemabarberia.fadex_backend.modules.ia.repository.HaircutFeaturesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HaircutFeaturesServiceImpl implements IHaircutFeaturesService {

    @Autowired
    private HaircutFeaturesRepository repository;

    @Override
    public HaircutFeaturesResponseDTO obtener(Integer idCorte) {
        HaircutFeatures features = repository.findById(idCorte)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Features no encontradas para el corte: " + idCorte));
        return toResponse(features);
    }

    @Override
    public HaircutFeaturesResponseDTO actualizar(Integer idCorte, HaircutFeaturesRequestDTO dto) {
        HaircutFeatures features = repository.findById(idCorte)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Features no encontradas para el corte: " + idCorte));

        if (dto.getAddsHeight()      != null) features.setAddsHeight(dto.getAddsHeight());
        if (dto.getReducesWidth()    != null) features.setReducesWidth(dto.getReducesWidth());
        if (dto.getSoftensJaw()      != null) features.setSoftensJaw(dto.getSoftensJaw());
        if (dto.getAddsVolumeSides() != null) features.setAddsVolumeSides(dto.getAddsVolumeSides());
        if (dto.getAddsVolumeTop()   != null) features.setAddsVolumeTop(dto.getAddsVolumeTop());
        if (dto.getReducesForehead() != null) features.setReducesForehead(dto.getReducesForehead());
        if (dto.getMaintenanceLevel()!= null) features.setMaintenanceLevel(dto.getMaintenanceLevel());
        features.setFeaturesVerified(true);

        return toResponse(repository.save(features));
    }

    private HaircutFeaturesResponseDTO toResponse(HaircutFeatures f) {
        HaircutFeaturesResponseDTO dto = new HaircutFeaturesResponseDTO();
        dto.setIdCorte(f.getIdCorte());
        dto.setAddsHeight(f.getAddsHeight());
        dto.setReducesWidth(f.getReducesWidth());
        dto.setSoftensJaw(f.getSoftensJaw());
        dto.setAddsVolumeSides(f.getAddsVolumeSides());
        dto.setAddsVolumeTop(f.getAddsVolumeTop());
        dto.setReducesForehead(f.getReducesForehead());
        dto.setMaintenanceLevel(f.getMaintenanceLevel());
        dto.setFeaturesVerified(f.getFeaturesVerified());
        return dto;
    }
}