package co.emi.trazabilidad.dominio;

import java.util.Objects;

/** La condicion asignada se consulta en AsignacionEtiqueta, no en estado. */
public record EtiquetaRFID(long etiquetaId, String epc, EstadoEtiqueta estado) {
    public EtiquetaRFID {
        if (etiquetaId <= 0) throw new IllegalArgumentException("etiquetaId debe ser positivo");
        if (epc == null || epc.isBlank()) throw new IllegalArgumentException("epc es obligatorio");
        Objects.requireNonNull(estado, "estado es obligatorio");
    }
}
