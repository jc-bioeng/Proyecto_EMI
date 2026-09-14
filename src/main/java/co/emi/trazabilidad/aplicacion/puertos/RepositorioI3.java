package co.emi.trazabilidad.aplicacion.puertos;

import co.emi.trazabilidad.dominio.*;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Datos I3 dentro de una unica transaccion; no expone JDBC ni SDK. */
public interface RepositorioI3 {
    SesionOperacion crearSesion(String punto, String actor, TipoOperacion tipo, Instant inicio, String contexto);
    Optional<SesionOperacion> sesion(long id);
    boolean cerrarSesion(long id, Instant fin);
    LecturaRFID crearLectura(LecturaEntradaRFID entrada, Long sesionId);
    Optional<LecturaRFID> lectura(long id);
    List<LecturaRFID> lecturasDeSesion(long sesionId);
}
