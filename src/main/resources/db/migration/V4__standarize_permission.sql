

INSERT INTO permiso (nombre) VALUES


('RESERVA_CREATE'),

('RESERVA_READ_SELF'),
('RESERVA_READ_ASSIGNED'),
('RESERVA_READ_ALL'),

('RESERVA_UPDATE_SELF'),
('RESERVA_UPDATE_ASSIGNED'),
('RESERVA_UPDATE_ALL'),

('RESERVA_CANCEL_SELF'),
('RESERVA_CANCEL_ALL'),

('RESERVA_CONFIRM_ASSIGNED'),

('RESERVA_COMPLETE_ASSIGNED'),

('RESERVA_DELETE_ALL'),

('RESERVA_MANAGE'),


('PRODUCTO_CREATE'),
('PRODUCTO_READ'),
('PRODUCTO_UPDATE_ALL'),
('PRODUCTO_DELETE_ALL'),


('CATEGORIA_CREATE'),
('CATEGORIA_READ'),
('CATEGORIA_UPDATE_ALL'),
('CATEGORIA_DELETE_ALL'),


('SERVICIO_CREATE'),
('SERVICIO_READ'),
('SERVICIO_UPDATE_ALL'),
('SERVICIO_DELETE_ALL'),


('VENTA_CREATE'),

('VENTA_READ_SELF'),
('VENTA_READ_ASSIGNED'),
('VENTA_READ_ALL'),

('VENTA_UPDATE_ALL'),
('VENTA_DELETE_ALL'),
('VENTA_CANCEL_ALL'),


('CORTE_CREATE_ASSIGNED'),
('CORTE_READ_ASSIGNED'),
('CORTE_READ_ALL'),

('CORTE_UPDATE_ASSIGNED'),

('CORTE_COMPLETE_ASSIGNED'),

('CORTE_DELETE_ALL'),


('USUARIO_CREATE'),
('USUARIO_READ_ALL'),
('USUARIO_UPDATE_ALL'),
('USUARIO_DELETE_ALL'),


('CLIENTE_CREATE'),
('CLIENTE_READ_ALL'),
('CLIENTE_UPDATE_ALL'),
('CLIENTE_DELETE_ALL'),


('BARBERO_CREATE'),
('BARBERO_READ_ALL'),
('BARBERO_UPDATE_ALL'),
('BARBERO_DELETE_ALL'),


('REPORTE_READ_ALL'),
('ESTADISTICA_READ_ALL'),

('DASHBOARD_READ_ADMIN'),
('DASHBOARD_READ_BARBERO'),


('CONFIGURACION_READ'),
('CONFIGURACION_UPDATE')

    ON CONFLICT (nombre) DO NOTHING;





DELETE FROM rol_permiso
WHERE id_permiso IN (
    SELECT id_permiso
    FROM permiso
    WHERE nombre IN (

                     'BARBERO_VIEW',
                     'BARBERO_UPDATE',
                     'BARBERO_DELETE',

                     'CLIENTE_VIEW',
                     'CLIENTE_UPDATE',
                     'CLIENTE_DELETE',

                     'PRODUCTO_UPDATE',
                     'PRODUCTO_DELETE',

                     'CATEGORIA_UPDATE',
                     'CATEGORIA_DELETE',

                     'SERVICIO_UPDATE',
                     'SERVICIO_DELETE',

                     'RESERVA_READ',
                     'RESERVA_UPDATE',
                     'RESERVA_DELETE',
                     'RESERVA_CANCEL',
                     'RESERVA_CONFIRM',

                     'VENTA_READ',
                     'VENTA_UPDATE',
                     'VENTA_DELETE',
                     'VENTA_ANULAR',

                     'CORTE_CREATE',
                     'CORTE_READ',
                     'CORTE_UPDATE',
                     'CORTE_DELETE',
                     'CORTE_FINALIZAR',

                     'USUARIO_READ',
                     'USUARIO_UPDATE',
                     'USUARIO_DELETE',

                     'REPORTE_READ',
                     'ESTADISTICA_READ',

                     'DASHBOARD_READ'
        )
);





INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso
FROM rol r
         JOIN permiso p ON TRUE
WHERE r.nombre = 'cliente'
  AND p.nombre IN (


                   'RESERVA_CREATE',
                   'RESERVA_READ_SELF',
                   'RESERVA_UPDATE_SELF',
                   'RESERVA_CANCEL_SELF',


                   'PRODUCTO_READ',
                   'SERVICIO_READ',


                   'VENTA_READ_SELF'

    )
    ON CONFLICT DO NOTHING;





INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso
FROM rol r
         JOIN permiso p ON TRUE
WHERE r.nombre = 'barbero'
  AND p.nombre IN (


                   'RESERVA_CREATE',

                   'RESERVA_READ_ASSIGNED',
                   'RESERVA_UPDATE_ASSIGNED',

                   'RESERVA_CONFIRM_ASSIGNED',

                   'RESERVA_COMPLETE_ASSIGNED',


                   'CORTE_CREATE_ASSIGNED',
                   'CORTE_READ_ASSIGNED',
                   'CORTE_UPDATE_ASSIGNED',
                   'CORTE_COMPLETE_ASSIGNED',


                   'VENTA_CREATE',
                   'VENTA_READ_ASSIGNED',


                   'PRODUCTO_READ',
                   'SERVICIO_READ',
                   'CATEGORIA_READ',


                   'DASHBOARD_READ_BARBERO'

    )
    ON CONFLICT DO NOTHING;





INSERT INTO rol_permiso (id_rol, id_permiso)
SELECT r.id_rol, p.id_permiso
FROM rol r
         JOIN permiso p ON TRUE
WHERE r.nombre = 'admin'
  AND p.nombre IN (


                   'RESERVA_CREATE',

                   'RESERVA_READ_ALL',
                   'RESERVA_UPDATE_ALL',
                   'RESERVA_DELETE_ALL',

                   'RESERVA_CANCEL_ALL',

                   'RESERVA_MANAGE',


                   'PRODUCTO_CREATE',
                   'PRODUCTO_READ',
                   'PRODUCTO_UPDATE_ALL',
                   'PRODUCTO_DELETE_ALL',


                   'CATEGORIA_CREATE',
                   'CATEGORIA_READ',
                   'CATEGORIA_UPDATE_ALL',
                   'CATEGORIA_DELETE_ALL',

                   'SERVICIO_CREATE',
                   'SERVICIO_READ',
                   'SERVICIO_UPDATE_ALL',
                   'SERVICIO_DELETE_ALL',


                   'VENTA_CREATE',
                   'VENTA_READ_ALL',
                   'VENTA_UPDATE_ALL',
                   'VENTA_DELETE_ALL',
                   'VENTA_CANCEL_ALL',

                   'CORTE_READ_ALL',
                   'CORTE_DELETE_ALL',


                   'USUARIO_CREATE',
                   'USUARIO_READ_ALL',
                   'USUARIO_UPDATE_ALL',
                   'USUARIO_DELETE_ALL',


                   'CLIENTE_CREATE',
                   'CLIENTE_READ_ALL',
                   'CLIENTE_UPDATE_ALL',
                   'CLIENTE_DELETE_ALL',


                   'BARBERO_CREATE',
                   'BARBERO_READ_ALL',
                   'BARBERO_UPDATE_ALL',
                   'BARBERO_DELETE_ALL',


                   'REPORTE_READ_ALL',
                   'ESTADISTICA_READ_ALL',


                   'DASHBOARD_READ_ADMIN',


                   'CONFIGURACION_READ',
                   'CONFIGURACION_UPDATE'

    )
    ON CONFLICT DO NOTHING;