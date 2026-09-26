package co.emi.trazabilidad.dominio;

import java.util.Objects;

/** Un resultado por equipo. Los EPC y origenes se consultan en las evidencias. */
public record VerificacionItem(long verificacionId, long equipoId, ResultadoVerificacion resultado) {
    public VerificacionItem {
        if (verificacionId <= 0 || equipoId <= 0) throw new IllegalArgumentException("Identificadores invalidos");
        Objects.requireNonNull(resultado);
    }
}
