package co.emi.trazabilidad.infraestructura.persistencia;

import co.emi.trazabilidad.aplicacion.ErrorAplicacion;
import co.emi.trazabilidad.aplicacion.puertos.RepositorioI1;
import co.emi.trazabilidad.dominio.*;
import java.sql.*;
import java.time.Instant;
import java.util.*;

/** Solo mapeo y SQL parametrizado. Su conexion pertenece a JdbcUnidadDeTrabajo. */
final class JdbcRepositorioI1 implements RepositorioI1 {
    private final Connection c;
    JdbcRepositorioI1(Connection c) { this.c = c; }

    @Override public Equipo crearEquipo(String codigo, String descripcion, boolean activo) {
        return uno("INSERT INTO equipo(codigo_institucional, descripcion, activo_piloto) VALUES (?, ?, ?) RETURNING *",
            JdbcRepositorioI1::equipo, codigo, descripcion, activo ? 1 : 0).orElseThrow();
    }
    @Override public EtiquetaRFID crearEtiqueta(String epc, EstadoEtiqueta estado) {
        return uno("INSERT INTO etiqueta_rfid(epc, estado) VALUES (?, ?) RETURNING *",
            JdbcRepositorioI1::etiqueta, epc, estado.name()).orElseThrow();
    }
    @Override public Optional<Equipo> equipo(long id) {
        return uno("SELECT * FROM equipo WHERE equipo_id = ?", JdbcRepositorioI1::equipo, id);
    }
    @Override public Optional<Equipo> equipoPorCodigo(String codigo) {
        return uno("SELECT * FROM equipo WHERE codigo_institucional = ?", JdbcRepositorioI1::equipo, codigo);
    }
    @Override public Optional<EtiquetaRFID> etiqueta(long id) {
        return uno("SELECT * FROM etiqueta_rfid WHERE etiqueta_id = ?", JdbcRepositorioI1::etiqueta, id);
    }
    @Override public Optional<EtiquetaRFID> etiquetaPorEpc(String epc) {
        return uno("SELECT * FROM etiqueta_rfid WHERE epc = ?", JdbcRepositorioI1::etiqueta, epc);
    }
    @Override public Optional<AsignacionEtiqueta> vigenteDeEquipo(long id) {
        return uno("SELECT * FROM asignacion_etiqueta WHERE equipo_id = ? AND fecha_fin IS NULL",
            JdbcRepositorioI1::asignacion, id);
    }
    @Override public Optional<AsignacionEtiqueta> vigenteDeEtiqueta(long id) {
        return uno("SELECT * FROM asignacion_etiqueta WHERE etiqueta_id = ? AND fecha_fin IS NULL",
            JdbcRepositorioI1::asignacion, id);
    }
    @Override public AsignacionEtiqueta crearAsignacion(long equipoId, long etiquetaId, Instant inicio) {
        return uno("INSERT INTO asignacion_etiqueta(equipo_id, etiqueta_id, fecha_inicio) VALUES (?, ?, ?) RETURNING *",
            JdbcRepositorioI1::asignacion, equipoId, etiquetaId, inicio.toEpochMilli()).orElseThrow();
    }
    @Override public boolean cerrarAsignacion(long id, Instant fin, String motivo) {
        try (var s = preparar("UPDATE asignacion_etiqueta SET fecha_fin = ?, motivo_cambio = ? WHERE asignacion_id = ? AND fecha_fin IS NULL",
                fin.toEpochMilli(), motivo, id)) {
            return s.executeUpdate() == 1;
        } catch (SQLException e) { throw error(e); }
    }
    @Override public List<AsignacionEtiqueta> historial(long id) {
        return lista("SELECT * FROM asignacion_etiqueta WHERE equipo_id = ? ORDER BY fecha_inicio, asignacion_id",
            JdbcRepositorioI1::asignacion, id);
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
    private static Equipo equipo(ResultSet r) throws SQLException {
        return new Equipo(r.getLong("equipo_id"), r.getString("codigo_institucional"), r.getString("descripcion"), r.getInt("activo_piloto") == 1);
    }
    private static EtiquetaRFID etiqueta(ResultSet r) throws SQLException {
        return new EtiquetaRFID(r.getLong("etiqueta_id"), r.getString("epc"), EstadoEtiqueta.valueOf(r.getString("estado")));
    }
    private static AsignacionEtiqueta asignacion(ResultSet r) throws SQLException {
        long fin = r.getLong("fecha_fin");
        Instant fechaFin = r.wasNull() ? null : Instant.ofEpochMilli(fin);
        return new AsignacionEtiqueta(r.getLong("asignacion_id"), r.getLong("equipo_id"), r.getLong("etiqueta_id"),
            Instant.ofEpochMilli(r.getLong("fecha_inicio")), fechaFin, r.getString("motivo_cambio"));
    }
    @FunctionalInterface private interface Mapeo<T> { T leer(ResultSet rs) throws SQLException; }
}
