package co.emi.trazabilidad.entrada;

import co.emi.trazabilidad.aplicacion.*;
import co.emi.trazabilidad.dominio.EstadoEtiqueta;
import java.io.PrintWriter;
import java.util.Objects;

/** Adaptacion de argumentos y resultados; todas las reglas residen en ServicioI1. */
public final class ConsolaEmi {
    private final ServicioI1 servicio;
    public ConsolaEmi(ServicioI1 servicio) { this.servicio = Objects.requireNonNull(servicio); }

    public int ejecutar(String[] args, PrintWriter salida, PrintWriter errores) {
        try {
            if (args.length == 0) throw new IllegalArgumentException("Falta el comando; use ayuda");
            switch (args[0]) {
                case "ayuda" -> {
                    cantidad(args, 1, 1);
                    salida.println("crear-equipo <codigo> <true|false> [descripcion]");
                    salida.println("crear-etiqueta <epc> <ACTIVA|INACTIVA>");
                    salida.println("asociar <equipoId> <etiquetaId>");
                    salida.println("vigente <equipoId>");
                    salida.println("buscar-epc <epc>");
                    salida.println("corregir <equipoId> <nuevaEtiquetaId> [motivo]");
                    salida.println("historial <equipoId>");
                }
                case "crear-equipo" -> {
                    cantidad(args, 3, 4);
                    if (!args[2].equals("true") && !args[2].equals("false")) throw new IllegalArgumentException("activo_piloto debe ser true o false");
                    salida.println(servicio.crearEquipo(args[1], args.length == 4 ? args[3] : null, Boolean.parseBoolean(args[2])));
                }
                case "crear-etiqueta" -> {
                    cantidad(args, 3, 3);
                    salida.println(servicio.crearEtiqueta(args[1], EstadoEtiqueta.valueOf(args[2])));
                }
                case "asociar" -> {
                    cantidad(args, 3, 3);
                    salida.println(servicio.asociar(Long.parseLong(args[1]), Long.parseLong(args[2])));
                }
                case "vigente" -> {
                    cantidad(args, 2, 2);
                    salida.println(servicio.consultarVigente(Long.parseLong(args[1])).map(Object::toString).orElse("Sin asociacion vigente"));
                }
                case "buscar-epc" -> {
                    cantidad(args, 2, 2);
                    salida.println(servicio.buscarEquipoPorEpc(args[1]).map(Object::toString).orElse("Sin equipo asociado"));
                }
                case "corregir" -> {
                    cantidad(args, 3, 4);
                    salida.println(servicio.corregir(Long.parseLong(args[1]), Long.parseLong(args[2]), args.length == 4 ? args[3] : null));
                }
                case "historial" -> {
                    cantidad(args, 2, 2);
                    var historial = servicio.historial(Long.parseLong(args[1]));
                    if (historial.isEmpty()) salida.println("Sin asociaciones");
                    else historial.forEach(salida::println);
                }
                default -> throw new IllegalArgumentException("Comando desconocido; use ayuda");
            }
            salida.flush();
            return 0;
        } catch (ErrorAplicacion e) {
            errores.println(e.codigo() + ": " + e.getMessage()); errores.flush(); return 1;
        } catch (IllegalArgumentException e) {
            errores.println("Argumentos invalidos: " + e.getMessage()); errores.flush(); return 2;
        }
    }
    private static void cantidad(String[] args, int minimo, int maximo) {
        if (args.length < minimo || args.length > maximo) throw new IllegalArgumentException("Cantidad de argumentos incorrecta; use ayuda");
    }
}
