CREATE TABLE sustitucion_temporal (
    sustitucion_id INTEGER PRIMARY KEY CHECK(sustitucion_id>0),
    equipo_id INTEGER NOT NULL REFERENCES equipo(equipo_id),
    origen TEXT NOT NULL CHECK(length(trim(origen))>0),
    destino TEXT NOT NULL CHECK(length(trim(destino))>0),
    responsable TEXT NOT NULL CHECK(length(trim(responsable))>0),
    motivo TEXT NOT NULL CHECK(length(trim(motivo))>0),
    fecha_apertura INTEGER NOT NULL,
    fecha_cierre INTEGER CHECK(fecha_cierre IS NULL OR fecha_cierre>=fecha_apertura)
) STRICT;
CREATE UNIQUE INDEX uq_sustitucion_activa ON sustitucion_temporal(equipo_id) WHERE fecha_cierre IS NULL;
CREATE INDEX ix_sustitucion_historial ON sustitucion_temporal(equipo_id,fecha_apertura,sustitucion_id);
CREATE TRIGGER validar_apertura_sustitucion BEFORE INSERT ON sustitucion_temporal
WHEN NEW.fecha_cierre IS NOT NULL OR EXISTS(SELECT 1 FROM sustitucion_temporal
 WHERE equipo_id=NEW.equipo_id AND fecha_cierre>NEW.fecha_apertura)
BEGIN SELECT RAISE(ABORT,'Apertura de sustitucion invalida'); END;
CREATE TRIGGER proteger_sustitucion BEFORE UPDATE ON sustitucion_temporal
WHEN OLD.fecha_cierre IS NOT NULL OR NEW.fecha_cierre IS NULL
 OR NEW.sustitucion_id IS NOT OLD.sustitucion_id OR NEW.equipo_id IS NOT OLD.equipo_id
 OR NEW.origen IS NOT OLD.origen OR NEW.destino IS NOT OLD.destino
 OR NEW.responsable IS NOT OLD.responsable OR NEW.motivo IS NOT OLD.motivo
 OR NEW.fecha_apertura IS NOT OLD.fecha_apertura
BEGIN SELECT RAISE(ABORT,'Solo se permite devolver una sustitucion activa'); END;
CREATE TRIGGER impedir_borrado_sustitucion BEFORE DELETE ON sustitucion_temporal
BEGIN SELECT RAISE(ABORT,'Historial de sustituciones protegido'); END;
