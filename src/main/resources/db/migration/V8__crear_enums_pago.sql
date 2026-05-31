CREATE TYPE metodo_pago AS ENUM (
    'EFECTIVO',
    'YAPE',
    'PLIN',
    'TARJETA',
    'TRANSFERENCIA'
);

CREATE TYPE tipo_pago AS ENUM (
    'RESERVA',
    'VENTA',
    'SERVICIO',
    'PRODUCTO',
    'MIXTO'
);

ALTER TABLE pago
ALTER COLUMN metodo TYPE metodo_pago
USING metodo::metodo_pago;

ALTER TABLE pago
ALTER COLUMN tipo TYPE tipo_pago
USING tipo::tipo_pago;