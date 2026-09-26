package co.emi.trazabilidad.dominio;

import java.time.Instant;
import java.util.Objects;

public record SustitucionTemporal(long sustitucionId, long equipoId, String origen, String destino,
        String responsable, String motivo, Instant fechaApertura, Instant fechaCierre) {
    public SustitucionTemporal {
        if (sustitucionId <= 0 || equipoId <= 0) throw new IllegalArgumentException("Identificadores invalidos");
        for (String texto : new String[]{origen,destino,responsable,motivo})
            if (texto == null || texto.isBlank()) throw new IllegalArgumentException("Contexto obligatorio");
        Objects.requireNonNull(fechaApertura);
        if (fechaCierre != null && fechaCierre.isBefore(fechaApertura)) throw new IllegalArgumentException("Cierre anterior a apertura");
    }
    public boolean activa() { return fechaCierre == null; }
}
