package co.emi.trazabilidad.aplicacion.puertos;

import co.emi.trazabilidad.dominio.*;
import java.time.Instant;
import java.util.*;

public interface RepositorioI7 {
    Optional<Equipo> equipo(long id);
    SustitucionTemporal abrir(long equipo, String origen, String destino, String responsable, String motivo, Instant fecha);
    Optional<SustitucionTemporal> sustitucion(long id);
    Optional<SustitucionTemporal> activa(long equipo);
    List<SustitucionTemporal> historial(long equipo);
    boolean cerrar(long id, Instant fecha);
}
