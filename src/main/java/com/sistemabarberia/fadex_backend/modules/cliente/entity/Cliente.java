package com.sistemabarberia.fadex_backend.modules.cliente.entity;


import com.sistemabarberia.fadex_backend.modules.persona.entity.Persona;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cliente")
    private Integer clienteId;

    @ManyToOne
    @JoinColumn(name = "id_persona")
    private Persona persona;

    @Column(name = "fecha_registro")
    private LocalDate fechaRegistro;



    @PrePersist   //Si Fecha Registro es null lo pondra automaticamente
    public void prePersist() {
        if (this.fechaRegistro == null) {
            this.fechaRegistro = LocalDate.now();
        }
    }
}
