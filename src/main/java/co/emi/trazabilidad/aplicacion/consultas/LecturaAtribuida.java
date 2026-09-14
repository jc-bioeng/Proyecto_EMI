package co.emi.trazabilidad.aplicacion.consultas;

import co.emi.trazabilidad.dominio.*;
import java.util.Objects;

/** Proyeccion de consulta; no es una entidad ni una copia persistida de la lectura. */
public record LecturaAtribuida(LecturaRFID lectura, AsignacionEtiqueta asignacion) {
    public LecturaAtribuida {
        Objects.requireNonNull(lectura); Objects.requireNonNull(asignacion);
        if (lectura.timestamp().isBefore(asignacion.fechaInicio())
                || (asignacion.fechaFin() != null && !lectura.timestamp().isBefore(asignacion.fechaFin())))
            throw new IllegalArgumentException("Lectura fuera del intervalo de atribucion");
    }
}
