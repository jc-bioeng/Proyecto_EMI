package co.emi.trazabilidad.aplicacion;

import java.time.*;
import static co.emi.trazabilidad.aplicacion.ErrorAplicacion.Codigo.DATO_INVALIDO;

final class ValidacionMvp {
    private ValidacionMvp() { }
    static void id(long id) { if (id <= 0) throw new ErrorAplicacion(DATO_INVALIDO, "Identificador debe ser positivo"); }
    static void texto(String valor) { if (valor == null || valor.isBlank()) throw new ErrorAplicacion(DATO_INVALIDO, "Texto obligatorio"); }
    static Instant ahora(Clock reloj) { return Instant.ofEpochMilli(reloj.instant().toEpochMilli()); }
}
