package co.emi.trazabilidad.aplicacion;

import co.emi.trazabilidad.infraestructura.persistencia.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static co.emi.trazabilidad.aplicacion.ErrorAplicacion.Codigo.*;

class ServicioI7Test extends SoporteMvpTest {
    private co.emi.trazabilidad.dominio.SustitucionTemporal abrir(long e) { return i7(T).abrir(e,"ORIGEN","DESTINO","responsable","motivo"); }
    @Test void aperturaConservaContextoLiteralYActiva() {
        long e=equipo("A");var s=i7(T).abrir(e," Origen ' ","Destino","Actor","Motivo ñ");
        assertTrue(s.activa());assertEquals(" Origen ' ",s.origen());assertEquals("Motivo ñ",s.motivo());assertEquals(T,s.fechaApertura());
        assertEquals(s,i7(T).activa(e).orElseThrow());assertEquals(s,i7(T).consultar(s.sustitucionId()).orElseThrow());
    }
    @Test void equipoSinSustitucionDevuelveAusencia() {
        long e=equipo("A");assertTrue(i7(T).activa(e).isEmpty());assertTrue(i7(T).historial(e).isEmpty());assertTrue(i7(T).consultar(999).isEmpty());
    }
    @Test void cierreConservaContextoYFecha() {
        long e=equipo("A");var s=abrir(e);var cerrada=i7(T.plusSeconds(1)).cerrar(s.sustitucionId());
        assertFalse(cerrada.activa());assertEquals(T.plusSeconds(1),cerrada.fechaCierre());assertEquals(s.origen(),cerrada.origen());assertTrue(i7(T).activa(e).isEmpty());
    }
    @Test void aperturaPosteriorConservaHistorialCompleto() {
        long e=equipo("A");var s=abrir(e);var cerrada=i7(T.plusSeconds(1)).cerrar(s.sustitucionId());
        var segunda=i7(T.plusSeconds(2)).abrir(e,"D","O","otro","segunda");
        assertNotEquals(s.sustitucionId(),segunda.sustitucionId());assertEquals(List.of(cerrada,segunda),i7(T).historial(e));
    }
    @Test void cierreDuplicadoEsConflicto() {
        var s=abrir(equipo("A"));var cerrada=i7(T).cerrar(s.sustitucionId());
        error(CONFLICTO,()->i7(T.plusSeconds(2)).cerrar(s.sustitucionId()));assertEquals(cerrada,i7(T).consultar(s.sustitucionId()).orElseThrow());
    }
    @Test void aperturaDuplicadaNoSobrescribe() {
        long e=equipo("A");var s=abrir(e);error(CONFLICTO,()->abrir(e));assertEquals(List.of(s),i7(T).historial(e));
    }
    @Test void equiposDistintosPuedenTenerSustitucionesActivas() { abrir(equipo("A"));abrir(equipo("B")); }
    @Test void rechazaCierreAntesDeAperturaYReaperturaAntesDeDevolucion() {
        long e=equipo("A");var s=abrir(e);error(DATO_INVALIDO,()->i7(T.minusSeconds(1)).cerrar(s.sustitucionId()));
        i7(T.plusSeconds(3)).cerrar(s.sustitucionId());error(DATO_INVALIDO,()->i7(T.plusSeconds(2)).abrir(e,"A","B","C","D"));
    }
    @Test void validaDatosYEquiposInexistentes() {
        long e=equipo("A");for(String valor:new String[]{null,"","  ","\t"}) {
            error(DATO_INVALIDO,()->i7(T).abrir(e,valor,"B","C","D"));error(DATO_INVALIDO,()->i7(T).abrir(e,"A",valor,"C","D"));
            error(DATO_INVALIDO,()->i7(T).abrir(e,"A","B",valor,"D"));error(DATO_INVALIDO,()->i7(T).abrir(e,"A","B","C",valor));
        }
        error(NO_ENCONTRADO,()->abrir(999));error(NO_ENCONTRADO,()->i7(T).cerrar(999));error(NO_ENCONTRADO,()->i7(T).historial(999));
        error(DATO_INVALIDO,()->abrir(0));error(DATO_INVALIDO,()->i7(T).cerrar(-1));
    }
    @Test void aperturasConcurrentesSoloDejanUnaActiva() throws Exception {
        long e=equipo("A");compiten(()->abrir(e));assertEquals(1,i7(T).historial(e).size());
    }
    @Test void cierresConcurrentesSoloUnoGana() throws Exception {
        var s=abrir(equipo("A"));compiten(()->i7(T).cerrar(s.sustitucionId()));assertFalse(i7(T).consultar(s.sustitucionId()).orElseThrow().activa());
    }
    @Test void rollbackDespuesDeAperturaRevierteInsercion() throws Exception {
        long e=equipo("A");assertThrows(IllegalStateException.class,()->unidad().ejecutarI7(r->{r.abrir(e,"A","B","C","D",T);throw new IllegalStateException("fallo parcial");}));
        assertEquals(0,contar("sustitucion_temporal"));
    }
    @Test void rollbackDespuesDeCierrePreservaActiva() {
        var s=abrir(equipo("A"));assertThrows(IllegalStateException.class,()->unidad().ejecutarI7(r->{r.cerrar(s.sustitucionId(),T);throw new IllegalStateException("fallo parcial");}));
        assertEquals(s,i7(T).consultar(s.sustitucionId()).orElseThrow());
    }
    @Test void falloSqlDespuesDeUpdateRevierteCierre() throws Exception {
        var s=abrir(equipo("A"));sql("CREATE TRIGGER fallo_cierre AFTER UPDATE ON sustitucion_temporal BEGIN SELECT RAISE(ABORT,'fallo parcial'); END");
        error(CONFLICTO,()->i7(T).cerrar(s.sustitucionId()));assertEquals(s,i7(T).consultar(s.sustitucionId()).orElseThrow());
    }
    @Test void reaperturaDeBaseConservaSustitucionYPermiteDevolucion() throws Exception {
        var s=abrir(equipo("A"));base=new BaseDatos(temporal.resolve("mvp.db"));base.migrar();
        assertEquals(s,i7(T).activa(s.equipoId()).orElseThrow());assertFalse(i7(T).cerrar(s.sustitucionId()).activa());
    }
    @Test void noGeneraLecturasNiEventosNiVerificacion() throws Exception {
        var s=abrir(equipo("A"));i7(T).cerrar(s.sustitucionId());assertEquals(0,contar("lectura_rfid"));assertEquals(0,contar("evento_operativo"));assertEquals(0,contar("verificacion"));
    }
}
