package co.emi.trazabilidad.aplicacion;

import co.emi.trazabilidad.dominio.*;
import co.emi.trazabilidad.infraestructura.persistencia.*;
import java.nio.file.*;
import java.sql.*;
import java.time.Instant;
import java.util.*;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MigracionMvpTest extends SoporteMvpTest {
    private static final List<String> PREVIAS=List.of("equipo","etiqueta_rfid","asignacion_etiqueta","sesion_operacion","lectura_rfid","lectura_rfid_metadata","evento_operativo","evento_lectura");
    private Map<String,List<List<String>>> snapshot() throws Exception {
        Map<String,List<List<String>>> datos=new TreeMap<>();
        try(var c=base.abrir();var s=c.createStatement()) {
            for(String tabla:PREVIAS) try(var r=s.executeQuery("SELECT * FROM "+tabla+" ORDER BY 1,2")) {
                List<List<String>> filas=new ArrayList<>();
                while(r.next()) { List<String> fila=new ArrayList<>();for(int n=1;n<=r.getMetaData().getColumnCount();n++)fila.add(r.getString(n));filas.add(fila); }
                datos.put(tabla,filas);
            }
        }return datos;
    }
    private Map<String,Integer> checksums() throws Exception {
        Map<String,Integer> salida=new TreeMap<>();try(var c=base.abrir();var s=c.createStatement();var r=s.executeQuery("SELECT version,checksum FROM flyway_schema_history")) {
            while(r.next())salida.put(r.getString(1),r.getInt(2));
        }return salida;
    }
    @Test void baseDesdeCeroTieneOchoMigracionesRepetiblesEIntegridad() throws Exception {
        base.migrar();assertEquals(8,checksums().size());assertEquals(14,contar("sqlite_master WHERE type='table'"));
        try(var c=base.abrir();var s=c.createStatement();var r=s.executeQuery("PRAGMA foreign_key_check")) { assertFalse(r.next()); }
        try(var c=base.abrir();var s=c.createStatement();var r=s.executeQuery("PRAGMA integrity_check")) { assertTrue(r.next());assertEquals("ok",r.getString(1)); }
    }
    @Test void v5ConDatosEHistorialMigraAV8SinModificarUnSoloCampoPrevio() throws Exception {
        Path archivo=temporal.resolve("v5.db");Flyway.configure().dataSource("jdbc:sqlite:"+archivo,null,null).locations("classpath:db/migration").target("5").load().migrate();
        base=new BaseDatos(archivo);long a=asociado("A"),s=i3(T).crearSesion("P","A",TipoOperacion.INGRESO,"contexto").sesionId();
        leer("A",s,T);leer("A",s,T);leer("DESCONOCIDO",s,T);i4(T).procesarSesion(s);i3(T).cerrarSesion(s);
        var nueva=i1(T.plusSeconds(1)).crearEtiqueta("NUEVA",EstadoEtiqueta.ACTIVA);i1(T.plusSeconds(1)).corregir(a,nueva.etiquetaId(),"historial");
        var antes=snapshot();var hashes=checksums();base.migrar();base.migrar();assertEquals(antes,snapshot());
        var despues=checksums();assertEquals(8,despues.size());hashes.forEach((k,v)->assertEquals(v,despues.get(k)));
        var v=i6(T).verificar(sesion(),Set.of(a));assertEquals(ResultadoVerificacion.FALTANTE,resultados(v).get(a));
        i7(T).abrir(a,"O","D","R","M");i8(T).registrarFueraDeRuta(a,"R",null,"C","M");
        assertEquals(antes.get("lectura_rfid"),snapshot().get("lectura_rfid"));assertEquals(antes.get("evento_operativo"),snapshot().get("evento_operativo"));
    }
    @Test void v6YV7TambienPuedenActualizarseConSusDatos() throws Exception {
        Path archivo=temporal.resolve("v6.db");Flyway.configure().dataSource("jdbc:sqlite:"+archivo,null,null).locations("classpath:db/migration").target("6").load().migrate();
        base=new BaseDatos(archivo);long a=asociado("A"),s=sesion();leer("A",s,T);var v=i6(T).verificar(s,Set.of(a));var ev=i6(T).evidencias(v.verificacionId());
        Flyway.configure().dataSource("jdbc:sqlite:"+archivo,null,null).locations("classpath:db/migration").target("7").load().migrate();
        var sustitucion=i7(T).abrir(a,"O","D","R","M");base.migrar();
        assertEquals(v,i6(T).consultar(v.verificacionId()).orElseThrow());assertEquals(ev,i6(T).evidencias(v.verificacionId()));
        assertEquals(sustitucion,i7(T).activa(a).orElseThrow());i8(T).registrarFueraDeRuta(a,"R",null,"C","M");
    }
    @Test void sqlDirectoNoPuedeAlterarNiBorrarVerificaciones() throws Exception {
        long a=asociado("A"),s=sesion();leer("A",s,T);i6(T).verificar(s,Set.of(a));
        for(String tabla:List.of("verificacion","verificacion_item","verificacion_lectura")) {
            assertThrows(SQLException.class,()->sql("DELETE FROM "+tabla));
            assertThrows(SQLException.class,()->sql("UPDATE "+tabla+" SET verificacion_id=verificacion_id"));
        }
    }
    @Test void sqlDirectoProtegeHistorialSustitucionYContingencia() throws Exception {
        long a=equipo("A");i7(T).abrir(a,"O","D","R","M");i8(T).registrarFueraDeRuta(a,"R",null,"C","M");
        assertThrows(SQLException.class,()->sql("DELETE FROM sustitucion_temporal"));assertThrows(SQLException.class,()->sql("UPDATE sustitucion_temporal SET motivo='cambio'"));
        assertThrows(SQLException.class,()->sql("DELETE FROM contingencia_manual"));assertThrows(SQLException.class,()->sql("UPDATE contingencia_manual SET motivo='cambio'"));
        assertThrows(SQLException.class,()->sql("UPDATE contingencia_manual SET origen_registro='RF_REAL'"));
    }
    @Test void sqlDirectoRechazaEquipoAjenoYLecturaDeOtraSesion() throws Exception {
        long a=asociado("A"),b=equipo("B"),s=sesion();var l=leer("A",s,T);var v=i6(T).verificar(s,Set.of(a,b));
        var otra=leer("A",sesion(),T);
        assertThrows(SQLException.class,()->sql("INSERT INTO verificacion_lectura VALUES("+v.verificacionId()+","+otra.lecturaId()+",NULL,NULL,'SIN_ASOCIACION')"));
        assertThrows(SQLException.class,()->sql("INSERT INTO verificacion_item VALUES("+v.verificacionId()+",999,'FALTANTE')"));
        assertThrows(SQLException.class,()->sql("INSERT INTO verificacion_lectura VALUES("+v.verificacionId()+","+l.lecturaId()+","+b+",1,'ATRIBUIDA')"));
    }
    @Test void sqlDirectoNoAceptaContingenciaDeOmisionInicial() throws Exception {
        long a=equipo("A"),s=sesion();var v=i6(T).verificar(s,Set.of(a));
        assertThrows(SQLException.class,()->sql("INSERT INTO contingencia_manual(equipo_id,actor,fecha,sesion_id,contexto,motivo,verificacion_id) VALUES("+a+",'A',"+T.toEpochMilli()+","+s+",'C','M',"+v.verificacionId()+")"));
    }
    @Test void tiemposNegativosYLimiteExclusivoDeAsociacionSeConservan() {
        Instant t=Instant.ofEpochMilli(-1500);var i1=i1(t);long a=i1.crearEquipo("NEG",null,true).equipoId();var tag=i1.crearEtiqueta("N",EstadoEtiqueta.ACTIVA);i1.asociar(a,tag.etiquetaId());
        long s=i3(t).crearSesion("P","A",TipoOperacion.VERIFICACION,"C").sesionId();leer("N",s,t);leer("N",s,t.minusNanos(1));
        var nueva=i1(Instant.ofEpochMilli(-1001)).crearEtiqueta("M",EstadoEtiqueta.ACTIVA);i1(Instant.ofEpochMilli(-1001)).corregir(a,nueva.etiquetaId(),"cambio");
        leer("N",s,Instant.ofEpochMilli(-1001));var v=i6(Instant.EPOCH).verificar(s,Set.of(a));
        assertEquals(ResultadoVerificacion.DETECTADO,resultados(v).get(a));
        assertEquals(List.of(EvidenciaVerificacion.Estado.ATRIBUIDA,EvidenciaVerificacion.Estado.ANTERIOR_A_SESION,EvidenciaVerificacion.Estado.SIN_ASOCIACION),
            i6(T).evidencias(v.verificacionId()).stream().map(EvidenciaVerificacion::estado).toList());
    }
}
