package co.emi.trazabilidad.aplicacion;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import javax.tools.ToolProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class AislamientoI5Test {
    @TempDir Path temporal;
    @Test void consultasDominioYPuertosCompilanSinJdbcFlywayNiSdk()throws Exception {
        var raiz=Path.of("src/main/java/co/emi/trazabilidad");List<Path> archivos=new ArrayList<>();
        for(String carpeta:List.of("dominio","aplicacion/puertos","aplicacion/consultas")) {
            try(var fuentes=Files.list(raiz.resolve(carpeta))){archivos.addAll(fuentes.filter(p->p.toString().endsWith(".java")).toList());}
        }
        archivos.add(raiz.resolve("aplicacion/ErrorAplicacion.java"));archivos.add(raiz.resolve("aplicacion/ServicioI5.java"));
        var compilador=ToolProvider.getSystemJavaCompiler();assertNotNull(compilador);var diagnostico=new StringWriter();
        try(var gestor=compilador.getStandardFileManager(null,null,StandardCharsets.UTF_8)) {
            var opciones=List.of("--release","21","-proc:none","-encoding","UTF-8","-classpath",temporal.toString(),"-sourcepath",temporal.toString(),"-d",temporal.toString());
            assertTrue(compilador.getTask(diagnostico,gestor,null,opciones,null,gestor.getJavaFileObjectsFromPaths(archivos)).call(),diagnostico::toString);
        }
    }
}
