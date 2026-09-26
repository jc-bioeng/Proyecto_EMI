package co.emi.trazabilidad.aplicacion.puertos;

import co.emi.trazabilidad.dominio.*;
import java.time.Instant;
import java.util.*;

public interface RepositorioI6 {
    Optional<Equipo> equipo(long id);
    Optional<SesionOperacion> sesion(long id);
    List<LecturaRFID> lecturasDeSesion(long id);
    List<AsignacionEtiqueta> asociacionesPorEpc(String epc);
    Verificacion crear(long sesion, Instant fecha, Long anterior);
    void agregarItem(VerificacionItem item);
    void agregarEvidencia(EvidenciaVerificacion evidencia);
    Optional<Verificacion> verificacion(long id);
    List<Verificacion> historial(long sesion);
    List<VerificacionItem> items(long id);
    List<EvidenciaVerificacion> evidencias(long id);
}
