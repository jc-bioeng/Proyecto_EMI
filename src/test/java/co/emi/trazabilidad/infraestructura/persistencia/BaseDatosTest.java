package co.emi.trazabilidad.infraestructura.persistencia;

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class BaseDatosTest {
    @TempDir Path temporal;
    @Test void migracionPersisteYReabrirNoLaRepite() throws Exception {
        Path archivo = temporal.resolve("emi.db");
        var primera = new BaseDatos(archivo);
        primera.migrar();
        var segunda = new BaseDatos(archivo);
        segunda.migrar();
        try (var connection = segunda.abrir(); var statement = connection.createStatement()) {
            try (var result = statement.executeQuery("SELECT count(*) FROM flyway_schema_history WHERE version = '1' AND success = 1")) {
                assertTrue(result.next()); assertEquals(1, result.getInt(1));
            }
            try (var result = statement.executeQuery("PRAGMA foreign_keys")) {
                assertTrue(result.next()); assertEquals(1, result.getInt(1));
            }
        }
    }
}
