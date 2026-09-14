package co.emi.trazabilidad.dominio;

import java.time.Instant;
import java.util.Objects;

/** Interpretacion inmutable. La procedencia se audita en sus lecturas fuente. */
public record EventoOperativo(long eventoId, long equipoId, long sesionId, TipoEvento tipoEvento,
        Instant timestamp, Instant fechaCreacion, long lecturaBaseId) {
    public EventoOperativo {
        if (eventoId <= 0 || equipoId <= 0 || sesionId <= 0 || lecturaBaseId <= 0)
            throw new IllegalArgumentException("Identificadores deben ser positivos");
        Objects.requireNonNull(tipoEvento);
        Objects.requireNonNull(timestamp);
        Objects.requireNonNull(fechaCreacion);
        if (fechaCreacion.isBefore(timestamp))
            throw new IllegalArgumentException("La creacion no puede preceder a la lectura base");
    }
}
