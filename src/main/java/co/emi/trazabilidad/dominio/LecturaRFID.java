package co.emi.trazabilidad.dominio;

import java.time.Instant;
import java.util.Map;

/** Evidencia persistida inmutable; sesionId null significa contexto no suministrado. */
public record LecturaRFID(long lecturaId, String epc, Instant timestamp, OrigenDatos origenDatos,
        Long sesionId, Map<String, String> metadata) {
    public LecturaRFID {
        if (lecturaId <= 0 || (sesionId != null && sesionId <= 0))
            throw new IllegalArgumentException("Identificadores deben ser positivos");
        metadata = new LecturaEntradaRFID(epc, timestamp, origenDatos, metadata).metadata();
    }
}
