package co.emi.trazabilidad.dominio;

import java.util.Objects;
import java.time.Instant;

/** Vinculo auditable con la asignacion historica usada y la lectura intacta. */
public record EvidenciaEvento(long eventoId, long asignacionId, LecturaRFID lectura, Instant fechaVinculacion) {
    public EvidenciaEvento {
        if (eventoId <= 0 || asignacionId <= 0) throw new IllegalArgumentException("Identificadores deben ser positivos");
        Objects.requireNonNull(lectura);
        Objects.requireNonNull(fechaVinculacion);
        if (fechaVinculacion.isBefore(lectura.timestamp())) throw new IllegalArgumentException("Vinculacion anterior a lectura");
    }
}
