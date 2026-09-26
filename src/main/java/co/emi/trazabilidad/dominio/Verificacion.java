package co.emi.trazabilidad.dominio;

import java.time.Instant;
import java.util.Objects;

/** Cabecera inmutable de un intento; anteriorId enlaza un reintento explicito. */
public record Verificacion(long verificacionId, long sesionId, Instant fecha, Long anteriorId) {
    public Verificacion {
        if (verificacionId <= 0 || sesionId <= 0 || (anteriorId != null && anteriorId <= 0))
            throw new IllegalArgumentException("Identificadores deben ser positivos");
        Objects.requireNonNull(fecha);
    }
}
