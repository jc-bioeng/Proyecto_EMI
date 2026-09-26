package co.emi.trazabilidad.infraestructura.persistencia;

import co.emi.trazabilidad.aplicacion.puertos.RepositorioI6;
import co.emi.trazabilidad.dominio.*;
import java.sql.*;
import java.time.Instant;
import java.util.*;

final class JdbcRepositorioI6 extends JdbcSoporteMvp implements RepositorioI6 {
    private final JdbcRepositorioI3 i3;
    JdbcRepositorioI6(Connection c) { super(c); i3=new JdbcRepositorioI3(c); }
    @Override public Optional<Equipo> equipo(long id) { return new JdbcRepositorioI1(c).equipo(id); }
    @Override public Optional<SesionOperacion> sesion(long id) { return i3.sesion(id); }
    @Override public List<LecturaRFID> lecturasDeSesion(long id) { return i3.lecturasDeSesion(id); }
    @Override public List<AsignacionEtiqueta> asociacionesPorEpc(String epc) { return new JdbcRepositorioI4(c).asociacionesPorEpc(epc); }
    @Override public Verificacion crear(long sesion, Instant fecha, Long anterior) {
        return uno("INSERT INTO verificacion(sesion_id,fecha,anterior_id) VALUES(?,?,?) RETURNING *",
            JdbcRepositorioI6::mapear,sesion,fecha.toEpochMilli(),anterior).orElseThrow();
    }
    @Override public void agregarItem(VerificacionItem i) {
        ejecutar("INSERT INTO verificacion_item VALUES(?,?,?)",i.verificacionId(),i.equipoId(),i.resultado().name());
    }
    @Override public void agregarEvidencia(EvidenciaVerificacion e) {
        ejecutar("INSERT INTO verificacion_lectura VALUES(?,?,?,?,?)",e.verificacionId(),e.lectura().lecturaId(),e.equipoId(),e.asignacionId(),e.estado().name());
    }
    @Override public Optional<Verificacion> verificacion(long id) {
        return uno("SELECT * FROM verificacion WHERE verificacion_id=?",JdbcRepositorioI6::mapear,id);
    }
    @Override public List<Verificacion> historial(long id) {
        return lista("SELECT * FROM verificacion WHERE sesion_id=? ORDER BY verificacion_id",JdbcRepositorioI6::mapear,id);
    }
    @Override public List<VerificacionItem> items(long id) {
        return lista("SELECT * FROM verificacion_item WHERE verificacion_id=? ORDER BY equipo_id",
            r -> new VerificacionItem(r.getLong("verificacion_id"),r.getLong("equipo_id"),ResultadoVerificacion.valueOf(r.getString("resultado"))),id);
    }
    @Override public List<EvidenciaVerificacion> evidencias(long id) {
        return lista("SELECT * FROM verificacion_lectura WHERE verificacion_id=? ORDER BY lectura_id",
            r -> new EvidenciaVerificacion(r.getLong("verificacion_id"),i3.lectura(r.getLong("lectura_id")).orElseThrow(),
                nullable(r,"equipo_id"),nullable(r,"asignacion_id"),EvidenciaVerificacion.Estado.valueOf(r.getString("estado"))),id);
    }
    private static Verificacion mapear(ResultSet r) throws SQLException {
        return new Verificacion(r.getLong("verificacion_id"),r.getLong("sesion_id"),Instant.ofEpochMilli(r.getLong("fecha")),nullable(r,"anterior_id"));
    }
}
