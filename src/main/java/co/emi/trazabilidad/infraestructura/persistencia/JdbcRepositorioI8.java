package co.emi.trazabilidad.infraestructura.persistencia;

import co.emi.trazabilidad.aplicacion.puertos.RepositorioI8;
import co.emi.trazabilidad.dominio.*;
import java.sql.*;
import java.time.Instant;
import java.util.*;

final class JdbcRepositorioI8 extends JdbcSoporteMvp implements RepositorioI8 {
    private final JdbcRepositorioI6 i6;
    JdbcRepositorioI8(Connection c) { super(c); i6=new JdbcRepositorioI6(c); }
    @Override public Optional<Equipo> equipo(long id) { return i6.equipo(id); }
    @Override public Optional<SesionOperacion> sesion(long id) { return i6.sesion(id); }
    @Override public Optional<Verificacion> verificacion(long id) { return i6.verificacion(id); }
    @Override public List<VerificacionItem> items(long id) { return i6.items(id); }
    @Override public List<Verificacion> verificaciones(long sesion) { return i6.historial(sesion); }
    @Override public ContingenciaManual registrar(long equipo,String actor,Instant fecha,Long sesion,String contexto,String motivo,Long verificacion) {
        return uno("INSERT INTO contingencia_manual(equipo_id,actor,fecha,sesion_id,contexto,motivo,verificacion_id) VALUES(?,?,?,?,?,?,?) RETURNING *",
            JdbcRepositorioI8::mapear,equipo,actor,fecha.toEpochMilli(),sesion,contexto,motivo,verificacion).orElseThrow();
    }
    @Override public Optional<ContingenciaManual> contingencia(long id) {
        return uno("SELECT * FROM contingencia_manual WHERE contingencia_id=?",JdbcRepositorioI8::mapear,id);
    }
    @Override public List<ContingenciaManual> historial(long equipo) {
        return lista("SELECT * FROM contingencia_manual WHERE equipo_id=? ORDER BY fecha,contingencia_id",JdbcRepositorioI8::mapear,equipo);
    }
    @Override public boolean confirmar(long id,Instant fecha,String actor) {
        return ejecutar("UPDATE contingencia_manual SET fecha_confirmacion=?,actor_confirmacion=? WHERE contingencia_id=? AND fecha_confirmacion IS NULL",
            fecha.toEpochMilli(),actor,id)==1;
    }
    private static ContingenciaManual mapear(ResultSet r) throws SQLException {
        return new ContingenciaManual(r.getLong("contingencia_id"),r.getLong("equipo_id"),r.getString("actor"),Instant.ofEpochMilli(r.getLong("fecha")),
            nullable(r,"sesion_id"),r.getString("contexto"),r.getString("motivo"),nullable(r,"verificacion_id"),fechaNullable(r,"fecha_confirmacion"),r.getString("actor_confirmacion"));
    }
}
