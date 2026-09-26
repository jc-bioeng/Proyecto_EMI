package co.emi.trazabilidad.aplicacion.puertos;

import java.util.function.Function;

/** Puertos aditivos: no cambian el contrato publico de UnidadDeTrabajo I1-I5. */
public interface UnidadDeTrabajoMvp {
    <T> T ejecutarI6(Function<RepositorioI6, T> operacion);
    <T> T ejecutarI7(Function<RepositorioI7, T> operacion);
    <T> T ejecutarI8(Function<RepositorioI8, T> operacion);
}
