-- Intentos inmutables; fechas de proceso en milisegundos UTC como SesionOperacion.
CREATE TABLE verificacion (
    verificacion_id INTEGER PRIMARY KEY CHECK(verificacion_id > 0),
    sesion_id INTEGER NOT NULL REFERENCES sesion_operacion(sesion_id),
    fecha INTEGER NOT NULL,
    anterior_id INTEGER UNIQUE REFERENCES verificacion(verificacion_id),
    CHECK(anterior_id IS NULL OR anterior_id < verificacion_id)
) STRICT;
CREATE UNIQUE INDEX uq_verificacion_inicial ON verificacion(sesion_id) WHERE anterior_id IS NULL;
CREATE INDEX ix_verificacion_sesion ON verificacion(sesion_id, verificacion_id);
CREATE TABLE verificacion_item (
    verificacion_id INTEGER NOT NULL REFERENCES verificacion(verificacion_id),
    equipo_id INTEGER NOT NULL REFERENCES equipo(equipo_id),
    resultado TEXT NOT NULL CHECK(resultado IN ('DETECTADO','FALTANTE','NO_ESPERADO')),
    PRIMARY KEY(verificacion_id, equipo_id)
) STRICT;
-- EPC, timestamp y origen permanecen en la lectura inmutable enlazada.
CREATE TABLE verificacion_lectura (
    verificacion_id INTEGER NOT NULL REFERENCES verificacion(verificacion_id),
    lectura_id INTEGER NOT NULL REFERENCES lectura_rfid(lectura_id),
    equipo_id INTEGER,
    asignacion_id INTEGER REFERENCES asignacion_etiqueta(asignacion_id),
    estado TEXT NOT NULL CHECK(estado IN ('ATRIBUIDA','SIN_ASOCIACION','ASOCIACION_AMBIGUA','ANTERIOR_A_SESION','LECTURA_FUTURA')),
    PRIMARY KEY(verificacion_id, lectura_id),
    FOREIGN KEY(verificacion_id,equipo_id) REFERENCES verificacion_item(verificacion_id,equipo_id),
    CHECK((estado='ATRIBUIDA' AND equipo_id IS NOT NULL AND asignacion_id IS NOT NULL)
       OR (estado!='ATRIBUIDA' AND equipo_id IS NULL AND asignacion_id IS NULL))
) STRICT;
CREATE TRIGGER validar_verificacion BEFORE INSERT ON verificacion
WHEN NOT EXISTS (SELECT 1 FROM sesion_operacion s WHERE s.sesion_id=NEW.sesion_id
 AND s.tipo_operacion='VERIFICACION' AND s.estado='ABIERTA' AND NEW.fecha>=s.fecha_inicio)
 OR (NEW.anterior_id IS NOT NULL AND NOT EXISTS (SELECT 1 FROM verificacion v
 WHERE v.verificacion_id=NEW.anterior_id AND v.sesion_id=NEW.sesion_id AND v.fecha<=NEW.fecha))
BEGIN SELECT RAISE(ABORT,'Contexto o reintento invalido'); END;
CREATE TRIGGER validar_evidencia_verificacion BEFORE INSERT ON verificacion_lectura
WHEN NOT EXISTS (SELECT 1 FROM lectura_rfid l JOIN verificacion v ON v.sesion_id=l.sesion_id
 WHERE l.lectura_id=NEW.lectura_id AND v.verificacion_id=NEW.verificacion_id)
 OR (NEW.estado='ATRIBUIDA' AND NOT EXISTS (
 SELECT 1 FROM asignacion_etiqueta a JOIN etiqueta_rfid t ON t.etiqueta_id=a.etiqueta_id
 JOIN lectura_rfid l ON l.epc=t.epc JOIN verificacion v ON v.verificacion_id=NEW.verificacion_id
 JOIN sesion_operacion s ON s.sesion_id=v.sesion_id
 JOIN verificacion_item i ON i.verificacion_id=v.verificacion_id AND i.equipo_id=a.equipo_id
 WHERE l.lectura_id=NEW.lectura_id AND a.asignacion_id=NEW.asignacion_id AND a.equipo_id=NEW.equipo_id
 AND i.resultado!='FALTANTE'
 AND (l.timestamp_segundos,l.timestamp_nanos) >= ((s.fecha_inicio/1000)-(s.fecha_inicio%1000<0),((s.fecha_inicio%1000+1000)%1000)*1000000)
 AND (l.timestamp_segundos,l.timestamp_nanos) <= ((v.fecha/1000)-(v.fecha%1000<0),((v.fecha%1000+1000)%1000)*1000000)
 AND 1=(SELECT count(*) FROM asignacion_etiqueta otra WHERE otra.etiqueta_id=a.etiqueta_id
 AND (l.timestamp_segundos,l.timestamp_nanos) >= ((otra.fecha_inicio/1000)-(otra.fecha_inicio%1000<0),((otra.fecha_inicio%1000+1000)%1000)*1000000)
 AND (otra.fecha_fin IS NULL OR (l.timestamp_segundos,l.timestamp_nanos) < ((otra.fecha_fin/1000)-(otra.fecha_fin%1000<0),((otra.fecha_fin%1000+1000)%1000)*1000000)))
 AND (l.timestamp_segundos,l.timestamp_nanos) >= ((a.fecha_inicio/1000)-(a.fecha_inicio%1000<0),((a.fecha_inicio%1000+1000)%1000)*1000000)
 AND (a.fecha_fin IS NULL OR (l.timestamp_segundos,l.timestamp_nanos) < ((a.fecha_fin/1000)-(a.fecha_fin%1000<0),((a.fecha_fin%1000+1000)%1000)*1000000))
 ))
BEGIN SELECT RAISE(ABORT,'Respaldo de verificacion invalido'); END;
CREATE TRIGGER impedir_cambio_verificacion BEFORE UPDATE ON verificacion
BEGIN SELECT RAISE(ABORT,'Intento inmutable'); END;
CREATE TRIGGER impedir_borrado_verificacion BEFORE DELETE ON verificacion
BEGIN SELECT RAISE(ABORT,'Historial de verificacion protegido'); END;
CREATE TRIGGER impedir_cambio_verificacion_item BEFORE UPDATE ON verificacion_item
BEGIN SELECT RAISE(ABORT,'Resultado inmutable'); END;
CREATE TRIGGER impedir_borrado_verificacion_item BEFORE DELETE ON verificacion_item
BEGIN SELECT RAISE(ABORT,'Resultado protegido'); END;
CREATE TRIGGER impedir_cambio_verificacion_lectura BEFORE UPDATE ON verificacion_lectura
BEGIN SELECT RAISE(ABORT,'Evidencia inmutable'); END;
CREATE TRIGGER impedir_borrado_verificacion_lectura BEFORE DELETE ON verificacion_lectura
BEGIN SELECT RAISE(ABORT,'Evidencia protegida'); END;
