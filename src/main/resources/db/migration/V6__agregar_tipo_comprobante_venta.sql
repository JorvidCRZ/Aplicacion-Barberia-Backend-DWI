ALTER TABLE venta
ADD COLUMN tipo_comprobante VARCHAR(20) NOT NULL DEFAULT 'BOLETA';

ALTER TABLE venta
ADD CONSTRAINT chk_tipo_comprobante
CHECK (tipo_comprobante IN ('BOLETA', 'FACTURA'));