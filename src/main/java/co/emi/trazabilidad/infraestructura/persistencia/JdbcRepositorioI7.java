package co.emi.trazabilidad.infraestructura.persistencia;

import co.emi.trazabilidad.aplicacion.puertos.RepositorioI7;
import co.emi.trazabilidad.dominio.*;
import java.sql.*;
import java.time.Instant;
import java.util.*;

final class JdbcRepositorioI7 extends JdbcSoporteMvp implements RepositorioI7 {
    JdbcRepositorioI7(Connection c) { super(c); }
    @Override public Optional<Equipo> equipo(long id) { return new JdbcRepositorioI1(c).equipo(id); }
    @Override public SustitucionTemporal abrir(long equipo,String origen,String destino,String responsable,String motivo,Instant fecha) {
        return uno("INSERT INTO sustitucion_temporal(equipo_id,origen,destino,responsable,motivo,fecha_apertura) VALUES(?,?,?,?,?,?) RETURNING *",
            JdbcRepositorioI7::mapear,equipo,origen,destino,responsable,motivo,fecha.toEpochMilli()).orElseThrow();
    }
    @Override public Optional<SustitucionTemporal> sustitucion(long id) {
        return uno("SELECT * FROM sustitucion_temporal WHERE sustitucion_id=?",JdbcRepositorioI7::mapear,id);
    }
    @Override public Optional<SustitucionTemporal> activa(long equipo) {
        return uno("SELECT * FROM sustitucion_temporal WHERE equipo_id=? AND fecha_cierre IS NULL",JdbcRepositorioI7::mapear,equipo);
    }
    @Override public List<SustitucionTemporal> historial(long equipo) {
        return lista("SELECT * FROM sustitucion_temporal WHERE equipo_id=? ORDER BY fecha_apertura,sustitucion_id",JdbcRepositorioI7::mapear,equipo);
    }
    @Override public boolean cerrar(long id,Instant fecha) {
        return ejecutar("UPDATE sustitucion_temporal SET fecha_cierre=? WHERE sustitucion_id=? AND fecha_cierre IS NULL",fecha.toEpochMilli(),id)==1;
    }
    private static SustitucionTemporal mapear(ResultSet r) throws SQLException {
        return new SustitucionTemporal(r.getLong("sustitucion_id"),r.getLong("equipo_id"),r.getString("origen"),r.getString("destino"),
            r.getString("responsable"),r.getString("motivo"),Instant.ofEpochMilli(r.getLong("fecha_apertura")),fechaNullable(r,"fecha_cierre"));
    }
}
