package co.emi.trazabilidad.aplicacion.puertos;

import co.emi.trazabilidad.dominio.*;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

/** Operaciones de datos ligadas a una unidad de trabajo; no expone JDBC. */
public interface RepositorioI1 {
    Equipo crearEquipo(String codigo, String descripcion, boolean activoPiloto);
    EtiquetaRFID crearEtiqueta(String epc, EstadoEtiqueta estado);
    Optional<Equipo> equipo(long id);
    Optional<Equipo> equipoPorCodigo(String codigo);
    Optional<EtiquetaRFID> etiqueta(long id);
    Optional<EtiquetaRFID> etiquetaPorEpc(String epc);
    Optional<AsignacionEtiqueta> vigenteDeEquipo(long equipoId);
    Optional<AsignacionEtiqueta> vigenteDeEtiqueta(long etiquetaId);
    AsignacionEtiqueta crearAsignacion(long equipoId, long etiquetaId, Instant inicio);
    boolean cerrarAsignacion(long id, Instant fin, String motivo);
    List<AsignacionEtiqueta> historial(long equipoId);
}
