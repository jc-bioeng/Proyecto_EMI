package co.emi.trazabilidad;

import java.nio.file.Path;
import co.emi.trazabilidad.infraestructura.persistencia.BaseDatos;

public final class EmiApplication {
    private EmiApplication() { }
    public static void main(String[] args) throws Exception {
        Path archivo = Path.of(args.length == 0 ? "data/emi.db" : args[0]);
        new BaseDatos(archivo).migrar();
        System.out.println("Persistencia EMI preparada: " + archivo.toAbsolutePath());
    }
}
