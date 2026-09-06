package co.emi.trazabilidad.aplicacion;

import co.emi.trazabilidad.dominio.*;
import co.emi.trazabilidad.infraestructura.persistencia.*;
import java.nio.file.Path;
import java.time.*;
import java.util.List;
import java.util.concurrent.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import static co.emi.trazabilidad.aplicacion.ErrorAplicacion.Codigo.*;

class ServicioI1Test {
    @TempDir Path temporal;
    private BaseDatos base;
    private ServicioI1 servicio;
    private static final Instant INICIO = Instant.parse("2026-09-06T10:00:00Z");

    @BeforeEach void preparar() throws Exception {
        base = new BaseDatos(temporal.resolve("servicios.db"));
        base.migrar();
        servicio = conReloj(Clock.fixed(INICIO, ZoneOffset.UTC));
    }
    private ServicioI1 conReloj(Clock reloj) { return new ServicioI1(new JdbcUnidadDeTrabajo(base), reloj); }
    private Equipo equipo(String codigo) { return servicio.crearEquipo(codigo, "Monitor", true); }
    private EtiquetaRFID etiqueta(String epc) { return servicio.crearEtiqueta(epc, EstadoEtiqueta.ACTIVA); }

    @Test void creaEquipoYLoPersisteConIdentidadPropia() throws Exception {
        var equipo = servicio.crearEquipo("EQ-001", null, false);
        assertTrue(equipo.equipoId() > 0);
        try (var c = base.abrir(); var s = c.createStatement(); var r = s.executeQuery("SELECT * FROM equipo")) {
            assertTrue(r.next()); assertEquals(equipo.equipoId(), r.getLong("equipo_id"));
            assertEquals("EQ-001", r.getString("codigo_institucional"));
            assertNull(r.getString("descripcion")); assertEquals(0, r.getInt("activo_piloto")); assertFalse(r.next());
        }
    }
    @Test void rechazaCodigoDuplicado() {
        equipo("EQ-001");
        assertEquals(CONFLICTO, assertThrows(ErrorAplicacion.class, () -> equipo("EQ-001")).codigo());
    }
    @Test void creaEtiquetaSinPerderCerosIniciales() {
        var etiqueta = etiqueta("0000AB01");
        assertTrue(etiqueta.etiquetaId() > 0); assertEquals("0000AB01", etiqueta.epc());
        assertEquals(EstadoEtiqueta.ACTIVA, etiqueta.estado());
        var recuperada = new JdbcUnidadDeTrabajo(base).ejecutar(r -> r.etiqueta(etiqueta.etiquetaId()).orElseThrow());
        assertEquals(etiqueta, recuperada);
    }
    @Test void rechazaEpcDuplicado() {
        etiqueta("0001");
        assertEquals(CONFLICTO, assertThrows(ErrorAplicacion.class, () -> etiqueta("0001")).codigo());
    }
    @Test void rechazaDatosObligatoriosInvalidos() {
        for (String valor : new String[] {null, "", " ", "\t"}) {
            assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> equipo(valor)).codigo());
            assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> etiqueta(valor)).codigo());
        }
        assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> servicio.crearEtiqueta("0001", null)).codigo());
        assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> servicio.asociar(0, 1)).codigo());
    }
    @Test void creaAsociacionValida() {
        var e = equipo("EQ-001"); var t = etiqueta("0001");
        var a = servicio.asociar(e.equipoId(), t.etiquetaId());
        assertTrue(a.asignacionId() > 0); assertEquals(e.equipoId(), a.equipoId());
        assertEquals(t.etiquetaId(), a.etiquetaId()); assertEquals(INICIO, a.fechaInicio());
        assertTrue(a.vigente()); assertNull(a.fechaFin());
    }
    @Test void consultaVigenteTrasReabrirLaBase() throws Exception {
        var e = equipo("EQ-001"); var t = etiqueta("0001");
        var a = servicio.asociar(e.equipoId(), t.etiquetaId());
        var reabierto = new ServicioI1(new JdbcUnidadDeTrabajo(new BaseDatos(temporal.resolve("servicios.db"))), Clock.systemUTC());
        assertEquals(a, reabierto.consultarVigente(e.equipoId()).orElseThrow());
    }
    @Test void buscaEquipoPorEpcActualmenteAsociado() {
        var e = equipo("EQ-001"); var t = etiqueta("0001");
        servicio.asociar(e.equipoId(), t.etiquetaId());
        assertEquals(e, servicio.buscarEquipoPorEpc("0001").orElseThrow());
    }
    @Test void consultasSinAsociacionDevuelvenAusencia() {
        var e = equipo("EQ-001"); etiqueta("0001");
        assertTrue(servicio.consultarVigente(e.equipoId()).isEmpty());
        assertTrue(servicio.historial(e.equipoId()).isEmpty());
        assertTrue(servicio.buscarEquipoPorEpc("0001").isEmpty());
        assertTrue(servicio.buscarEquipoPorEpc("desconocido").isEmpty());
    }
    @Test void rechazaEquipoQueYaTieneAsociacion() {
        var e = equipo("EQ-001"); var t1 = etiqueta("0001"); var t2 = etiqueta("0002");
        servicio.asociar(e.equipoId(), t1.etiquetaId());
        assertEquals(CONFLICTO, assertThrows(ErrorAplicacion.class, () -> servicio.asociar(e.equipoId(), t2.etiquetaId())).codigo());
        assertEquals(1, servicio.historial(e.equipoId()).size());
    }
    @Test void rechazaEtiquetaOcupadaPorOtroEquipo() {
        var e1 = equipo("EQ-001"); var e2 = equipo("EQ-002"); var t = etiqueta("0001");
        servicio.asociar(e1.equipoId(), t.etiquetaId());
        assertEquals(CONFLICTO, assertThrows(ErrorAplicacion.class, () -> servicio.asociar(e2.equipoId(), t.etiquetaId())).codigo());
        assertTrue(servicio.historial(e2.equipoId()).isEmpty());
    }
    @Test void rechazaReferenciasInexistentes() {
        var e = equipo("EQ-001"); var t = etiqueta("0001");
        assertEquals(NO_ENCONTRADO, assertThrows(ErrorAplicacion.class, () -> servicio.asociar(999, t.etiquetaId())).codigo());
        assertEquals(NO_ENCONTRADO, assertThrows(ErrorAplicacion.class, () -> servicio.asociar(e.equipoId(), 999)).codigo());
        assertEquals(NO_ENCONTRADO, assertThrows(ErrorAplicacion.class, () -> servicio.historial(999)).codigo());
    }
    @Test void corrigeConUnSoloInstanteYConservaAmbasAsociaciones() {
        var e = equipo("EQ-001"); var t1 = etiqueta("0001"); var t2 = etiqueta("0002");
        var anterior = servicio.asociar(e.equipoId(), t1.etiquetaId());
        var reloj = new RelojContado(INICIO.plusSeconds(60).plusNanos(123456789));
        var nueva = conReloj(reloj).corregir(e.equipoId(), t2.etiquetaId(), "Etiqueta corregida");
        assertEquals(1, reloj.consultas);
        var historial = servicio.historial(e.equipoId());
        assertEquals(2, historial.size()); assertEquals(anterior.asignacionId(), historial.getFirst().asignacionId());
        assertEquals(nueva.fechaInicio(), historial.getFirst().fechaFin());
        assertEquals(INICIO.plusSeconds(60).plusMillis(123), nueva.fechaInicio());
        assertEquals("Etiqueta corregida", historial.getFirst().motivoCambio());
        assertNull(nueva.motivoCambio()); assertTrue(nueva.vigente());
        assertEquals(nueva, historial.getLast());
        assertTrue(servicio.buscarEquipoPorEpc("0001").isEmpty());
        assertEquals(e, servicio.buscarEquipoPorEpc("0002").orElseThrow());
    }
    @Test void historialOrdenadoConEmpatesIncluyeCerradasYVigente() {
        var e = equipo("EQ-001"); var t1 = etiqueta("0001"); var t2 = etiqueta("0002"); var t3 = etiqueta("0003");
        var a1 = servicio.asociar(e.equipoId(), t1.etiquetaId());
        var a2 = servicio.corregir(e.equipoId(), t2.etiquetaId(), null);
        var a3 = conReloj(Clock.fixed(INICIO.plusSeconds(1), ZoneOffset.UTC)).corregir(e.equipoId(), t3.etiquetaId(), null);
        var h = servicio.historial(e.equipoId());
        assertEquals(List.of(a1.asignacionId(), a2.asignacionId(), a3.asignacionId()), h.stream().map(AsignacionEtiqueta::asignacionId).toList());
        assertFalse(h.get(0).vigente()); assertFalse(h.get(1).vigente()); assertTrue(h.get(2).vigente());
    }
    @Test void falloRealAlInsertarDespuesDelCierreRevierteTodo() throws Exception {
        var e = equipo("EQ-001"); var t1 = etiqueta("0001"); var t2 = etiqueta("0002");
        var anterior = servicio.asociar(e.equipoId(), t1.etiquetaId());
        try (var c = base.abrir(); var s = c.createStatement()) {
            // Solo en esta base temporal: falla si el UPDATE anterior ya ocurrio en la misma transaccion.
            s.executeUpdate("""
                CREATE TRIGGER fallo_prueba BEFORE INSERT ON asignacion_etiqueta
                WHEN EXISTS (SELECT 1 FROM asignacion_etiqueta WHERE fecha_fin IS NOT NULL)
                BEGIN SELECT RAISE(ABORT, 'fallo_despues_del_cierre'); END
                """);
        }
        var fallo = assertThrows(ErrorAplicacion.class, () -> servicio.corregir(e.equipoId(), t2.etiquetaId(), "No debe persistir"));
        assertNotNull(fallo.getCause()); assertTrue(fallo.getCause().getMessage().contains("fallo_despues_del_cierre"));
        assertEquals(List.of(anterior), servicio.historial(e.equipoId()));
        assertEquals(anterior, servicio.consultarVigente(e.equipoId()).orElseThrow());
        assertEquals(e, servicio.buscarEquipoPorEpc("0001").orElseThrow());
        assertTrue(servicio.buscarEquipoPorEpc("0002").isEmpty());
    }
    @Test void corregirValidaConflictosAntesDeCerrar() {
        var e1 = equipo("EQ-001"); var e2 = equipo("EQ-002"); var t1 = etiqueta("0001"); var t2 = etiqueta("0002");
        var a1 = servicio.asociar(e1.equipoId(), t1.etiquetaId());
        servicio.asociar(e2.equipoId(), t2.etiquetaId());
        var reloj = new RelojContado(INICIO.plusSeconds(1));
        assertEquals(CONFLICTO, assertThrows(ErrorAplicacion.class, () -> conReloj(reloj).corregir(e1.equipoId(), t2.etiquetaId(), null)).codigo());
        assertEquals(0, reloj.consultas); assertEquals(List.of(a1), servicio.historial(e1.equipoId()));
    }
    @Test void corregirSinVigenteOConEtiquetaInexistenteNoEscribe() {
        var e = equipo("EQ-001"); var t = etiqueta("0001");
        assertEquals(NO_ENCONTRADO, assertThrows(ErrorAplicacion.class, () -> servicio.corregir(e.equipoId(), t.etiquetaId(), null)).codigo());
        var a = servicio.asociar(e.equipoId(), t.etiquetaId());
        assertEquals(NO_ENCONTRADO, assertThrows(ErrorAplicacion.class, () -> servicio.corregir(e.equipoId(), 999, null)).codigo());
        assertEquals(List.of(a), servicio.historial(e.equipoId()));
    }
    @Test void corregirHaciaLaMismaEtiquetaEsConflictoSinCambios() {
        var e = equipo("EQ-001"); var t = etiqueta("0001");
        var a = servicio.asociar(e.equipoId(), t.etiquetaId());
        assertEquals(CONFLICTO, assertThrows(ErrorAplicacion.class, () -> servicio.corregir(e.equipoId(), t.etiquetaId(), null)).codigo());
        assertEquals(List.of(a), servicio.historial(e.equipoId()));
    }
    @Test void relojAnteriorAlInicioNoCorrompeElHistorial() {
        var e = equipo("EQ-001"); var t1 = etiqueta("0001"); var t2 = etiqueta("0002");
        var a = servicio.asociar(e.equipoId(), t1.etiquetaId());
        assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class,
            () -> conReloj(Clock.fixed(INICIO.minusSeconds(1), ZoneOffset.UTC)).corregir(e.equipoId(), t2.etiquetaId(), null)).codigo());
        assertEquals(List.of(a), servicio.historial(e.equipoId()));
    }
    @Test void inactivaNoSeRechazaSinReglaAprobada() {
        var e = equipo("EQ-001"); var t = servicio.crearEtiqueta("0001", EstadoEtiqueta.INACTIVA);
        assertTrue(servicio.asociar(e.equipoId(), t.etiquetaId()).vigente());
        var otra = servicio.crearEtiqueta("0002", EstadoEtiqueta.INACTIVA);
        assertEquals(otra.etiquetaId(), servicio.corregir(e.equipoId(), otra.etiquetaId(), null).etiquetaId());
    }
    @Test void textoConComillasSeTrataComoDato() {
        var e = equipo("EQ-'001"); var t = etiqueta("EPC' OR 1=1 --");
        servicio.asociar(e.equipoId(), t.etiquetaId());
        assertEquals(e, servicio.buscarEquipoPorEpc(t.epc()).orElseThrow());
        assertTrue(servicio.buscarEquipoPorEpc("otro").isEmpty());
    }
    @Test void dosServiciosConcurrentesNoAsignanUnaEtiquetaDosVeces() throws Exception {
        var e1 = equipo("EQ-001"); var e2 = equipo("EQ-002"); var t = etiqueta("0001");
        var salida = new CountDownLatch(1);
        try (var ejecutor = Executors.newFixedThreadPool(2)) {
            Callable<Boolean> primero = () -> intentar(salida, e1.equipoId(), t.etiquetaId());
            Callable<Boolean> segundo = () -> intentar(salida, e2.equipoId(), t.etiquetaId());
            var f1 = ejecutor.submit(primero); var f2 = ejecutor.submit(segundo); salida.countDown();
            assertNotEquals(f1.get(10, TimeUnit.SECONDS), f2.get(10, TimeUnit.SECONDS));
        }
        assertEquals(1, servicio.historial(e1.equipoId()).size() + servicio.historial(e2.equipoId()).size());
    }
    private boolean intentar(CountDownLatch inicio, long equipo, long etiqueta) throws Exception {
        inicio.await();
        try { servicio.asociar(equipo, etiqueta); return true; }
        catch (ErrorAplicacion e) { assertEquals(CONFLICTO, e.codigo()); return false; }
    }
    private static final class RelojContado extends Clock {
        private final Instant instante;
        private int consultas;
        RelojContado(Instant instante) { this.instante = instante; }
        @Override public ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(ZoneId zona) { return Clock.fixed(instante, zona); }
        @Override public Instant instant() { consultas++; return instante; }
    }
}
