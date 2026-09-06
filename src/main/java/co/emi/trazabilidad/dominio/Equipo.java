package co.emi.trazabilidad.dominio;

/** Identidad institucional independiente de la etiqueta RFID. */
public record Equipo(long equipoId, String codigoInstitucional, String descripcion,
                     boolean activoPiloto) {
    public Equipo {
        if (equipoId <= 0) throw new IllegalArgumentException("equipoId debe ser positivo");
        if (codigoInstitucional == null || codigoInstitucional.isBlank()) {
            throw new IllegalArgumentException("codigoInstitucional es obligatorio");
        }
    }
}
