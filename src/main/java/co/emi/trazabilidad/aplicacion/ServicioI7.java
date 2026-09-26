package co.emi.trazabilidad.aplicacion;

import co.emi.trazabilidad.aplicacion.puertos.*;
import co.emi.trazabilidad.dominio.*;
import java.time.*;
import java.util.*;
import static co.emi.trazabilidad.aplicacion.ErrorAplicacion.Codigo.*;

public final class ServicioI7 {
    private final UnidadDeTrabajoMvp unidad;
    private final Clock reloj;
    public ServicioI7(UnidadDeTrabajoMvp unidad, Clock reloj) {
        this.unidad=Objects.requireNonNull(unidad); this.reloj=Objects.requireNonNull(reloj);
    }
    public SustitucionTemporal abrir(long equipo, String origen, String destino, String responsable, String motivo) {
        ValidacionMvp.id(equipo);
        for (String valor : new String[]{origen,destino,responsable,motivo}) ValidacionMvp.texto(valor);
        return unidad.ejecutarI7(r -> {
            exigirEquipo(r,equipo);
            if (r.activa(equipo).isPresent()) throw new ErrorAplicacion(CONFLICTO,"Ya existe una sustitucion activa");
            var fecha=ValidacionMvp.ahora(reloj);
            if (r.historial(equipo).stream().anyMatch(s -> s.fechaCierre() != null && fecha.isBefore(s.fechaCierre())))
                throw new ErrorAplicacion(DATO_INVALIDO,"Apertura anterior a una devolucion registrada");
            return r.abrir(equipo,origen,destino,responsable,motivo,fecha);
        });
    }
    public SustitucionTemporal cerrar(long id) {
        ValidacionMvp.id(id);
        return unidad.ejecutarI7(r -> {
            var s=r.sustitucion(id).orElseThrow(() -> new ErrorAplicacion(NO_ENCONTRADO,"La sustitucion no existe"));
            if (!s.activa()) throw new ErrorAplicacion(CONFLICTO,"La sustitucion ya fue devuelta");
            var fecha=ValidacionMvp.ahora(reloj);
            if (fecha.isBefore(s.fechaApertura())) throw new ErrorAplicacion(DATO_INVALIDO,"Devolucion anterior a apertura");
            if (!r.cerrar(id,fecha)) throw new ErrorAplicacion(CONFLICTO,"La sustitucion cambio");
            return r.sustitucion(id).orElseThrow();
        });
    }
    public Optional<SustitucionTemporal> consultar(long id) { ValidacionMvp.id(id); return unidad.ejecutarI7(r -> r.sustitucion(id)); }
    public Optional<SustitucionTemporal> activa(long equipo) {
        ValidacionMvp.id(equipo); return unidad.ejecutarI7(r -> { exigirEquipo(r,equipo); return r.activa(equipo); });
    }
    public List<SustitucionTemporal> historial(long equipo) {
        ValidacionMvp.id(equipo); return unidad.ejecutarI7(r -> { exigirEquipo(r,equipo); return r.historial(equipo); });
    }
    private static void exigirEquipo(RepositorioI7 r,long id) {
        if(r.equipo(id).isEmpty()) throw new ErrorAplicacion(NO_ENCONTRADO,"El equipo no existe");
    }
}
