CREATE TABLE contingencia_manual (
    contingencia_id INTEGER PRIMARY KEY CHECK(contingencia_id>0),
    equipo_id INTEGER NOT NULL REFERENCES equipo(equipo_id),
    actor TEXT NOT NULL CHECK(length(trim(actor))>0),
    fecha INTEGER NOT NULL,
    sesion_id INTEGER REFERENCES sesion_operacion(sesion_id),
    contexto TEXT NOT NULL CHECK(length(trim(contexto))>0),
    motivo TEXT NOT NULL CHECK(length(trim(motivo))>0),
    origen_registro TEXT NOT NULL DEFAULT 'MANUAL' CHECK(origen_registro='MANUAL'),
    verificacion_id INTEGER REFERENCES verificacion(verificacion_id),
    fecha_confirmacion INTEGER,
    actor_confirmacion TEXT,
    CHECK(verificacion_id IS NULL OR sesion_id IS NOT NULL),
    CHECK((fecha_confirmacion IS NULL AND actor_confirmacion IS NULL) OR
      (fecha_confirmacion IS NOT NULL AND fecha_confirmacion>=fecha AND actor_confirmacion IS NOT NULL AND length(trim(actor_confirmacion))>0))
) STRICT;
CREATE INDEX ix_contingencia_historial ON contingencia_manual(equipo_id,fecha,contingencia_id);
CREATE UNIQUE INDEX uq_contingencia_omision ON contingencia_manual(verificacion_id,equipo_id) WHERE verificacion_id IS NOT NULL;
CREATE TRIGGER validar_contingencia BEFORE INSERT ON contingencia_manual
WHEN NEW.fecha_confirmacion IS NOT NULL OR NEW.actor_confirmacion IS NOT NULL
 OR (NEW.sesion_id IS NOT NULL AND NOT EXISTS(SELECT 1 FROM sesion_operacion s
 WHERE s.sesion_id=NEW.sesion_id AND s.estado='ABIERTA' AND s.fecha_inicio<=NEW.fecha))
 OR (NEW.verificacion_id IS NOT NULL AND NOT EXISTS(SELECT 1 FROM verificacion v
 JOIN verificacion_item i ON i.verificacion_id=v.verificacion_id
 WHERE v.verificacion_id=NEW.verificacion_id AND v.sesion_id=NEW.sesion_id AND v.anterior_id IS NOT NULL
 AND v.fecha<=NEW.fecha AND i.equipo_id=NEW.equipo_id AND i.resultado='FALTANTE'
 AND NOT EXISTS(SELECT 1 FROM verificacion siguiente WHERE siguiente.anterior_id=v.verificacion_id)))
BEGIN SELECT RAISE(ABORT,'Contexto de contingencia invalido'); END;
CREATE TRIGGER proteger_contingencia BEFORE UPDATE ON contingencia_manual
WHEN OLD.fecha_confirmacion IS NOT NULL OR NEW.fecha_confirmacion IS NULL
 OR NEW.contingencia_id IS NOT OLD.contingencia_id OR NEW.equipo_id IS NOT OLD.equipo_id
 OR NEW.actor IS NOT OLD.actor OR NEW.fecha IS NOT OLD.fecha OR NEW.sesion_id IS NOT OLD.sesion_id
 OR NEW.contexto IS NOT OLD.contexto OR NEW.motivo IS NOT OLD.motivo
 OR NEW.origen_registro IS NOT OLD.origen_registro OR NEW.verificacion_id IS NOT OLD.verificacion_id
 OR (NEW.verificacion_id IS NOT NULL AND EXISTS(SELECT 1 FROM verificacion WHERE anterior_id=NEW.verificacion_id))
BEGIN SELECT RAISE(ABORT,'Solo se permite confirmar contingencia vigente conservando contexto'); END;
CREATE TRIGGER impedir_borrado_contingencia BEFORE DELETE ON contingencia_manual
BEGIN SELECT RAISE(ABORT,'Historial manual protegido'); END;
