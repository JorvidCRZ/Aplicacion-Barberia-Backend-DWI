package com.sistemabarberia.fadex_backend.modules.reclamo.entity;

import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.modules.reclamo.entity.enums.TipoAdjuntoReclamo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reclamo_adjunto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReclamoAdjunto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_adjunto")
    private Long idAdjunto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reclamo", nullable = false)
    private Reclamo reclamo;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_adjunto")
    private TipoAdjuntoReclamo tipoAdjunto;

    @Column(name = "nombre_original", nullable = false, length = 255)
    private String nombreOriginal;

    @Column(name = "nombre_archivo", nullable = false, length = 255)
    private String nombreArchivo;

    @Column(name = "url_archivo", nullable = false, columnDefinition = "TEXT")
    private String urlArchivo;

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "peso_bytes")
    private Long pesoBytes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subido_por_id")
    private Usuario subidoPor;

    @Column(name = "fecha_subida")
    private LocalDateTime fechaSubida;

    @PrePersist
    public void prePersist() {
        if (fechaSubida == null) {
            fechaSubida = LocalDateTime.now();
        }
    }
}