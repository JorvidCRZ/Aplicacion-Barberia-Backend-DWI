package com.sistemabarberia.fadex_backend.modules.ia.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class HaircutFeaturesRequestDTO {

    @Min(0) @Max(5)
    private Integer addsHeight;

    @Min(0) @Max(5)
    private Integer reducesWidth;

    @Min(0) @Max(5)
    private Integer softensJaw;

    @Min(0) @Max(5)
    private Integer addsVolumeSides;

    @Min(0) @Max(5)
    private Integer addsVolumeTop;

    @Min(0) @Max(5)
    private Integer reducesForehead;

    @Min(1) @Max(5)
    private Integer maintenanceLevel;
}