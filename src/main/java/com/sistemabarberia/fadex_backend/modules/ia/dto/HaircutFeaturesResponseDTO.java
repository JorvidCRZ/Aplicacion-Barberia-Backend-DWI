package com.sistemabarberia.fadex_backend.modules.ia.dto;

import lombok.Data;

@Data
public class HaircutFeaturesResponseDTO {
    private Integer idCorte;
    private Integer addsHeight;
    private Integer reducesWidth;
    private Integer softensJaw;
    private Integer addsVolumeSides;
    private Integer addsVolumeTop;
    private Integer reducesForehead;
    private Integer maintenanceLevel;
    private Boolean featuresVerified;
}