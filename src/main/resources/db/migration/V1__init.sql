
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";


CREATE TABLE rol (
                     id_rol SERIAL PRIMARY KEY,
                     nombre VARCHAR(50)
);


CREATE TABLE permiso (
                         id_permiso SERIAL PRIMARY KEY,
                         nombre VARCHAR(100) NOT NULL,
                         descripcion VARCHAR(255)
);


CREATE TABLE rol_permiso (
                             id_rol INT NOT NULL,
                             id_permiso INT NOT NULL,
                             PRIMARY KEY (id_rol, id_permiso),
                             CONSTRAINT fk_rol FOREIGN KEY (id_rol) REFERENCES rol(id_rol) ON DELETE CASCADE,
                             CONSTRAINT fk_permiso FOREIGN KEY (id_permiso) REFERENCES permiso(id_permiso) ON DELETE CASCADE
);


CREATE TABLE usuario (
                         id_usuario SERIAL PRIMARY KEY,
                         qr_token VARCHAR(255),
                         usuario VARCHAR(50) UNIQUE,
                         password VARCHAR(255)
);


CREATE TABLE usuario_rol (
                             usuario_id INT NOT NULL,
                             rol_id INT NOT NULL,
                             PRIMARY KEY (usuario_id, rol_id),
                             CONSTRAINT fk_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id_usuario) ON DELETE CASCADE,
                             CONSTRAINT fk_rol_ur FOREIGN KEY (rol_id) REFERENCES rol(id_rol) ON DELETE CASCADE
);


CREATE TABLE persona (
                         id_persona SERIAL PRIMARY KEY,
                         id_usuario INT,
                         nombre VARCHAR(100),
                         apellido VARCHAR(100),
                         telefono VARCHAR(20),
                         email VARCHAR(150),
                         CONSTRAINT fk_persona_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE
);


CREATE TABLE barbero (
                         id_barbero SERIAL PRIMARY KEY,
                         id_persona INT,
                         experiencia INT,
                         fecha_ingreso DATE,
                         ocupado BOOLEAN,
                         sueldo DECIMAL(10,2),
                         comision DECIMAL(5,2),
                         descripcion TEXT,
                         foto_url VARCHAR(255),
                         FOREIGN KEY (id_persona) REFERENCES persona(id_persona) ON DELETE CASCADE
);

CREATE TABLE cliente (
                         id_cliente SERIAL PRIMARY KEY,
                         id_persona INT,
                         fecha_registro DATE,
                         FOREIGN KEY (id_persona) REFERENCES persona(id_persona) ON DELETE CASCADE
);


CREATE TABLE recompensa (
                            id_recompensa SERIAL PRIMARY KEY,
                            id_cliente INT,
                            cortes_acumulados INT,
                            cortes_gratis INT,
                            fecha_actualizacion TIMESTAMP,
                            FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
);

CREATE TABLE categoria (
                           id_categoria SERIAL PRIMARY KEY,
                           nombre VARCHAR(100),
                           descripcion TEXT,
                           estado BOOLEAN DEFAULT true,
                           tipo VARCHAR(20) NOT NULL,
                           id_padre INT NULL,
                           FOREIGN KEY (id_padre) REFERENCES categoria(id_categoria),
                           CONSTRAINT chk_categoria_tipo CHECK (tipo IN ('PRODUCTO', 'SERVICIO'))
);

CREATE INDEX idx_categoria_padre ON categoria(id_padre);


CREATE TABLE cortes (
                        id_corte SERIAL PRIMARY KEY,
                        nombre VARCHAR(100),
                        precio DECIMAL(10,2),
                        id_categoria INT,
                        FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
);


CREATE TABLE producto (
                          id_producto SERIAL PRIMARY KEY,
                          nombre VARCHAR(100),
                          descripcion TEXT,
                          precio DECIMAL(10,2),
                          stock INT,
                          estado BOOLEAN,
                          publicado_ecommerce BOOLEAN,
                          id_categoria INT,
                          FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria)
);


CREATE TABLE producto_multimedia (
                                     id_producto INT,
                                     url_recurso VARCHAR(255),
                                     PRIMARY KEY (id_producto, url_recurso),
                                     FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);


CREATE TABLE reservas (
                          id_reservas SERIAL PRIMARY KEY,
                          id_cliente INT,
                          id_barbero INT,
                          fecha TIMESTAMP,
                          estado VARCHAR(50),
                          tipo_reservas VARCHAR(50),
                          FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
                          FOREIGN KEY (id_barbero) REFERENCES barbero(id_barbero)
);


CREATE TABLE detalle_reservas (
                                  id_detalle_reserva SERIAL PRIMARY KEY,
                                  id_reservas INT,
                                  id_corte INT,
                                  FOREIGN KEY (id_reservas) REFERENCES reservas(id_reservas),
                                  FOREIGN KEY (id_corte) REFERENCES cortes(id_corte)
);


CREATE TABLE historial_cortes (
                                  id_historial SERIAL PRIMARY KEY,
                                  id_cliente INT,
                                  id_detalle_reserva INT,
                                  fecha TIMESTAMP,
                                  FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
                                  FOREIGN KEY (id_detalle_reserva) REFERENCES detalle_reservas(id_detalle_reserva)
);


CREATE TABLE venta (
                       id_venta SERIAL PRIMARY KEY,
                       id_cliente INT,
                       id_barbero INT,
                       fecha TIMESTAMP,
                       FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
                       FOREIGN KEY (id_barbero) REFERENCES barbero(id_barbero)
);


CREATE TABLE detalle_venta (
                               id_detalle_venta SERIAL PRIMARY KEY,
                               id_venta INT,
                               id_producto INT,
                               cantidad INT,
                               precio_unitario DECIMAL(10,2),
                               FOREIGN KEY (id_venta) REFERENCES venta(id_venta),
                               FOREIGN KEY (id_producto) REFERENCES producto(id_producto)
);


CREATE TABLE historial_venta (
                                 id_historial_venta SERIAL PRIMARY KEY,
                                 id_venta INT,
                                 fecha TIMESTAMP,
                                 FOREIGN KEY (id_venta) REFERENCES venta(id_venta)
);


CREATE TABLE pago (
                      id_pago SERIAL PRIMARY KEY,
                      id_cliente INT,
                      id_barbero INT,
                      id_reservas INT,
                      id_venta INT,
                      monto DECIMAL(10,2),
                      metodo VARCHAR(50),
                      fecha TIMESTAMP,
                      tipo VARCHAR(50),
                      FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente),
                      FOREIGN KEY (id_barbero) REFERENCES barbero(id_barbero),
                      FOREIGN KEY (id_reservas) REFERENCES reservas(id_reservas),
                      FOREIGN KEY (id_venta) REFERENCES venta(id_venta)
);

CREATE TABLE historial_pago (
                                id_historial_pago SERIAL PRIMARY KEY,
                                id_pago INT,
                                id_cliente INT,
                                fecha TIMESTAMP,
                                FOREIGN KEY (id_pago) REFERENCES pago(id_pago),
                                FOREIGN KEY (id_cliente) REFERENCES cliente(id_cliente)
);

