package co.emi.trazabilidad.infraestructura.persistencia;

import co.emi.trazabilidad.aplicacion.ErrorAplicacion;
import co.emi.trazabilidad.aplicacion.puertos.RepositorioI4;
import co.emi.trazabilidad.dominio.*;
import java.sql.*;
import java.time.Instant;
import java.util.*;

/** Consultas historicas y eventos. Delega las lecturas I3 sobre la misma conexion. */
final class JdbcRepositorioI4 implements RepositorioI4 {
    private final Connection c;
    private final JdbcRepositorioI3 i3;
    JdbcRepositorioI4(Connection c) { this.c = c; this.i3 = new JdbcRepositorioI3(c); }
    @Override public Optional<LecturaRFID> lectura(long id) { return i3.lectura(id); }
    @Override public Optional<SesionOperacion> sesion(long id) { return i3.sesion(id); }
    @Override public List<LecturaRFID> lecturasDeSesion(long id) { return i3.lecturasDeSesion(id); }
    @Override public List<AsignacionEtiqueta> asociacionesPorEpc(String epc) {
        return lista("SELECT a.* FROM asignacion_etiqueta a JOIN etiqueta_rfid t ON t.etiqueta_id = a.etiqueta_id WHERE t.epc = ? ORDER BY a.fecha_inicio, a.asignacion_id",
            JdbcRepositorioI4::asignacion, epc);
    }
    @Override public Optional<EventoOperativo> evento(long id) {
        return uno("SELECT * FROM evento_operativo WHERE evento_id = ?", JdbcRepositorioI4::evento, id);
    }
    @Override public Optional<EventoOperativo> eventoDeLectura(long id) {
        return uno("SELECT e.* FROM evento_operativo e JOIN evento_lectura v ON v.evento_id = e.evento_id WHERE v.lectura_id = ?", JdbcRepositorioI4::evento, id);
    }
    @Override public Optional<EventoOperativo> eventoEquivalente(long sesion, long equipo, TipoEvento tipo) {
        return uno("SELECT * FROM evento_operativo WHERE sesion_id = ? AND equipo_id = ? AND tipo_evento = ?", JdbcRepositorioI4::evento, sesion, equipo, tipo.name());
    }
    @Override public List<EventoOperativo> eventosDeSesion(long id) {
        return lista("SELECT * FROM evento_operativo WHERE sesion_id = ? ORDER BY timestamp_segundos, timestamp_nanos, evento_id", JdbcRepositorioI4::evento, id);
    }
    @Override public List<EvidenciaEvento> evidenciasDeEvento(long id) {
        return lista("SELECT v.* FROM evento_lectura v JOIN lectura_rfid l ON l.lectura_id = v.lectura_id WHERE v.evento_id = ? ORDER BY l.timestamp_segundos, l.timestamp_nanos, l.lectura_id",
            r -> new EvidenciaEvento(r.getLong("evento_id"), r.getLong("asignacion_id"),
                i3.lectura(r.getLong("lectura_id")).orElseThrow(), instante(r, "vinculacion")), id);
    }
    @Override public EventoOperativo crearEvento(long equipo, long sesion, TipoEvento tipo, LecturaRFID base, Instant creacion) {
        return uno("INSERT INTO evento_operativo(equipo_id, sesion_id, tipo_evento, timestamp_segundos, timestamp_nanos, creacion_segundos, creacion_nanos, lectura_base_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING *",
            JdbcRepositorioI4::evento, equipo, sesion, tipo.name(), base.timestamp().getEpochSecond(), base.timestamp().getNano(),
            creacion.getEpochSecond(), creacion.getNano(), base.lecturaId()).orElseThrow();
    }
    @Override public void vincular(long evento, long lectura, long asignacion, Instant vinculacion) {
        try (var s = preparar("INSERT INTO evento_lectura(evento_id, lectura_id, asignacion_id, vinculacion_segundos, vinculacion_nanos) VALUES (?, ?, ?, ?, ?)",
                evento, lectura, asignacion, vinculacion.getEpochSecond(), vinculacion.getNano())) {
            s.executeUpdate();
        } catch (SQLException e) { throw error(e); }
    }
    private static EventoOperativo evento(ResultSet r) throws SQLException {
        return new EventoOperativo(r.getLong("evento_id"), r.getLong("equipo_id"), r.getLong("sesion_id"),
            TipoEvento.valueOf(r.getString("tipo_evento")), instante(r, "timestamp"), instante(r, "creacion"), r.getLong("lectura_base_id"));
    }
    private static Instant instante(ResultSet r, String prefijo) throws SQLException {
        return Instant.ofEpochSecond(r.getLong(prefijo + "_segundos"), r.getInt(prefijo + "_nanos"));
    }
    private static AsignacionEtiqueta asignacion(ResultSet r) throws SQLException {
        long fin = r.getLong("fecha_fin"); Instant fechaFin = r.wasNull() ? null : Instant.ofEpochMilli(fin);
        return new AsignacionEtiqueta(r.getLong("asignacion_id"), r.getLong("equipo_id"), r.getLong("etiqueta_id"),
            Instant.ofEpochMilli(r.getLong("fecha_inicio")), fechaFin, r.getString("motivo_cambio"));
    }
    private <T> Optional<T> uno(String sql, Mapeo<T> mapa, Object... parametros) {
        return lista(sql, mapa, parametros).stream().findFirst();
    }
    private <T> List<T> lista(String sql, Mapeo<T> mapa, Object... parametros) {
        try (var s = preparar(sql, parametros); var rs = s.executeQuery()) {
            List<T> salida = new ArrayList<>();
            while (rs.next()) salida.add(mapa.leer(rs));
            return List.copyOf(salida);
        } catch (SQLException e) { throw error(e); }
    }
    private PreparedStatement preparar(String sql, Object... parametros) throws SQLException {
        PreparedStatement s = c.prepareStatement(sql);
        try {
            for (int i = 0; i < parametros.length; i++) s.setObject(i + 1, parametros[i]);
            return s;
        } catch (SQLException e) {
            try { s.close(); } catch (SQLException cierre) { e.addSuppressed(cierre); }
            throw e;
        }
    }
    private static ErrorAplicacion error(SQLException e) {
        // Codigo primario SQLite 19: restriccion de integridad; no depender del texto del driver.
        boolean restriccion = (e.getErrorCode() & 0xff) == 19;
        return new ErrorAplicacion(restriccion ? ErrorAplicacion.Codigo.CONFLICTO : ErrorAplicacion.Codigo.PERSISTENCIA,
            restriccion ? "La operacion entra en conflicto con la integridad de los datos" : "No se pudo acceder a la persistencia", e);
    }
    @FunctionalInterface private interface Mapeo<T> { T leer(ResultSet rs) throws SQLException; }
}
