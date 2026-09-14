package co.emi.trazabilidad.aplicacion.puertos;

import co.emi.trazabilidad.aplicacion.consultas.*;
import co.emi.trazabilidad.dominio.*;
import java.util.*;

/** Puerto exclusivo de consulta. Orden cronologico por timestamp e ID, inverso para ultimo. */
public interface RepositorioI5 {
    Optional<Equipo> equipo(long id);
    Optional<Equipo> equipoPorCodigo(String codigo);
    Optional<LecturaAtribuida> ultimaLecturaPorEquipo(long id);
    List<LecturaAtribuida> historialLecturasPorEquipo(long id);
    Optional<EventoOperativo> ultimoEventoPorEquipo(long id);
    List<EventoConContexto> historialEventosPorEquipo(long id);
    Optional<EventoOperativo> evento(long id);
    List<EvidenciaEvento> evidenciasDeEvento(long id);
}
