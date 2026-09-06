package co.emi.trazabilidad;

import java.io.PrintWriter;
import java.nio.file.Path;
import java.time.Clock;
import java.util.Arrays;
import co.emi.trazabilidad.aplicacion.ServicioI1;
import co.emi.trazabilidad.entrada.ConsolaEmi;
import co.emi.trazabilidad.infraestructura.persistencia.*;

public final class EmiApplication {
    private EmiApplication() { }
    public static void main(String[] args) throws Exception {
        Path archivo = Path.of(args.length == 0 ? "data/emi.db" : args[0]);
        var base = new BaseDatos(archivo);
        base.migrar();
        if (args.length <= 1) {
            System.out.println("Persistencia EMI preparada: " + archivo.toAbsolutePath());
            return;
        }
        var consola = new ConsolaEmi(new ServicioI1(new JdbcUnidadDeTrabajo(base), Clock.systemUTC()));
        int resultado = consola.ejecutar(Arrays.copyOfRange(args, 1, args.length),
            new PrintWriter(System.out, true), new PrintWriter(System.err, true));
        if (resultado != 0) System.exit(resultado);
    }
}
