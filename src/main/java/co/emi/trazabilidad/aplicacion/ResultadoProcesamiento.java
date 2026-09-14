package co.emi.trazabilidad.aplicacion;

import co.emi.trazabilidad.dominio.EventoOperativo;
import java.util.Objects;
import java.util.Optional;

/** No persiste rechazos ni crea entidades de verificacion/contingencia. */
public record ResultadoProcesamiento(long lecturaId, Estado estado, Optional<EventoOperativo> evento, String motivo) {
    public enum Estado {
        EVENTO_CREADO, EVIDENCIA_AGREGADA, LECTURA_YA_PROCESADA,
        CONTEXTO_INSUFICIENTE, LECTURA_NO_ELEGIBLE, SIN_ASOCIACION_VALIDA
    }
    public ResultadoProcesamiento {
        if (lecturaId <= 0) throw new IllegalArgumentException("lecturaId debe ser positivo");
        Objects.requireNonNull(estado); Objects.requireNonNull(evento); Objects.requireNonNull(motivo);
        boolean vinculado = estado == Estado.EVENTO_CREADO || estado == Estado.EVIDENCIA_AGREGADA || estado == Estado.LECTURA_YA_PROCESADA;
        if (vinculado != evento.isPresent()) throw new IllegalArgumentException("Resultado y evento incompatibles");
    }
}
