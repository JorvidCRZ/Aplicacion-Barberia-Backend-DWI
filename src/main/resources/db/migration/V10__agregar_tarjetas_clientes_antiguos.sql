INSERT INTO recompensa (id_cliente, cortes_acumulados, cortes_gratis, fecha_actualizacion)
SELECT id_cliente, 0, 0, NOW()
FROM cliente
WHERE id_cliente NOT IN (SELECT id_cliente FROM recompensa);