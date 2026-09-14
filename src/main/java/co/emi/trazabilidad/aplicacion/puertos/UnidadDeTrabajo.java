package co.emi.trazabilidad.aplicacion.puertos;

import java.util.function.Function;

public interface UnidadDeTrabajo {
    /** Toda la operacion comparte una transaccion; cualquier fallo revierte sus escrituras. */
    <T> T ejecutar(Function<RepositorioI1, T> operacion);

    /** Extension I3; conserva el contrato y los implementadores existentes de I1. */
    default <T> T ejecutarI3(Function<RepositorioI3, T> operacion) {
        throw new UnsupportedOperationException("Esta unidad no ofrece persistencia I3");
    }
    /** Extension I4 compatible con los implementadores anteriores. */
    default <T> T ejecutarI4(Function<RepositorioI4, T> operacion) {
        throw new UnsupportedOperationException("Esta unidad no ofrece persistencia I4");
    }
    /** Extension I5 de solo consulta, compatible con implementadores anteriores. */
    default <T> T ejecutarI5(Function<RepositorioI5, T> consulta) {
        throw new UnsupportedOperationException("Esta unidad no ofrece consultas I5");
    }
}
