package co.emi.trazabilidad.infraestructura.persistencia;

import co.emi.trazabilidad.aplicacion.ErrorAplicacion;
import co.emi.trazabilidad.aplicacion.puertos.RepositorioI3;
import co.emi.trazabilidad.dominio.*;
import java.sql.*;
import java.time.Instant;
import java.util.*;

/** SQL parametrizado; la unidad de trabajo posee la conexion y la transaccion. */
final class JdbcRepositorioI3 implements RepositorioI3 {
    private final Connection c;
    JdbcRepositorioI3(Connection c) { this.c = c; }

    @Override public SesionOperacion crearSesion(String punto, String actor, TipoOperacion tipo, Instant inicio, String contexto) {
        return uno("INSERT INTO sesion_operacion(punto_control, actor_contexto, tipo_operacion, fecha_inicio, estado, contexto) VALUES (?, ?, ?, ?, 'ABIERTA', ?) RETURNING *",
            JdbcRepositorioI3::sesion, punto, actor, tipo.name(), inicio.toEpochMilli(), contexto).orElseThrow();
    }
    @Override public Optional<SesionOperacion> sesion(long id) {
        return uno("SELECT * FROM sesion_operacion WHERE sesion_id = ?", JdbcRepositorioI3::sesion, id);
    }
    @Override public boolean cerrarSesion(long id, Instant fin) {
        try (var s = preparar("UPDATE sesion_operacion SET estado = 'CERRADA', fecha_fin = ? WHERE sesion_id = ? AND estado = 'ABIERTA'", fin.toEpochMilli(), id)) {
            return s.executeUpdate() == 1;
        } catch (SQLException e) { throw error(e); }
    }
    @Override public LecturaRFID crearLectura(LecturaEntradaRFID entrada, Long sesionId) {
        long id = uno("INSERT INTO lectura_rfid(epc, timestamp_segundos, timestamp_nanos, origen_datos, sesion_id) VALUES (?, ?, ?, ?, ?) RETURNING lectura_id",
            r -> r.getLong(1), entrada.epc(), entrada.timestamp().getEpochSecond(), entrada.timestamp().getNano(), entrada.origenDatos().name(), sesionId).orElseThrow();
        for (var dato : entrada.metadata().entrySet()) {
            try (var s = preparar("INSERT INTO lectura_rfid_metadata(lectura_id, clave, valor) VALUES (?, ?, ?)", id, dato.getKey(), dato.getValue())) {
                s.executeUpdate();
            } catch (SQLException e) { throw error(e); }
        }
        return lectura(id).orElseThrow();
    }
    @Override public Optional<LecturaRFID> lectura(long id) {
        return uno("SELECT * FROM lectura_rfid WHERE lectura_id = ?", this::lectura, id);
    }
    @Override public List<LecturaRFID> lecturasDeSesion(long id) {
        return lista("SELECT * FROM lectura_rfid WHERE sesion_id = ? ORDER BY timestamp_segundos, timestamp_nanos, lectura_id", this::lectura, id);
    }
    private static SesionOperacion sesion(ResultSet r) throws SQLException {
        long fin = r.getLong("fecha_fin");
        Instant fechaFin = r.wasNull() ? null : Instant.ofEpochMilli(fin);
        return new SesionOperacion(r.getLong("sesion_id"), r.getString("punto_control"), r.getString("actor_contexto"),
            TipoOperacion.valueOf(r.getString("tipo_operacion")), Instant.ofEpochMilli(r.getLong("fecha_inicio")), fechaFin,
            EstadoSesion.valueOf(r.getString("estado")), r.getString("contexto"));
    }
    private LecturaRFID lectura(ResultSet r) throws SQLException {
        long id = r.getLong("lectura_id");
        long sesion = r.getLong("sesion_id");
        Long sesionId = r.wasNull() ? null : sesion;
        Map<String, String> metadata = new HashMap<>();
        try (var s = preparar("SELECT clave, valor FROM lectura_rfid_metadata WHERE lectura_id = ?", id); var datos = s.executeQuery()) {
            while (datos.next()) metadata.put(datos.getString(1), datos.getString(2));
        }
        return new LecturaRFID(id, r.getString("epc"), Instant.ofEpochSecond(r.getLong("timestamp_segundos"), r.getInt("timestamp_nanos")),
            OrigenDatos.valueOf(r.getString("origen_datos")), sesionId, metadata);
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
