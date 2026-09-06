package co.emi.trazabilidad.aplicacion.puertos;

import java.util.function.Function;

public interface UnidadDeTrabajo {
    /** Toda la operacion comparte una transaccion; cualquier fallo revierte sus escrituras. */
    <T> T ejecutar(Function<RepositorioI1, T> operacion);
}
