-- Sesiones: milisegundos UTC, como I1. Lecturas: segundos+nanos para conservar Instant de I2 sin perdida.
CREATE TABLE sesion_operacion (
    sesion_id INTEGER PRIMARY KEY CHECK (sesion_id > 0),
    punto_control TEXT NOT NULL CHECK (length(trim(punto_control)) > 0),
    actor_contexto TEXT NOT NULL CHECK (length(trim(actor_contexto)) > 0),
    tipo_operacion TEXT NOT NULL CHECK (tipo_operacion IN ('INGRESO', 'SALIDA', 'VERIFICACION')),
    fecha_inicio INTEGER NOT NULL,
    fecha_fin INTEGER,
    estado TEXT NOT NULL CHECK (estado IN ('ABIERTA', 'CERRADA')),
    contexto TEXT,
    CHECK ((estado = 'ABIERTA' AND fecha_fin IS NULL) OR
           (estado = 'CERRADA' AND fecha_fin IS NOT NULL AND fecha_fin >= fecha_inicio))
) STRICT;

CREATE TABLE lectura_rfid (
    lectura_id INTEGER PRIMARY KEY CHECK (lectura_id > 0),
    epc TEXT NOT NULL CHECK (length(trim(epc)) > 0),
    timestamp_segundos INTEGER NOT NULL CHECK (timestamp_segundos BETWEEN -31557014167219200 AND 31556889864403199),
    timestamp_nanos INTEGER NOT NULL CHECK (timestamp_nanos BETWEEN 0 AND 999999999),
    origen_datos TEXT NOT NULL CHECK (origen_datos IN ('SIMULACION', 'RF_REAL')),
    sesion_id INTEGER REFERENCES sesion_operacion(sesion_id) ON DELETE RESTRICT
) STRICT;
CREATE INDEX ix_lectura_sesion_tiempo ON lectura_rfid(sesion_id, timestamp_segundos, timestamp_nanos, lectura_id);

-- Mapa textual sin serializacion propietaria ni nueva dependencia. No es otra entidad operativa.
CREATE TABLE lectura_rfid_metadata (
    lectura_id INTEGER NOT NULL REFERENCES lectura_rfid(lectura_id) ON DELETE RESTRICT,
    clave TEXT NOT NULL,
    valor TEXT NOT NULL,
    PRIMARY KEY (lectura_id, clave)
) STRICT;

CREATE TRIGGER impedir_borrado_sesion BEFORE DELETE ON sesion_operacion
BEGIN SELECT RAISE(ABORT, 'No se permite eliminar sesiones'); END;
CREATE TRIGGER proteger_contexto_sesion BEFORE UPDATE ON sesion_operacion
WHEN OLD.estado = 'CERRADA' OR NEW.estado != 'CERRADA'
 OR NEW.sesion_id IS NOT OLD.sesion_id OR NEW.punto_control IS NOT OLD.punto_control
 OR NEW.actor_contexto IS NOT OLD.actor_contexto OR NEW.tipo_operacion IS NOT OLD.tipo_operacion
 OR NEW.fecha_inicio IS NOT OLD.fecha_inicio OR NEW.contexto IS NOT OLD.contexto
BEGIN SELECT RAISE(ABORT, 'Solo se permite cerrar una sesion abierta conservando contexto'); END;
CREATE TRIGGER impedir_borrado_lectura BEFORE DELETE ON lectura_rfid
BEGIN SELECT RAISE(ABORT, 'No se permite eliminar evidencia cruda'); END;
CREATE TRIGGER impedir_cambio_lectura BEFORE UPDATE ON lectura_rfid
BEGIN SELECT RAISE(ABORT, 'No se permite sobrescribir evidencia cruda'); END;
CREATE TRIGGER impedir_borrado_metadata BEFORE DELETE ON lectura_rfid_metadata
BEGIN SELECT RAISE(ABORT, 'No se permite eliminar metadata'); END;
CREATE TRIGGER impedir_cambio_metadata BEFORE UPDATE ON lectura_rfid_metadata
BEGIN SELECT RAISE(ABORT, 'No se permite sobrescribir metadata'); END;
