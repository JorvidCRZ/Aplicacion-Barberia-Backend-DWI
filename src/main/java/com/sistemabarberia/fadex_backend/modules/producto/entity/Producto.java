package com.sistemabarberia.fadex_backend.modules.producto.entity;

import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "producto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_producto")
    private Long id;

    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(precision = 10, scale = 2)
    private BigDecimal precio;

    @Builder.Default
    private Integer stock = 0;

    @Column(name = "publicado_ecommerce")
    @Builder.Default
    private boolean publicado = false;

    @Column(name = "estado")
    private boolean estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_categoria")
    private Categoria categoria;

    @ElementCollection
    @CollectionTable(name = "producto_multimedia", joinColumns = @JoinColumn(name = "id_producto"))
    @Column(name = "url_recurso")
    @Builder.Default
    private List<String> urlsMultimedia = new ArrayList<>();
}