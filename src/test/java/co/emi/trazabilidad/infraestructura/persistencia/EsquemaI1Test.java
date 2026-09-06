package co.emi.trazabilidad.infraestructura.persistencia;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class EsquemaI1Test {
    @TempDir Path temporal;
    private BaseDatos base;

    @BeforeEach void preparar() throws Exception {
        base = new BaseDatos(temporal.resolve("i1.db"));
        base.migrar();
        try (var c = base.abrir()) {
            sql(c, "INSERT INTO equipo VALUES (1, 'EQ-001', NULL, 1), (2, 'EQ-002', 'Monitor', 0)");
            sql(c, "INSERT INTO etiqueta_rfid VALUES (1, '0001', 'ACTIVA'), (2, '0002', 'ACTIVA'), (3, '0003', 'INACTIVA')");
            sql(c, "INSERT INTO asignacion_etiqueta VALUES (1, 1, 1, 1000, NULL, NULL)");
        }
    }

    @Test void codigoYEpcSonUnicosYObligatorios() throws Exception {
        try (var c = base.abrir()) {
            assertThrows(SQLException.class, () -> sql(c, "INSERT INTO equipo VALUES (3, 'EQ-001', NULL, 1)"));
            assertThrows(SQLException.class, () -> sql(c, "INSERT INTO equipo VALUES (3, NULL, NULL, 1)"));
            assertThrows(SQLException.class, () -> sql(c, "INSERT INTO equipo VALUES (3, ' ', NULL, 1)"));
            assertThrows(SQLException.class, () -> sql(c, "INSERT INTO etiqueta_rfid VALUES (4, '0001', 'ACTIVA')"));
            assertThrows(SQLException.class, () -> sql(c, "INSERT INTO etiqueta_rfid VALUES (4, NULL, 'ACTIVA')"));
            assertThrows(SQLException.class, () -> sql(c, "INSERT INTO etiqueta_rfid VALUES (4, '', 'ACTIVA')"));
        }
    }

    @Test void estadoBooleanoYFechasTienenDominioControlado() throws Exception {
        try (var c = base.abrir()) {
            assertThrows(SQLException.class, () -> sql(c, "INSERT INTO etiqueta_rfid VALUES (4, '0004', 'ASIGNADA')"));
            assertThrows(SQLException.class, () -> sql(c, "UPDATE equipo SET activo_piloto = 2 WHERE equipo_id = 1"));
            assertThrows(SQLException.class, () -> sql(c, "UPDATE asignacion_etiqueta SET fecha_fin = 999 WHERE asignacion_id = 1"));
            assertThrows(SQLException.class, () -> sql(c, "UPDATE asignacion_etiqueta SET fecha_inicio = 'ayer' WHERE asignacion_id = 1"));
        }
    }

    @Test void impideDosVigentesPorEquipoOPorEtiqueta() throws Exception {
        try (var c = base.abrir()) {
            assertThrows(SQLException.class, () -> sql(c, "INSERT INTO asignacion_etiqueta VALUES (2, 1, 2, 2000, NULL, NULL)"));
            assertThrows(SQLException.class, () -> sql(c, "INSERT INTO asignacion_etiqueta VALUES (2, 2, 1, 2000, NULL, NULL)"));
            assertEquals(1, contar(c, "SELECT count(*) FROM asignacion_etiqueta"));
        }
    }

    @Test void impideReferenciasInexistentesYEliminarHistorial() throws Exception {
        try (var c = base.abrir()) {
            assertThrows(SQLException.class, () -> sql(c, "INSERT INTO asignacion_etiqueta VALUES (2, 999, 2, 2000, NULL, NULL)"));
            assertThrows(SQLException.class, () -> sql(c, "INSERT INTO asignacion_etiqueta VALUES (2, 2, 999, 2000, NULL, NULL)"));
            assertThrows(SQLException.class, () -> sql(c, "DELETE FROM equipo WHERE equipo_id = 1"));
            assertThrows(SQLException.class, () -> sql(c, "DELETE FROM etiqueta_rfid WHERE etiqueta_id = 1"));
            sql(c, "UPDATE asignacion_etiqueta SET fecha_fin = 2000 WHERE asignacion_id = 1");
            assertThrows(SQLException.class, () -> sql(c, "DELETE FROM asignacion_etiqueta WHERE asignacion_id = 1"));
        }
    }

    @Test void correccionTransaccionalConservaHistorialTrasReabrir() throws Exception {
        try (var c = base.abrir()) {
            c.setAutoCommit(false);
            try {
                sql(c, "UPDATE asignacion_etiqueta SET fecha_fin = 2000, motivo_cambio = 'Correccion' WHERE asignacion_id = 1");
                sql(c, "INSERT INTO asignacion_etiqueta VALUES (2, 1, 2, 2000, NULL, NULL)");
                c.commit();
            } catch (SQLException e) { c.rollback(); throw e; }
        }
        try (var c = new BaseDatos(temporal.resolve("i1.db")).abrir()) {
            assertEquals(2, contar(c, "SELECT count(*) FROM asignacion_etiqueta WHERE equipo_id = 1"));
            assertEquals(1, contar(c, "SELECT count(*) FROM asignacion_etiqueta WHERE equipo_id = 1 AND fecha_fin IS NULL AND etiqueta_id = 2"));
            assertEquals(1, contar(c, "SELECT count(*) FROM asignacion_etiqueta WHERE asignacion_id = 1 AND fecha_fin = 2000 AND motivo_cambio = 'Correccion'"));
        }
    }

    @Test void falloDespuesDeCerrarSeRevierteSinPersistenciaParcial() throws Exception {
        try (var c = base.abrir()) {
            sql(c, "INSERT INTO asignacion_etiqueta VALUES (2, 2, 2, 1000, NULL, NULL)");
            c.setAutoCommit(false);
            sql(c, "UPDATE asignacion_etiqueta SET fecha_fin = 2000 WHERE asignacion_id = 1");
            assertThrows(SQLException.class, () -> sql(c, "INSERT INTO asignacion_etiqueta VALUES (3, 1, 2, 2000, NULL, NULL)"));
            c.rollback();
        }
        try (var c = base.abrir()) {
            assertEquals(2, contar(c, "SELECT count(*) FROM asignacion_etiqueta"));
            assertEquals(1, contar(c, "SELECT count(*) FROM asignacion_etiqueta WHERE asignacion_id = 1 AND fecha_fin IS NULL"));
        }
    }

    @Test void unaEtiquetaPuedeVolverAAsociarseConHistorialCerrado() throws Exception {
        try (var c = base.abrir()) {
            sql(c, "UPDATE asignacion_etiqueta SET fecha_fin = 2000 WHERE asignacion_id = 1");
            sql(c, "INSERT INTO asignacion_etiqueta VALUES (2, 2, 1, 2000, NULL, NULL)");
            assertEquals(2, contar(c, "SELECT count(*) FROM asignacion_etiqueta WHERE etiqueta_id = 1"));
        }
    }

    private static void sql(Connection c, String sql) throws SQLException {
        try (var s = c.createStatement()) { s.executeUpdate(sql); }
    }
    private static int contar(Connection c, String sql) throws SQLException {
        try (var s = c.createStatement(); var r = s.executeQuery(sql)) {
            assertTrue(r.next()); return r.getInt(1);
        }
    }
}
