package co.emi.trazabilidad.dominio;

import java.time.Instant;
import java.util.Objects;

/** Intervalo de asociacion; fechaFin nula identifica la asociacion vigente. */
public record AsignacionEtiqueta(long asignacionId, long equipoId, long etiquetaId,
                                Instant fechaInicio, Instant fechaFin, String motivoCambio) {
    public AsignacionEtiqueta {
        if (asignacionId <= 0 || equipoId <= 0 || etiquetaId <= 0) {
            throw new IllegalArgumentException("Los identificadores deben ser positivos");
        }
        Objects.requireNonNull(fechaInicio, "fechaInicio es obligatoria");
        if (fechaFin != null && fechaFin.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("fechaFin no puede preceder a fechaInicio");
        }
    }

    public boolean vigente() {
        return fechaFin == null;
    }
}
