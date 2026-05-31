ALTER TABLE cortes
ADD COLUMN IF NOT EXISTS estado BOOLEAN DEFAULT true;

ALTER TABLE cortes
ADD COLUMN IF NOT EXISTS publicado BOOLEAN DEFAULT false;

CREATE TABLE IF NOT EXISTS servicio_multimedia (
    id_servicio INT,
    url_recurso VARCHAR(255),

    PRIMARY KEY (id_servicio, url_recurso),

    CONSTRAINT fk_servicio_multimedia
        FOREIGN KEY (id_servicio)
        REFERENCES cortes(id_corte)
        ON DELETE CASCADE
);