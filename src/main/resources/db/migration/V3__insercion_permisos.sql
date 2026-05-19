ALTER TABLE permiso
    ADD CONSTRAINT uk_permiso_nombre UNIQUE (nombre);

ALTER TABLE rol_permiso
    ADD CONSTRAINT uk_rol_permiso UNIQUE (id_rol, id_permiso);


INSERT INTO permiso (nombre) VALUES
                                 ('PRODUCTO_CREATE'),
                                 ('PRODUCTO_READ'),
                                 ('PRODUCTO_UPDATE'),
                                 ('PRODUCTO_DELETE'),

                                 ('CATEGORIA_CREATE'),
                                 ('CATEGORIA_READ'),
                                 ('CATEGORIA_UPDATE'),
                                 ('CATEGORIA_DELETE'),

                                 ('SERVICIO_CREATE'),
                                 ('SERVICIO_READ'),
                                 ('SERVICIO_UPDATE'),
                                 ('SERVICIO_DELETE'),

                                 ('RESERVA_CREATE'),
                                 ('RESERVA_READ'),
                                 ('RESERVA_UPDATE'),
                                 ('RESERVA_DELETE'),
                                 ('RESERVA_CONFIRM'),
                                 ('RESERVA_CANCEL'),

                                 ('VENTA_CREATE'),
                                 ('VENTA_READ'),
                                 ('VENTA_UPDATE'),
                                 ('VENTA_DELETE'),
                                 ('VENTA_ANULAR'),

                                 ('CORTE_CREATE'),
                                 ('CORTE_READ'),
                                 ('CORTE_UPDATE'),
                                 ('CORTE_DELETE'),
                                 ('CORTE_FINALIZAR'),

                                 ('USUARIO_CREATE'),
                                 ('USUARIO_READ'),
                                 ('USUARIO_UPDATE'),
                                 ('USUARIO_DELETE'),

                                 ('REPORTE_READ'),
                                 ('ESTADISTICA_READ'),
                                 ('DASHBOARD_READ'),

                                 ('CONFIGURACION_READ'),
                                 ('CONFIGURACION_UPDATE')
    ON CONFLICT (nombre) DO NOTHING;


INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso
FROM rol r
         CROSS JOIN permiso p
WHERE r.nombre = 'admin'
    ON CONFLICT DO NOTHING;


INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso
FROM rol r
         JOIN permiso p ON TRUE
WHERE r.nombre = 'barbero'
  AND p.nombre IN (

                   'PRODUCTO_READ',
                   'CATEGORIA_READ',
                   'SERVICIO_READ',

                   'RESERVA_CREATE',
                   'RESERVA_READ',
                   'RESERVA_UPDATE',
                   'RESERVA_CONFIRM',
                   'RESERVA_CANCEL',

                   'VENTA_CREATE',
                   'VENTA_READ',

                   'CORTE_CREATE',
                   'CORTE_READ',
                   'CORTE_UPDATE',
                   'CORTE_FINALIZAR',

                   'DASHBOARD_READ'
    )
    ON CONFLICT DO NOTHING;

INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso
FROM rol r
         JOIN permiso p ON TRUE
WHERE r.nombre = 'cliente'
  AND p.nombre IN (

                   'PRODUCTO_READ',
                   'SERVICIO_READ',

                   'RESERVA_CREATE',
                   'RESERVA_READ',
                   'RESERVA_CANCEL',

                   'VENTA_READ'
    )
    ON CONFLICT DO NOTHING;