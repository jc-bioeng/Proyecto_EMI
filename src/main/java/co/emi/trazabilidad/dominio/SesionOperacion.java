package co.emi.trazabilidad.dominio;

import java.time.Instant;
import java.util.Objects;

public record SesionOperacion(long sesionId, String puntoControl, String actorContexto,
        TipoOperacion tipoOperacion, Instant fechaInicio, Instant fechaFin,
        EstadoSesion estado, String contexto) {
    public SesionOperacion {
        if (sesionId <= 0) throw new IllegalArgumentException("sesionId debe ser positivo");
        if (puntoControl == null || puntoControl.isBlank() || actorContexto == null || actorContexto.isBlank())
            throw new IllegalArgumentException("Punto y actor/contexto son obligatorios");
        Objects.requireNonNull(tipoOperacion);
        Objects.requireNonNull(fechaInicio);
        Objects.requireNonNull(estado);
        if ((estado == EstadoSesion.ABIERTA) != (fechaFin == null))
            throw new IllegalArgumentException("Estado y cierre incompatibles");
        if (fechaFin != null && fechaFin.isBefore(fechaInicio))
            throw new IllegalArgumentException("Cierre anterior al inicio");
    }
}
