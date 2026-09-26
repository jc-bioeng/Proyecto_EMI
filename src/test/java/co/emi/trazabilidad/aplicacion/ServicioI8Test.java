package co.emi.trazabilidad.aplicacion;

import co.emi.trazabilidad.infraestructura.persistencia.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static co.emi.trazabilidad.aplicacion.ErrorAplicacion.Codigo.*;

class ServicioI8Test extends SoporteMvpTest {
    @Test void omisionTrasReintentoGuardaEquipoActorSesionYMotivo() {
        long e=equipo("A"),s=sesion();var v=reintento(e,s);
        var c=i8(T.plusSeconds(2)).registrarOmision(e,v.verificacionId(),"actor","ensayo","omision persistente");
        assertEquals(e,c.equipoId());assertEquals(s,c.sesionId());assertEquals(v.verificacionId(),c.verificacionId());
        assertEquals("MANUAL",c.origenRegistro());assertFalse(c.confirmada());assertEquals("omision persistente",c.motivo());
    }
    @Test void omisionInicialNoEsFallaFinal() {
        long e=equipo("A"),s=sesion();var v=i6(T).verificar(s,Set.of(e));
        error(CONFLICTO,()->i8(T).registrarOmision(e,v.verificacionId(),"A","C","M"));
    }
    @Test void detectadoONoEsperadoNoJustificanContingenciaDeOmision() {
        long e=asociado("A"),s=sesion();leer("A",s,T);var v=reintento(e,s);
        error(CONFLICTO,()->i8(T.plusSeconds(2)).registrarOmision(e,v.verificacionId(),"A","C","M"));
        long s2=sesion();leer("A",s2,T);var inicial=i6(T).verificar(s2,Set.of());var otro=i6(T).reintentar(inicial.verificacionId());
        error(CONFLICTO,()->i8(T).registrarOmision(e,otro.verificacionId(),"A","C","M"));
    }
    @Test void reintentoObsoletoNoAutorizaRegistro() {
        long e=equipo("A"),s=sesion();var v=reintento(e,s);i6(T.plusSeconds(2)).reintentar(v.verificacionId());
        error(CONFLICTO,()->i8(T.plusSeconds(3)).registrarOmision(e,v.verificacionId(),"A","C","M"));
    }
    @Test void fueraDeRutaNoExigeSesionEtiquetaNiEpc() throws Exception {
        long e=equipo("COD-INSTITUCIONAL");var c=i8(T).registrarFueraDeRuta(e,"actor",null,"fuera del punto en este ensayo","registro manual");
        assertNull(c.sesionId());assertNull(c.verificacionId());assertEquals("MANUAL",c.origenRegistro());
        assertEquals(0,contar("lectura_rfid"));assertEquals(0,contar("sesion_operacion"));assertEquals(0,contar("etiqueta_rfid"));
    }
    @Test void fueraDeRutaPuedeConservarSesionOperativaExistente() {
        long e=equipo("A"),s=sesion();var c=i8(T).registrarFueraDeRuta(e,"A",s,"C","M");assertEquals(s,c.sesionId());
    }
    @Test void confirmacionEsExplicitaYConservaAuditoria() {
        long e=equipo("A");var c=i8(T).registrarFueraDeRuta(e,"registrador",null,"C","M");
        var confirmada=i8(T.plusSeconds(1)).confirmar(c.contingenciaId(),"confirmador");assertTrue(confirmada.confirmada());
        assertEquals("registrador",confirmada.actor());assertEquals("confirmador",confirmada.actorConfirmacion());assertEquals(T.plusSeconds(1),confirmada.fechaConfirmacion());
        assertEquals(confirmada,i8(T).consultar(c.contingenciaId()).orElseThrow());
    }
    @Test void noConfirmaDosVeces() {
        var c=i8(T).registrarFueraDeRuta(equipo("A"),"A",null,"C","M");i8(T).confirmar(c.contingenciaId(),"A");
        error(CONFLICTO,()->i8(T).confirmar(c.contingenciaId(),"otro"));
    }
    @Test void intentoNuevoExigeRevisarAntesDeConfirmar() {
        long e=asociado("A"),s=sesion();var v=reintento(e,s);var c=i8(T.plusSeconds(2)).registrarOmision(e,v.verificacionId(),"A","C","M");
        leer("A",s,T.plusSeconds(3));i6(T.plusSeconds(4)).reintentar(v.verificacionId());
        error(CONFLICTO,()->i8(T.plusSeconds(5)).confirmar(c.contingenciaId(),"A"));assertFalse(i8(T).consultar(c.contingenciaId()).orElseThrow().confirmada());
    }
    @Test void registrosManualesConservanHistorialSinSobrescribir() {
        long e=equipo("A");var a=i8(T).registrarFueraDeRuta(e,"A",null,"C","M");var b=i8(T).registrarFueraDeRuta(e,"B",null,"C","otro");
        assertEquals(List.of(a,b),i8(T).historial(e));
    }
    @Test void repeticionMismaOmisionEsConflictoYNoDuplica() throws Exception {
        long e=equipo("A"),s=sesion();var v=reintento(e,s);i8(T.plusSeconds(2)).registrarOmision(e,v.verificacionId(),"A","C","M");
        error(CONFLICTO,()->i8(T.plusSeconds(2)).registrarOmision(e,v.verificacionId(),"A","C","M"));assertEquals(1,contar("contingencia_manual"));
    }
    @Test void flujoManualCompletoNoCreaLecturasSinteticasNiEventos() throws Exception {
        long e=equipo("A"),s=sesion();var v=reintento(e,s);var c=i8(T.plusSeconds(2)).registrarOmision(e,v.verificacionId(),"A","C","M");
        i8(T.plusSeconds(3)).confirmar(c.contingenciaId(),"A");var fuera=i8(T).registrarFueraDeRuta(e,"A",null,"C","M");i8(T).confirmar(fuera.contingenciaId(),"A");
        assertEquals(0,contar("lectura_rfid"));assertEquals(0,contar("evento_operativo"));assertEquals(2,contar("contingencia_manual"));
    }
    @Test void relojRegresivoYContextoCerradoSeRechazan() {
        long e=equipo("A"),s=sesion();var v=reintento(e,s);
        error(DATO_INVALIDO,()->i8(T).registrarOmision(e,v.verificacionId(),"A","C","M"));
        var c=i8(T).registrarFueraDeRuta(e,"A",null,"C","M");error(DATO_INVALIDO,()->i8(T.minusSeconds(1)).confirmar(c.contingenciaId(),"A"));
        i3(T.plusSeconds(2)).cerrarSesion(s);error(CONFLICTO,()->i8(T.plusSeconds(3)).registrarOmision(e,v.verificacionId(),"A","C","M"));
    }
    @Test void validaDatosYReferenciasInexistentes() {
        long e=equipo("A");error(DATO_INVALIDO,()->i8(T).registrarFueraDeRuta(0,"A",null,"C","M"));
        for(String valor:new String[]{null,""," "}) {
            error(DATO_INVALIDO,()->i8(T).registrarFueraDeRuta(e,valor,null,"C","M"));
            error(DATO_INVALIDO,()->i8(T).registrarFueraDeRuta(e,"A",null,valor,"M"));
            error(DATO_INVALIDO,()->i8(T).registrarFueraDeRuta(e,"A",null,"C",valor));
        }
        error(NO_ENCONTRADO,()->i8(T).registrarFueraDeRuta(999,"A",null,"C","M"));
        error(NO_ENCONTRADO,()->i8(T).registrarFueraDeRuta(e,"A",999L,"C","M"));
        error(NO_ENCONTRADO,()->i8(T).registrarOmision(e,999,"A","C","M"));error(NO_ENCONTRADO,()->i8(T).confirmar(999,"A"));
    }
    @Test void concurrentesNoDuplicanOmisionNiConfirmacion() throws Exception {
        long e=equipo("A"),s=sesion();var v=reintento(e,s);compiten(()->i8(T.plusSeconds(2)).registrarOmision(e,v.verificacionId(),"A","C","M"));
        var c=i8(T).historial(e).getFirst();compiten(()->i8(T.plusSeconds(3)).confirmar(c.contingenciaId(),"A"));
    }
    @Test void rollbackRegistroYConfirmacion() throws Exception {
        long e=equipo("A");assertThrows(IllegalStateException.class,()->unidad().ejecutarI8(r->{r.registrar(e,"A",T,null,"C","M",null);throw new IllegalStateException();}));
        assertEquals(0,contar("contingencia_manual"));var c=i8(T).registrarFueraDeRuta(e,"A",null,"C","M");
        assertThrows(IllegalStateException.class,()->unidad().ejecutarI8(r->{r.confirmar(c.contingenciaId(),T,"A");throw new IllegalStateException();}));
        assertEquals(c,i8(T).consultar(c.contingenciaId()).orElseThrow());
    }
    @Test void reaperturaConservaHistorialYConfirmacion() throws Exception {
        var c=i8(T).registrarFueraDeRuta(equipo("A"),"A",null,"C","M");var confirmada=i8(T).confirmar(c.contingenciaId(),"A");
        base=new BaseDatos(temporal.resolve("mvp.db"));base.migrar();assertEquals(List.of(confirmada),i8(T).historial(c.equipoId()));
    }
}
