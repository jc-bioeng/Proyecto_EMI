package co.emi.trazabilidad.dominio;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LecturaEntradaRFIDTest {
    @Test void metadataVaciaEsValida() {
        var lectura = new LecturaEntradaRFID("000a", Instant.EPOCH, OrigenDatos.SIMULACION, Map.of());
        assertEquals("000a", lectura.epc());
        assertTrue(lectura.metadata().isEmpty());
    }

    @Test void metadataSeCopiaYNoSePuedeModificar() {
        var metadata = new HashMap<>(Map.of("descripcion", "dato de prueba"));
        var lectura = new LecturaEntradaRFID("0001", Instant.EPOCH, OrigenDatos.SIMULACION, metadata);
        metadata.clear();
        assertEquals(Map.of("descripcion", "dato de prueba"), lectura.metadata());
        assertThrows(UnsupportedOperationException.class, () -> lectura.metadata().put("otro", "valor"));
    }

    @Test void rechazaEpcAusenteComoI1() {
        for (String epc : new String[] {null, "", "\t "}) {
            assertThrows(IllegalArgumentException.class, () -> new LecturaEntradaRFID(epc, Instant.EPOCH, OrigenDatos.SIMULACION, Map.of()));
        }
    }

    @Test void exigeTimestampOrigenYMapaNoNulos() {
        assertThrows(NullPointerException.class, () -> new LecturaEntradaRFID("0001", null, OrigenDatos.SIMULACION, Map.of()));
        assertThrows(NullPointerException.class, () -> new LecturaEntradaRFID("0001", Instant.EPOCH, null, Map.of()));
        assertThrows(NullPointerException.class, () -> new LecturaEntradaRFID("0001", Instant.EPOCH, OrigenDatos.SIMULACION, null));
    }

    @Test void metadataNoAdmiteClavesNiValoresNulos() {
        var metadata = new HashMap<String, String>();
        metadata.put("campo", null);
        assertThrows(NullPointerException.class, () -> new LecturaEntradaRFID("0001", Instant.EPOCH, OrigenDatos.SIMULACION, metadata));
        metadata.clear();
        metadata.put(null, "valor");
        assertThrows(NullPointerException.class, () -> new LecturaEntradaRFID("0001", Instant.EPOCH, OrigenDatos.SIMULACION, metadata));
    }
}
