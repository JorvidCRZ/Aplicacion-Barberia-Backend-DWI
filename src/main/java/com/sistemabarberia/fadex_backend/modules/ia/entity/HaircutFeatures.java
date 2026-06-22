package com.sistemabarberia.fadex_backend.modules.ia.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "haircut_features", schema = "ia")
public class HaircutFeatures {

    @Id
    @Column(name = "id_corte")
    private Integer idCorte;

    @Column(name = "adds_height")
    private Integer addsHeight;

    @Column(name = "reduces_width")
    private Integer reducesWidth;

    @Column(name = "softens_jaw")
    private Integer softensJaw;

    @Column(name = "adds_volume_sides")
    private Integer addsVolumeSides;

    @Column(name = "adds_volume_top")
    private Integer addsVolumeTop;

    @Column(name = "reduces_forehead")
    private Integer reducesForehead;

    @Column(name = "maintenance_level")
    private Integer maintenanceLevel;

    @Column(name = "features_verified")
    private Boolean featuresVerified;
}