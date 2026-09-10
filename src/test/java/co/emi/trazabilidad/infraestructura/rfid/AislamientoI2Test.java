package co.emi.trazabilidad.infraestructura.rfid;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;
import javax.tools.ToolProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class AislamientoI2Test {
    @TempDir Path temporal;

    @Test void i2CompilaSoloConJavaEstandarSinSdkNiDependenciasDelProyecto() throws Exception {
        var compilador = ToolProvider.getSystemJavaCompiler();
        assertNotNull(compilador, "La verificacion requiere el JDK 21 del proyecto");
        var archivos = List.of(
            "dominio/OrigenDatos.java",
            "dominio/LecturaEntradaRFID.java",
            "aplicacion/puertos/FuenteLecturasRFID.java",
            "infraestructura/rfid/FuenteSimulada.java"
        ).stream().map(p -> Path.of("src/main/java/co/emi/trazabilidad").resolve(p)).toList();
        var diagnostico = new StringWriter();
        try (var gestor = compilador.getStandardFileManager(null, null, StandardCharsets.UTF_8)) {
            // El classpath/sourcepath vacio impide resolver SDK, JDBC o clases
            // productivas compiladas por Maven fuera de estos cuatro fuentes.
            var opciones = List.of("--release", "21", "-proc:none", "-encoding", "UTF-8",
                "-classpath", temporal.toString(), "-sourcepath", temporal.toString(),
                "-d", temporal.toString());
            assertTrue(compilador.getTask(diagnostico, gestor, null, opciones, null,
                gestor.getJavaFileObjectsFromPaths(archivos)).call(), diagnostico::toString);
        }
    }
}
