package co.emi.trazabilidad.infraestructura.persistencia;

import co.emi.trazabilidad.aplicacion.*;
import co.emi.trazabilidad.dominio.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class EsquemaI5Test {
    @TempDir Path temporal;
    private static final Instant T=Instant.parse("2026-09-13T08:00:00Z");
    private record Datos(Equipo equipo,SesionOperacion sesion,List<LecturaRFID> lecturas,EventoOperativo evento) { }
    private Datos poblar(BaseDatos base,int cantidad) {
        var unidad=new JdbcUnidadDeTrabajo(base);var reloj=Clock.fixed(T,ZoneOffset.UTC);
        var i1=new ServicioI1(unidad,reloj);var i3=new ServicioI3(unidad,reloj);var i4=new ServicioI4(unidad,reloj);
        var equipo=i1.crearEquipo("EQ",null,true);var tag=i1.crearEtiqueta("X",EstadoEtiqueta.ACTIVA);i1.asociar(equipo.equipoId(),tag.etiquetaId());
        var sesion=i3.crearSesion("P","A",TipoOperacion.INGRESO,"software");
        List<LecturaRFID> lecturas=new ArrayList<>();
        for(int i=0;i<cantidad;i++)lecturas.add(i3.persistirLectura(new LecturaEntradaRFID("X",T,i%2==0?OrigenDatos.SIMULACION:OrigenDatos.RF_REAL,Map.of("a","1","b","2","c","3")),sesion.sesionId()));
        i4.procesarSesion(sesion.sesionId());var evento=i4.eventosDeSesion(sesion.sesionId()).getFirst();
        return new Datos(equipo,sesion,List.copyOf(lecturas),evento);
    }
    private static long contar(Connection c,String sql)throws SQLException {
        try(var s=c.createStatement();var r=s.executeQuery(sql)){assertTrue(r.next());return r.getLong(1);}
    }
    private static Map<String,Integer> checksums(BaseDatos base)throws SQLException {
        Map<String,Integer> salida=new HashMap<>();
        try(var c=base.abrir();var s=c.createStatement();var r=s.executeQuery("SELECT version,checksum FROM flyway_schema_history")) {
            while(r.next())salida.put(r.getString(1),r.getInt(2));
        }return salida;
    }
    @Test void migracionDesdeCeroSoloAgregaDosIndicesSinTablasNuevas()throws Exception {
        var base=new BaseDatos(temporal.resolve("nueva.db"));base.migrar();base.migrar();
        try(var c=base.abrir()) {
            assertEquals(5,contar(c,"SELECT count(*) FROM flyway_schema_history WHERE success=1"));
            assertEquals(9,contar(c,"SELECT count(*) FROM sqlite_master WHERE type='table'"));
            assertEquals(2,contar(c,"SELECT count(*) FROM sqlite_master WHERE type='index' AND name IN ('ix_lectura_epc_tiempo','ix_evento_equipo_tiempo')"));
            assertEquals(1,contar(c,"PRAGMA foreign_keys"));
        }
    }
    @Test void v4ConEvidenciaMigraAV5SinCambiarDatosNiChecksumsPrevios()throws Exception {
        var archivo=temporal.resolve("previa.db");
        Flyway.configure().dataSource("jdbc:sqlite:"+archivo,null,null).locations("classpath:db/migration").target("4").load().migrate();
        var base=new BaseDatos(archivo);var datos=poblar(base,4);var antes=checksums(base);
        var i4=new ServicioI4(new JdbcUnidadDeTrabajo(base),Clock.fixed(T,ZoneOffset.UTC));var respaldos=i4.evidenciasDeEvento(datos.evento.eventoId());
        var cerrada=new ServicioI3(new JdbcUnidadDeTrabajo(base),Clock.fixed(T,ZoneOffset.UTC)).cerrarSesion(datos.sesion.sesionId());
        base.migrar();base.migrar();var despues=checksums(base);assertEquals(5,despues.size());antes.forEach((k,v)->assertEquals(v,despues.get(k)));
        var consulta=new ServicioI5(new JdbcUnidadDeTrabajo(base));
        assertEquals(datos.lecturas,consulta.historialLecturasPorEquipo(datos.equipo.equipoId()).stream().map(v->v.lectura()).toList());
        assertEquals(datos.evento,consulta.ultimoEventoPorEquipo(datos.equipo.equipoId()).orElseThrow());
        assertEquals(respaldos,consulta.evidenciasDeEvento(datos.evento.eventoId()));
        assertEquals(cerrada,consulta.historialEventosPorEquipo(datos.equipo.equipoId()).getFirst().sesion());
    }
    private static List<String> plan(Connection c,String sql,long id)throws SQLException {
        List<String> salida=new ArrayList<>();try(var s=c.prepareStatement("EXPLAIN QUERY PLAN "+sql)) {
            s.setLong(1,id);try(var r=s.executeQuery()){while(r.next())salida.add(r.getString("detail"));}
        }return salida;
    }
    @Test void planesRealesDelDriverUsanLosIndicesJustificados()throws Exception {
        var archivo=temporal.resolve("planes.db");
        Flyway.configure().dataSource("jdbc:sqlite:"+archivo,null,null).locations("classpath:db/migration").target("4").load().migrate();
        var base=new BaseDatos(archivo);var datos=poblar(base,20);
        try(var c=base.abrir()) {
            System.out.println("PLAN V4 LECTURAS "+plan(c,JdbcRepositorioI5.sqlLecturas(true),datos.equipo.equipoId()));
            System.out.println("PLAN V4 EVENTO "+plan(c,JdbcRepositorioI5.SQL_ULTIMO_EVENTO,datos.equipo.equipoId()));
        }
        base.migrar();
        try(var c=base.abrir()) {
            var lecturas=plan(c,JdbcRepositorioI5.sqlLecturas(true),datos.equipo.equipoId());
            var eventos=plan(c,JdbcRepositorioI5.SQL_ULTIMO_EVENTO,datos.equipo.equipoId());
            var historial=plan(c,JdbcRepositorioI5.SQL_HISTORIAL_EVENTOS,datos.equipo.equipoId());
            System.out.println("PLAN V5 LECTURAS "+lecturas);System.out.println("PLAN V5 EVENTO "+eventos);
            assertTrue(lecturas.stream().anyMatch(p->p.contains("SEARCH l")&&p.contains("ix_lectura_epc_tiempo")));
            assertTrue(eventos.stream().anyMatch(p->p.contains("SEARCH evento_operativo")&&p.contains("ix_evento_equipo_tiempo")));
            assertTrue(historial.stream().anyMatch(p->p.contains("SEARCH e")&&p.contains("ix_evento_equipo_tiempo")));
        }
    }
    @Test void historialesYRespaldosNoEjecutanConsultasNMasUno()throws Exception {
        var base=new BaseDatos(temporal.resolve("conteo.db"));base.migrar();var datos=poblar(base,20);
        try(var real=base.abrir()) {
            var cantidad=new AtomicInteger();
            Connection observada=(Connection)Proxy.newProxyInstance(Connection.class.getClassLoader(),new Class<?>[]{Connection.class},(proxy,metodo,args)->{
                if(metodo.getName().equals("prepareStatement"))cantidad.incrementAndGet();
                try{return metodo.invoke(real,args);}catch(InvocationTargetException e){throw e.getCause();}
            });
            var r=new JdbcRepositorioI5(observada);
            assertEquals(20,r.historialLecturasPorEquipo(datos.equipo.equipoId()).size());assertEquals(1,cantidad.getAndSet(0));
            assertEquals(3,r.ultimaLecturaPorEquipo(datos.equipo.equipoId()).orElseThrow().lectura().metadata().size());assertEquals(1,cantidad.getAndSet(0));
            assertEquals(1,r.historialEventosPorEquipo(datos.equipo.equipoId()).size());assertEquals(1,cantidad.getAndSet(0));
            assertEquals(datos.evento,r.ultimoEventoPorEquipo(datos.equipo.equipoId()).orElseThrow());assertEquals(1,cantidad.getAndSet(0));
            var respaldos=r.evidenciasDeEvento(datos.evento.eventoId());assertEquals(20,respaldos.size());assertEquals(1,cantidad.get());
            assertTrue(respaldos.stream().allMatch(v->v.lectura().metadata().size()==3));
        }
    }
    @Test void repositorioFuncionaConQueryOnlyYNoAcumulaCambios()throws Exception {
        var base=new BaseDatos(temporal.resolve("lectura.db"));base.migrar();var datos=poblar(base,2);
        try(var c=base.abrir();var s=c.createStatement()) {
            s.execute("PRAGMA query_only=ON");long antes=contar(c,"SELECT total_changes()");var r=new JdbcRepositorioI5(c);
            r.equipo(datos.equipo.equipoId());r.equipoPorCodigo("EQ");r.ultimaLecturaPorEquipo(datos.equipo.equipoId());
            r.historialLecturasPorEquipo(datos.equipo.equipoId());r.ultimoEventoPorEquipo(datos.equipo.equipoId());
            r.historialEventosPorEquipo(datos.equipo.equipoId());r.evento(datos.evento.eventoId());r.evidenciasDeEvento(datos.evento.eventoId());
            assertEquals(antes,contar(c,"SELECT total_changes()"));
            assertThrows(SQLException.class,()->s.executeUpdate("INSERT INTO equipo VALUES (99,'no',NULL,0)"));
        }
    }
}
