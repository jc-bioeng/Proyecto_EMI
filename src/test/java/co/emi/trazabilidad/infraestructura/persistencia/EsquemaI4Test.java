package co.emi.trazabilidad.infraestructura.persistencia;

import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class EsquemaI4Test {
    @TempDir Path temporal;
    private BaseDatos base;
    @BeforeEach void preparar() throws Exception {
        base = new BaseDatos(temporal.resolve("i4.db")); migrarHastaI4(temporal.resolve("i4.db"));
        try (var c = base.abrir()) { datos(c); }
    }
    /** Fixture historico V4; EsquemaI5Test verifica la version mas reciente. */
    private static void migrarHastaI4(Path archivo) {
        Flyway.configure().dataSource("jdbc:sqlite:" + archivo, null, null)
            .locations("classpath:db/migration").target("4").load().migrate();
    }
    private static void sql(Connection c, String consulta) throws SQLException {
        try (var s = c.createStatement()) { s.executeUpdate(consulta); }
    }
    private static long contar(Connection c, String consulta) throws SQLException {
        try (var s = c.createStatement(); var r = s.executeQuery(consulta)) { assertTrue(r.next()); return r.getLong(1); }
    }
    private static void datos(Connection c) throws SQLException {
        sql(c, "INSERT INTO equipo VALUES (1, 'A', NULL, 1), (2, 'B', NULL, 0)");
        sql(c, "INSERT INTO etiqueta_rfid VALUES (1, 'X', 'INACTIVA'), (2, 'Y', 'ACTIVA')");
        sql(c, "INSERT INTO asignacion_etiqueta VALUES (1,1,1,0,NULL,NULL), (2,2,2,0,NULL,NULL)");
        sql(c, "INSERT INTO sesion_operacion VALUES (1,'P','A','INGRESO',1000,NULL,'ABIERTA',NULL), (2,'P','A','SALIDA',1000,NULL,'ABIERTA',NULL), (3,'P','A','VERIFICACION',1000,NULL,'ABIERTA',NULL)");
        sql(c, "INSERT INTO lectura_rfid VALUES (1,'X',1,0,'SIMULACION',1), (2,'X',1,1,'SIMULACION',1), (3,'Y',1,0,'RF_REAL',1), (4,'X',1,0,'SIMULACION',2), (5,'X',1,0,'SIMULACION',3), (6,'X',1,0,'SIMULACION',NULL)");
        sql(c, "INSERT INTO lectura_rfid_metadata VALUES (1,'antena','2')");
    }
    private static String alta(long id, long equipo, long sesion, String tipo, long lectura) {
        return "INSERT INTO evento_operativo VALUES (" + id + "," + equipo + "," + sesion + ",'" + tipo + "',1,0,10,0," + lectura + ")";
    }
    private static String vinculo(long lectura, long evento, long asignacion) {
        return "INSERT INTO evento_lectura VALUES (" + lectura + "," + evento + "," + asignacion + ",10,0)";
    }
    private void crear() throws SQLException {
        try (var c = base.abrir()) {
            c.setAutoCommit(false); sql(c, alta(1,1,1,"INGRESO",1)); sql(c, vinculo(1,1,1)); c.commit();
        }
    }
    @Test void v4DesdeCeroReaplicaSinRepetirseYSoloAgregaEventosYRelacion() throws Exception {
        migrarHastaI4(temporal.resolve("i4.db"));
        try (var c = base.abrir()) {
            assertEquals(4, contar(c, "SELECT count(*) FROM flyway_schema_history WHERE success = 1"));
            assertEquals(1, contar(c, "PRAGMA foreign_keys"));
            Set<String> tablas = new HashSet<>();
            try (var s = c.createStatement(); var r = s.executeQuery("SELECT name FROM sqlite_master WHERE type = 'table'")) {
                while (r.next()) tablas.add(r.getString(1));
            }
            assertEquals(Set.of("flyway_schema_history","equipo","etiqueta_rfid","asignacion_etiqueta","sesion_operacion","lectura_rfid","lectura_rfid_metadata","evento_operativo","evento_lectura"), tablas);
            assertEquals(2, contar(c, "SELECT count(*) FROM sqlite_master WHERE type = 'index' AND name IN ('ix_evento_sesion_tiempo','ix_evento_lectura_asignacion')"));
        }
    }
    @Test void v3ConDatosSeActualizaAV4SinPerdidasNiCambiarChecksums() throws Exception {
        var archivo = temporal.resolve("anterior.db");
        Flyway.configure().dataSource("jdbc:sqlite:" + archivo, null, null).locations("classpath:db/migration").target("3").load().migrate();
        var anterior = new BaseDatos(archivo);
        Map<String,Integer> antes = checksums(anterior);
        try (var c = anterior.abrir()) {
            datos(c); sql(c, "UPDATE sesion_operacion SET estado = 'CERRADA', fecha_fin = 2000 WHERE sesion_id = 2");
        }
        migrarHastaI4(archivo); migrarHastaI4(archivo); var despues = checksums(anterior);
        assertEquals(4, despues.size()); antes.forEach((k,v) -> assertEquals(v, despues.get(k)));
        try (var c = anterior.abrir()) {
            assertEquals(6, contar(c, "SELECT count(*) FROM lectura_rfid"));
            assertEquals(1, contar(c, "SELECT count(*) FROM lectura_rfid_metadata WHERE clave = 'antena' AND valor = '2'"));
            assertEquals(1, contar(c, "SELECT count(*) FROM lectura_rfid WHERE lectura_id=2 AND timestamp_segundos=1 AND timestamp_nanos=1 AND epc='X' AND origen_datos='SIMULACION'"));
            assertEquals(1, contar(c, "SELECT count(*) FROM sesion_operacion WHERE sesion_id=2 AND estado='CERRADA' AND fecha_fin=2000"));
            assertEquals(2, contar(c, "SELECT count(*) FROM asignacion_etiqueta")); assertEquals(0, contar(c, "SELECT count(*) FROM evento_operativo"));
            c.setAutoCommit(false); sql(c, alta(1,1,1,"INGRESO",1)); sql(c, vinculo(1,1,1)); c.commit();
        }
    }
    private static Map<String,Integer> checksums(BaseDatos b) throws SQLException {
        Map<String,Integer> mapa = new HashMap<>();
        try (var c=b.abrir(); var s=c.createStatement(); var r=s.executeQuery("SELECT version,checksum FROM flyway_schema_history")) {
            while(r.next()) mapa.put(r.getString(1),r.getInt(2));
        } return mapa;
    }
    @Test void eventoProtegeIdsTiposContextoYTiempo() throws Exception {
        try (var c = base.abrir()) {
            c.setAutoCommit(false);
            for (String valores : List.of(
                "0,1,1,'INGRESO',1,0,10,0,1", "1,999,1,'INGRESO',1,0,10,0,1",
                "1,NULL,1,'INGRESO',1,0,10,0,1", "1,1,999,'INGRESO',1,0,10,0,1",
                "1,1,1,'VERIFICACION',1,0,10,0,1", "1,1,1,'SALIDA',1,0,10,0,1",
                "1,1,3,'INGRESO',1,0,10,0,5", "1,1,1,'INGRESO',1,1,10,0,1",
                "1,1,1,'INGRESO',NULL,0,10,0,1", "1,1,1,'INGRESO',1,0,0,999999999,1",
                "1,1,1,'INGRESO',1,0,10,1000000000,1", "1,1,1,'INGRESO',1,0,10,0,NULL",
                "1,1,1,'INGRESO',1,0,10,0,6", "1,1,1,'INGRESO',1,0,10,0,999")) {
                assertThrows(SQLException.class, () -> sql(c, "INSERT INTO evento_operativo VALUES (" + valores + ")"), valores);
            }
            c.rollback();
        }
    }
    @Test void fkDiferidaImpideConfirmarEventoSinLecturaBaseVinculada() throws Exception {
        try (var c=base.abrir()) {
            c.setAutoCommit(false); sql(c, alta(1,1,1,"INGRESO",1));
            assertThrows(SQLException.class,c::commit); c.rollback();
            assertEquals(0,contar(c,"SELECT count(*) FROM evento_operativo"));
        }
    }
    @Test void otraLecturaNoSustituyeElRespaldoBaseObligatorio() throws Exception {
        try(var c=base.abrir()) {
            c.setAutoCommit(false);sql(c,alta(1,1,1,"INGRESO",1));sql(c,vinculo(2,1,1));
            assertThrows(SQLException.class,c::commit);c.rollback();
        }
    }
    @Test void respaldoValidaLecturaEventoAsignacionSesionYEquipo() throws Exception {
        crear();
        try(var c=base.abrir()) {
            for(String consulta:List.of(vinculo(999,1,1),vinculo(2,999,1),vinculo(2,1,999),vinculo(2,1,2),vinculo(3,1,2),vinculo(4,1,1),vinculo(5,1,1),vinculo(6,1,1),
                "INSERT INTO evento_lectura VALUES (2,1,1,0,0)","INSERT INTO evento_lectura VALUES (2,1,1,10,-1)","INSERT INTO evento_lectura VALUES (2,1,1,NULL,0)"))
                assertThrows(SQLException.class,()->sql(c,consulta),consulta);
            assertEquals(1,contar(c,"SELECT count(*) FROM evento_lectura"));
        }
    }
    @Test void uniqueYReemplazoImpidenEventosEquivalentes() throws Exception {
        crear();
        try(var c=base.abrir()) {
            assertThrows(SQLException.class,()->sql(c,alta(2,1,1,"INGRESO",1)));
            assertThrows(SQLException.class,()->sql(c,alta(2,1,1,"INGRESO",1).replace("INSERT INTO","INSERT OR REPLACE INTO")));
            assertThrows(SQLException.class,()->sql(c,alta(1,2,1,"INGRESO",3).replace("INSERT INTO","INSERT OR REPLACE INTO")));
            assertEquals(1,contar(c,"SELECT count(*) FROM evento_operativo"));
            // Tambien existe la barrera UNIQUE cuando no actua el trigger de reemplazo.
            sql(c,"DROP TRIGGER impedir_reemplazo_evento");
            assertThrows(SQLException.class,()->sql(c,alta(2,1,1,"INGRESO",1)));
        }
    }
    @Test void unaLecturaNoPuedeReasignarseAOtroEvento() throws Exception {
        crear();
        try(var c=base.abrir()) {
            c.setAutoCommit(false);sql(c,alta(2,2,1,"INGRESO",3));sql(c,vinculo(3,2,2));c.commit();
            assertThrows(SQLException.class,()->sql(c,vinculo(1,2,2)));
            assertThrows(SQLException.class,()->sql(c,vinculo(1,2,2).replace("INSERT INTO","INSERT OR REPLACE INTO")));
            assertEquals(2,contar(c,"SELECT count(*) FROM evento_lectura"));
        }
    }
    @Test void eventosYRespaldosNoAdmitenBorradoNiReescritura() throws Exception {
        crear();
        try(var c=base.abrir()) {
            for(String consulta:List.of("DELETE FROM evento_operativo","DELETE FROM evento_lectura","UPDATE evento_operativo SET equipo_id=2","UPDATE evento_operativo SET tipo_evento='SALIDA'","UPDATE evento_operativo SET timestamp_nanos=1","UPDATE evento_lectura SET asignacion_id=2","UPDATE evento_lectura SET lectura_id=2","DELETE FROM lectura_rfid WHERE lectura_id=1","DELETE FROM equipo WHERE equipo_id=1"))
                assertThrows(SQLException.class,()->sql(c,consulta),consulta);
            assertEquals(1,contar(c,"SELECT count(*) FROM evento_operativo WHERE equipo_id=1 AND tipo_evento='INGRESO' AND timestamp_nanos=0"));
        }
    }
    @Test void sesionCerradaImpideEventosYRespaldosNuevosInclusoDesdeSql() throws Exception {
        crear();
        try(var c=base.abrir()) {
            sql(c,"UPDATE sesion_operacion SET estado='CERRADA',fecha_fin=10000 WHERE sesion_id=1");
            assertThrows(SQLException.class,()->sql(c,vinculo(2,1,1)));
            assertThrows(SQLException.class,()->sql(c,alta(2,2,1,"INGRESO",3)));
            assertEquals(1,contar(c,"SELECT count(*) FROM evento_operativo"));
        }
    }
    @Test void intervalosNegativosYLimiteExclusivoSeComparanSinPerderNanos() throws Exception {
        try(var c=base.abrir()) {
            sql(c,"INSERT INTO equipo VALUES (3,'C',NULL,1)");
            sql(c,"INSERT INTO etiqueta_rfid VALUES (3,'Z','ACTIVA')");
            sql(c,"INSERT INTO asignacion_etiqueta VALUES (3,3,3,-1500,-500,NULL)");
            sql(c,"INSERT INTO sesion_operacion VALUES (4,'P','A','INGRESO',-1500,NULL,'ABIERTA',NULL)");
            sql(c,"INSERT INTO lectura_rfid VALUES (7,'Z',-1,499999999,'SIMULACION',4),(8,'Z',-1,500000000,'SIMULACION',4)");
            c.setAutoCommit(false);
            sql(c,"INSERT INTO evento_operativo VALUES (1,3,4,'INGRESO',-1,499999999,10,0,7)");sql(c,vinculo(7,1,3));c.commit();
            assertThrows(SQLException.class,()->sql(c,vinculo(8,1,3)));
        }
    }
    @Test void asociacionSolapadaNoPuedeForzarsePorSql() throws Exception {
        try(var c=base.abrir()) {
            sql(c,"INSERT INTO asignacion_etiqueta VALUES (3,2,1,0,2000,NULL)");
            c.setAutoCommit(false);sql(c,alta(1,1,1,"INGRESO",1));
            assertThrows(SQLException.class,()->sql(c,vinculo(1,1,1)));c.rollback();
        }
    }
}
