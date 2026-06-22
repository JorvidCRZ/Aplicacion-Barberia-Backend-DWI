CREATE TYPE estado_reclamo AS ENUM (
    'ABIERTO',
    'EN_PROCESO',
    'RESUELTO',
    'CERRADO',
    'ANULADO'
);

CREATE TYPE tipo_problema AS ENUM (
    'SERVICIO_DEFECTUOSO',
    'CORTE_INCORRECTO',
    'BARBERO_INCORRECTO',
    'ATENCION_AL_CLIENTE',
    'INCUMPLIMIENTO_HORARIO',
    'DEMORA_EN_ATENCION',
    'RESERVA_INCORRECTA',
    'COBRO_INCORRECTO',
    'PRODUCTO_DEFECTUOSO',
    'PRODUCTO_INCORRECTO',
    'PRODUCTO_INCOMPLETO',
    'PRODUCTO_NO_LLEGO',
    'DEMORA_EN_ENTREGA',
    'OTRO'
);

CREATE TYPE causa_reclamo AS ENUM (
    'INCIDENCIA_DEL_PERSONAL',
    'INCIDENCIA_EN_RESERVA',
    'INCIDENCIA_EN_COBRO',
    'MALA_ATENCION',
    'INCUMPLIMIENTO_DE_HORARIO',
    'INCIDENCIA_EN_SISTEMA',
    'FALTA_DE_STOCK',
    'INCIDENCIA_EN_EMPAQUE',
    'INCIDENCIA_EN_DESPACHO',
    'FALLA_DEL_PROVEEDOR',
    'OTRO'
);

CREATE TYPE tipo_reclamacion AS ENUM (
    'RECLAMO',
    'QUEJA'
);

CREATE TYPE solucion_reclamo AS ENUM (
    'REEMBOLSO_TOTAL',
    'REEMBOLSO_PARCIAL',
    'REENVIO_PRODUCTO',
    'CAMBIO_PRODUCTO',
    'NOTA_CREDITO',
    'DISCULPA_FORMAL',
    'BONIFICACION',
    'SIN_SOLUCION',
    'OTRO'
);

CREATE TYPE tipo_adjunto_reclamo AS ENUM (
    'FOTO',
    'VIDEO',
    'PDF',
    'AUDIO',
    'CAPTURA',
    'OTRO'
);

CREATE TABLE reclamos (
    id_reclamo SERIAL PRIMARY KEY,
    numero_reclamo VARCHAR(30) UNIQUE NOT NULL,
    id_cliente INT,
    id_venta INT,
    id_reservas INT,
    id_usuario_responsable INT,
    id_usuario_atendio INT,
    id_usuario_creador INT,
    es_publico BOOLEAN NOT NULL DEFAULT FALSE,
    nombre_cliente VARCHAR(150) NOT NULL,
    correo_cliente VARCHAR(150),
    telefono_cliente VARCHAR(30),
    tipo_documento_cliente VARCHAR(20),
    numero_documento_cliente VARCHAR(30),
    tipo_reclamacion tipo_reclamacion NOT NULL,
    tipo_problema tipo_problema NOT NULL,
    causa_reclamo causa_reclamo,
    estado_reclamo estado_reclamo NOT NULL DEFAULT 'ABIERTO',
    solucion_reclamo solucion_reclamo,
    descripcion TEXT NOT NULL,
    notas_internas TEXT,
    monto_reclamado DECIMAL(10,2),
    monto_compensado DECIMAL(10,2),
    fecha_ocurrencia TIMESTAMP,
    fecha_reclamo TIMESTAMP NOT NULL DEFAULT NOW(),
    fecha_resolucion TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_reclamo_cliente
    FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
    CONSTRAINT fk_reclamo_venta
    FOREIGN KEY (id_venta) REFERENCES venta(id_venta),
    CONSTRAINT fk_reclamo_reserva
    FOREIGN KEY (id_reservas) REFERENCES reservas(id_reservas),
    CONSTRAINT fk_reclamo_responsable
    FOREIGN KEY (id_usuario_responsable) REFERENCES usuario(id_usuario),
    CONSTRAINT fk_reclamo_atendio
    FOREIGN KEY (id_usuario_atendio) REFERENCES usuario(id_usuario),
    CONSTRAINT fk_reclamo_creador
    FOREIGN KEY (id_usuario_creador) REFERENCES usuario(id_usuario)
);

CREATE TABLE reclamo_adjunto (
    id_adjunto SERIAL PRIMARY KEY,
    id_reclamo INT NOT NULL,
    tipo_adjunto tipo_adjunto_reclamo,
    nombre_original VARCHAR(255) NOT NULL,
    nombre_archivo VARCHAR(255) NOT NULL,
   url_archivo TEXT NOT NULL,
    mime_type VARCHAR(100),
    peso_bytes BIGINT,
    subido_por_id INT,
    fecha_subida TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_reclamo_adjunto_reclamo
    FOREIGN KEY (id_reclamo) REFERENCES reclamos(id_reclamo) ON DELETE CASCADE,
    CONSTRAINT fk_reclamo_adjunto_usuario
    FOREIGN KEY (subido_por_id) REFERENCES usuario(id_usuario)
);

CREATE INDEX idx_reclamos_numero
ON reclamos(numero_reclamo);

CREATE INDEX idx_reclamos_estado
ON reclamos(estado_reclamo);

CREATE INDEX idx_reclamos_fecha
ON reclamos(fecha_reclamo);

CREATE INDEX idx_reclamos_cliente
ON reclamos(id_cliente);

CREATE INDEX idx_reclamos_reserva
ON reclamos(id_reservas);

CREATE INDEX idx_reclamos_venta
ON reclamos(id_venta);

CREATE INDEX idx_reclamos_responsable
ON reclamos(id_usuario_responsable);