package co.emi.trazabilidad.dominio;

import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ModeloI3Test {
    @Test void sesionRechazaIdentidadContextoYEstadoIncoherentes() {
        assertThrows(IllegalArgumentException.class, () -> sesion(0, "P", EstadoSesion.ABIERTA, null));
        assertThrows(IllegalArgumentException.class, () -> sesion(1, " ", EstadoSesion.ABIERTA, null));
        assertThrows(IllegalArgumentException.class, () -> sesion(1, "P", EstadoSesion.CERRADA, null));
        assertThrows(IllegalArgumentException.class, () -> sesion(1, "P", EstadoSesion.ABIERTA, Instant.EPOCH));
        assertThrows(IllegalArgumentException.class, () -> sesion(1, "P", EstadoSesion.CERRADA, Instant.EPOCH.minusSeconds(1)));
        assertThrows(NullPointerException.class, () -> sesion(1, "P", null, null));
    }
    private SesionOperacion sesion(long id, String punto, EstadoSesion estado, Instant fin) {
        return new SesionOperacion(id, punto, "A", TipoOperacion.VERIFICACION, Instant.EPOCH, fin, estado, null);
    }
    @Test void lecturaReutilizaContratoI2YValidaIdentidades() {
        assertThrows(IllegalArgumentException.class, () -> new LecturaRFID(0, "x", Instant.EPOCH, OrigenDatos.SIMULACION, null, Map.of()));
        assertThrows(IllegalArgumentException.class, () -> new LecturaRFID(1, "x", Instant.EPOCH, OrigenDatos.SIMULACION, 0L, Map.of()));
        assertThrows(IllegalArgumentException.class, () -> new LecturaRFID(1, " ", Instant.EPOCH, OrigenDatos.SIMULACION, null, Map.of()));
        assertThrows(NullPointerException.class, () -> new LecturaRFID(1, "x", null, OrigenDatos.SIMULACION, null, Map.of()));
        assertThrows(NullPointerException.class, () -> new LecturaRFID(1, "x", Instant.EPOCH, null, null, Map.of()));
        assertThrows(NullPointerException.class, () -> new LecturaRFID(1, "x", Instant.EPOCH, OrigenDatos.SIMULACION, null, null));
    }
}
