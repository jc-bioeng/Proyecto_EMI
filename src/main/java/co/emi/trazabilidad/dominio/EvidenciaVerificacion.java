package co.emi.trazabilidad.dominio;

import java.util.Objects;

/** Conserva incluso lecturas excluidas; no son equipos NO_ESPERADO identificados. */
public record EvidenciaVerificacion(long verificacionId, LecturaRFID lectura,
        Long equipoId, Long asignacionId, Estado estado) {
    public enum Estado { ATRIBUIDA, SIN_ASOCIACION, ASOCIACION_AMBIGUA, ANTERIOR_A_SESION, LECTURA_FUTURA }
    public EvidenciaVerificacion {
        if (verificacionId <= 0) throw new IllegalArgumentException("Identificador invalido");
        Objects.requireNonNull(lectura); Objects.requireNonNull(estado);
        if (estado == Estado.ATRIBUIDA) {
            if (equipoId == null || equipoId <= 0 || asignacionId == null || asignacionId <= 0)
                throw new IllegalArgumentException("Evidencia atribuida requiere equipo y asignacion");
        } else if (equipoId != null || asignacionId != null)
            throw new IllegalArgumentException("Evidencia excluida no atribuye equipo");
    }
}
