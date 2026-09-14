-- I5: indices justificados por EXPLAIN QUERY PLAN; sin entidades ni cambios de datos.
CREATE INDEX ix_lectura_epc_tiempo
    ON lectura_rfid(epc, timestamp_segundos, timestamp_nanos, lectura_id);
CREATE INDEX ix_evento_equipo_tiempo
    ON evento_operativo(equipo_id, timestamp_segundos, timestamp_nanos, evento_id);
