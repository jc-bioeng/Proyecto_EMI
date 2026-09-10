package co.emi.trazabilidad.infraestructura.rfid;

import co.emi.trazabilidad.aplicacion.puertos.FuenteLecturasRFID;
import co.emi.trazabilidad.dominio.LecturaEntradaRFID;
import co.emi.trazabilidad.dominio.OrigenDatos;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Fuente deterministica sin hardware, temporizadores ni hilos propios.
 * Uso confinado a un hilo por el llamador; no ofrece seguridad entre hilos.
 * El receptor se invoca sincronicamente; su excepcion se propaga sin reintento
 * ni rollback de entregas previas. La fuente sigue INICIADA salvo parada explicita.
 */
public final class FuenteSimulada implements FuenteLecturasRFID {
    private final Clock reloj;
    private Estado estado = Estado.NUEVA;
    private Consumer<LecturaEntradaRFID> receptor;
    private boolean emitiendo;

    public FuenteSimulada(Clock reloj) {
        this.reloj = Objects.requireNonNull(reloj, "reloj es obligatorio");
    }

    @Override public void iniciar(Consumer<LecturaEntradaRFID> receptor) {
        Objects.requireNonNull(receptor, "receptor es obligatorio");
        if (estado != Estado.NUEVA) throw new IllegalStateException("La fuente solo puede iniciarse una vez");
        this.receptor = receptor;
        estado = Estado.INICIADA;
    }

    @Override public void detener() {
        estado = Estado.DETENIDA;
        receptor = null;
    }

    @Override public Estado estado() { return estado; }

    /**
     * Genera y entrega una observacion SIMULACION, aun cuando se repita el EPC.
     * Lee el reloj una vez por observacion y nunca inventa metadata fisica.
     * Rechaza emisiones fuera de INICIADA y emisiones reentrantes del receptor.
     */
    public void emitir(String epc) {
        exigirEmisionPermitida();
        emitiendo = true;
        try {
            var lectura = new LecturaEntradaRFID(epc, reloj.instant(), OrigenDatos.SIMULACION, Map.of());
            receptor.accept(lectura);
        } finally {
            emitiendo = false;
        }
    }

    /**
     * Copia el lote y emite secuencialmente. Lote vacio no entrega datos, pero
     * requiere INICIADA. Ante EPC invalido, parada o excepcion del receptor se
     * interrumpe el lote: entregas anteriores permanecen y las restantes no se
     * intentan. No es una transaccion ni hay reintento automatico.
     */
    public void emitir(List<String> epcs) {
        exigirEmisionPermitida();
        for (String epc : new ArrayList<>(Objects.requireNonNull(epcs, "epcs es obligatorio"))) {
            emitir(epc);
        }
    }

    private void exigirEmisionPermitida() {
        if (estado != Estado.INICIADA) throw new IllegalStateException("La fuente no esta iniciada");
        if (emitiendo) throw new IllegalStateException("No se permite emitir desde el receptor");
    }
}
