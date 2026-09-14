-- I4: eventos derivados de contexto explicito; no cambia ni elimina lecturas.
-- Comparaciones temporales por pares INTEGER, sin REAL ni truncamiento de nanos.
CREATE TABLE evento_operativo (
    evento_id INTEGER PRIMARY KEY CHECK (evento_id > 0),
    equipo_id INTEGER NOT NULL REFERENCES equipo(equipo_id) ON DELETE RESTRICT,
    sesion_id INTEGER NOT NULL REFERENCES sesion_operacion(sesion_id) ON DELETE RESTRICT,
    tipo_evento TEXT NOT NULL CHECK (tipo_evento IN ('INGRESO', 'SALIDA')),
    timestamp_segundos INTEGER NOT NULL CHECK (timestamp_segundos BETWEEN -31557014167219200 AND 31556889864403199),
    timestamp_nanos INTEGER NOT NULL CHECK (timestamp_nanos BETWEEN 0 AND 999999999),
    creacion_segundos INTEGER NOT NULL CHECK (creacion_segundos BETWEEN -31557014167219200 AND 31556889864403199),
    creacion_nanos INTEGER NOT NULL CHECK (creacion_nanos BETWEEN 0 AND 999999999),
    lectura_base_id INTEGER NOT NULL REFERENCES lectura_rfid(lectura_id) ON DELETE RESTRICT,
    CHECK ((creacion_segundos, creacion_nanos) >= (timestamp_segundos, timestamp_nanos)),
    UNIQUE (sesion_id, equipo_id, tipo_evento),
    FOREIGN KEY (evento_id, lectura_base_id) REFERENCES evento_lectura(evento_id, lectura_id)
        DEFERRABLE INITIALLY DEFERRED
) STRICT;

CREATE TABLE evento_lectura (
    lectura_id INTEGER PRIMARY KEY REFERENCES lectura_rfid(lectura_id) ON DELETE RESTRICT CHECK (lectura_id > 0),
    evento_id INTEGER NOT NULL REFERENCES evento_operativo(evento_id) ON DELETE RESTRICT,
    asignacion_id INTEGER NOT NULL REFERENCES asignacion_etiqueta(asignacion_id) ON DELETE RESTRICT,
    vinculacion_segundos INTEGER NOT NULL CHECK (vinculacion_segundos BETWEEN -31557014167219200 AND 31556889864403199),
    vinculacion_nanos INTEGER NOT NULL CHECK (vinculacion_nanos BETWEEN 0 AND 999999999),
    UNIQUE (evento_id, lectura_id)
) STRICT;
CREATE INDEX ix_evento_sesion_tiempo ON evento_operativo(sesion_id, timestamp_segundos, timestamp_nanos, evento_id);
CREATE INDEX ix_evento_lectura_asignacion ON evento_lectura(asignacion_id);

-- El tipo procede de la sesion y el timestamp de su lectura base identificada.
CREATE TRIGGER validar_contexto_evento BEFORE INSERT ON evento_operativo
WHEN NOT EXISTS (
    SELECT 1 FROM sesion_operacion s JOIN lectura_rfid l ON l.sesion_id = s.sesion_id
    WHERE s.sesion_id = NEW.sesion_id AND s.estado = 'ABIERTA' AND s.tipo_operacion = NEW.tipo_evento
      AND l.lectura_id = NEW.lectura_base_id
      AND (l.timestamp_segundos, l.timestamp_nanos) = (NEW.timestamp_segundos, NEW.timestamp_nanos)
      AND (l.timestamp_segundos, l.timestamp_nanos) >= ((s.fecha_inicio / 1000) - (s.fecha_inicio % 1000 < 0), ((s.fecha_inicio % 1000 + 1000) % 1000) * 1000000)
)
BEGIN SELECT RAISE(ABORT, 'Contexto o lectura base de evento incompatible'); END;

-- Respaldo: misma sesion/equipo, EPC literal, intervalo historico unico y reloj compatible.
CREATE TRIGGER validar_respaldo_evento BEFORE INSERT ON evento_lectura
WHEN NOT EXISTS (
    SELECT 1 FROM evento_operativo e
    JOIN sesion_operacion s ON s.sesion_id = e.sesion_id
    JOIN lectura_rfid l ON l.sesion_id = e.sesion_id
    JOIN asignacion_etiqueta a ON a.equipo_id = e.equipo_id
    JOIN etiqueta_rfid t ON t.etiqueta_id = a.etiqueta_id AND t.epc = l.epc
    WHERE e.evento_id = NEW.evento_id AND l.lectura_id = NEW.lectura_id AND a.asignacion_id = NEW.asignacion_id
      AND s.estado = 'ABIERTA' AND s.tipo_operacion = e.tipo_evento
      AND (l.timestamp_segundos, l.timestamp_nanos) >= ((s.fecha_inicio / 1000) - (s.fecha_inicio % 1000 < 0), ((s.fecha_inicio % 1000 + 1000) % 1000) * 1000000)
      AND (l.timestamp_segundos, l.timestamp_nanos) >= ((a.fecha_inicio / 1000) - (a.fecha_inicio % 1000 < 0), ((a.fecha_inicio % 1000 + 1000) % 1000) * 1000000)
      AND (a.fecha_fin IS NULL OR (l.timestamp_segundos, l.timestamp_nanos) < ((a.fecha_fin / 1000) - (a.fecha_fin % 1000 < 0), ((a.fecha_fin % 1000 + 1000) % 1000) * 1000000))
      AND (NEW.vinculacion_segundos, NEW.vinculacion_nanos) >= (l.timestamp_segundos, l.timestamp_nanos)
      AND (NEW.vinculacion_segundos, NEW.vinculacion_nanos) >= (e.creacion_segundos, e.creacion_nanos)
) OR 1 != (
    SELECT count(*) FROM lectura_rfid l
    JOIN etiqueta_rfid t ON t.epc = l.epc JOIN asignacion_etiqueta a ON a.etiqueta_id = t.etiqueta_id
    WHERE l.lectura_id = NEW.lectura_id AND (l.timestamp_segundos, l.timestamp_nanos) >= ((a.fecha_inicio / 1000) - (a.fecha_inicio % 1000 < 0), ((a.fecha_inicio % 1000 + 1000) % 1000) * 1000000)
      AND (a.fecha_fin IS NULL OR (l.timestamp_segundos, l.timestamp_nanos) < ((a.fecha_fin / 1000) - (a.fecha_fin % 1000 < 0), ((a.fecha_fin % 1000 + 1000) % 1000) * 1000000))
)
BEGIN SELECT RAISE(ABORT, 'Respaldo sin contexto o asociacion historica unica compatible'); END;

CREATE TRIGGER impedir_borrado_evento BEFORE DELETE ON evento_operativo
BEGIN SELECT RAISE(ABORT, 'No se permite borrar eventos'); END;
CREATE TRIGGER impedir_cambio_evento BEFORE UPDATE ON evento_operativo
BEGIN SELECT RAISE(ABORT, 'No se permite reescribir eventos'); END;
CREATE TRIGGER impedir_borrado_evento_lectura BEFORE DELETE ON evento_lectura
BEGIN SELECT RAISE(ABORT, 'No se permite borrar respaldos'); END;
CREATE TRIGGER impedir_cambio_evento_lectura BEFORE UPDATE ON evento_lectura
BEGIN SELECT RAISE(ABORT, 'No se permite reasignar respaldos'); END;
-- Protege tambien INSERT OR REPLACE, sin depender de recursive_triggers.
CREATE TRIGGER impedir_reemplazo_evento BEFORE INSERT ON evento_operativo
WHEN EXISTS (SELECT 1 FROM evento_operativo WHERE evento_id = NEW.evento_id
 OR (sesion_id = NEW.sesion_id AND equipo_id = NEW.equipo_id AND tipo_evento = NEW.tipo_evento))
BEGIN SELECT RAISE(ABORT, 'Evento equivalente ya existe'); END;
CREATE TRIGGER impedir_reemplazo_respaldo BEFORE INSERT ON evento_lectura
WHEN EXISTS (SELECT 1 FROM evento_lectura WHERE lectura_id = NEW.lectura_id)
BEGIN SELECT RAISE(ABORT, 'Lectura ya vinculada'); END;
