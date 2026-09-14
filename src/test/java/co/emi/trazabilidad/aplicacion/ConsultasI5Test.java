package co.emi.trazabilidad.aplicacion;

import co.emi.trazabilidad.aplicacion.consultas.*;
import co.emi.trazabilidad.dominio.*;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConsultasI5Test {
    @Test void atribucionValidaIntervaloSemiabiertoSinModificarLectura() {
        var a=new AsignacionEtiqueta(1,1,1,Instant.EPOCH,Instant.EPOCH.plusSeconds(1),null);
        var dentro=new LecturaRFID(1,"X",Instant.EPOCH,OrigenDatos.SIMULACION,null,Map.of());
        assertSame(dentro,new LecturaAtribuida(dentro,a).lectura());
        var fuera=new LecturaRFID(2,"X",a.fechaFin(),OrigenDatos.SIMULACION,null,Map.of());
        assertThrows(IllegalArgumentException.class,()->new LecturaAtribuida(fuera,a));
        assertThrows(NullPointerException.class,()->new LecturaAtribuida(dentro,null));
    }
    @Test void eventoConContextoCopiaOrigenesYExigeSesionCorrecta() {
        var e=new EventoOperativo(1,1,1,TipoEvento.INGRESO,Instant.EPOCH,Instant.EPOCH,1);
        var s=new SesionOperacion(1,"P","A",TipoOperacion.INGRESO,Instant.EPOCH,null,EstadoSesion.ABIERTA,null);
        var origenes=EnumSet.of(OrigenDatos.SIMULACION,OrigenDatos.RF_REAL);var detalle=new EventoConContexto(e,s,origenes);origenes.clear();
        assertEquals(2,detalle.origenes().size());assertThrows(UnsupportedOperationException.class,()->detalle.origenes().clear());
        var otra=new SesionOperacion(2,"P","A",TipoOperacion.INGRESO,Instant.EPOCH,null,EstadoSesion.ABIERTA,null);
        assertThrows(IllegalArgumentException.class,()->new EventoConContexto(e,otra,Set.of()));
    }
}
