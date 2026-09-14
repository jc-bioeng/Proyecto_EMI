package co.emi.trazabilidad.aplicacion;

import co.emi.trazabilidad.aplicacion.puertos.*;
import co.emi.trazabilidad.dominio.*;
import java.time.*;
import java.util.*;
import static co.emi.trazabilidad.aplicacion.ErrorAplicacion.Codigo.*;

/** Persistencia cruda y contexto; ninguna inferencia de eventos o de equipo. */
public final class ServicioI3 {
    private final UnidadDeTrabajo unidad;
    private final Clock reloj;
    public ServicioI3(UnidadDeTrabajo unidad, Clock reloj) {
        this.unidad = Objects.requireNonNull(unidad);
        this.reloj = Objects.requireNonNull(reloj);
    }
    public SesionOperacion crearSesion(String punto, String actor, TipoOperacion tipo, String contexto) {
        if (punto == null || punto.isBlank() || actor == null || actor.isBlank() || tipo == null)
            throw new ErrorAplicacion(DATO_INVALIDO, "Punto, actor/contexto y tipo son obligatorios");
        return unidad.ejecutarI3(r -> r.crearSesion(punto, actor, tipo, ahora(), contexto));
    }
    public Optional<SesionOperacion> consultarSesion(long id) {
        identificador(id);
        return unidad.ejecutarI3(r -> r.sesion(id));
    }
    public SesionOperacion cerrarSesion(long id) {
        identificador(id);
        return unidad.ejecutarI3(r -> {
            var sesion = exigirSesion(r, id);
            if (sesion.estado() == EstadoSesion.CERRADA)
                throw new ErrorAplicacion(CONFLICTO, "La sesion ya esta cerrada");
            Instant fin = ahora();
            if (fin.isBefore(sesion.fechaInicio()))
                throw new ErrorAplicacion(DATO_INVALIDO, "El cierre precede al inicio");
            if (!r.cerrarSesion(id, fin)) throw new ErrorAplicacion(CONFLICTO, "La sesion cambio");
            return r.sesion(id).orElseThrow();
        });
    }
    /** Acepta entrega tardia con contexto explicito, incluso si la sesion ya cerro. */
    public LecturaRFID persistirLectura(LecturaEntradaRFID entrada, Long sesionId) {
        if (entrada == null) throw new ErrorAplicacion(DATO_INVALIDO, "La entrada es obligatoria");
        if (sesionId != null) identificador(sesionId);
        return unidad.ejecutarI3(r -> {
            if (sesionId != null) exigirSesion(r, sesionId);
            return r.crearLectura(entrada, sesionId);
        });
    }
    public Optional<LecturaRFID> consultarLectura(long id) {
        identificador(id);
        return unidad.ejecutarI3(r -> r.lectura(id));
    }
    public List<LecturaRFID> lecturasDeSesion(long id) {
        identificador(id);
        return unidad.ejecutarI3(r -> { exigirSesion(r, id); return r.lecturasDeSesion(id); });
    }
    private Instant ahora() { return Instant.ofEpochMilli(reloj.instant().toEpochMilli()); }
    private static void identificador(long id) {
        if (id <= 0) throw new ErrorAplicacion(DATO_INVALIDO, "El identificador debe ser positivo");
    }
    private static SesionOperacion exigirSesion(RepositorioI3 r, long id) {
        return r.sesion(id).orElseThrow(() -> new ErrorAplicacion(NO_ENCONTRADO, "La sesion no existe"));
    }
}
