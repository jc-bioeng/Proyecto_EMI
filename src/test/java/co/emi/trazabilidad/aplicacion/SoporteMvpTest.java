package co.emi.trazabilidad.aplicacion;

import co.emi.trazabilidad.dominio.*;
import co.emi.trazabilidad.infraestructura.persistencia.*;
import java.nio.file.Path;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Supplier;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

abstract class SoporteMvpTest {
    @TempDir Path temporal;
    protected BaseDatos base;
    protected static final Instant T=Instant.parse("2026-09-14T10:00:00Z");
    @BeforeEach void prepararBase() throws Exception { base=new BaseDatos(temporal.resolve("mvp.db")); base.migrar(); }
    protected Clock reloj(Instant t) { return Clock.fixed(t,ZoneOffset.UTC); }
    protected JdbcUnidadDeTrabajoMvp unidad() { return new JdbcUnidadDeTrabajoMvp(base); }
    protected ServicioI1 i1(Instant t) { return new ServicioI1(new JdbcUnidadDeTrabajo(base),reloj(t)); }
    protected ServicioI3 i3(Instant t) { return new ServicioI3(new JdbcUnidadDeTrabajo(base),reloj(t)); }
    protected ServicioI4 i4(Instant t) { return new ServicioI4(new JdbcUnidadDeTrabajo(base),reloj(t)); }
    protected ServicioI6 i6(Instant t) { return new ServicioI6(unidad(),reloj(t)); }
    protected ServicioI7 i7(Instant t) { return new ServicioI7(unidad(),reloj(t)); }
    protected ServicioI8 i8(Instant t) { return new ServicioI8(unidad(),reloj(t)); }
    protected long equipo(String codigo) { return i1(T.minusSeconds(60)).crearEquipo(codigo,null,true).equipoId(); }
    protected long asociado(String codigo) {
        long equipo=equipo(codigo); var tag=i1(T.minusSeconds(60)).crearEtiqueta(codigo,EstadoEtiqueta.INACTIVA);
        i1(T.minusSeconds(60)).asociar(equipo,tag.etiquetaId()); return equipo;
    }
    protected long sesion() { return i3(T).crearSesion("P","actor",TipoOperacion.VERIFICACION,"SOFTWARE").sesionId(); }
    protected LecturaRFID leer(String epc,long sesion,Instant t) {
        return i3(t).persistirLectura(new LecturaEntradaRFID(epc,t,OrigenDatos.SIMULACION,Map.of("antena","1")),sesion);
    }
    protected void sql(String texto) throws Exception { try(var c=base.abrir();var s=c.createStatement()) { s.executeUpdate(texto); } }
    protected long contar(String tabla) throws Exception {
        try(var c=base.abrir();var s=c.createStatement();var r=s.executeQuery("SELECT count(*) FROM "+tabla)) { assertTrue(r.next()); return r.getLong(1); }
    }
    protected void error(ErrorAplicacion.Codigo codigo,org.junit.jupiter.api.function.Executable accion) {
        assertEquals(codigo,assertThrows(ErrorAplicacion.class,accion).codigo());
    }
    protected Map<Long,ResultadoVerificacion> resultados(Verificacion v) {
        Map<Long,ResultadoVerificacion> salida=new TreeMap<>();
        i6(T).items(v.verificacionId()).forEach(i -> salida.put(i.equipoId(),i.resultado())); return salida;
    }
    protected Verificacion reintento(long equipo,long sesion) {
        var primero=i6(T).verificar(sesion,Set.of(equipo)); return i6(T.plusSeconds(1)).reintentar(primero.verificacionId());
    }
    /** Dos conexiones reales compiten tras una barrera; exactamente una debe ganar. */
    protected void compiten(Supplier<?> operacion) throws Exception {
        try(var pool=Executors.newFixedThreadPool(2)) {
            var barrera=new CyclicBarrier(2);
            Callable<Boolean> tarea=() -> {
                barrera.await(5,TimeUnit.SECONDS);
                try { operacion.get(); return true; }
                catch(ErrorAplicacion e) { assertEquals(ErrorAplicacion.Codigo.CONFLICTO,e.codigo()); return false; }
            };
            var a=pool.submit(tarea); var b=pool.submit(tarea);
            assertNotEquals(a.get(15,TimeUnit.SECONDS),b.get(15,TimeUnit.SECONDS));
        }
    }
}
