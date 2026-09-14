package co.emi.trazabilidad.infraestructura.persistencia;

import java.nio.file.Path;
import java.sql.*;
import java.util.*;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class EsquemaI3Test {
    @TempDir Path temporal;
    private BaseDatos base;
    @BeforeEach void preparar() throws Exception {
        base = new BaseDatos(temporal.resolve("esquema.db")); migrarHastaI3(temporal.resolve("esquema.db"));
    }
    /** Esta regresion conserva el esquema I3; EsquemaI4Test verifica el esquema mas reciente. */
    private static void migrarHastaI3(Path archivo) {
        Flyway.configure().dataSource("jdbc:sqlite:" + archivo, null, null)
            .locations("classpath:db/migration").target("3").load().migrate();
    }
    private void sql(String texto) throws Exception {
        try (var c = base.abrir(); var s = c.createStatement()) { s.executeUpdate(texto); }
    }
    private long contar(String texto) throws Exception {
        try (var c = base.abrir(); var s = c.createStatement(); var r = s.executeQuery(texto)) {
            assertTrue(r.next()); return r.getLong(1);
        }
    }
    private void sesion() throws Exception { sql("INSERT INTO sesion_operacion VALUES (1, 'P', 'A', 'INGRESO', 1000, NULL, 'ABIERTA', NULL)"); }
    private void lectura() throws Exception { sql("INSERT INTO lectura_rfid VALUES (1, '000a', 1, 5, 'SIMULACION', 1)"); }
    @Test void migracionNuevaEsRepetibleYNoCreaEntidadesFueraDeI3() throws Exception {
        migrarHastaI3(temporal.resolve("esquema.db"));
        assertEquals(3, contar("SELECT count(*) FROM flyway_schema_history WHERE success = 1"));
        Set<String> tablas = new HashSet<>();
        try (var c = base.abrir(); var s = c.createStatement(); var r = s.executeQuery("SELECT name FROM sqlite_master WHERE type = 'table'")) {
            while (r.next()) tablas.add(r.getString(1));
        }
        assertEquals(Set.of("flyway_schema_history", "equipo", "etiqueta_rfid", "asignacion_etiqueta", "sesion_operacion", "lectura_rfid", "lectura_rfid_metadata"), tablas);
        assertEquals(1, contar("SELECT count(*) FROM sqlite_master WHERE type = 'index' AND name = 'ix_lectura_sesion_tiempo'"));
    }
    @Test void migraDesdeV2ConDatosEHistorialSinModificarChecksums() throws Exception {
        Path archivo = temporal.resolve("anterior.db");
        var flyway = Flyway.configure().dataSource("jdbc:sqlite:" + archivo, null, null)
            .locations("classpath:db/migration").target("2").load();
        flyway.migrate();
        var previa = new BaseDatos(archivo);
        Map<String, Integer> antes = checksums(previa);
        try (var c = previa.abrir(); var s = c.createStatement()) {
            s.executeUpdate("INSERT INTO equipo VALUES (1, 'EQ', 'Monitor', 1)");
            s.executeUpdate("INSERT INTO etiqueta_rfid VALUES (1, '0001', 'INACTIVA')");
            s.executeUpdate("INSERT INTO asignacion_etiqueta VALUES (1, 1, 1, 1000, 2000, 'historia')");
        }
        migrarHastaI3(archivo); migrarHastaI3(archivo);
        var despues = checksums(previa);
        assertEquals(3, despues.size()); assertEquals(antes.get("1"), despues.get("1")); assertEquals(antes.get("2"), despues.get("2"));
        try (var c = previa.abrir(); var s = c.createStatement(); var r = s.executeQuery("SELECT e.codigo_institucional, t.epc, t.estado, a.* FROM asignacion_etiqueta a JOIN equipo e USING(equipo_id) JOIN etiqueta_rfid t USING(etiqueta_id)")) {
            assertTrue(r.next()); assertEquals("EQ", r.getString("codigo_institucional")); assertEquals("0001", r.getString("epc"));
            assertEquals("INACTIVA", r.getString("estado")); assertEquals(1000, r.getLong("fecha_inicio"));
            assertEquals(2000, r.getLong("fecha_fin")); assertEquals("historia", r.getString("motivo_cambio")); assertFalse(r.next());
        }
    }
    private static Map<String, Integer> checksums(BaseDatos b) throws Exception {
        Map<String, Integer> valores = new HashMap<>();
        try (var c = b.abrir(); var s = c.createStatement(); var r = s.executeQuery("SELECT version, checksum FROM flyway_schema_history WHERE success = 1")) {
            while (r.next()) valores.put(r.getString(1), r.getInt(2));
        } return valores;
    }
    @Test void sesionProtegePkCamposObligatoriosTiposYCoherenciaDeCierre() throws Exception {
        for (String valores : List.of(
                "0, 'P', 'A', 'INGRESO', 1, NULL, 'ABIERTA', NULL",
                "1, NULL, 'A', 'INGRESO', 1, NULL, 'ABIERTA', NULL",
                "1, ' ', 'A', 'INGRESO', 1, NULL, 'ABIERTA', NULL",
                "1, 'P', NULL, 'INGRESO', 1, NULL, 'ABIERTA', NULL",
                "1, 'P', ' ', 'INGRESO', 1, NULL, 'ABIERTA', NULL",
                "1, 'P', 'A', 'OTRO', 1, NULL, 'ABIERTA', NULL",
                "1, 'P', 'A', 'INGRESO', NULL, NULL, 'ABIERTA', NULL",
                "1, 'P', 'A', 'INGRESO', 'ayer', NULL, 'ABIERTA', NULL",
                "1, 'P', 'A', 'INGRESO', 1, 2, 'ABIERTA', NULL",
                "1, 'P', 'A', 'INGRESO', 1, NULL, 'CERRADA', NULL",
                "1, 'P', 'A', 'INGRESO', 1, 0, 'CERRADA', NULL",
                "1, 'P', 'A', 'INGRESO', 1, NULL, 'OTRO', NULL")) {
            assertThrows(SQLException.class, () -> sql("INSERT INTO sesion_operacion VALUES (" + valores + ")"), valores);
        }
        sesion(); assertThrows(SQLException.class, this::sesion);
    }
    @Test void lecturaProtegeCamposOrigenPrecisionYReferencias() throws Exception {
        sesion();
        for (String valores : List.of(
                "0, 'x', 1, 0, 'SIMULACION', 1", "1, NULL, 1, 0, 'SIMULACION', 1",
                "1, ' ', 1, 0, 'SIMULACION', 1", "1, 'x', NULL, 0, 'SIMULACION', 1",
                "1, 'x', 1, NULL, 'SIMULACION', 1", "1, 'x', 1, -1, 'SIMULACION', 1",
                "1, 'x', 1, 1000000000, 'SIMULACION', 1", "1, 'x', 1, 0, 'MANUAL', 1",
                "1, 'x', 1, 0, NULL, 1", "1, 'x', 1, 0, 'SIMULACION', 999",
                "1, 'x', 'ayer', 0, 'SIMULACION', 1", "1, 'x', 31556889864403200, 0, 'SIMULACION', 1")) {
            assertThrows(SQLException.class, () -> sql("INSERT INTO lectura_rfid VALUES (" + valores + ")"), valores);
        }
        lectura(); assertThrows(SQLException.class, this::lectura);
        sql("INSERT INTO lectura_rfid VALUES (2, '000a', 1, 5, 'RF_REAL', NULL)");
        sql("INSERT INTO lectura_rfid VALUES (3, '000a', 1, 5, 'SIMULACION', 1)");
        assertEquals(3, contar("SELECT count(*) FROM lectura_rfid"));
    }
    @Test void metadataProtegeFkClaveUnicaYNoNulos() throws Exception {
        sesion(); lectura();
        assertThrows(SQLException.class, () -> sql("INSERT INTO lectura_rfid_metadata VALUES (999, 'a', 'v')"));
        assertThrows(SQLException.class, () -> sql("INSERT INTO lectura_rfid_metadata VALUES (1, NULL, 'v')"));
        assertThrows(SQLException.class, () -> sql("INSERT INTO lectura_rfid_metadata VALUES (1, 'a', NULL)"));
        sql("INSERT INTO lectura_rfid_metadata VALUES (1, 'a', 'v')");
        assertThrows(SQLException.class, () -> sql("INSERT INTO lectura_rfid_metadata VALUES (1, 'a', 'otro')"));
    }
    @Test void historialCrudoYContextoNoSeBorranNiSobrescriben() throws Exception {
        sesion(); lectura(); sql("INSERT INTO lectura_rfid_metadata VALUES (1, 'a', 'v')");
        for (String consulta : List.of("DELETE FROM sesion_operacion", "DELETE FROM lectura_rfid", "DELETE FROM lectura_rfid_metadata",
                "UPDATE lectura_rfid SET epc = 'otro'", "UPDATE lectura_rfid_metadata SET valor = 'otro'",
                "UPDATE sesion_operacion SET actor_contexto = 'otro'", "UPDATE sesion_operacion SET fecha_inicio = 0")) {
            assertThrows(SQLException.class, () -> sql(consulta));
        }
        sql("UPDATE sesion_operacion SET estado = 'CERRADA', fecha_fin = 2000 WHERE sesion_id = 1");
        assertThrows(SQLException.class, () -> sql("UPDATE sesion_operacion SET estado = 'ABIERTA', fecha_fin = NULL"));
        assertThrows(SQLException.class, () -> sql("UPDATE sesion_operacion SET fecha_fin = 3000"));
        assertEquals(1, contar("SELECT count(*) FROM lectura_rfid")); assertEquals(1, contar("SELECT count(*) FROM lectura_rfid_metadata"));
    }
}
