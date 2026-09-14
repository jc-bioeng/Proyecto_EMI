package co.emi.trazabilidad.infraestructura.persistencia;

import co.emi.trazabilidad.aplicacion.ErrorAplicacion;
import co.emi.trazabilidad.aplicacion.consultas.*;
import co.emi.trazabilidad.aplicacion.puertos.RepositorioI5;
import co.emi.trazabilidad.dominio.*;
import java.sql.*;
import java.time.Instant;
import java.util.*;

/** Solo SELECT. Metadata, contexto y origenes se resuelven por joins, nunca N+1. */
final class JdbcRepositorioI5 implements RepositorioI5 {
    private final Connection c;
    private final JdbcRepositorioI1 i1;
    JdbcRepositorioI5(Connection c) { this.c = c; this.i1 = new JdbcRepositorioI1(c); }

    private static final String ATRIBUCION = """
            SELECT l.*, a.asignacion_id, a.equipo_id, a.etiqueta_id,
                   a.fecha_inicio, a.fecha_fin, a.motivo_cambio
            FROM asignacion_etiqueta a
            JOIN etiqueta_rfid t ON t.etiqueta_id = a.etiqueta_id
            JOIN lectura_rfid l ON l.epc = t.epc
            WHERE a.equipo_id = ?
              AND (l.timestamp_segundos, l.timestamp_nanos) >= ((a.fecha_inicio / 1000) - (a.fecha_inicio % 1000 < 0), ((a.fecha_inicio % 1000 + 1000) % 1000) * 1000000)
              AND (a.fecha_fin IS NULL OR (l.timestamp_segundos, l.timestamp_nanos) < ((a.fecha_fin / 1000) - (a.fecha_fin % 1000 < 0), ((a.fecha_fin % 1000 + 1000) % 1000) * 1000000))
              AND NOT EXISTS (
                  SELECT 1 FROM asignacion_etiqueta otra
                  WHERE otra.etiqueta_id = a.etiqueta_id AND otra.asignacion_id != a.asignacion_id
                    AND (l.timestamp_segundos, l.timestamp_nanos) >= ((otra.fecha_inicio / 1000) - (otra.fecha_inicio % 1000 < 0), ((otra.fecha_inicio % 1000 + 1000) % 1000) * 1000000)
                    AND (otra.fecha_fin IS NULL OR (l.timestamp_segundos, l.timestamp_nanos) < ((otra.fecha_fin / 1000) - (otra.fecha_fin % 1000 < 0), ((otra.fecha_fin % 1000 + 1000) % 1000) * 1000000))
              )
            """;
    static String sqlLecturas(boolean ultima) {
        String direccion = ultima ? " DESC" : " ASC";
        String orden = "timestamp_segundos" + direccion + ", timestamp_nanos" + direccion + ", lectura_id" + direccion;
        // LIMIT se aplica a lecturas antes del join de metadata; no corta el mapa de la ultima.
        return "WITH atribuidas AS (" + ATRIBUCION + " ORDER BY " + orden + (ultima ? " LIMIT 1" : "") + ") "
            + "SELECT p.*, m.clave, m.valor FROM atribuidas p LEFT JOIN lectura_rfid_metadata m ON m.lectura_id = p.lectura_id "
            + "ORDER BY p.timestamp_segundos" + direccion + ", p.timestamp_nanos" + direccion + ", p.lectura_id" + direccion + ", m.clave";
    }
    static final String SQL_ULTIMO_EVENTO = """
        SELECT * FROM evento_operativo WHERE equipo_id = ?
        ORDER BY timestamp_segundos DESC, timestamp_nanos DESC, evento_id DESC LIMIT 1
        """;
    static final String SQL_HISTORIAL_EVENTOS = """
        SELECT DISTINCT e.*, s.punto_control, s.actor_contexto, s.tipo_operacion,
            s.fecha_inicio AS sesion_inicio, s.fecha_fin AS sesion_fin,
            s.estado AS sesion_estado, s.contexto AS sesion_contexto, l.origen_datos
        FROM evento_operativo e JOIN sesion_operacion s ON s.sesion_id = e.sesion_id
        JOIN evento_lectura v ON v.evento_id = e.evento_id
        JOIN lectura_rfid l ON l.lectura_id = v.lectura_id
        WHERE e.equipo_id = ?
        ORDER BY e.timestamp_segundos, e.timestamp_nanos, e.evento_id, l.origen_datos
        """;
    static final String SQL_EVIDENCIAS = """
        SELECT l.*, v.evento_id, v.asignacion_id, v.vinculacion_segundos, v.vinculacion_nanos, m.clave, m.valor
        FROM evento_lectura v JOIN lectura_rfid l ON l.lectura_id = v.lectura_id
        LEFT JOIN lectura_rfid_metadata m ON m.lectura_id = l.lectura_id
        WHERE v.evento_id = ? ORDER BY l.timestamp_segundos, l.timestamp_nanos, l.lectura_id, m.clave
        """;
    @Override public Optional<Equipo> equipo(long id) { return i1.equipo(id); }
    @Override public Optional<Equipo> equipoPorCodigo(String codigo) { return i1.equipoPorCodigo(codigo); }
    @Override public Optional<LecturaAtribuida> ultimaLecturaPorEquipo(long id) {
        return lecturas(id, true).stream().findFirst();
    }
    @Override public List<LecturaAtribuida> historialLecturasPorEquipo(long id) { return lecturas(id, false); }
    private List<LecturaAtribuida> lecturas(long id, boolean ultima) {
        Map<Long, LecturaEnConstruccion> lecturas = new LinkedHashMap<>();
        Map<Long, AsignacionEtiqueta> asignaciones = new HashMap<>();
        consultar(sqlLecturas(ultima), id, r -> {
            long lecturaId = r.getLong("lectura_id");
            if (!lecturas.containsKey(lecturaId)) {
                lecturas.put(lecturaId, new LecturaEnConstruccion(r));
                asignaciones.put(lecturaId, asignacion(r));
            }
            lecturas.get(lecturaId).agregarMetadata(r);
        });
        return lecturas.entrySet().stream().map(e -> new LecturaAtribuida(e.getValue().construir(), asignaciones.get(e.getKey()))).toList();
    }
    @Override public Optional<EventoOperativo> ultimoEventoPorEquipo(long id) {
        List<EventoOperativo> eventos = new ArrayList<>();
        consultar(SQL_ULTIMO_EVENTO, id, r -> eventos.add(evento(r)));
        return eventos.stream().findFirst();
    }
    @Override public Optional<EventoOperativo> evento(long id) {
        List<EventoOperativo> eventos = new ArrayList<>();
        consultar("SELECT * FROM evento_operativo WHERE evento_id = ?", id, r -> eventos.add(evento(r)));
        return eventos.stream().findFirst();
    }
    @Override public List<EventoConContexto> historialEventosPorEquipo(long id) {
        Map<Long, EventoOperativo> eventos = new LinkedHashMap<>();
        Map<Long, SesionOperacion> sesiones = new HashMap<>();
        Map<Long, Set<OrigenDatos>> origenes = new HashMap<>();
        consultar(SQL_HISTORIAL_EVENTOS, id, r -> {
            long eventoId = r.getLong("evento_id");
            if (!eventos.containsKey(eventoId)) {
                eventos.put(eventoId, evento(r)); sesiones.put(eventoId, sesion(r));
                origenes.put(eventoId, EnumSet.noneOf(OrigenDatos.class));
            }
            origenes.get(eventoId).add(OrigenDatos.valueOf(r.getString("origen_datos")));
        });
        return eventos.entrySet().stream().map(e -> new EventoConContexto(e.getValue(), sesiones.get(e.getKey()), origenes.get(e.getKey()))).toList();
    }
    @Override public List<EvidenciaEvento> evidenciasDeEvento(long id) {
        Map<Long, LecturaEnConstruccion> lecturas = new LinkedHashMap<>();
        Map<Long, Long> asignaciones = new HashMap<>();
        Map<Long, Instant> vinculaciones = new HashMap<>();
        consultar(SQL_EVIDENCIAS, id, r -> {
            long lecturaId = r.getLong("lectura_id");
            if (!lecturas.containsKey(lecturaId)) {
                lecturas.put(lecturaId, new LecturaEnConstruccion(r));
                asignaciones.put(lecturaId, r.getLong("asignacion_id"));
                vinculaciones.put(lecturaId, instante(r, "vinculacion"));
            }
            lecturas.get(lecturaId).agregarMetadata(r);
        });
        return lecturas.entrySet().stream().map(e -> new EvidenciaEvento(id, asignaciones.get(e.getKey()), e.getValue().construir(), vinculaciones.get(e.getKey()))).toList();
    }
    private void consultar(String sql, long id, Fila fila) {
        try (var s = c.prepareStatement(sql)) {
            s.setLong(1, id);
            try (var r = s.executeQuery()) { while (r.next()) fila.leer(r); }
        } catch (SQLException e) {
            throw new ErrorAplicacion(ErrorAplicacion.Codigo.PERSISTENCIA, "No se pudo consultar la evidencia", e);
        }
    }
    private static final class LecturaEnConstruccion {
        private final long id;
        private final String epc;
        private final Instant timestamp;
        private final OrigenDatos origen;
        private final Long sesionId;
        private final Map<String, String> metadata = new LinkedHashMap<>();
        LecturaEnConstruccion(ResultSet r) throws SQLException {
            id = r.getLong("lectura_id"); epc = r.getString("epc"); timestamp = instante(r, "timestamp");
            origen = OrigenDatos.valueOf(r.getString("origen_datos"));
            long sesion = r.getLong("sesion_id"); sesionId = r.wasNull() ? null : sesion;
        }
        void agregarMetadata(ResultSet r) throws SQLException {
            String clave = r.getString("clave");
            if (clave != null) metadata.put(clave, r.getString("valor"));
        }
        LecturaRFID construir() { return new LecturaRFID(id, epc, timestamp, origen, sesionId, metadata); }
    }
    private static Instant instante(ResultSet r, String prefijo) throws SQLException {
        return Instant.ofEpochSecond(r.getLong(prefijo + "_segundos"), r.getInt(prefijo + "_nanos"));
    }
    private static Instant milisNullable(ResultSet r, String columna) throws SQLException {
        long valor = r.getLong(columna); return r.wasNull() ? null : Instant.ofEpochMilli(valor);
    }
    private static AsignacionEtiqueta asignacion(ResultSet r) throws SQLException {
        return new AsignacionEtiqueta(r.getLong("asignacion_id"), r.getLong("equipo_id"), r.getLong("etiqueta_id"),
            Instant.ofEpochMilli(r.getLong("fecha_inicio")), milisNullable(r, "fecha_fin"), r.getString("motivo_cambio"));
    }
    private static EventoOperativo evento(ResultSet r) throws SQLException {
        return new EventoOperativo(r.getLong("evento_id"), r.getLong("equipo_id"), r.getLong("sesion_id"),
            TipoEvento.valueOf(r.getString("tipo_evento")), instante(r, "timestamp"), instante(r, "creacion"), r.getLong("lectura_base_id"));
    }
    private static SesionOperacion sesion(ResultSet r) throws SQLException {
        return new SesionOperacion(r.getLong("sesion_id"), r.getString("punto_control"), r.getString("actor_contexto"),
            TipoOperacion.valueOf(r.getString("tipo_operacion")), Instant.ofEpochMilli(r.getLong("sesion_inicio")),
            milisNullable(r, "sesion_fin"), EstadoSesion.valueOf(r.getString("sesion_estado")), r.getString("sesion_contexto"));
    }
    @FunctionalInterface private interface Fila { void leer(ResultSet r) throws SQLException; }
}
