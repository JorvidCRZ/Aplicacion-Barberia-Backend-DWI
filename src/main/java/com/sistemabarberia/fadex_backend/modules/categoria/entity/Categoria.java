package com.sistemabarberia.fadex_backend.modules.categoria.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categoria")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_categoria")
    private Long id;

    private String nombre;
    private String descripcion;

    @Builder.Default
    @Column(name = "estado")
    private boolean estado = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo",  nullable = false)
    private CategoriaEnum tipo;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_padre")
    private Categoria padre;

    @JsonIgnore
    @OneToMany(mappedBy = "padre", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Categoria> hijos = new ArrayList<>();
}