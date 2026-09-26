package co.emi.trazabilidad.aplicacion.puertos;

import co.emi.trazabilidad.dominio.*;
import java.time.Instant;
import java.util.*;

public interface RepositorioI8 {
    Optional<Equipo> equipo(long id);
    Optional<SesionOperacion> sesion(long id);
    Optional<Verificacion> verificacion(long id);
    List<VerificacionItem> items(long id);
    List<Verificacion> verificaciones(long sesion);
    ContingenciaManual registrar(long equipo,String actor,Instant fecha,Long sesion,String contexto,String motivo,Long verificacion);
    Optional<ContingenciaManual> contingencia(long id);
    List<ContingenciaManual> historial(long equipo);
    boolean confirmar(long id,Instant fecha,String actor);
}
