package co.emi.trazabilidad.aplicacion;

import co.emi.trazabilidad.dominio.*;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static co.emi.trazabilidad.aplicacion.ErrorAplicacion.Codigo.*;
import static co.emi.trazabilidad.dominio.ResultadoVerificacion.*;
import static co.emi.trazabilidad.dominio.EvidenciaVerificacion.Estado.*;

class ServicioI6Test extends SoporteMvpTest {
    @Test void todosDetectadosSinCrearEventos() throws Exception {
        long a=asociado("A"),b=asociado("B"),s=sesion(); leer("A",s,T);leer("B",s,T);
        var v=i6(T).verificar(s,Set.of(a,b)); assertEquals(Map.of(a,DETECTADO,b,DETECTADO),resultados(v));
        assertEquals(0,contar("evento_operativo"));assertEquals(2,contar("lectura_rfid"));
    }
    @Test void unoDetectadoYVariosFaltantes() {
        long a=asociado("A"),b=equipo("B"),c=equipo("C"),s=sesion();leer("A",s,T);
        assertEquals(Map.of(a,DETECTADO,b,FALTANTE,c,FALTANTE),resultados(i6(T).verificar(s,Set.of(a,b,c))));
    }
    @Test void detectadosFaltantesYNoEsperadosSeSeparan() {
        long a=asociado("A"),b=equipo("B"),c=asociado("C"),s=sesion();leer("A",s,T);leer("C",s,T);
        assertEquals(Map.of(a,DETECTADO,b,FALTANTE,c,NO_ESPERADO),resultados(i6(T).verificar(s,Set.of(a,b))));
    }
    @Test void esperadosVaciosDetectadoEsNoEsperado() {
        long a=asociado("A"),s=sesion();leer("A",s,T);
        assertEquals(Map.of(a,NO_ESPERADO),resultados(i6(T).verificar(s,Set.of())));
    }
    @Test void detectadosVaciosEsperadoEsFaltante() {
        long a=equipo("A"); assertEquals(Map.of(a,FALTANTE),resultados(i6(T).verificar(sesion(),Set.of(a))));
    }
    @Test void ambosVaciosProducenInstantaneaVacia() {
        var v=i6(T).verificar(sesion(),Set.of());assertTrue(resultados(v).isEmpty());assertTrue(i6(T).evidencias(v.verificacionId()).isEmpty());
    }
    @Test void veinteRepeticionesConservanVeinteRespaldosYUnResultado() throws Exception {
        long a=asociado("A"),s=sesion();for(int n=0;n<20;n++)leer("A",s,T);
        var v=i6(T).verificar(s,Set.of(a));assertEquals(Map.of(a,DETECTADO),resultados(v));
        assertEquals(20,i6(T).evidencias(v.verificacionId()).size());assertEquals(20,contar("lectura_rfid"));
    }
    @Test void epcDesconocidoNoInventaEquipo() {
        long a=equipo("A"),s=sesion();var l=leer("DESCONOCIDO",s,T);
        var v=i6(T).verificar(s,Set.of(a));assertEquals(Map.of(a,FALTANTE),resultados(v));
        var e=i6(T).evidencias(v.verificacionId()).getFirst();assertEquals(SIN_ASOCIACION,e.estado());assertNull(e.equipoId());assertEquals(l,e.lectura());
    }
    @Test void etiquetaExistenteSinAsignacionNoEsDetectada() {
        i1(T).crearEtiqueta("LIBRE",EstadoEtiqueta.ACTIVA);long s=sesion();leer("LIBRE",s,T);
        var v=i6(T).verificar(s,Set.of());assertTrue(resultados(v).isEmpty());assertEquals(SIN_ASOCIACION,i6(T).evidencias(v.verificacionId()).getFirst().estado());
    }
    @Test void reintentoIncorporaNuevaLecturaSinCambiarIntentoAnterior() {
        long a=asociado("A"),s=sesion();var anterior=i6(T).verificar(s,Set.of(a));
        var l=leer("A",s,T.plusSeconds(1));var nuevo=i6(T.plusSeconds(2)).reintentar(anterior.verificacionId());
        assertEquals(anterior.verificacionId(),nuevo.anteriorId());assertEquals(Map.of(a,FALTANTE),resultados(anterior));assertEquals(Map.of(a,DETECTADO),resultados(nuevo));
        assertTrue(i6(T).evidencias(anterior.verificacionId()).isEmpty());assertEquals(l,i6(T).evidencias(nuevo.verificacionId()).getFirst().lectura());
    }
    @Test void reintentoSinLecturasNuevasConservaMismoResultadoEHistorial() {
        long a=asociado("A"),s=sesion();leer("A",s,T);var uno=i6(T).verificar(s,Set.of(a));var dos=i6(T).reintentar(uno.verificacionId());
        assertEquals(resultados(uno),resultados(dos));assertEquals(List.of(uno,dos),i6(T).historial(s));
    }
    @Test void noPermiteCambiarEsperadosNiBifurcarCadena() {
        long a=equipo("A"),s=sesion();var v=i6(T).verificar(s,Set.of(a));i6(T).reintentar(v.verificacionId());
        error(CONFLICTO,()->i6(T).verificar(s,Set.of()));error(CONFLICTO,()->i6(T).reintentar(v.verificacionId()));
    }
    @Test void respetaAsociacionHistoricaTrasReasignacionDeEpc() {
        long a=asociado("A"),b=equipo("B"),s=sesion();var vieja=leer("A",s,T);
        var tag=i1(T.plusSeconds(1)).crearEtiqueta("NUEVA",EstadoEtiqueta.ACTIVA);
        i1(T.plusSeconds(1)).corregir(a,tag.etiquetaId(),"prueba");
        long etiqueta=i1(T).historial(a).getFirst().etiquetaId();i1(T.plusSeconds(1)).asociar(b,etiqueta);
        var nueva=leer("A",s,T.plusSeconds(1));var v=i6(T.plusSeconds(2)).verificar(s,Set.of(a,b));
        assertEquals(Map.of(a,DETECTADO,b,DETECTADO),resultados(v));var es=i6(T).evidencias(v.verificacionId());
        assertEquals(vieja,es.getFirst().lectura());assertEquals(a,es.getFirst().equipoId());assertEquals(nueva,es.getLast().lectura());assertEquals(b,es.getLast().equipoId());
    }
    @Test void variasEtiquetasHistoricasDelMismoEquipoNoDuplicanItem() {
        long a=asociado("A"),s=sesion();leer("A",s,T);var tag=i1(T.plusSeconds(1)).crearEtiqueta("B",EstadoEtiqueta.ACTIVA);
        i1(T.plusSeconds(1)).corregir(a,tag.etiquetaId(),"cambio");leer("B",s,T.plusSeconds(1));
        var v=i6(T.plusSeconds(2)).verificar(s,Set.of(a));assertEquals(1,resultados(v).size());assertEquals(2,i6(T).evidencias(v.verificacionId()).size());
    }
    @Test void excluyeAnteriorYFuturaSinEliminarEvidencia() {
        long a=asociado("A"),s=sesion();leer("A",s,T.minusNanos(1));leer("A",s,T.plusNanos(1));
        var v=i6(T).verificar(s,Set.of(a));assertEquals(Map.of(a,FALTANTE),resultados(v));
        assertEquals(List.of(ANTERIOR_A_SESION,LECTURA_FUTURA),i6(T).evidencias(v.verificacionId()).stream().map(EvidenciaVerificacion::estado).toList());
    }
    @Test void lecturasDeOtraSesionNoContaminanVerificacion() {
        long a=asociado("A"),s=sesion();leer("A",sesion(),T);assertEquals(Map.of(a,FALTANTE),resultados(i6(T).verificar(s,Set.of(a))));
    }
    @Test void rechazaSesionDeMovimientoYCerrada() {
        long s=i3(T).crearSesion("P","A",TipoOperacion.INGRESO,null).sesionId();error(CONFLICTO,()->i6(T).verificar(s,Set.of()));
        long v=sesion();i3(T).cerrarSesion(v);error(CONFLICTO,()->i6(T).verificar(v,Set.of()));
    }
    @Test void relojRegresivoNoCreaIntento() throws Exception {
        long s=sesion();error(DATO_INVALIDO,()->i6(T.minusSeconds(1)).verificar(s,Set.of()));assertEquals(0,contar("verificacion"));
        var v=i6(T.plusSeconds(2)).verificar(s,Set.of());error(DATO_INVALIDO,()->i6(T).reintentar(v.verificacionId()));
    }
    @Test void validaIdentificadoresEsperadosYExistencia() throws Exception {
        long s=sesion();error(DATO_INVALIDO,()->i6(T).verificar(0,Set.of()));error(DATO_INVALIDO,()->i6(T).verificar(s,null));
        error(DATO_INVALIDO,()->i6(T).verificar(s,Set.of(-1L)));error(DATO_INVALIDO,()->i6(T).verificar(s,new HashSet<>(Arrays.asList((Long)null))));
        error(NO_ENCONTRADO,()->i6(T).verificar(s,Set.of(999L)));error(NO_ENCONTRADO,()->i6(T).verificar(999,Set.of()));
        error(NO_ENCONTRADO,()->i6(T).reintentar(999));assertEquals(0,contar("verificacion"));
    }
    @Test void concurrentesNoDuplicanIntentoInicialNiReintento() throws Exception {
        long a=equipo("A"),s=sesion();compiten(()->i6(T).verificar(s,Set.of(a)));
        long id=i6(T).historial(s).getFirst().verificacionId();compiten(()->i6(T).reintentar(id));assertEquals(2,contar("verificacion"));
    }
    @Test void falloEnSegundoItemRevierteCabeceraEItems() throws Exception {
        long a=equipo("A"),b=equipo("B"),s=sesion();
        sql("CREATE TRIGGER fallo_i6 BEFORE INSERT ON verificacion_item WHEN NEW.equipo_id="+b+" BEGIN SELECT RAISE(ABORT,'fallo inyectado'); END");
        error(CONFLICTO,()->i6(T).verificar(s,Set.of(a,b)));assertEquals(0,contar("verificacion"));assertEquals(0,contar("verificacion_item"));assertEquals(0,contar("verificacion_lectura"));
    }
    @Test void falloEnRespaldoRevierteResultadoCompletoPeroNoLecturas() throws Exception {
        long a=asociado("A"),s=sesion();leer("A",s,T);
        sql("CREATE TRIGGER fallo_evidencia BEFORE INSERT ON verificacion_lectura BEGIN SELECT RAISE(ABORT,'fallo inyectado'); END");
        error(CONFLICTO,()->i6(T).verificar(s,Set.of(a)));assertEquals(0,contar("verificacion"));assertEquals(0,contar("verificacion_item"));assertEquals(1,contar("lectura_rfid"));
    }
}
