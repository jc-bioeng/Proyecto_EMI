package co.emi.trazabilidad.aplicacion.puertos;

import co.emi.trazabilidad.dominio.LecturaEntradaRFID;
import java.util.function.Consumer;

/**
 * Entrada de observaciones sin dependencia de fabricante, persistencia o UI.
 * Obtener lecturas se materializa mediante el receptor registrado al iniciar.
 * No interpreta movimientos ni elimina repeticiones. El hilo de entrega depende
 * de la implementacion: el receptor no debe asumir el hilo que llamo iniciar.
 * Una parada es local; no acredita una parada fisica RF. Una entrega ya en curso
 * puede terminar, pero no deben iniciarse nuevas entregas estando DETENIDA.
 */
public interface FuenteLecturasRFID {
    enum Estado { NUEVA, INICIADA, DETENIDA }

    /**
     * Registra un unico receptor no nulo y pasa de NUEVA a INICIADA.
     * @throws NullPointerException si el receptor es null (no cambia el estado)
     * @throws IllegalStateException si ya se inicio o detuvo esta instancia
     */
    void iniciar(Consumer<LecturaEntradaRFID> receptor);

    /** Pasa a DETENIDA incluso desde NUEVA. Idempotente; no permite reiniciar. */
    void detener();

    /** Estado local de la fuente; no describe disponibilidad de hardware. */
    Estado estado();
}
