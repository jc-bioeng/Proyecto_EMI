package co.emi.trazabilidad.aplicacion;

import co.emi.trazabilidad.aplicacion.puertos.*;
import co.emi.trazabilidad.dominio.*;
import java.time.*;
import java.util.*;
import static co.emi.trazabilidad.aplicacion.ErrorAplicacion.Codigo.*;
import static co.emi.trazabilidad.aplicacion.ResultadoProcesamiento.Estado.*;

/** Motor explicito sobre evidencia persistida. No se invoca desde el callback I2/I3. */
public final class ServicioI4 {
    private final UnidadDeTrabajo unidad;
    private final Clock reloj;
    public ServicioI4(UnidadDeTrabajo unidad, Clock reloj) {
        this.unidad = Objects.requireNonNull(unidad);
        this.reloj = Objects.requireNonNull(reloj);
    }
    public ResultadoProcesamiento procesarLectura(long id) {
        identificador(id);
        return unidad.ejecutarI4(r -> {
            var lectura = r.lectura(id).orElseThrow(() -> new ErrorAplicacion(NO_ENCONTRADO, "La lectura no existe"));
            return procesar(r, lectura, new Contexto(r, reloj.instant()));
        });
    }
    /** Snapshot y transaccion del lote completo; un fallo SQL revierte todas sus escrituras. */
    public List<ResultadoProcesamiento> procesarSesion(long id) {
        identificador(id);
        return unidad.ejecutarI4(r -> {
            exigirSesion(r, id);
            var contexto = new Contexto(r, reloj.instant());
            List<ResultadoProcesamiento> resultados = new ArrayList<>();
            for (var lectura : contexto.lecturas(id)) resultados.add(procesar(r, lectura, contexto));
            return List.copyOf(resultados);
        });
    }
    public Optional<EventoOperativo> consultarEvento(long id) {
        identificador(id);
        return unidad.ejecutarI4(r -> r.evento(id));
    }
    public List<EventoOperativo> eventosDeSesion(long id) {
        identificador(id);
        return unidad.ejecutarI4(r -> { exigirSesion(r, id); return r.eventosDeSesion(id); });
    }
    public List<EvidenciaEvento> evidenciasDeEvento(long id) {
        identificador(id);
        return unidad.ejecutarI4(r -> {
            if (r.evento(id).isEmpty()) throw new ErrorAplicacion(NO_ENCONTRADO, "El evento no existe");
            return r.evidenciasDeEvento(id);
        });
    }
    private ResultadoProcesamiento procesar(RepositorioI4 r, LecturaRFID lectura, Contexto contexto) {
        var previo = r.eventoDeLectura(lectura.lecturaId());
        // Un vinculo confirmado no se reinterpreta tras cierre o cambios de asociacion.
        if (previo.isPresent()) return resultado(lectura, LECTURA_YA_PROCESADA, previo.get());
        var evaluacion = contexto.evaluar(lectura);
        if (evaluacion.rechazo != null) return evaluacion.rechazo;
        var tipo = TipoEvento.valueOf(evaluacion.sesion.tipoOperacion().name());
        long equipo = evaluacion.asignacion.equipoId();
        var existente = r.eventoEquivalente(lectura.sesionId(), equipo, tipo);
        if (existente.isPresent()) {
            if (contexto.ahora.isBefore(existente.get().fechaCreacion()))
                return new ResultadoProcesamiento(lectura.lecturaId(), LECTURA_NO_ELEGIBLE, Optional.empty(), "RELOJ_ANTERIOR_A_EVENTO");
            r.vincular(existente.get().eventoId(), lectura.lecturaId(), evaluacion.asignacion.asignacionId(), contexto.ahora);
            return resultado(lectura, EVIDENCIA_AGREGADA, existente.get());
        }
        // Menor evidencia elegible disponible, aun si se solicito otra lectura posterior.
        LecturaRFID base = contexto.lecturas(lectura.sesionId()).stream()
            .filter(l -> {
                var e = contexto.evaluar(l);
                return e.rechazo == null && e.asignacion.equipoId() == equipo;
            }).findFirst().orElseThrow();
        var evento = r.crearEvento(equipo, lectura.sesionId(), tipo, base, contexto.ahora);
        r.vincular(evento.eventoId(), base.lecturaId(), contexto.evaluar(base).asignacion.asignacionId(), contexto.ahora);
        if (base.lecturaId() != lectura.lecturaId())
            r.vincular(evento.eventoId(), lectura.lecturaId(), evaluacion.asignacion.asignacionId(), contexto.ahora);
        return resultado(lectura, EVENTO_CREADO, evento);
    }
    private static ResultadoProcesamiento resultado(LecturaRFID l, ResultadoProcesamiento.Estado estado, EventoOperativo evento) {
        return new ResultadoProcesamiento(l.lecturaId(), estado, Optional.of(evento), "");
    }
    private record Evaluacion(SesionOperacion sesion, AsignacionEtiqueta asignacion, ResultadoProcesamiento rechazo) { }
    /** Cache local a una unica transaccion; nunca conserva asociaciones entre llamadas. */
    private static final class Contexto {
        private final RepositorioI4 r;
        private final Instant ahora;
        private final Map<Long, SesionOperacion> sesiones = new HashMap<>();
        private final Map<Long, List<LecturaRFID>> lecturas = new HashMap<>();
        private final Map<String, List<AsignacionEtiqueta>> asociaciones = new HashMap<>();
        private final Map<Long, Evaluacion> evaluaciones = new HashMap<>();
        Contexto(RepositorioI4 r, Instant ahora) { this.r = r; this.ahora = Objects.requireNonNull(ahora); }
        List<LecturaRFID> lecturas(long id) { return lecturas.computeIfAbsent(id, r::lecturasDeSesion); }
        Evaluacion evaluar(LecturaRFID l) { return evaluaciones.computeIfAbsent(l.lecturaId(), id -> elegibilidad(l)); }
        private Evaluacion elegibilidad(LecturaRFID l) {
            if (l.sesionId() == null) return rechazar(l, CONTEXTO_INSUFICIENTE, "SIN_SESION");
            var s = sesiones.computeIfAbsent(l.sesionId(), id -> exigirSesion(r, id));
            if (s.estado() != EstadoSesion.ABIERTA) return rechazar(l, LECTURA_NO_ELEGIBLE, "SESION_CERRADA");
            if (s.tipoOperacion() == TipoOperacion.VERIFICACION) return rechazar(l, LECTURA_NO_ELEGIBLE, "SESION_VERIFICACION");
            if (l.timestamp().isBefore(s.fechaInicio())) return rechazar(l, LECTURA_NO_ELEGIBLE, "ANTERIOR_A_SESION");
            if (l.timestamp().isAfter(ahora)) return rechazar(l, LECTURA_NO_ELEGIBLE, "LECTURA_FUTURA");
            var candidatas = asociaciones.computeIfAbsent(l.epc(), r::asociacionesPorEpc).stream()
                .filter(a -> !l.timestamp().isBefore(a.fechaInicio()) && (a.fechaFin() == null || l.timestamp().isBefore(a.fechaFin())))
                .toList();
            if (candidatas.isEmpty()) return rechazar(l, SIN_ASOCIACION_VALIDA, "ASOCIACION_AUSENTE");
            if (candidatas.size() != 1) return rechazar(l, SIN_ASOCIACION_VALIDA, "ASOCIACION_AMBIGUA");
            return new Evaluacion(s, candidatas.getFirst(), null);
        }
        private Evaluacion rechazar(LecturaRFID l, ResultadoProcesamiento.Estado estado, String motivo) {
            return new Evaluacion(null, null, new ResultadoProcesamiento(l.lecturaId(), estado, Optional.empty(), motivo));
        }
    }
    private static void identificador(long id) {
        if (id <= 0) throw new ErrorAplicacion(DATO_INVALIDO, "El identificador debe ser positivo");
    }
    private static SesionOperacion exigirSesion(RepositorioI4 r, long id) {
        return r.sesion(id).orElseThrow(() -> new ErrorAplicacion(NO_ENCONTRADO, "La sesion no existe"));
    }
}
