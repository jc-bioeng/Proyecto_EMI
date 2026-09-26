package co.emi.trazabilidad.infraestructura.persistencia;

import co.emi.trazabilidad.aplicacion.ErrorAplicacion;
import java.sql.*;
import java.time.Instant;
import java.util.*;

/** Utilidades internas de los nuevos repositorios; siempre la conexion transaccional recibida. */
abstract class JdbcSoporteMvp {
    protected final Connection c;
    JdbcSoporteMvp(Connection c) { this.c = Objects.requireNonNull(c); }
    protected <T> Optional<T> uno(String sql, Mapeo<T> mapa, Object... args) {
        return lista(sql,mapa,args).stream().findFirst();
    }
    protected <T> List<T> lista(String sql, Mapeo<T> mapa, Object... args) {
        try (var s = preparar(sql,args); var rs = s.executeQuery()) {
            List<T> salida = new ArrayList<>();
            while (rs.next()) salida.add(mapa.leer(rs));
            return List.copyOf(salida);
        } catch (SQLException e) { throw error(e); }
    }
    protected int ejecutar(String sql, Object... args) {
        try (var s = preparar(sql,args)) { return s.executeUpdate(); }
        catch (SQLException e) { throw error(e); }
    }
    private PreparedStatement preparar(String sql, Object... args) throws SQLException {
        var s = c.prepareStatement(sql);
        try { for (int i=0;i<args.length;i++) s.setObject(i+1,args[i]); return s; }
        catch (SQLException e) { try { s.close(); } catch (SQLException cierre) { e.addSuppressed(cierre); } throw e; }
    }
    protected static Long nullable(ResultSet r, String campo) throws SQLException {
        long valor=r.getLong(campo); return r.wasNull()?null:valor;
    }
    protected static Instant fechaNullable(ResultSet r, String campo) throws SQLException {
        Long valor=nullable(r,campo); return valor==null?null:Instant.ofEpochMilli(valor);
    }
    protected static ErrorAplicacion error(SQLException e) {
        boolean conflicto=(e.getErrorCode()&0xff)==19;
        return new ErrorAplicacion(conflicto?ErrorAplicacion.Codigo.CONFLICTO:ErrorAplicacion.Codigo.PERSISTENCIA,
            conflicto?"Conflicto de integridad de datos":"Error de persistencia",e);
    }
    @FunctionalInterface protected interface Mapeo<T> { T leer(ResultSet r) throws SQLException; }
}
