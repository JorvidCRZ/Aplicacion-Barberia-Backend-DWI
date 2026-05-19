package com.sistemabarberia.fadex_backend.auth.permiso.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permiso")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Permiso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permiso")
    private Integer idPermiso;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "descripcion")
    private String descripcion;
}

