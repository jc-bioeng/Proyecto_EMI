package co.emi.trazabilidad.dominio;

import java.time.Instant;
import java.util.Objects;

/** Registro manual independiente de OrigenDatos RFID; confirmacion no genera movimiento. */
public record ContingenciaManual(long contingenciaId, long equipoId, String actor, Instant fecha,
        Long sesionId, String contexto, String motivo, Long verificacionId,
        Instant fechaConfirmacion, String actorConfirmacion) {
    public ContingenciaManual {
        if (contingenciaId<=0 || equipoId<=0 || (sesionId!=null && sesionId<=0) || (verificacionId!=null && verificacionId<=0))
            throw new IllegalArgumentException("Identificadores invalidos");
        for (String texto : new String[]{actor,contexto,motivo})
            if (texto==null || texto.isBlank()) throw new IllegalArgumentException("Contexto manual obligatorio");
        Objects.requireNonNull(fecha);
        if (verificacionId!=null && sesionId==null) throw new IllegalArgumentException("Omitido requiere sesion");
        if ((fechaConfirmacion==null)!=(actorConfirmacion==null)) throw new IllegalArgumentException("Confirmacion incompleta");
        if (fechaConfirmacion!=null && (fechaConfirmacion.isBefore(fecha) || actorConfirmacion.isBlank()))
            throw new IllegalArgumentException("Confirmacion invalida");
    }
    public String origenRegistro() { return "MANUAL"; }
    public boolean confirmada() { return fechaConfirmacion!=null; }
}
