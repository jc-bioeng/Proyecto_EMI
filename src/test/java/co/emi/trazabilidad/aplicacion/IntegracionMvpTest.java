package co.emi.trazabilidad.aplicacion;

import co.emi.trazabilidad.aplicacion.puertos.FuenteLecturasRFID;
import co.emi.trazabilidad.dominio.*;
import co.emi.trazabilidad.infraestructura.persistencia.*;
import co.emi.trazabilidad.infraestructura.rfid.FuenteSimulada;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static co.emi.trazabilidad.dominio.ResultadoVerificacion.*;

class IntegracionMvpTest extends SoporteMvpTest {
    private void emitir(long sesion,Instant t,List<String> epcs) {
        var simulada=new FuenteSimulada(reloj(t));FuenteLecturasRFID fuente=simulada;
        fuente.iniciar(l -> i3(t).persistirLectura(l,sesion));simulada.emitir(epcs);fuente.detener();
    }
    @Test void softwareSimuladoIntegraI1AI8SinConvertirVerificacionOManualEnMovimiento() throws Exception {
        long a=asociado("A"),b=asociado("B"),c=asociado("C");
        long ingreso=i3(T).crearSesion("P","actor",TipoOperacion.INGRESO,"SIMULACION").sesionId();
        emitir(ingreso,T,Collections.nCopies(20,"A"));assertEquals(0,contar("evento_operativo"));i4(T).procesarSesion(ingreso);
        var evento=i4(T).eventosDeSesion(ingreso).getFirst();assertEquals(20,i4(T).evidenciasDeEvento(evento.eventoId()).size());
        i3(T).cerrarSesion(ingreso);
        long verificacion=sesion();emitir(verificacion,T,List.of("A","A","C","SIN-ASOCIACION"));
        var inicial=i6(T).verificar(verificacion,Set.of(a,b));assertEquals(Map.of(a,DETECTADO,b,FALTANTE,c,NO_ESPERADO),resultados(inicial));
        emitir(verificacion,T.plusSeconds(1),List.of("A"));var reintento=i6(T.plusSeconds(2)).reintentar(inicial.verificacionId());
        var contingencia=i8(T.plusSeconds(3)).registrarOmision(b,reintento.verificacionId(),"actor","ensayo de omision","sin deteccion tras reintento");
        var confirmada=i8(T.plusSeconds(4)).confirmar(contingencia.contingenciaId(),"confirmador");
        var sustitucion=i7(T.plusSeconds(5)).abrir(b,"origen de ensayo","destino de ensayo","responsable","prestamo temporal");
        var devuelta=i7(T.plusSeconds(6)).cerrar(sustitucion.sustitucionId());
        long fuera=equipo("FUERA");var manual=i8(T.plusSeconds(6)).registrarFueraDeRuta(fuera,"actor",null,"trayectoria fuera del punto declarada","confirmacion por equipo");
        i8(T.plusSeconds(7)).confirmar(manual.contingenciaId(),"confirmador");i3(T.plusSeconds(8)).cerrarSesion(verificacion);
        assertEquals(25,contar("lectura_rfid"));assertEquals(1,contar("evento_operativo"));assertEquals(20,contar("evento_lectura"));
        assertTrue(i3(T).lecturasDeSesion(verificacion).stream().allMatch(l -> l.origenDatos()==OrigenDatos.SIMULACION));
        var consulta=new ServicioI5(new JdbcUnidadDeTrabajo(base));assertEquals(evento,consulta.ultimoEventoPorEquipo(a).orElseThrow());
        assertEquals(23,consulta.historialLecturasPorEquipo(a).size());assertTrue(consulta.historialLecturasPorEquipo(b).isEmpty());
        base=new BaseDatos(temporal.resolve("mvp.db"));base.migrar();assertEquals(confirmada,i8(T).consultar(contingencia.contingenciaId()).orElseThrow());
        assertEquals(devuelta,i7(T).consultar(sustitucion.sustitucionId()).orElseThrow());assertEquals(Map.of(a,DETECTADO,b,FALTANTE,c,NO_ESPERADO),resultados(inicial));
    }
    @Test void reintentoRecuperaOmitidoSinNecesitarContingencia() throws Exception {
        long a=asociado("A"),s=sesion();var primero=i6(T).verificar(s,Set.of(a));emitir(s,T.plusSeconds(1),List.of("A","A"));
        var segundo=i6(T.plusSeconds(2)).reintentar(primero.verificacionId());assertEquals(Map.of(a,FALTANTE),resultados(primero));assertEquals(Map.of(a,DETECTADO),resultados(segundo));
        assertEquals(0,contar("contingencia_manual"));assertEquals(0,contar("evento_operativo"));assertEquals(2,contar("lectura_rfid"));
    }
    @Test void consultasNuevasSonInmutablesYNoEscriben() throws Exception {
        long a=asociado("A"),s=sesion();leer("A",s,T);var v=i6(T).verificar(s,Set.of(a));
        var sustitucion=i7(T).abrir(a,"A","B","C","D");var c=i8(T).registrarFueraDeRuta(a,"A",null,"C","M");
        byte[] antes=java.nio.file.Files.readAllBytes(temporal.resolve("mvp.db"));
        i6(T).consultar(v.verificacionId());i6(T).historial(s);i6(T).items(v.verificacionId());i6(T).evidencias(v.verificacionId());
        i7(T).consultar(sustitucion.sustitucionId());i7(T).activa(a);i7(T).historial(a);i8(T).consultar(c.contingenciaId());i8(T).historial(a);
        assertArrayEquals(antes,java.nio.file.Files.readAllBytes(temporal.resolve("mvp.db")));
        assertThrows(UnsupportedOperationException.class,()->i6(T).items(v.verificacionId()).clear());
        assertThrows(UnsupportedOperationException.class,()->i6(T).evidencias(v.verificacionId()).clear());
        assertThrows(UnsupportedOperationException.class,()->i7(T).historial(a).clear());assertThrows(UnsupportedOperationException.class,()->i8(T).historial(a).clear());
    }
}
