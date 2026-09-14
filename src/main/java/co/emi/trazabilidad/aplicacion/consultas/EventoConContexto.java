package co.emi.trazabilidad.aplicacion.consultas;

import co.emi.trazabilidad.dominio.*;
import java.util.*;

/** Proyeccion auditable: origenes mixtos siguen siendo un conjunto, no un origen RF_REAL unico. */
public record EventoConContexto(EventoOperativo evento, SesionOperacion sesion, Set<OrigenDatos> origenes) {
    public EventoConContexto {
        Objects.requireNonNull(evento); Objects.requireNonNull(sesion);
        origenes = Set.copyOf(Objects.requireNonNull(origenes));
        if (evento.sesionId() != sesion.sesionId()) throw new IllegalArgumentException("Sesion incompatible con evento");
    }
}
