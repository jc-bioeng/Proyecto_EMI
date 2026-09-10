package co.emi.trazabilidad.infraestructura.rfid;

import co.emi.trazabilidad.aplicacion.puertos.FuenteLecturasRFID;
import co.emi.trazabilidad.dominio.LecturaEntradaRFID;
import co.emi.trazabilidad.dominio.OrigenDatos;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import static co.emi.trazabilidad.aplicacion.puertos.FuenteLecturasRFID.Estado.*;
import static org.junit.jupiter.api.Assertions.*;

class FuenteSimuladaTest {
    private static final Instant AHORA = Instant.parse("2026-09-07T12:00:00.123456789Z");
    private final FuenteSimulada simulada = new FuenteSimulada(Clock.fixed(AHORA, ZoneOffset.UTC));
    private final FuenteLecturasRFID fuente = simulada;
    private final List<LecturaEntradaRFID> recibidas = new ArrayList<>();

    @Test void contratoEntregaObservacionPropiaYSeDetiene() {
        assertEquals(NUEVA, fuente.estado());
        fuente.iniciar(recibidas::add);
        assertEquals(INICIADA, fuente.estado());
        simulada.emitir("000aBC");
        assertEquals(List.of(new LecturaEntradaRFID("000aBC", AHORA, OrigenDatos.SIMULACION, Map.of())), recibidas);
        fuente.detener();
        assertEquals(DETENIDA, fuente.estado());
    }

    @Test void conservaOrdenYRepeticionesSinDeduplicar() {
        fuente.iniciar(recibidas::add);
        simulada.emitir(List.of("0001", "00aB", "0001", "0001", "00aB"));
        assertEquals(List.of("0001", "00aB", "0001", "0001", "00aB"), recibidas.stream().map(LecturaEntradaRFID::epc).toList());
        assertTrue(recibidas.stream().allMatch(l -> l.origenDatos() == OrigenDatos.SIMULACION));
        assertTrue(recibidas.stream().allMatch(l -> l.metadata().isEmpty()));
    }

    @Test void rechazaEmisionAntesDeIniciarIncluidoLoteVacio() {
        assertThrows(IllegalStateException.class, () -> simulada.emitir("0001"));
        assertThrows(IllegalStateException.class, () -> simulada.emitir(List.of()));
        assertEquals(NUEVA, fuente.estado());
    }

    @Test void rechazaEmisionDespuesDeDetener() {
        fuente.iniciar(recibidas::add);
        fuente.detener();
        assertThrows(IllegalStateException.class, () -> simulada.emitir("0001"));
        assertThrows(IllegalStateException.class, () -> simulada.emitir(List.of("0002")));
        assertTrue(recibidas.isEmpty());
    }

    @Test void segundoInicioNoReemplazaReceptor() {
        fuente.iniciar(recibidas::add);
        assertThrows(IllegalStateException.class, () -> fuente.iniciar(l -> fail("Receptor reemplazado")));
        simulada.emitir("0001");
        assertEquals(1, recibidas.size());
    }

    @Test void detenerDosVecesEsIdempotenteYNoPermiteReinicio() {
        fuente.iniciar(recibidas::add);
        fuente.detener();
        fuente.detener();
        assertEquals(DETENIDA, fuente.estado());
        assertThrows(IllegalStateException.class, () -> fuente.iniciar(recibidas::add));
    }

    @Test void detenerSinIniciarEsTerminalEIdempotente() {
        fuente.detener();
        fuente.detener();
        assertEquals(DETENIDA, fuente.estado());
        assertThrows(IllegalStateException.class, () -> fuente.iniciar(recibidas::add));
    }

    @Test void receptorNuloNoIniciaLaFuente() {
        assertThrows(NullPointerException.class, () -> fuente.iniciar(null));
        assertEquals(NUEVA, fuente.estado());
        fuente.iniciar(recibidas::add);
        simulada.emitir("0001");
        assertEquals(1, recibidas.size());
    }

    @Test void rechazaEpcObligatorioSinEntregarNiInutilizarFuente() {
        fuente.iniciar(recibidas::add);
        for (String epc : new String[] {null, "", " ", "\t\n"}) {
            assertThrows(IllegalArgumentException.class, () -> simulada.emitir(epc));
        }
        assertTrue(recibidas.isEmpty());
        simulada.emitir("0001");
        assertEquals(1, recibidas.size());
    }

    @Test void noIntroduceNormalizacionNiRestriccionHexAjenaAI1() {
        fuente.iniciar(recibidas::add);
        simulada.emitir(List.of("00aB", " 00aB ", "epc-prueba", "A"));
        assertEquals(List.of("00aB", " 00aB ", "epc-prueba", "A"), recibidas.stream().map(LecturaEntradaRFID::epc).toList());
    }

    @Test void timestampSeObtienePorObservacionDelRelojInyectado() {
        var reloj = new Clock() {
            private int consultas;
            @Override public ZoneId getZone() { return ZoneOffset.ofHours(-5); }
            @Override public Clock withZone(ZoneId zone) { throw new UnsupportedOperationException(); }
            @Override public Instant instant() { return AHORA.plusSeconds(consultas++); }
        };
        var otra = new FuenteSimulada(reloj);
        otra.iniciar(recibidas::add);
        otra.emitir(List.of("0001", "0001", "0002"));
        assertEquals(List.of(AHORA, AHORA.plusSeconds(1), AHORA.plusSeconds(2)), recibidas.stream().map(LecturaEntradaRFID::timestamp).toList());
    }

    @Test void excepcionDelReceptorEsVisibleSinReintentoYPermiteOtraEmision() {
        var fallo = new IllegalArgumentException("Fallo del receptor");
        fuente.iniciar(l -> {
            recibidas.add(l);
            if (recibidas.size() == 1) throw fallo;
        });
        assertSame(fallo, assertThrows(IllegalArgumentException.class, () -> simulada.emitir(List.of("0001", "0002"))));
        assertEquals(List.of("0001"), recibidas.stream().map(LecturaEntradaRFID::epc).toList());
        assertEquals(INICIADA, fuente.estado());
        simulada.emitir("0003");
        assertEquals(List.of("0001", "0003"), recibidas.stream().map(LecturaEntradaRFID::epc).toList());
    }

    @Test void datoInvalidoInterrumpeLoteSinRevertirEntregasPrevias() {
        fuente.iniciar(recibidas::add);
        assertThrows(IllegalArgumentException.class, () -> simulada.emitir(Arrays.asList("0001", null, "0002")));
        assertEquals(List.of("0001"), recibidas.stream().map(LecturaEntradaRFID::epc).toList());
    }

    @Test void paradaDesdeReceptorImpideRestoDelLote() {
        fuente.iniciar(l -> { recibidas.add(l); fuente.detener(); });
        assertThrows(IllegalStateException.class, () -> simulada.emitir(List.of("0001", "0002")));
        assertEquals(1, recibidas.size());
        assertEquals(DETENIDA, fuente.estado());
    }

    @Test void rechazaEmisionReentranteSinOcultarla() {
        fuente.iniciar(l -> { recibidas.add(l); simulada.emitir("0002"); });
        assertThrows(IllegalStateException.class, () -> simulada.emitir("0001"));
        assertEquals(List.of("0001"), recibidas.stream().map(LecturaEntradaRFID::epc).toList());
        assertEquals(INICIADA, fuente.estado());
    }

    @Test void loteEsUnaCopiaEstableFrenteACambiosDelReceptor() {
        var lote = new ArrayList<>(List.of("0001", "0002"));
        fuente.iniciar(l -> { recibidas.add(l); lote.clear(); });
        simulada.emitir(lote);
        assertEquals(List.of("0001", "0002"), recibidas.stream().map(LecturaEntradaRFID::epc).toList());
    }

    @Test void loteVacioNoEmiteYLoteNuloFalla() {
        fuente.iniciar(recibidas::add);
        simulada.emitir(List.of());
        assertThrows(NullPointerException.class, () -> simulada.emitir((List<String>) null));
        assertTrue(recibidas.isEmpty());
    }

    @Test void instanciasNoCompartenEstadoNiReceptor() {
        var otra = new FuenteSimulada(Clock.fixed(AHORA, ZoneOffset.UTC));
        fuente.iniciar(recibidas::add);
        otra.detener();
        simulada.emitir("0001");
        assertEquals(INICIADA, fuente.estado());
        assertEquals(DETENIDA, otra.estado());
        assertEquals(1, recibidas.size());
    }

    @Test void relojEsObligatorio() {
        assertThrows(NullPointerException.class, () -> new FuenteSimulada(null));
    }
}
