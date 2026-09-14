package co.emi.trazabilidad.aplicacion.puertos;

import co.emi.trazabilidad.dominio.*;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Todas las lecturas y escrituras comparten la conexion de la unidad de trabajo. */
public interface RepositorioI4 {
    Optional<LecturaRFID> lectura(long id);
    Optional<SesionOperacion> sesion(long id);
    List<LecturaRFID> lecturasDeSesion(long id);
    List<AsignacionEtiqueta> asociacionesPorEpc(String epc);
    Optional<EventoOperativo> evento(long id);
    Optional<EventoOperativo> eventoDeLectura(long lecturaId);
    Optional<EventoOperativo> eventoEquivalente(long sesionId, long equipoId, TipoEvento tipo);
    List<EventoOperativo> eventosDeSesion(long sesionId);
    List<EvidenciaEvento> evidenciasDeEvento(long eventoId);
    EventoOperativo crearEvento(long equipoId, long sesionId, TipoEvento tipo, LecturaRFID base, Instant creacion);
    void vincular(long eventoId, long lecturaId, long asignacionId, Instant vinculacion);
}
