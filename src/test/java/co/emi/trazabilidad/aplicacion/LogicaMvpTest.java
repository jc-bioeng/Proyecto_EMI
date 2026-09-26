package co.emi.trazabilidad.aplicacion;

import co.emi.trazabilidad.aplicacion.puertos.*;
import co.emi.trazabilidad.dominio.*;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Pruebas unitarias sin JDBC, ficheros ni hardware. */
class LogicaMvpTest {
    private static final Instant T=Instant.parse("2026-09-14T10:00:00Z");
    private static final class Memoria implements RepositorioI6,UnidadDeTrabajoMvp {
        final List<LecturaRFID> lecturas=new ArrayList<>();
        final List<AsignacionEtiqueta> asociaciones=new ArrayList<>();
        final List<VerificacionItem> items=new ArrayList<>();
        final List<EvidenciaVerificacion> evidencias=new ArrayList<>();
        final List<Verificacion> intentos=new ArrayList<>();
        @Override public <T> T ejecutarI6(Function<RepositorioI6,T> f) { return f.apply(this); }
        @Override public <T> T ejecutarI7(Function<RepositorioI7,T> f) { throw new AssertionError("I6 no debe llamar I7"); }
        @Override public <T> T ejecutarI8(Function<RepositorioI8,T> f) { throw new AssertionError("I6 no debe llamar I8"); }
        @Override public Optional<Equipo> equipo(long id) { return Optional.of(new Equipo(id,"E"+id,null,true)); }
        @Override public Optional<SesionOperacion> sesion(long id) { return Optional.of(new SesionOperacion(id,"P","A",TipoOperacion.VERIFICACION,T,null,EstadoSesion.ABIERTA,null)); }
        @Override public List<LecturaRFID> lecturasDeSesion(long id) { return List.copyOf(lecturas); }
        @Override public List<AsignacionEtiqueta> asociacionesPorEpc(String epc) { return List.copyOf(asociaciones); }
        @Override public Verificacion crear(long sesion,Instant fecha,Long anterior) {
            var v=new Verificacion(intentos.size()+1,sesion,fecha,anterior);intentos.add(v);return v;
        }
        @Override public void agregarItem(VerificacionItem i) { items.add(i); }
        @Override public void agregarEvidencia(EvidenciaVerificacion e) { evidencias.add(e); }
        @Override public Optional<Verificacion> verificacion(long id) { return intentos.stream().filter(v->v.verificacionId()==id).findFirst(); }
        @Override public List<Verificacion> historial(long sesion) { return intentos.stream().filter(v->v.sesionId()==sesion).toList(); }
        @Override public List<VerificacionItem> items(long id) { return items.stream().filter(i->i.verificacionId()==id).toList(); }
        @Override public List<EvidenciaVerificacion> evidencias(long id) { return evidencias.stream().filter(e->e.verificacionId()==id).toList(); }
        void leer(long id) { lecturas.add(new LecturaRFID(id,"EPC",T,OrigenDatos.SIMULACION,1L,Map.of())); }
    }
    private ServicioI6 servicio(Memoria m) { return new ServicioI6(m,Clock.fixed(T,ZoneOffset.UTC)); }
    @Test void comparacionDeConjuntosEsIndependienteDeJdbc() {
        var m=new Memoria();m.leer(1);m.leer(2);m.asociaciones.add(new AsignacionEtiqueta(1,1,1,T,null,null));
        servicio(m).verificar(1,Set.of(1L,2L));assertEquals(List.of(new VerificacionItem(1,1,ResultadoVerificacion.DETECTADO),new VerificacionItem(1,2,ResultadoVerificacion.FALTANTE)),m.items);
        assertEquals(2,m.evidencias.size());assertEquals(2,m.lecturas.size());
    }
    @Test void asociacionAmbiguaNoAtribuyeEquipo() {
        var m=new Memoria();m.leer(1);m.asociaciones.add(new AsignacionEtiqueta(1,1,1,T,null,null));m.asociaciones.add(new AsignacionEtiqueta(2,2,1,T,null,null));
        servicio(m).verificar(1,Set.of(1L));assertEquals(ResultadoVerificacion.FALTANTE,m.items.getFirst().resultado());
        assertEquals(EvidenciaVerificacion.Estado.ASOCIACION_AMBIGUA,m.evidencias.getFirst().estado());assertNull(m.evidencias.getFirst().equipoId());
    }
    @Test void finDeAsociacionEsExclusivo() {
        var m=new Memoria();m.leer(1);m.asociaciones.add(new AsignacionEtiqueta(1,1,1,T.minusSeconds(1),T,"fin"));
        servicio(m).verificar(1,Set.of(1L));assertEquals(EvidenciaVerificacion.Estado.SIN_ASOCIACION,m.evidencias.getFirst().estado());
    }
    @Test void reintentoCopiaSoloEsperadosDelIntentoAnterior() {
        var m=new Memoria();m.leer(1);m.asociaciones.add(new AsignacionEtiqueta(1,2,1,T,null,null));
        var v=servicio(m).verificar(1,Set.of(1L));servicio(m).reintentar(v.verificacionId());
        assertEquals(List.of(new VerificacionItem(2,1,ResultadoVerificacion.FALTANTE),new VerificacionItem(2,2,ResultadoVerificacion.NO_ESPERADO)),m.items(2));
    }
    @Test void evidenciaExcluidaNoPuedePortarEquipoOAsignacion() {
        var l=new LecturaRFID(1,"A",T,OrigenDatos.SIMULACION,1L,Map.of());
        assertThrows(IllegalArgumentException.class,()->new EvidenciaVerificacion(1,l,1L,null,EvidenciaVerificacion.Estado.SIN_ASOCIACION));
        assertThrows(IllegalArgumentException.class,()->new EvidenciaVerificacion(1,l,null,null,EvidenciaVerificacion.Estado.ATRIBUIDA));
    }
    @Test void modeloVerificacionValidaIdentidadYResultado() {
        assertThrows(IllegalArgumentException.class,()->new Verificacion(0,1,T,null));
        assertThrows(IllegalArgumentException.class,()->new VerificacionItem(1,0,ResultadoVerificacion.FALTANTE));
        assertThrows(NullPointerException.class,()->new VerificacionItem(1,1,null));
    }
    @Test void modeloSustitucionValidaDatosYDerivaActiva() {
        var s=new SustitucionTemporal(1,1,"O","D","R","M",T,null);assertTrue(s.activa());
        assertThrows(IllegalArgumentException.class,()->new SustitucionTemporal(1,1,"O","D"," ","M",T,null));
        assertThrows(IllegalArgumentException.class,()->new SustitucionTemporal(1,1,"O","D","R","M",T,T.minusSeconds(1)));
    }
    @Test void modeloManualNoAdmiteConfirmacionParcial() {
        assertThrows(IllegalArgumentException.class,()->new ContingenciaManual(1,1,"A",T,null,"C","M",null,T,null));
        assertThrows(IllegalArgumentException.class,()->new ContingenciaManual(1,1,"A",T,null,"C","M",1L,null,null));
        assertThrows(IllegalArgumentException.class,()->new ContingenciaManual(1,1,"A",T,null,"C","M",null,T.minusSeconds(1),"A"));
    }
}
