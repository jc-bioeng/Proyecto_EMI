package co.emi.trazabilidad.aplicacion;

import co.emi.trazabilidad.dominio.*;
import co.emi.trazabilidad.infraestructura.persistencia.*;
import co.emi.trazabilidad.infraestructura.rfid.FuenteSimulada;
import java.nio.file.Path;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import static co.emi.trazabilidad.aplicacion.ErrorAplicacion.Codigo.*;

class ServicioI3Test {
    @TempDir Path temporal;
    private BaseDatos base;
    private ServicioI3 servicio;
    private static final Instant INICIO = Instant.parse("2026-09-12T10:00:00Z");
    @BeforeEach void preparar() throws Exception {
        base = new BaseDatos(temporal.resolve("i3.db"));
        base.migrar();
        servicio = conReloj(INICIO);
    }
    private ServicioI3 conReloj(Instant instante) {
        return new ServicioI3(new JdbcUnidadDeTrabajo(base), Clock.fixed(instante, ZoneId.of("America/Bogota")));
    }
    private SesionOperacion sesion() { return servicio.crearSesion("PUNTO-PRUEBA", "actor de prueba", TipoOperacion.INGRESO, null); }
    private LecturaEntradaRFID entrada(Instant t) { return new LecturaEntradaRFID("000aB ", t, OrigenDatos.SIMULACION, Map.of()); }
    private long contar(String tabla) throws Exception {
        try (var c = base.abrir(); var s = c.createStatement(); var r = s.executeQuery("SELECT count(*) FROM " + tabla)) {
            assertTrue(r.next()); return r.getLong(1);
        }
    }
    @Test void creaYConsultaTodosLosTiposConContextoSinAsignarRolesInstitucionales() {
        for (var tipo : TipoOperacion.values()) {
            var s = servicio.crearSesion("Punto ' prueba", "Contexto libre", tipo, "ensayo de software");
            assertTrue(s.sesionId() > 0);
            assertEquals(s, servicio.consultarSesion(s.sesionId()).orElseThrow());
            assertEquals(tipo, s.tipoOperacion()); assertEquals(INICIO, s.fechaInicio());
            assertEquals("Punto ' prueba", s.puntoControl()); assertEquals("Contexto libre", s.actorContexto());
            assertEquals("ensayo de software", s.contexto()); assertEquals(EstadoSesion.ABIERTA, s.estado()); assertNull(s.fechaFin());
        }
    }
    @Test void cierreConservaContextoYLecturasTrasReabrir() throws Exception {
        var s = sesion(); var lectura = servicio.persistirLectura(entrada(INICIO), s.sesionId());
        var cerrada = conReloj(INICIO.plusSeconds(10).plusNanos(123456789)).cerrarSesion(s.sesionId());
        var reabierto = new ServicioI3(new JdbcUnidadDeTrabajo(new BaseDatos(temporal.resolve("i3.db"))), Clock.systemUTC());
        assertEquals(cerrada, reabierto.consultarSesion(s.sesionId()).orElseThrow());
        assertEquals(EstadoSesion.CERRADA, cerrada.estado()); assertEquals(INICIO.plusSeconds(10).plusMillis(123), cerrada.fechaFin());
        assertEquals(s.puntoControl(), cerrada.puntoControl()); assertEquals(s.actorContexto(), cerrada.actorContexto());
        assertEquals(s.tipoOperacion(), cerrada.tipoOperacion()); assertEquals(s.fechaInicio(), cerrada.fechaInicio());
        assertEquals(s.contexto(), cerrada.contexto()); assertEquals(List.of(lectura), reabierto.lecturasDeSesion(s.sesionId()));
    }
    @Test void cerrarDosVecesEsConflictoYNoReescribeFecha() {
        var s = sesion(); var cerrada = servicio.cerrarSesion(s.sesionId());
        assertEquals(CONFLICTO, assertThrows(ErrorAplicacion.class, () -> conReloj(INICIO.plusSeconds(30)).cerrarSesion(s.sesionId())).codigo());
        assertEquals(cerrada, servicio.consultarSesion(s.sesionId()).orElseThrow());
    }
    @Test void cierreAnteriorAlInicioNoEscribe() {
        var s = sesion();
        assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> conReloj(INICIO.minusMillis(1)).cerrarSesion(s.sesionId())).codigo());
        assertEquals(s, servicio.consultarSesion(s.sesionId()).orElseThrow());
    }
    @Test void validaDatosAntesDeEscribir() throws Exception {
        for (String dato : new String[] {null, "", " ", "\t"}) {
            assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> servicio.crearSesion(dato, "actor", TipoOperacion.SALIDA, null)).codigo());
            assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> servicio.crearSesion("punto", dato, TipoOperacion.SALIDA, null)).codigo());
        }
        assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> servicio.crearSesion("p", "a", null, null)).codigo());
        assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> servicio.persistirLectura(null, null)).codigo());
        assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> servicio.persistirLectura(entrada(INICIO), 0L)).codigo());
        assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> servicio.consultarSesion(0)).codigo());
        assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> servicio.consultarLectura(-1)).codigo());
        assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> servicio.cerrarSesion(-1)).codigo());
        assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> servicio.lecturasDeSesion(0)).codigo());
        assertEquals(0, contar("sesion_operacion")); assertEquals(0, contar("lectura_rfid"));
    }
    @Test void distingueAusenciaDeConsultaYReferenciaInexistente() {
        assertTrue(servicio.consultarSesion(999).isEmpty()); assertTrue(servicio.consultarLectura(999).isEmpty());
        assertEquals(NO_ENCONTRADO, assertThrows(ErrorAplicacion.class, () -> servicio.cerrarSesion(999)).codigo());
        assertEquals(NO_ENCONTRADO, assertThrows(ErrorAplicacion.class, () -> servicio.lecturasDeSesion(999)).codigo());
        assertEquals(NO_ENCONTRADO, assertThrows(ErrorAplicacion.class, () -> servicio.persistirLectura(entrada(INICIO), 999L)).codigo());
    }
    @Test void conservaEntradaLiteralMetadataYOrigenTrasReabrir() throws Exception {
        var s = sesion();
        var datos = new HashMap<>(Map.of("antena", "2", "rssi", "-45.2", "unicode", "férula\n'\\", "", ""));
        var e = new LecturaEntradaRFID(" 000aB' OR 1=1 -- ", INICIO.plusNanos(123456789), OrigenDatos.SIMULACION, datos);
        var l = servicio.persistirLectura(e, s.sesionId()); datos.clear();
        var reabierto = new ServicioI3(new JdbcUnidadDeTrabajo(new BaseDatos(temporal.resolve("i3.db"))), Clock.systemUTC());
        assertEquals(l, reabierto.consultarLectura(l.lecturaId()).orElseThrow());
        assertEquals(e.epc(), l.epc()); assertEquals(e.timestamp(), l.timestamp()); assertEquals(e.origenDatos(), l.origenDatos());
        assertEquals(e.metadata(), l.metadata()); assertEquals(s.sesionId(), l.sesionId());
        assertThrows(UnsupportedOperationException.class, () -> l.metadata().put("nueva", "x"));
    }
    @Test void rfRealEsSoloValorDeDominioEnEstaPruebaSoftware() {
        var e = new LecturaEntradaRFID("EPC-PRUEBA", INICIO, OrigenDatos.RF_REAL, Map.of());
        var l = servicio.persistirLectura(e, null);
        assertEquals(OrigenDatos.RF_REAL, servicio.consultarLectura(l.lecturaId()).orElseThrow().origenDatos());
    }
    @Test void lecturaSinSesionNoInventaContextoNiExigeEtiquetaRegistrada() throws Exception {
        var l = servicio.persistirLectura(entrada(INICIO), null);
        assertNull(l.sesionId()); assertEquals(l, servicio.consultarLectura(l.lecturaId()).orElseThrow());
        assertEquals(0, contar("sesion_operacion")); assertEquals(0, contar("etiqueta_rfid")); assertEquals(0, contar("equipo"));
    }
    @Test void mismoEpcMismaSesionMultiplesTimestampsConservaTodasYOrdenaConEmpates() {
        var s = sesion();
        var tardia = servicio.persistirLectura(entrada(INICIO.plusNanos(2)), s.sesionId());
        var primera = servicio.persistirLectura(entrada(INICIO), s.sesionId());
        var empate = servicio.persistirLectura(entrada(INICIO), s.sesionId());
        var intermedia = servicio.persistirLectura(entrada(INICIO.plusNanos(1)), s.sesionId());
        assertEquals(List.of(primera, empate, intermedia, tardia), servicio.lecturasDeSesion(s.sesionId()));
        assertEquals(4, Set.of(primera.lecturaId(), empate.lecturaId(), intermedia.lecturaId(), tardia.lecturaId()).size());
    }
    @Test void listarAislaSesionesYDevuelveVacioSiNoHayLecturas() {
        var a = sesion(); var b = sesion();
        var l = servicio.persistirLectura(entrada(INICIO), a.sesionId());
        assertEquals(List.of(l), servicio.lecturasDeSesion(a.sesionId())); assertTrue(servicio.lecturasDeSesion(b.sesionId()).isEmpty());
    }
    @Test void entregaTardiaNoReabreSesionNiDescartaEvidencia() {
        var s = sesion(); var cerrada = servicio.cerrarSesion(s.sesionId());
        var l = servicio.persistirLectura(entrada(INICIO.plusSeconds(1)), s.sesionId());
        assertEquals(cerrada, servicio.consultarSesion(s.sesionId()).orElseThrow());
        assertEquals(List.of(l), servicio.lecturasDeSesion(s.sesionId()));
    }
    @Test void integraFuenteSimuladaMedianteContratoExistente() {
        var s = sesion(); var fuente = new FuenteSimulada(Clock.fixed(INICIO, ZoneOffset.UTC));
        fuente.iniciar(e -> servicio.persistirLectura(e, s.sesionId()));
        fuente.emitir(List.of("0001", "0001", "0001")); fuente.detener();
        var lecturas = servicio.lecturasDeSesion(s.sesionId()); assertEquals(3, lecturas.size());
        assertTrue(lecturas.stream().allMatch(l -> l.origenDatos() == OrigenDatos.SIMULACION && l.epc().equals("0001")));
    }
    @Test void falloIntermedioDeMetadataRevierteLecturaYMetadataPrevia() throws Exception {
        var s = sesion(); var anterior = servicio.persistirLectura(entrada(INICIO), s.sesionId());
        try (var c = base.abrir(); var sql = c.createStatement()) {
            sql.executeUpdate("""
                CREATE TRIGGER fallo_metadata BEFORE INSERT ON lectura_rfid_metadata
                WHEN EXISTS (SELECT 1 FROM lectura_rfid_metadata WHERE lectura_id = NEW.lectura_id)
                BEGIN SELECT RAISE(ABORT, 'fallo_tras_primera_metadata'); END
                """);
        }
        var e = new LecturaEntradaRFID("otra", INICIO, OrigenDatos.SIMULACION, Map.of("a", "1", "b", "2"));
        var fallo = assertThrows(ErrorAplicacion.class, () -> servicio.persistirLectura(e, s.sesionId()));
        assertEquals(CONFLICTO, fallo.codigo()); assertTrue(fallo.getCause().getMessage().contains("fallo_tras_primera_metadata"));
        assertEquals(List.of(anterior), servicio.lecturasDeSesion(s.sesionId()));
        assertEquals(1, contar("lectura_rfid")); assertEquals(0, contar("lectura_rfid_metadata"));
    }
    @Test void preservaInstantsExtremosYNegativosSinTruncar() {
        var s = sesion();
        var instantes = List.of(Instant.MIN, Instant.ofEpochSecond(-1, 999999999), Instant.EPOCH, Instant.MAX);
        for (var t : instantes) assertEquals(t, servicio.persistirLectura(entrada(t), s.sesionId()).timestamp());
        assertEquals(instantes, servicio.lecturasDeSesion(s.sesionId()).stream().map(LecturaRFID::timestamp).toList());
    }
    @Test void cierresConcurrentesSoloConfirmanUnaVez() throws Exception {
        var s = sesion(); var inicio = new CountDownLatch(1);
        try (var ejecutor = Executors.newFixedThreadPool(2)) {
            Callable<Boolean> cierre = () -> {
                inicio.await();
                try { conReloj(INICIO.plusSeconds(1)).cerrarSesion(s.sesionId()); return true; }
                catch (ErrorAplicacion e) { assertEquals(CONFLICTO, e.codigo()); return false; }
            };
            var a = ejecutor.submit(cierre); var b = ejecutor.submit(cierre); inicio.countDown();
            assertNotEquals(a.get(10, TimeUnit.SECONDS), b.get(10, TimeUnit.SECONDS));
        }
        assertEquals(EstadoSesion.CERRADA, servicio.consultarSesion(s.sesionId()).orElseThrow().estado());
    }
    @Test void insercionesConcurrentesIdenticasSeConservan() throws Exception {
        var s = sesion(); var inicio = new CountDownLatch(1);
        try (var ejecutor = Executors.newFixedThreadPool(2)) {
            Callable<LecturaRFID> guardar = () -> { inicio.await(); return servicio.persistirLectura(entrada(INICIO), s.sesionId()); };
            var a = ejecutor.submit(guardar); var b = ejecutor.submit(guardar); inicio.countDown();
            assertNotEquals(a.get(10, TimeUnit.SECONDS).lecturaId(), b.get(10, TimeUnit.SECONDS).lecturaId());
        }
        assertEquals(2, servicio.lecturasDeSesion(s.sesionId()).size());
    }
}
