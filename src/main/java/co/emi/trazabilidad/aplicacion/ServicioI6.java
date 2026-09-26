package co.emi.trazabilidad.aplicacion;

import co.emi.trazabilidad.aplicacion.puertos.*;
import co.emi.trazabilidad.dominio.*;
import java.time.*;
import java.util.*;
import static co.emi.trazabilidad.aplicacion.ErrorAplicacion.Codigo.*;
import static co.emi.trazabilidad.dominio.ResultadoVerificacion.*;
import static co.emi.trazabilidad.dominio.EvidenciaVerificacion.Estado.*;

/** Comparacion explicita: no invoca el motor de eventos ni modifica evidencia cruda. */
public final class ServicioI6 {
    private final UnidadDeTrabajoMvp unidad;
    private final Clock reloj;
    public ServicioI6(UnidadDeTrabajoMvp unidad, Clock reloj) {
        this.unidad = Objects.requireNonNull(unidad); this.reloj = Objects.requireNonNull(reloj);
    }
    public Verificacion verificar(long sesionId, Set<Long> esperados) {
        ValidacionMvp.id(sesionId);
        if (esperados == null || esperados.stream().anyMatch(id -> id == null || id <= 0))
            throw new ErrorAplicacion(DATO_INVALIDO, "Esperados debe contener identificadores positivos");
        var copia = Set.copyOf(esperados);
        return unidad.ejecutarI6(r -> {
            if (!r.historial(sesionId).isEmpty()) throw new ErrorAplicacion(CONFLICTO, "Use reintentar sobre el ultimo intento");
            return comparar(r, sesionId, copia, null);
        });
    }
    public Verificacion reintentar(long anteriorId) {
        ValidacionMvp.id(anteriorId);
        return unidad.ejecutarI6(r -> {
            var anterior = exigir(r, anteriorId);
            if (r.historial(anterior.sesionId()).getLast().verificacionId() != anteriorId)
                throw new ErrorAplicacion(CONFLICTO, "Solo se reintenta el ultimo intento");
            Set<Long> esperados = new TreeSet<>();
            r.items(anteriorId).stream().filter(i -> i.resultado() != NO_ESPERADO).forEach(i -> esperados.add(i.equipoId()));
            return comparar(r, anterior.sesionId(), esperados, anterior);
        });
    }
    public Optional<Verificacion> consultar(long id) { ValidacionMvp.id(id); return unidad.ejecutarI6(r -> r.verificacion(id)); }
    public List<Verificacion> historial(long sesion) {
        ValidacionMvp.id(sesion);
        return unidad.ejecutarI6(r -> {
            if (r.sesion(sesion).isEmpty()) throw new ErrorAplicacion(NO_ENCONTRADO, "La sesion no existe");
            return r.historial(sesion);
        });
    }
    public List<VerificacionItem> items(long id) {
        ValidacionMvp.id(id); return unidad.ejecutarI6(r -> { exigir(r,id); return r.items(id); });
    }
    public List<EvidenciaVerificacion> evidencias(long id) {
        ValidacionMvp.id(id); return unidad.ejecutarI6(r -> { exigir(r,id); return r.evidencias(id); });
    }
    private Verificacion comparar(RepositorioI6 r, long sesionId, Set<Long> esperados, Verificacion anterior) {
        var s = r.sesion(sesionId).orElseThrow(() -> new ErrorAplicacion(NO_ENCONTRADO, "La sesion no existe"));
        if (s.tipoOperacion() != TipoOperacion.VERIFICACION || s.estado() != EstadoSesion.ABIERTA)
            throw new ErrorAplicacion(CONFLICTO, "Se requiere sesion VERIFICACION abierta");
        Instant ahora = ValidacionMvp.ahora(reloj);
        if (ahora.isBefore(s.fechaInicio()) || (anterior != null && ahora.isBefore(anterior.fecha())))
            throw new ErrorAplicacion(DATO_INVALIDO, "Reloj anterior al contexto");
        for (long id : esperados) if (r.equipo(id).isEmpty()) throw new ErrorAplicacion(NO_ENCONTRADO, "Equipo esperado inexistente");
        var v = r.crear(sesionId, ahora, anterior == null ? null : anterior.verificacionId());
        List<EvidenciaVerificacion> evidencia = new ArrayList<>();
        Set<Long> detectados = new TreeSet<>();
        Map<String,List<AsignacionEtiqueta>> asociaciones = new HashMap<>();
        for (var l : r.lecturasDeSesion(sesionId)) {
            var estado = ATRIBUIDA;
            Long equipo = null, asignacion = null;
            if (l.timestamp().isBefore(s.fechaInicio())) estado = ANTERIOR_A_SESION;
            else if (l.timestamp().isAfter(ahora)) estado = LECTURA_FUTURA;
            else {
                var validas = asociaciones.computeIfAbsent(l.epc(), r::asociacionesPorEpc).stream()
                    .filter(a -> !l.timestamp().isBefore(a.fechaInicio()) && (a.fechaFin() == null || l.timestamp().isBefore(a.fechaFin()))).toList();
                if (validas.isEmpty()) estado = SIN_ASOCIACION;
                else if (validas.size() != 1) estado = ASOCIACION_AMBIGUA;
                else { equipo = validas.getFirst().equipoId(); asignacion = validas.getFirst().asignacionId(); detectados.add(equipo); }
            }
            evidencia.add(new EvidenciaVerificacion(v.verificacionId(), l, equipo, asignacion, estado));
        }
        Set<Long> equipos = new TreeSet<>(esperados); equipos.addAll(detectados);
        for (long id : equipos) r.agregarItem(new VerificacionItem(v.verificacionId(), id,
            !esperados.contains(id) ? NO_ESPERADO : detectados.contains(id) ? DETECTADO : FALTANTE));
        evidencia.forEach(r::agregarEvidencia);
        return v;
    }
    private static Verificacion exigir(RepositorioI6 r, long id) {
        return r.verificacion(id).orElseThrow(() -> new ErrorAplicacion(NO_ENCONTRADO, "La verificacion no existe"));
    }
}
