-- Fechas INTEGER: milisegundos desde Unix epoch (UTC).
-- STRICT evita que SQLite acepte valores de tipo incompatible.
CREATE TABLE equipo (
    equipo_id INTEGER PRIMARY KEY CHECK (equipo_id > 0),
    codigo_institucional TEXT NOT NULL UNIQUE CHECK (length(trim(codigo_institucional)) > 0),
    descripcion TEXT,
    activo_piloto INTEGER NOT NULL CHECK (activo_piloto IN (0, 1))
) STRICT;

CREATE TABLE etiqueta_rfid (
    etiqueta_id INTEGER PRIMARY KEY CHECK (etiqueta_id > 0),
    epc TEXT NOT NULL UNIQUE CHECK (length(trim(epc)) > 0),
    estado TEXT NOT NULL CHECK (estado IN ('ACTIVA', 'INACTIVA'))
) STRICT;

CREATE TABLE asignacion_etiqueta (
    asignacion_id INTEGER PRIMARY KEY CHECK (asignacion_id > 0),
    equipo_id INTEGER NOT NULL REFERENCES equipo(equipo_id) ON DELETE RESTRICT,
    etiqueta_id INTEGER NOT NULL REFERENCES etiqueta_rfid(etiqueta_id) ON DELETE RESTRICT,
    fecha_inicio INTEGER NOT NULL,
    fecha_fin INTEGER,
    motivo_cambio TEXT,
    CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio)
) STRICT;

CREATE UNIQUE INDEX uq_asignacion_equipo_vigente
    ON asignacion_etiqueta(equipo_id) WHERE fecha_fin IS NULL;
CREATE UNIQUE INDEX uq_asignacion_etiqueta_vigente
    ON asignacion_etiqueta(etiqueta_id) WHERE fecha_fin IS NULL;
CREATE INDEX ix_asignacion_equipo_historial
    ON asignacion_etiqueta(equipo_id, fecha_inicio, asignacion_id);
CREATE INDEX ix_asignacion_etiqueta_historial
    ON asignacion_etiqueta(etiqueta_id, fecha_inicio, asignacion_id);

-- La correccion cierra e inserta. No elimina registros historicos.
CREATE TRIGGER impedir_borrado_asignacion
BEFORE DELETE ON asignacion_etiqueta
BEGIN
    SELECT RAISE(ABORT, 'No se permite eliminar el historial de asociaciones');
END;
