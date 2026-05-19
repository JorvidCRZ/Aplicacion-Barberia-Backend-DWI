INSERT INTO rol (nombre) VALUES ('admin'), ('barbero'), ('cliente');


INSERT INTO permiso (nombre) VALUES
                                 ('BARBERO_CREATE'),
                                 ('BARBERO_VIEW'),
                                 ('BARBERO_DELETE'),
                                 ('BARBERO_UPDATE'),
                                 ('CLIENTE_CREATE'),
                                 ('CLIENTE_VIEW'),
                                 ('CLIENTE_DELETE'),
                                 ('CLIENTE_UPDATE');


INSERT INTO usuario (qr_token, usuario, password) VALUES
                                                      ('token-admin-001',   'admin1',    '$2a$10$WFc0GeKoEGfFGjvpFGdQU.HcXyK0vmgeF/qrXXFtRr3Dw3DJdMQU2'),
                                                      ('token-barbero-001', 'juan123',   '$2a$10$OLT8yTs3MefyjyC.3yQHcuEYifRJRL4Naks3/jQR56N5AMLvNPDN6'),
                                                      ('token-barbero-002', 'luis123',   '$2a$10$TIA9W5kMKV9GGtvIRyLlKu4mhfWC6WohGBT/QBybY4Lgf.rTjKiOi'),
                                                      ('token-cliente-001', 'carlos123', '$2a$10$YACAsrkm3InDl0pVk6oZc.xYX8oHKgxN5.5/V2SZFeCSonuq2QU8W'),
                                                      ('token-cliente-002', 'ana123',    '$2a$10$qgUecdwyGCXwQPc4Nu.IH.yAXQiGvDUHMbM8gnxrfiOJD29KDxzzS');


INSERT INTO usuario_rol (usuario_id, rol_id) VALUES
                                                 (1, 1),
                                                 (2, 2),
                                                 (3, 2),
                                                 (4, 3),
                                                 (5, 3);


INSERT INTO rol_permiso (id_rol, id_permiso) VALUES
                                                 (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8);


INSERT INTO persona (id_usuario, nombre, apellido, telefono, email) VALUES
                                                                        (1, 'Admin',   'Sistema',  '900000000', 'admin@gmail.com'),
                                                                        (2, 'Juan',    'Perez',    '987654321', 'juan@gmail.com'),
                                                                        (3, 'Luis',    'Gomez',    '912345678', 'luis@gmail.com'),
                                                                        (4, 'Carlos',  'Ramirez',  '998877665', 'carlos@gmail.com'),
                                                                        (5, 'Ana',     'Torres',   '955443322', 'ana@gmail.com');


INSERT INTO barbero (id_persona, experiencia, fecha_ingreso, ocupado, sueldo, comision, descripcion) VALUES
                                                                                                         (2, 5, '2022-03-15', false, 1500.00, 10.00, 'Especialista en cortes clásicos'),
                                                                                                         (3, 3, '2023-07-10', false, 1200.00, 8.00,  'Especialista en degradados');


INSERT INTO cliente (id_persona, fecha_registro) VALUES
                                                     (4, '2024-01-10'),
                                                     (5, '2024-02-20');