package co.emi.trazabilidad.aplicacion;

import co.emi.trazabilidad.aplicacion.consultas.*;
import co.emi.trazabilidad.aplicacion.puertos.*;
import co.emi.trazabilidad.dominio.*;
import java.util.*;
import java.util.function.Function;
import static co.emi.trazabilidad.aplicacion.ErrorAplicacion.Codigo.*;

/** Consulta evidencia existente. No infiere ubicacion, disponibilidad ni movimientos. */
public final class ServicioI5 {
    private final UnidadDeTrabajo unidad;
    public ServicioI5(UnidadDeTrabajo unidad) { this.unidad = Objects.requireNonNull(unidad); }
    public Optional<Equipo> buscarEquipoPorCodigo(String codigo) {
        if (codigo == null || codigo.isBlank()) throw new ErrorAplicacion(DATO_INVALIDO, "El codigo institucional es obligatorio");
        return unidad.ejecutarI5(r -> r.equipoPorCodigo(codigo));
    }
    public Optional<LecturaAtribuida> ultimaLecturaPorEquipo(long id) {
        return porEquipo(id, r -> r.ultimaLecturaPorEquipo(id));
    }
    public Optional<EventoOperativo> ultimoEventoPorEquipo(long id) {
        return porEquipo(id, r -> r.ultimoEventoPorEquipo(id));
    }
    public List<LecturaAtribuida> historialLecturasPorEquipo(long id) {
        return porEquipo(id, r -> r.historialLecturasPorEquipo(id));
    }
    public List<EventoConContexto> historialEventosPorEquipo(long id) {
        return porEquipo(id, r -> r.historialEventosPorEquipo(id));
    }
    public List<EvidenciaEvento> evidenciasDeEvento(long id) {
        identificador(id);
        return unidad.ejecutarI5(r -> {
            if (r.evento(id).isEmpty()) throw new ErrorAplicacion(NO_ENCONTRADO, "El evento no existe");
            return r.evidenciasDeEvento(id);
        });
    }
    private <T> T porEquipo(long id, Function<RepositorioI5, T> consulta) {
        identificador(id);
        return unidad.ejecutarI5(r -> {
            if (r.equipo(id).isEmpty()) throw new ErrorAplicacion(NO_ENCONTRADO, "El equipo no existe");
            return consulta.apply(r);
        });
    }
    private static void identificador(long id) {
        if (id <= 0) throw new ErrorAplicacion(DATO_INVALIDO, "El identificador debe ser positivo");
    }
}
