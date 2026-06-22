package com.sistemabarberia.fadex_backend.modules.reclamo.entity;

import com.sistemabarberia.fadex_backend.auth.usuario.Entity.Usuario;
import com.sistemabarberia.fadex_backend.modules.cliente.entity.Cliente;
import com.sistemabarberia.fadex_backend.modules.reclamo.entity.enums.*;
import com.sistemabarberia.fadex_backend.modules.reserva.entity.Reserva;
import com.sistemabarberia.fadex_backend.modules.venta.entity.Venta;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "reclamos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reclamo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reclamo")
    private Long idReclamo;

    @Column(name = "numero_reclamo", nullable = false, unique = true, length = 30)
    private String numeroReclamo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_venta")
    private Venta venta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_reservas")
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_responsable")
    private Usuario usuarioResponsable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_atendio")
    private Usuario usuarioAtendio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario_creador")
    private Usuario usuarioCreador;

    @Builder.Default
    @Column(name = "es_publico", nullable = false)
    private boolean esPublico = false;

    @Column(name = "nombre_cliente", nullable = false)
    private String nombreCliente;

    @Column(name = "correo_cliente")
    private String correoCliente;

    @Column(name = "telefono_cliente")
    private String telefonoCliente;

    @Column(name = "tipo_documento_cliente")
    private String tipoDocumentoCliente;

    @Column(name = "numero_documento_cliente")
    private String numeroDocumentoCliente;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_reclamacion", nullable = false)
    private TipoReclamacion tipoReclamacion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_problema", nullable = false)
    private TipoProblema tipoProblema;

    @Enumerated(EnumType.STRING)
    @Column(name = "causa_reclamo")
    private CausaReclamo causaReclamo;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_reclamo", nullable = false)
    private EstadoReclamo estadoReclamo;

    @Enumerated(EnumType.STRING)
    @Column(name = "solucion_reclamo")
    private SolucionReclamo solucionReclamo;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "notas_internas", columnDefinition = "TEXT")
    private String notasInternas;

    @Column(name = "monto_reclamado", precision = 10, scale = 2)
    private BigDecimal montoReclamado;

    @Column(name = "monto_compensado", precision = 10, scale = 2)
    private BigDecimal montoCompensado;

    @Column(name = "fecha_ocurrencia")
    private LocalDateTime fechaOcurrencia;

    @Column(name = "fecha_reclamo")
    private LocalDateTime fechaReclamo;

    @Column(name = "fecha_resolucion")
    private LocalDateTime fechaResolucion;

    @OneToMany(mappedBy = "reclamo", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ReclamoAdjunto> adjuntos = new ArrayList<>();

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;


    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        if (this.fechaReclamo == null) {
            this.fechaReclamo = LocalDateTime.now();
        }

        if (this.estadoReclamo == null) {
            this.estadoReclamo = EstadoReclamo.ABIERTO;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}