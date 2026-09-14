package co.emi.trazabilidad.aplicacion;

import co.emi.trazabilidad.aplicacion.consultas.*;
import co.emi.trazabilidad.dominio.*;
import co.emi.trazabilidad.infraestructura.persistencia.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import static co.emi.trazabilidad.aplicacion.ErrorAplicacion.Codigo.*;

class ServicioI5Test {
    @TempDir Path temporal;
    private BaseDatos base;
    private ServicioI5 consulta;
    private static final Instant T = Instant.parse("2026-09-13T08:00:00Z");
    @BeforeEach void preparar() throws Exception {
        base = new BaseDatos(temporal.resolve("i5.db")); base.migrar();
        consulta = new ServicioI5(new JdbcUnidadDeTrabajo(base));
    }
    private Clock reloj(Instant t) { return Clock.fixed(t, ZoneId.of("America/Bogota")); }
    private ServicioI1 i1(Instant t) { return new ServicioI1(new JdbcUnidadDeTrabajo(base), reloj(t)); }
    private ServicioI3 i3(Instant t) { return new ServicioI3(new JdbcUnidadDeTrabajo(base), reloj(t)); }
    private ServicioI4 i4(Instant t) { return new ServicioI4(new JdbcUnidadDeTrabajo(base), reloj(t)); }
    private record Registro(Equipo equipo, EtiquetaRFID etiqueta, AsignacionEtiqueta asignacion) { }
    private Registro registrar(String epc) {
        var servicio = i1(T.minusSeconds(3600)); var e = servicio.crearEquipo("EQ-" + epc, null, false);
        var tag = servicio.crearEtiqueta(epc, EstadoEtiqueta.INACTIVA);
        return new Registro(e, tag, servicio.asociar(e.equipoId(), tag.etiquetaId()));
    }
    private SesionOperacion sesion(Instant inicio, TipoOperacion tipo) {
        return i3(inicio).crearSesion("PUNTO-PRUEBA", "actor de ensayo", tipo, "SOFTWARE");
    }
    private LecturaRFID lectura(String epc, Long sesion, Instant t) {
        return i3(t).persistirLectura(new LecturaEntradaRFID(epc, t, OrigenDatos.SIMULACION, Map.of("antena", "2", "dato", "férula\n'\\", "", "")), sesion);
    }
    private EventoOperativo procesar(LecturaRFID l, Instant cuando) {
        return i4(cuando).procesarLectura(l.lecturaId()).evento().orElseThrow();
    }
    private List<LecturaRFID> lecturas(long equipo) {
        return consulta.historialLecturasPorEquipo(equipo).stream().map(LecturaAtribuida::lectura).toList();
    }
    private List<EventoOperativo> eventos(long equipo) {
        return consulta.historialEventosPorEquipo(equipo).stream().map(EventoConContexto::evento).toList();
    }
    @Test void equipoExistenteSinEvidenciaDevuelveAusenciaYListasVacias() {
        var e = i1(T).crearEquipo("SIN-EVIDENCIA", null, true);
        assertTrue(consulta.ultimaLecturaPorEquipo(e.equipoId()).isEmpty()); assertTrue(consulta.ultimoEventoPorEquipo(e.equipoId()).isEmpty());
        assertTrue(consulta.historialLecturasPorEquipo(e.equipoId()).isEmpty()); assertTrue(consulta.historialEventosPorEquipo(e.equipoId()).isEmpty());
    }
    @Test void equipoInexistenteNoSeConfundeConEquipoSinEvidencia() {
        assertEquals(NO_ENCONTRADO, assertThrows(ErrorAplicacion.class, () -> consulta.ultimaLecturaPorEquipo(999)).codigo());
        assertEquals(NO_ENCONTRADO, assertThrows(ErrorAplicacion.class, () -> consulta.ultimoEventoPorEquipo(999)).codigo());
        assertEquals(NO_ENCONTRADO, assertThrows(ErrorAplicacion.class, () -> consulta.historialLecturasPorEquipo(999)).codigo());
        assertEquals(NO_ENCONTRADO, assertThrows(ErrorAplicacion.class, () -> consulta.historialEventosPorEquipo(999)).codigo());
    }
    @Test void idsInvalidosYRespaldoAusenteSiguenConvencion() {
        assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> consulta.ultimaLecturaPorEquipo(0)).codigo());
        assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> consulta.ultimoEventoPorEquipo(-1)).codigo());
        assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> consulta.historialLecturasPorEquipo(0)).codigo());
        assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> consulta.historialEventosPorEquipo(-1)).codigo());
        assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> consulta.evidenciasDeEvento(0)).codigo());
        assertEquals(NO_ENCONTRADO, assertThrows(ErrorAplicacion.class, () -> consulta.evidenciasDeEvento(999)).codigo());
        assertThrows(NullPointerException.class, () -> new ServicioI5(null));
    }
    @Test void codigoInstitucionalReutilizaIdentidadLiteralI1() {
        var e = i1(T).crearEquipo(" 00a' OR 1=1 -- ", null, false);
        assertEquals(e, consulta.buscarEquipoPorCodigo(e.codigoInstitucional()).orElseThrow());
        assertTrue(consulta.buscarEquipoPorCodigo("00A").isEmpty());
        for (String valor : new String[]{null,""," ","\t"})
            assertEquals(DATO_INVALIDO, assertThrows(ErrorAplicacion.class, () -> consulta.buscarEquipoPorCodigo(valor)).codigo());
    }
    @Test void unaLecturaSinSesionConservaAtribucionMetadataYOrigen() {
        var a = registrar("000a"); var l = lectura("000a", null, T.plusNanos(123456789));
        var resultado = consulta.ultimaLecturaPorEquipo(a.equipo.equipoId()).orElseThrow();
        assertEquals(l, resultado.lectura()); assertEquals(a.asignacion, resultado.asignacion());
        assertNull(resultado.lectura().sesionId()); assertEquals(OrigenDatos.SIMULACION, resultado.lectura().origenDatos());
        assertEquals(List.of(l), lecturas(a.equipo.equipoId())); assertTrue(consulta.ultimoEventoPorEquipo(a.equipo.equipoId()).isEmpty());
    }
    @Test void ultimaUsaTimestampYNoOrdenDeInsercionYDesempataPorId() {
        var a = registrar("X");
        var mayor = lectura("X", null, T.plusNanos(2)); var antigua = lectura("X", null, T);
        var media = lectura("X", null, T.plusNanos(1)); var empate = lectura("X", null, T.plusNanos(2));
        assertEquals(List.of(antigua, media, mayor, empate), lecturas(a.equipo.equipoId()));
        assertEquals(empate, consulta.ultimaLecturaPorEquipo(a.equipo.equipoId()).orElseThrow().lectura());
    }
    @Test void ultimoLimitadoNoCortaLaMetadata() {
        var a = registrar("X"); lectura("X", null, T); var ultima = lectura("X", null, T.plusSeconds(1));
        assertEquals(3, ultima.metadata().size());
        assertEquals(ultima, consulta.ultimaLecturaPorEquipo(a.equipo.equipoId()).orElseThrow().lectura());
    }
    @Test void mapaVacioNoSeConvierteEnClaveNula() {
        var a = registrar("X");
        var l = i3(T).persistirLectura(new LecturaEntradaRFID("X", T, OrigenDatos.SIMULACION, Map.of()), null);
        assertEquals(l, consulta.ultimaLecturaPorEquipo(a.equipo.equipoId()).orElseThrow().lectura());
    }
    @Test void epcReasignadoNoContaminaEquipoAnteriorIncluidoLimiteExacto() {
        var a = registrar("X"); Instant cambio = T.plusSeconds(60);
        var y = i1(cambio).crearEtiqueta("Y", EstadoEtiqueta.ACTIVA);
        i1(cambio).corregir(a.equipo.equipoId(), y.etiquetaId(), "cambio");
        var b = i1(cambio).crearEquipo("B", null, true); var ab = i1(cambio).asociar(b.equipoId(), a.etiqueta.etiquetaId());
        var antigua = lectura("X", null, cambio.minusNanos(1)); var limite = lectura("X", null, cambio); var nueva = lectura("X", null, cambio.plusNanos(1));
        assertEquals(List.of(antigua), lecturas(a.equipo.equipoId())); assertEquals(List.of(limite,nueva), lecturas(b.equipoId()));
        assertEquals(a.asignacion.asignacionId(), consulta.historialLecturasPorEquipo(a.equipo.equipoId()).getFirst().asignacion().asignacionId());
        assertEquals(ab, consulta.ultimaLecturaPorEquipo(b.equipoId()).orElseThrow().asignacion());
    }
    @Test void cambiarEtiquetaConservaLecturasDeAmbosEpCsEnSuIntervalo() {
        var a = registrar("X"); Instant cambio = T.plusSeconds(60); var y = i1(cambio).crearEtiqueta("Y", EstadoEtiqueta.ACTIVA);
        i1(cambio).corregir(a.equipo.equipoId(), y.etiquetaId(), "cambio");
        var x = lectura("X", null, T); var nueva = lectura("Y", null, cambio);
        lectura("X", null, cambio); lectura("Y", null, cambio.minusNanos(1));
        assertEquals(List.of(x,nueva), lecturas(a.equipo.equipoId()));
        assertEquals(nueva, consulta.ultimaLecturaPorEquipo(a.equipo.equipoId()).orElseThrow().lectura());
    }
    @Test void huecoEntreAsociacionesNoSeAtribuyePorCercania() {
        var a = registrar("X"); Instant c = T.plusSeconds(60); var y = i1(c).crearEtiqueta("Y", EstadoEtiqueta.ACTIVA);
        i1(c).corregir(a.equipo.equipoId(), y.etiquetaId(), null);
        var b = i1(c.plusSeconds(60)).crearEquipo("B",null,false); i1(c.plusSeconds(60)).asociar(b.equipoId(),a.etiqueta.etiquetaId());
        lectura("X",null,c.plusSeconds(10));
        assertTrue(lecturas(a.equipo.equipoId()).isEmpty()); assertTrue(lecturas(b.equipoId()).isEmpty());
    }
    @Test void ambiguedadHistoricaNoSeIncluyeEnNingunEquipo() throws Exception {
        var a = registrar("X"); var b = i1(T).crearEquipo("B",null,true);
        try(var c=base.abrir();var s=c.createStatement()) {
            s.executeUpdate("INSERT INTO asignacion_etiqueta VALUES (2,"+b.equipoId()+","+a.etiqueta.etiquetaId()+","+T.minusSeconds(1).toEpochMilli()+","+T.plusSeconds(1).toEpochMilli()+",NULL)");
        }
        lectura("X",null,T);
        assertTrue(lecturas(a.equipo.equipoId()).isEmpty()); assertTrue(lecturas(b.equipoId()).isEmpty());
    }
    @Test void antesDeAsociacionYEpcDesconocidoQuedanSoloComoLecturasCrudas() {
        var a=registrar("X"); var desconocida=lectura("desconocido",null,T); lectura("X",null,T.minusSeconds(3601));
        assertTrue(lecturas(a.equipo.equipoId()).isEmpty()); assertTrue(consulta.ultimaLecturaPorEquipo(a.equipo.equipoId()).isEmpty());
        assertEquals(desconocida,i3(T).consultarLectura(desconocida.lecturaId()).orElseThrow());
    }
    @Test void epcLiteralNoSeNormalizaDuranteJoin() {
        var a=registrar(" 00a' "); var correcta=lectura(" 00a' ",null,T); lectura("00A'",null,T.plusSeconds(1));
        assertEquals(List.of(correcta),lecturas(a.equipo.equipoId()));
    }
    @Test void lecturaDeOtroEquipoNoAparece() {
        var a=registrar("A"); registrar("B"); var propia=lectura("A",null,T); lectura("B",null,T.plusSeconds(1));
        assertEquals(List.of(propia),lecturas(a.equipo.equipoId()));
    }
    @Test void lecturaPosteriorSinEventoNoCambiaUltimoEvento() {
        var a=registrar("X"); var s=sesion(T,TipoOperacion.SALIDA);
        var inicial=lectura("X",s.sesionId(),T); var e=procesar(inicial,T);
        var posterior=lectura("X",null,T.plusSeconds(300));
        assertEquals(posterior,consulta.ultimaLecturaPorEquipo(a.equipo.equipoId()).orElseThrow().lectura());
        assertEquals(e,consulta.ultimoEventoPorEquipo(a.equipo.equipoId()).orElseThrow());
        assertEquals(T,e.timestamp()); assertEquals(T.plusSeconds(300),posterior.timestamp());
    }
    @Test void lecturaEnVerificacionYCerradaSigueVisibleSinMovimiento() {
        var a=registrar("X"); var s=sesion(T,TipoOperacion.VERIFICACION); var primera=lectura("X",s.sesionId(),T);
        i3(T.plusSeconds(60)).cerrarSesion(s.sesionId()); var tardia=lectura("X",s.sesionId(),T.plusSeconds(120));
        assertEquals(List.of(primera,tardia),lecturas(a.equipo.equipoId())); assertTrue(eventos(a.equipo.equipoId()).isEmpty());
    }
    @Test void rfRealComoValorDePruebaSeConservaSinAcreditarHardware() {
        var a=registrar("X"); var l=i3(T).persistirLectura(new LecturaEntradaRFID("X",T,OrigenDatos.RF_REAL,Map.of("prueba","software")),null);
        assertEquals(l,consulta.ultimaLecturaPorEquipo(a.equipo.equipoId()).orElseThrow().lectura());
    }
    @Test void historialEventosConContextoYOrigenMixtoSinDuplicarFilas() {
        var a=registrar("X"); var s=sesion(T,TipoOperacion.INGRESO); var l=lectura("X",s.sesionId(),T); var e=procesar(l,T);
        var rf=i3(T).persistirLectura(new LecturaEntradaRFID("X",T,OrigenDatos.RF_REAL,Map.of()),s.sesionId()); procesar(rf,T);
        procesar(lectura("X",s.sesionId(),T),T);
        var lista=consulta.historialEventosPorEquipo(a.equipo.equipoId()); assertEquals(1,lista.size());
        var contexto=lista.getFirst(); assertEquals(e,contexto.evento()); assertEquals(s,contexto.sesion());
        assertEquals("PUNTO-PRUEBA",contexto.sesion().puntoControl()); assertEquals("actor de ensayo",contexto.sesion().actorContexto());
        assertEquals(Set.of(OrigenDatos.SIMULACION,OrigenDatos.RF_REAL),contexto.origenes());
        assertEquals(i4(T).evidenciasDeEvento(e.eventoId()),consulta.evidenciasDeEvento(e.eventoId()));
        assertEquals(3,consulta.evidenciasDeEvento(e.eventoId()).size());
    }
    @Test void eventosOrdenanPorTimestampOperativoYNoPorCreacionConEmpates() {
        var a=registrar("X"); var s1=sesion(T,TipoOperacion.INGRESO); var s2=sesion(T,TipoOperacion.SALIDA); var s3=sesion(T,TipoOperacion.INGRESO);
        var posterior=procesar(lectura("X",s1.sesionId(),T.plusNanos(1)),T.plusSeconds(10));
        var anterior=procesar(lectura("X",s2.sesionId(),T),T.plusSeconds(20));
        var empate=procesar(lectura("X",s3.sesionId(),T.plusNanos(1)),T.plusSeconds(30));
        assertEquals(List.of(anterior,posterior,empate),eventos(a.equipo.equipoId()));
        assertEquals(empate,consulta.ultimoEventoPorEquipo(a.equipo.equipoId()).orElseThrow());
    }
    @Test void eventoPermaneceDelEquipoOriginalTrasReasignarEpc() {
        var a=registrar("X"); var s=sesion(T,TipoOperacion.INGRESO); var e=procesar(lectura("X",s.sesionId(),T),T);
        var cambio=T.plusSeconds(60); var y=i1(cambio).crearEtiqueta("Y",EstadoEtiqueta.ACTIVA); i1(cambio).corregir(a.equipo.equipoId(),y.etiquetaId(),null);
        var b=i1(cambio).crearEquipo("B",null,true); i1(cambio).asociar(b.equipoId(),a.etiqueta.etiquetaId());
        assertEquals(List.of(e),eventos(a.equipo.equipoId())); assertTrue(eventos(b.equipoId()).isEmpty());
    }
    @Test void consultasNoModificanNingunByteDeLaBaseNiCreanEventos() throws Exception {
        var a=registrar("X"); var s=sesion(T,TipoOperacion.INGRESO); var l=lectura("X",s.sesionId(),T); var e=procesar(l,T);
        lectura("X",null,T.plusSeconds(300)); lectura("desconocido",null,T);
        byte[] antes=Files.readAllBytes(temporal.resolve("i5.db"));
        for(int i=0;i<2;i++) {
            consulta.buscarEquipoPorCodigo(a.equipo.codigoInstitucional()); consulta.ultimaLecturaPorEquipo(a.equipo.equipoId());
            consulta.ultimoEventoPorEquipo(a.equipo.equipoId());consulta.historialLecturasPorEquipo(a.equipo.equipoId());
            consulta.historialEventosPorEquipo(a.equipo.equipoId());consulta.evidenciasDeEvento(e.eventoId());
        }
        assertArrayEquals(antes,Files.readAllBytes(temporal.resolve("i5.db")));
    }
    @Test void consultasSinEventosNoDisparanMotor() {
        var a=registrar("X");var s=sesion(T,TipoOperacion.INGRESO);lectura("X",s.sesionId(),T);
        assertEquals(1,lecturas(a.equipo.equipoId()).size());assertTrue(consulta.ultimaLecturaPorEquipo(a.equipo.equipoId()).isPresent());
        assertTrue(eventos(a.equipo.equipoId()).isEmpty());assertTrue(consulta.ultimoEventoPorEquipo(a.equipo.equipoId()).isEmpty());
        assertTrue(i4(T).eventosDeSesion(s.sesionId()).isEmpty());
    }
    @Test void resultadosSonInmutables() {
        var a=registrar("X");var s=sesion(T,TipoOperacion.INGRESO);var l=lectura("X",s.sesionId(),T);var e=procesar(l,T);
        var ls=consulta.historialLecturasPorEquipo(a.equipo.equipoId());var es=consulta.historialEventosPorEquipo(a.equipo.equipoId());
        assertThrows(UnsupportedOperationException.class,ls::clear);assertThrows(UnsupportedOperationException.class,es::clear);
        assertThrows(UnsupportedOperationException.class,()->ls.getFirst().lectura().metadata().clear());
        assertThrows(UnsupportedOperationException.class,()->es.getFirst().origenes().clear());
        assertThrows(UnsupportedOperationException.class,()->consulta.evidenciasDeEvento(e.eventoId()).clear());
    }
    @Test void reaperturaConservaConsultasYContextoDeSesionCerrada() throws Exception {
        var a=registrar("X");var s=sesion(T,TipoOperacion.INGRESO);var l=lectura("X",s.sesionId(),T);var e=procesar(l,T);
        var cerrada=i3(T.plusSeconds(60)).cerrarSesion(s.sesionId());
        var otra=new ServicioI5(new JdbcUnidadDeTrabajo(new BaseDatos(temporal.resolve("i5.db"))));
        assertEquals(l,otra.ultimaLecturaPorEquipo(a.equipo.equipoId()).orElseThrow().lectura());
        assertEquals(e,otra.ultimoEventoPorEquipo(a.equipo.equipoId()).orElseThrow());
        assertEquals(cerrada,otra.historialEventosPorEquipo(a.equipo.equipoId()).getFirst().sesion());
    }
    @Test void rf004EscenarioCompletoDosEtiquetasCuatroLecturasYDosEventos() {
        var a=registrar("A");var ingreso=sesion(T,TipoOperacion.INGRESO);
        var l0800=lectura("A",ingreso.sesionId(),T);var l0801=lectura("A",ingreso.sesionId(),T.plusSeconds(60));
        var entrada=procesar(l0800,T.plusSeconds(120));assertEquals(entrada,procesar(l0801,T.plusSeconds(120)));
        i3(T.plusSeconds(120)).cerrarSesion(ingreso.sesionId());
        var mediodia=T.plusSeconds(4*3600);var b=i1(mediodia).crearEtiqueta("B",EstadoEtiqueta.ACTIVA);
        i1(mediodia).corregir(a.equipo.equipoId(),b.etiquetaId(),"cambio de etiqueta de prueba");
        var salida=sesion(mediodia,TipoOperacion.SALIDA);var l1200=lectura("B",salida.sesionId(),mediodia);
        var egreso=procesar(l1200,mediodia);i3(mediodia.plusSeconds(60)).cerrarSesion(salida.sesionId());
        var l1205=lectura("B",salida.sesionId(),mediodia.plusSeconds(300));
        assertEquals(List.of(l0800,l0801,l1200,l1205),lecturas(a.equipo.equipoId()));
        assertEquals(List.of(entrada,egreso),eventos(a.equipo.equipoId()));
        assertEquals(TipoEvento.INGRESO,entrada.tipoEvento());assertEquals(TipoEvento.SALIDA,egreso.tipoEvento());
        assertEquals(T,entrada.timestamp());assertEquals(mediodia,egreso.timestamp());
        assertEquals(l1205,consulta.ultimaLecturaPorEquipo(a.equipo.equipoId()).orElseThrow().lectura());
        assertEquals(egreso,consulta.ultimoEventoPorEquipo(a.equipo.equipoId()).orElseThrow());
        assertEquals(2,consulta.evidenciasDeEvento(entrada.eventoId()).size());assertEquals(1,consulta.evidenciasDeEvento(egreso.eventoId()).size());
    }
    @Test void tiemposNegativosYExtremosDeLecturaMantienenOrdenExacto() {
        Instant comienzo=Instant.ofEpochMilli(-1500);
        var e=i1(comienzo).crearEquipo("NEGATIVO",null,false);var tag=i1(comienzo).crearEtiqueta("Z",EstadoEtiqueta.ACTIVA);
        i1(comienzo).asociar(e.equipoId(),tag.etiquetaId());
        var valida=lectura("Z",null,comienzo);lectura("Z",null,comienzo.minusNanos(1));lectura("Z",null,Instant.MIN);
        var maxima=lectura("Z",null,Instant.MAX);
        assertEquals(List.of(valida,maxima),lecturas(e.equipoId()));assertEquals(maxima,consulta.ultimaLecturaPorEquipo(e.equipoId()).orElseThrow().lectura());
    }
}
