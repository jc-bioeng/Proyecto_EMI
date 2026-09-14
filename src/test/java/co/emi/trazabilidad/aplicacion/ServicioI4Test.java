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
import static co.emi.trazabilidad.aplicacion.ResultadoProcesamiento.Estado.*;

class ServicioI4Test {
    @TempDir Path temporal;
    private BaseDatos base;
    private ServicioI3 i3;
    private ServicioI4 motor;
    private static final Instant T = Instant.parse("2026-09-12T10:00:00Z");
    @BeforeEach void preparar() throws Exception {
        base = new BaseDatos(temporal.resolve("motor.db")); base.migrar();
        i3 = i3(T); motor = motor(T.plusSeconds(60));
    }
    private Clock reloj(Instant t) { return Clock.fixed(t, ZoneId.of("America/Bogota")); }
    private ServicioI1 i1(Instant t) { return new ServicioI1(new JdbcUnidadDeTrabajo(base), reloj(t)); }
    private ServicioI3 i3(Instant t) { return new ServicioI3(new JdbcUnidadDeTrabajo(base), reloj(t)); }
    private ServicioI4 motor(Instant t) { return new ServicioI4(new JdbcUnidadDeTrabajo(base), reloj(t)); }
    private record Registro(Equipo equipo, EtiquetaRFID etiqueta, AsignacionEtiqueta asignacion) { }
    private Registro registrar(String epc) {
        var servicio = i1(T.minusSeconds(10));
        var e = servicio.crearEquipo("EQ-" + epc, "prueba", false);
        var tag = servicio.crearEtiqueta(epc, EstadoEtiqueta.INACTIVA);
        return new Registro(e, tag, servicio.asociar(e.equipoId(), tag.etiquetaId()));
    }
    private SesionOperacion sesion(TipoOperacion tipo) { return i3.crearSesion("PUNTO-PRUEBA", "actor de ensayo", tipo, "SOFTWARE"); }
    private LecturaRFID lectura(String epc, Long sesion, Instant t) {
        return i3.persistirLectura(new LecturaEntradaRFID(epc, t, OrigenDatos.SIMULACION, Map.of("antena", "2", "rssi", "-44")), sesion);
    }
    private EventoOperativo evento(LecturaRFID lectura) { return motor.procesarLectura(lectura.lecturaId()).evento().orElseThrow(); }
    private void sql(String sql) throws Exception { try (var c = base.abrir(); var s = c.createStatement()) { s.executeUpdate(sql); } }
    private long contar(String tabla) throws Exception {
        try (var c = base.abrir(); var s = c.createStatement(); var r = s.executeQuery("SELECT count(*) FROM " + tabla)) {
            assertTrue(r.next()); return r.getLong(1);
        }
    }
    private void sinEvento(LecturaRFID l, ResultadoProcesamiento.Estado estado, String motivo) {
        var resultado = motor.procesarLectura(l.lecturaId());
        assertEquals(estado, resultado.estado()); assertEquals(motivo, resultado.motivo()); assertTrue(resultado.evento().isEmpty());
        assertEquals(l, i3.consultarLectura(l.lecturaId()).orElseThrow());
    }
    @Test void unaLecturaGeneraIngresoConIdentidadContextoYTiemposDistintos() {
        var a = registrar("000a"); var s = sesion(TipoOperacion.INGRESO); var l = lectura("000a", s.sesionId(), T.plusNanos(123456789));
        var r = motor.procesarLectura(l.lecturaId()); var e = r.evento().orElseThrow();
        assertEquals(EVENTO_CREADO, r.estado()); assertEquals(a.equipo.equipoId(), e.equipoId());
        assertEquals(s.sesionId(), e.sesionId()); assertEquals(TipoEvento.INGRESO, e.tipoEvento());
        assertEquals(l.timestamp(), e.timestamp()); assertEquals(T.plusSeconds(60), e.fechaCreacion()); assertEquals(l.lecturaId(), e.lecturaBaseId());
        assertEquals(e, motor.consultarEvento(e.eventoId()).orElseThrow());
        assertEquals(new EvidenciaEvento(e.eventoId(), a.asignacion.asignacionId(), l, T.plusSeconds(60)), motor.evidenciasDeEvento(e.eventoId()).getFirst());
    }
    @Test void salidaProcedeDeSesionSinInterpretarAntenaNiRssi() {
        registrar("X"); var ingreso = sesion(TipoOperacion.INGRESO); var salida = sesion(TipoOperacion.SALIDA);
        var a = lectura("X", ingreso.sesionId(), T); var b = lectura("X", salida.sesionId(), T);
        assertEquals(a.metadata(), b.metadata()); assertEquals(TipoEvento.INGRESO, evento(a).tipoEvento());
        assertEquals(TipoEvento.SALIDA, evento(b).tipoEvento());
    }
    @Test void veinteCallbacksConservanVeinteLecturasYUnEventoSoloAlInvocarMotor() throws Exception {
        registrar("X"); var s = sesion(TipoOperacion.SALIDA);
        var fuente = new FuenteSimulada(reloj(T)); fuente.iniciar(e -> i3.persistirLectura(e, s.sesionId()));
        fuente.emitir(Collections.nCopies(20, "X")); fuente.detener();
        var originales = i3.lecturasDeSesion(s.sesionId()); assertEquals(20, originales.size()); assertEquals(0, contar("evento_operativo"));
        var resultados = motor.procesarSesion(s.sesionId());
        assertEquals(1, resultados.stream().filter(r -> r.estado() == EVENTO_CREADO).count());
        assertEquals(19, resultados.stream().filter(r -> r.estado() == EVIDENCIA_AGREGADA).count());
        var e = motor.eventosDeSesion(s.sesionId()).getFirst();
        assertEquals(1, contar("evento_operativo")); assertEquals(20, contar("lectura_rfid")); assertEquals(20, contar("evento_lectura"));
        assertEquals(originales, motor.evidenciasDeEvento(e.eventoId()).stream().map(EvidenciaEvento::lectura).toList());
        assertEquals(originales, i3.lecturasDeSesion(s.sesionId()));
    }
    @Test void repetirSesionYLecturaEsIdempotenteSinModificarVinculos() {
        registrar("X"); var s = sesion(TipoOperacion.SALIDA);
        var l = lectura("X", s.sesionId(), T); var e = evento(l); var evidencias = motor.evidenciasDeEvento(e.eventoId());
        assertEquals(LECTURA_YA_PROCESADA, motor.procesarLectura(l.lecturaId()).estado());
        assertTrue(motor.procesarSesion(s.sesionId()).stream().allMatch(r -> r.estado() == LECTURA_YA_PROCESADA));
        assertEquals(List.of(e), motor.eventosDeSesion(s.sesionId())); assertEquals(evidencias, motor.evidenciasDeEvento(e.eventoId()));
    }
    @Test void distintosEquiposProducenEventosCorrespondientes() {
        var a = registrar("A"); var b = registrar("B"); var s = sesion(TipoOperacion.INGRESO);
        lectura("A", s.sesionId(), T); lectura("B", s.sesionId(), T.plusNanos(1));
        motor.procesarSesion(s.sesionId());
        assertEquals(List.of(a.equipo.equipoId(), b.equipo.equipoId()), motor.eventosDeSesion(s.sesionId()).stream().map(EventoOperativo::equipoId).toList());
    }
    @Test void mismoEquipoEnSesionesDistintasPermiteOtroMovimiento() {
        registrar("X"); var a = sesion(TipoOperacion.SALIDA); var b = sesion(TipoOperacion.SALIDA);
        var ea = evento(lectura("X", a.sesionId(), T)); var eb = evento(lectura("X", b.sesionId(), T));
        assertNotEquals(ea.eventoId(), eb.eventoId());
    }
    @Test void epcReasignadoResuelveAntesEnLimiteYDespuesSinUsarVigente() {
        var a = registrar("X"); var s = sesion(TipoOperacion.SALIDA);
        Instant cambio = T.plusSeconds(10);
        var otra = i1(cambio).crearEtiqueta("Y", EstadoEtiqueta.ACTIVA);
        i1(cambio).corregir(a.equipo.equipoId(), otra.etiquetaId(), "cambio de prueba");
        var b = i1(cambio).crearEquipo("B", null, false);
        var asignacionB = i1(cambio).asociar(b.equipoId(), a.etiqueta.etiquetaId());
        var antes = lectura("X", s.sesionId(), cambio.minusNanos(1));
        var limite = lectura("X", s.sesionId(), cambio); var despues = lectura("X", s.sesionId(), cambio.plusNanos(1));
        var eb = evento(despues); var ea = evento(antes);
        assertEquals(a.equipo.equipoId(), ea.equipoId()); assertEquals(b.equipoId(), eb.equipoId());
        assertEquals(eb, evento(limite)); assertEquals(cambio, eb.timestamp());
        assertEquals(a.asignacion.asignacionId(), motor.evidenciasDeEvento(ea.eventoId()).getFirst().asignacionId());
        assertTrue(motor.evidenciasDeEvento(eb.eventoId()).stream().allMatch(v -> v.asignacionId() == asignacionB.asignacionId()));
    }
    @Test void dosEtiquetasDelMismoEquipoDentroDeSesionNoDuplicanEvento() {
        var a = registrar("X"); var s = sesion(TipoOperacion.INGRESO); Instant cambio = T.plusSeconds(10);
        var otra = i1(cambio).crearEtiqueta("Y", EstadoEtiqueta.ACTIVA);
        i1(cambio).corregir(a.equipo.equipoId(), otra.etiquetaId(), "cambio");
        var x = lectura("X", s.sesionId(), T); var y = lectura("Y", s.sesionId(), cambio);
        assertEquals(evento(x), evento(y)); assertEquals(1, motor.eventosDeSesion(s.sesionId()).size());
    }
    @Test void lecturaAntesDeAsociacionNoCreaEvento() {
        registrar("X"); var s = i3(T.minusSeconds(20)).crearSesion("P", "A", TipoOperacion.INGRESO, null);
        sinEvento(lectura("X", s.sesionId(), T.minusSeconds(11)), SIN_ASOCIACION_VALIDA, "ASOCIACION_AUSENTE");
    }
    @Test void huecoEntreAsignacionesNoSeRellenaConLaActual() {
        var a = registrar("X"); var s = sesion(TipoOperacion.INGRESO);
        var y = i1(T.plusSeconds(10)).crearEtiqueta("Y", EstadoEtiqueta.ACTIVA);
        i1(T.plusSeconds(10)).corregir(a.equipo.equipoId(), y.etiquetaId(), null);
        var b = i1(T.plusSeconds(20)).crearEquipo("B", null, true);
        i1(T.plusSeconds(20)).asociar(b.equipoId(), a.etiqueta.etiquetaId());
        sinEvento(lectura("X", s.sesionId(), T.plusSeconds(15)), SIN_ASOCIACION_VALIDA, "ASOCIACION_AUSENTE");
    }
    @Test void solapamientoHistoricoSeRechazaSinElegirArbitrariamente() throws Exception {
        var a = registrar("X"); var s = sesion(TipoOperacion.INGRESO);
        var b = i1(T).crearEquipo("B", null, true);
        sql("INSERT INTO asignacion_etiqueta(equipo_id, etiqueta_id, fecha_inicio, fecha_fin) VALUES (" + b.equipoId() + "," + a.etiqueta.etiquetaId() + "," + T.minusSeconds(1).toEpochMilli() + "," + T.plusSeconds(1).toEpochMilli() + ")");
        sinEvento(lectura("X", s.sesionId(), T), SIN_ASOCIACION_VALIDA, "ASOCIACION_AMBIGUA");
    }
    @Test void epcDesconocidoPermaneceSinEquipoInventado() throws Exception {
        var s = sesion(TipoOperacion.INGRESO);
        sinEvento(lectura("desconocido", s.sesionId(), T), SIN_ASOCIACION_VALIDA, "ASOCIACION_AUSENTE");
        assertEquals(0, contar("equipo")); assertEquals(0, contar("evento_operativo"));
    }
    @Test void epcLiteralNoNormalizaMayusculasEspaciosNiComillas() {
        registrar(" 00a' "); var s = sesion(TipoOperacion.INGRESO);
        assertNotNull(evento(lectura(" 00a' ", s.sesionId(), T)));
        sinEvento(lectura("00A'", s.sesionId(), T), SIN_ASOCIACION_VALIDA, "ASOCIACION_AUSENTE");
    }
    @Test void verificacionNoProduceMovimiento() throws Exception {
        registrar("X"); var s = sesion(TipoOperacion.VERIFICACION);
        sinEvento(lectura("X", s.sesionId(), T), LECTURA_NO_ELEGIBLE, "SESION_VERIFICACION"); assertEquals(0, contar("evento_operativo"));
    }
    @Test void lecturaSinSesionNoTomaOtraSesionAbierta() {
        registrar("X"); sesion(TipoOperacion.INGRESO);
        sinEvento(lectura("X", null, T), CONTEXTO_INSUFICIENTE, "SIN_SESION");
    }
    @Test void sesionCerradaNoGeneraEventoAunqueLecturaEsteDentroDelIntervalo() {
        registrar("X"); var s = sesion(TipoOperacion.SALIDA); var l = lectura("X", s.sesionId(), T);
        i3(T.plusSeconds(10)).cerrarSesion(s.sesionId()); sinEvento(l, LECTURA_NO_ELEGIBLE, "SESION_CERRADA");
    }
    @Test void lecturaTardiaFueraDelIntervaloDeSesionCerradaNoGeneraEvento() {
        registrar("X"); var s = sesion(TipoOperacion.SALIDA); i3(T.plusSeconds(10)).cerrarSesion(s.sesionId());
        sinEvento(lectura("X", s.sesionId(), T.plusSeconds(20)), LECTURA_NO_ELEGIBLE, "SESION_CERRADA");
    }
    @Test void cierreNoAmpliaRespaldoExistentePeroConservaIdempotenciaPrevia() {
        registrar("X"); var s = sesion(TipoOperacion.SALIDA); var l = lectura("X", s.sesionId(), T); var e = evento(l);
        i3(T.plusSeconds(60)).cerrarSesion(s.sesionId());
        assertEquals(LECTURA_YA_PROCESADA, motor.procesarLectura(l.lecturaId()).estado());
        sinEvento(lectura("X", s.sesionId(), T.plusSeconds(1)), LECTURA_NO_ELEGIBLE, "SESION_CERRADA");
        assertEquals(1, motor.evidenciasDeEvento(e.eventoId()).size());
    }
    @Test void antesDeSesionYFuturoNoProducenEvento() {
        registrar("X"); var s = sesion(TipoOperacion.INGRESO);
        sinEvento(lectura("X", s.sesionId(), T.minusNanos(1)), LECTURA_NO_ELEGIBLE, "ANTERIOR_A_SESION");
        sinEvento(lectura("X", s.sesionId(), T.plusSeconds(60).plusNanos(1)), LECTURA_NO_ELEGIBLE, "LECTURA_FUTURA");
        assertNotNull(evento(lectura("X", s.sesionId(), T.plusSeconds(60))));
    }
    @Test void rechazoTransitorioPuedeReevaluarseSinReintentoAutomatico() {
        registrar("X"); var s = sesion(TipoOperacion.INGRESO); var l = lectura("X", s.sesionId(), T.plusSeconds(61));
        sinEvento(l, LECTURA_NO_ELEGIBLE, "LECTURA_FUTURA");
        assertEquals(EVENTO_CREADO, motor(T.plusSeconds(62)).procesarLectura(l.lecturaId()).estado());
    }
    @Test void lecturaBaseEsPrimeraDisponibleAunqueSeProceseOtraYDesempataPorId() {
        registrar("X"); var s = sesion(TipoOperacion.INGRESO);
        var posterior = lectura("X", s.sesionId(), T.plusNanos(1));
        var primera = lectura("X", s.sesionId(), T); lectura("X", s.sesionId(), T);
        var e = evento(posterior); assertEquals(primera.lecturaId(), e.lecturaBaseId()); assertEquals(T, e.timestamp());
        assertEquals(List.of(primera, posterior), motor.evidenciasDeEvento(e.eventoId()).stream().map(EvidenciaEvento::lectura).toList());
        motor.procesarSesion(s.sesionId()); assertEquals(3, motor.evidenciasDeEvento(e.eventoId()).size());
    }
    @Test void evidenciaAnteriorPersistidaDespuesNoReescribeTimestampHistorico() {
        registrar("X"); var s = sesion(TipoOperacion.INGRESO); var l = lectura("X", s.sesionId(), T.plusSeconds(5)); var e = evento(l);
        var tardia = lectura("X", s.sesionId(), T);
        assertEquals(EVIDENCIA_AGREGADA, motor.procesarLectura(tardia.lecturaId()).estado());
        assertEquals(e, motor.consultarEvento(e.eventoId()).orElseThrow()); assertEquals(l.lecturaId(), e.lecturaBaseId());
        assertEquals(2, motor.evidenciasDeEvento(e.eventoId()).size());
    }
    @Test void origenesMixtosSeConservanPorLecturaSinAcreditarRfFisico() {
        registrar("X"); var s = sesion(TipoOperacion.INGRESO); var sim = lectura("X", s.sesionId(), T);
        var rf = i3.persistirLectura(new LecturaEntradaRFID("X", T, OrigenDatos.RF_REAL, Map.of("dato", "prueba software")), s.sesionId());
        var e = evento(sim); assertEquals(e, evento(rf));
        assertEquals(Set.of(OrigenDatos.SIMULACION, OrigenDatos.RF_REAL), new HashSet<>(motor.evidenciasDeEvento(e.eventoId()).stream().map(v -> v.lectura().origenDatos()).toList()));
    }
    @Test void consultarTrasReabrirConservaEventoAsignacionMetadataYRespaldo() throws Exception {
        registrar("X"); var s = sesion(TipoOperacion.INGRESO); var l = lectura("X", s.sesionId(), T.plusNanos(8)); var e = evento(l);
        var respaldos = motor.evidenciasDeEvento(e.eventoId());
        var reabierto = new ServicioI4(new JdbcUnidadDeTrabajo(new BaseDatos(temporal.resolve("motor.db"))), reloj(T.plusSeconds(90)));
        assertEquals(e, reabierto.consultarEvento(e.eventoId()).orElseThrow()); assertEquals(respaldos, reabierto.evidenciasDeEvento(e.eventoId()));
        assertEquals(l, respaldos.getFirst().lectura()); assertEquals(LECTURA_YA_PROCESADA, reabierto.procesarLectura(l.lecturaId()).estado());
    }
    @Test void eventoPrevioNoSeReinterpretaAlCorregirAsociacion() {
        var a = registrar("X"); var s = sesion(TipoOperacion.INGRESO); var l = lectura("X", s.sesionId(), T); var e = evento(l);
        var y = i1(T.plusSeconds(10)).crearEtiqueta("Y", EstadoEtiqueta.ACTIVA);
        i1(T.plusSeconds(10)).corregir(a.equipo.equipoId(), y.etiquetaId(), "correccion");
        var b = i1(T.plusSeconds(10)).crearEquipo("B", null, false);
        i1(T.plusSeconds(10)).asociar(b.equipoId(), a.etiqueta.etiquetaId());
        assertEquals(e, evento(l)); assertEquals(a.equipo.equipoId(), e.equipoId());
    }
    @Test void idsInvalidosYEntidadesAusentesTienenErroresExplicitos() {
        assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> motor.procesarLectura(0)).codigo());
        assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> motor.procesarSesion(-1)).codigo());
        assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> motor.consultarEvento(0)).codigo());
        assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> motor.eventosDeSesion(0)).codigo());
        assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> motor.evidenciasDeEvento(0)).codigo());
        assertEquals(NO_ENCONTRADO, assertThrows(ErrorAplicacion.class, () -> motor.procesarLectura(999)).codigo());
        assertEquals(NO_ENCONTRADO, assertThrows(ErrorAplicacion.class, () -> motor.procesarSesion(999)).codigo());
        assertEquals(NO_ENCONTRADO, assertThrows(ErrorAplicacion.class, () -> motor.eventosDeSesion(999)).codigo());
        assertEquals(NO_ENCONTRADO, assertThrows(ErrorAplicacion.class, () -> motor.evidenciasDeEvento(999)).codigo());
        assertTrue(motor.consultarEvento(999).isEmpty());
    }
    @Test void sesionSinLecturasDevuelveResultadosVacios() {
        var s = sesion(TipoOperacion.INGRESO); assertTrue(motor.procesarSesion(s.sesionId()).isEmpty()); assertTrue(motor.eventosDeSesion(s.sesionId()).isEmpty());
    }
    @Test void falloTrasInsertarEventoAntesDeVinculoRevierteSinHuerfano() throws Exception {
        registrar("X"); var s = sesion(TipoOperacion.INGRESO); var l = lectura("X", s.sesionId(), T);
        sql("CREATE TRIGGER fallo_prueba BEFORE INSERT ON evento_lectura WHEN EXISTS (SELECT 1 FROM evento_operativo WHERE evento_id = NEW.evento_id) BEGIN SELECT RAISE(ABORT, 'fallo_despues_evento'); END");
        var error = assertThrows(ErrorAplicacion.class, () -> evento(l)); assertTrue(error.getCause().getMessage().contains("fallo_despues_evento"));
        assertEquals(0, contar("evento_operativo")); assertEquals(0, contar("evento_lectura")); assertEquals(l, i3.consultarLectura(l.lecturaId()).orElseThrow());
    }
    @Test void falloAlSegundoRespaldoDeCreacionRevierteEventoYPrimerVinculo() throws Exception {
        registrar("X"); var s = sesion(TipoOperacion.INGRESO); lectura("X", s.sesionId(), T); var posterior = lectura("X", s.sesionId(), T.plusSeconds(1));
        sql("CREATE TRIGGER fallo_prueba BEFORE INSERT ON evento_lectura WHEN EXISTS (SELECT 1 FROM evento_lectura WHERE evento_id = NEW.evento_id) BEGIN SELECT RAISE(ABORT, 'fallo_segundo_respaldo'); END");
        assertTrue(assertThrows(ErrorAplicacion.class, () -> evento(posterior)).getCause().getMessage().contains("fallo_segundo_respaldo"));
        assertEquals(0, contar("evento_operativo")); assertEquals(0, contar("evento_lectura")); assertEquals(2, contar("lectura_rfid"));
    }
    @Test void falloSegundoEventoDelLoteRevierteTodoElLote() throws Exception {
        registrar("A"); var b = registrar("B"); var s = sesion(TipoOperacion.INGRESO);
        lectura("A", s.sesionId(), T); lectura("B", s.sesionId(), T.plusSeconds(1));
        sql("CREATE TRIGGER fallo_prueba BEFORE INSERT ON evento_lectura WHEN EXISTS (SELECT 1 FROM evento_operativo WHERE evento_id = NEW.evento_id AND equipo_id = " + b.equipo.equipoId() + ") BEGIN SELECT RAISE(ABORT, 'fallo_segundo_evento'); END");
        assertTrue(assertThrows(ErrorAplicacion.class, () -> motor.procesarSesion(s.sesionId())).getCause().getMessage().contains("fallo_segundo_evento"));
        assertEquals(0, contar("evento_operativo")); assertEquals(0, contar("evento_lectura")); assertEquals(2, contar("lectura_rfid"));
    }
    @Test void falloAlAgregarEvidenciaNoBorraEventoConfirmado() throws Exception {
        registrar("X"); var s = sesion(TipoOperacion.INGRESO); var l = lectura("X", s.sesionId(), T); var e = evento(l);
        var nueva = lectura("X", s.sesionId(), T.plusSeconds(1));
        sql("CREATE TRIGGER fallo_prueba BEFORE INSERT ON evento_lectura BEGIN SELECT RAISE(ABORT, 'fallo_agregado'); END");
        assertThrows(ErrorAplicacion.class, () -> evento(nueva)); assertEquals(List.of(e), motor.eventosDeSesion(s.sesionId()));
        assertEquals(List.of(l), motor.evidenciasDeEvento(e.eventoId()).stream().map(EvidenciaEvento::lectura).toList());
    }
    @Test void dosHilosProcesanLecturasIgualesSinDuplicados() throws Exception { concurrencia(false); }
    @Test void dosHilosProcesanLaMismaLecturaIdempotentemente() throws Exception { concurrencia(true); }
    private void concurrencia(boolean misma) throws Exception {
        registrar("X"); var s = sesion(TipoOperacion.SALIDA); var a = lectura("X", s.sesionId(), T); var b = lectura("X", s.sesionId(), T);
        var inicio = new CountDownLatch(1);
        try (var pool = Executors.newFixedThreadPool(2)) {
            Callable<ResultadoProcesamiento> primero = () -> { inicio.await(); return motor(T.plusSeconds(60)).procesarLectura(a.lecturaId()); };
            Callable<ResultadoProcesamiento> segundo = () -> { inicio.await(); return motor(T.plusSeconds(60)).procesarLectura(misma ? a.lecturaId() : b.lecturaId()); };
            var f1 = pool.submit(primero); var f2 = pool.submit(segundo); inicio.countDown();
            var r1 = f1.get(10, TimeUnit.SECONDS); var r2 = f2.get(10, TimeUnit.SECONDS);
            assertEquals(r1.evento(), r2.evento()); assertEquals(1, List.of(r1, r2).stream().filter(r -> r.estado() == EVENTO_CREADO).count());
        }
        motor.procesarSesion(s.sesionId()); assertEquals(2, contar("lectura_rfid")); assertEquals(2, contar("evento_lectura")); assertEquals(1, contar("evento_operativo"));
    }
    @Test void relojHaciaAtrasNoAgregaEvidenciaPeroPermiteConsultarProcesada() {
        registrar("X"); var s = sesion(TipoOperacion.INGRESO); var l = lectura("X", s.sesionId(), T); var e = evento(l);
        var nueva = lectura("X", s.sesionId(), T.plusSeconds(1));
        var anterior = motor(T.plusSeconds(30));
        assertEquals("RELOJ_ANTERIOR_A_EVENTO", anterior.procesarLectura(nueva.lecturaId()).motivo());
        assertEquals(LECTURA_YA_PROCESADA, anterior.procesarLectura(l.lecturaId()).estado()); assertEquals(1, motor.evidenciasDeEvento(e.eventoId()).size());
    }
    @Test void procesamientoDeSesionUsaUnSoloInstante() {
        registrar("X"); var s = sesion(TipoOperacion.INGRESO); lectura("X", s.sesionId(), T); lectura("X", s.sesionId(), T.plusSeconds(1));
        var consultas = new java.util.concurrent.atomic.AtomicInteger();
        Clock contado = new Clock() {
            @Override public ZoneId getZone() { return ZoneOffset.UTC; }
            @Override public Clock withZone(ZoneId z) { return this; }
            @Override public Instant instant() { consultas.incrementAndGet(); return T.plusSeconds(60); }
        };
        new ServicioI4(new JdbcUnidadDeTrabajo(base), contado).procesarSesion(s.sesionId()); assertEquals(1, consultas.get());
    }
    @Test void referenciaDeSesionCorruptaSeInformaSinInventarContexto() throws Exception {
        // Simula una importacion externa corrupta; las conexiones normales de BaseDatos activan FK.
        try (var c = base.abrir(); var statement = c.createStatement()) {
            statement.execute("PRAGMA foreign_keys = OFF");
            statement.executeUpdate("INSERT INTO lectura_rfid VALUES (1, 'X', " + T.getEpochSecond() + ", 0, 'SIMULACION', 999)");
        }
        assertEquals(NO_ENCONTRADO, assertThrows(ErrorAplicacion.class, () -> motor.procesarLectura(1)).codigo());
        assertEquals(1, contar("lectura_rfid")); assertEquals(0, contar("evento_operativo"));
    }
}
