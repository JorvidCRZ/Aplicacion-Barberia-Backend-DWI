    package com.sistemabarberia.fadex_backend.modules.servicio.entity;

    import com.sistemabarberia.fadex_backend.modules.categoria.entity.Categoria;
    import jakarta.persistence.*;
    import lombok.*;

    import java.math.BigDecimal;
    import java.util.ArrayList;
    import java.util.List;

    @Entity
    @Table(name = "cortes")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class Servicio {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "id_corte")
        private Long servicioId;

        @Column(name = "nombre", length = 100)
        private String nombre;

        @Column(name = "precio")
        private BigDecimal precio;

        @ManyToOne
        @JoinColumn(name = "id_categoria")
        private Categoria categoria;

        private Integer duracion;

        @Column(name = "descripcion", columnDefinition = "TEXT")
        private String descripcion;

        @Column(name = "estado")
        @Builder.Default
        private boolean estado = true;

        @Column(name = "publicado")
        @Builder.Default
        private boolean publicado = false;

        @ElementCollection
        @CollectionTable(name = "servicio_multimedia", joinColumns = @JoinColumn(name = "id_servicio"))
        @Column(name = "url_recurso")
        @Builder.Default
        private List<String> urlsMultimedia = new ArrayList<>();
    }