package com.sistemabarberia.fadex_backend.modules.barbero.entity;

import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name="barbero")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Barbero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_barbero")
    private Integer barberoId;

    @OneToOne
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @Column(name = "experiencia")
    private Integer experiencia;

    @Column(name = "fecha_ingreso")
    private LocalDate fechaIngreso;

    @Column(name = "ocupado")
    private boolean ocupado;

    @Column(name = "sueldo", precision = 10, scale = 2)
    private BigDecimal sueldo;

    @Column(name = "comision", precision = 5, scale = 2)
    private BigDecimal comision;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "foto_url", length = 255)
    private String fotoUrl;

    @Column(name = "activo")
    private boolean activo;

    @PrePersist
    public void prePersist() {
        if (this.fechaIngreso == null) {
            this.fechaIngreso = LocalDate.now();
        }
        this.activo = true;
    }


}