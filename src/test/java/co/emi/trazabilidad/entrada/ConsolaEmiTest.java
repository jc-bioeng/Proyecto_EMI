package co.emi.trazabilidad.entrada;

import co.emi.trazabilidad.aplicacion.ServicioI1;
import co.emi.trazabilidad.infraestructura.persistencia.*;
import java.io.*;
import java.nio.file.Path;
import java.time.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class ConsolaEmiTest {
    @TempDir Path temporal;
    private ConsolaEmi consola;
    private StringWriter salida;
    private StringWriter errores;

    @BeforeEach void preparar() throws Exception {
        var base = new BaseDatos(temporal.resolve("consola.db")); base.migrar();
        consola = new ConsolaEmi(new ServicioI1(new JdbcUnidadDeTrabajo(base), Clock.fixed(Instant.parse("2026-09-06T10:00:00Z"), ZoneOffset.UTC)));
    }
    private int ejecutar(String... args) {
        salida = new StringWriter(); errores = new StringWriter();
        return consola.ejecutar(args, new PrintWriter(salida), new PrintWriter(errores));
    }
    @Test void ejecutaLosSieteCasosDeUso() {
        assertEquals(0, ejecutar("crear-equipo", "EQ-001", "true", "Monitor"));
        assertTrue(salida.toString().contains("equipoId=1"));
        assertEquals(0, ejecutar("crear-etiqueta", "0001", "ACTIVA"));
        assertEquals(0, ejecutar("crear-etiqueta", "0002", "ACTIVA"));
        assertEquals(0, ejecutar("asociar", "1", "1"));
        assertEquals(0, ejecutar("vigente", "1")); assertTrue(salida.toString().contains("etiquetaId=1"));
        assertEquals(0, ejecutar("buscar-epc", "0001")); assertTrue(salida.toString().contains("EQ-001"));
        assertEquals(0, ejecutar("corregir", "1", "2", "Cambio"));
        assertEquals(0, ejecutar("historial", "1")); assertEquals(2, salida.toString().lines().count());
        assertTrue(salida.toString().contains("motivoCambio=Cambio"));
    }
    @Test void comunicaErroresDeUsoYNegocio() {
        assertEquals(2, ejecutar("crear-equipo", "EQ", "quizas"));
        assertEquals(2, ejecutar("crear-etiqueta", "0001", "ASIGNADA"));
        assertEquals(2, ejecutar("asociar", "abc", "1"));
        assertEquals(2, ejecutar("otro"));
        assertEquals(0, ejecutar("crear-equipo", "EQ-001", "false"));
        assertEquals(1, ejecutar("crear-equipo", "EQ-001", "false"));
        assertTrue(errores.toString().contains("CONFLICTO")); assertTrue(salida.toString().isBlank());
    }
}
