-- BARBERO
ALTER TABLE barbero
    ADD COLUMN activo BOOLEAN DEFAULT true;

UPDATE barbero
SET activo = true
WHERE activo IS NULL;


-- CLIENTE
ALTER TABLE cliente
    ADD COLUMN activo BOOLEAN DEFAULT true;

UPDATE cliente
SET activo = true
WHERE activo IS NULL;