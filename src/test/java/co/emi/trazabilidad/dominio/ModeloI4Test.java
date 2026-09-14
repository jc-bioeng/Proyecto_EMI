package co.emi.trazabilidad.dominio;

import co.emi.trazabilidad.aplicacion.ResultadoProcesamiento;
import java.time.Instant;
import java.util.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ModeloI4Test {
    @Test void eventoValidaIdentidadesTipoYTiempo() {
        assertThrows(IllegalArgumentException.class,()->new EventoOperativo(0,1,1,TipoEvento.INGRESO,Instant.EPOCH,Instant.EPOCH,1));
        assertThrows(IllegalArgumentException.class,()->new EventoOperativo(1,0,1,TipoEvento.INGRESO,Instant.EPOCH,Instant.EPOCH,1));
        assertThrows(IllegalArgumentException.class,()->new EventoOperativo(1,1,0,TipoEvento.INGRESO,Instant.EPOCH,Instant.EPOCH,1));
        assertThrows(IllegalArgumentException.class,()->new EventoOperativo(1,1,1,TipoEvento.INGRESO,Instant.EPOCH,Instant.EPOCH,0));
        assertThrows(NullPointerException.class,()->new EventoOperativo(1,1,1,null,Instant.EPOCH,Instant.EPOCH,1));
        assertThrows(IllegalArgumentException.class,()->new EventoOperativo(1,1,1,TipoEvento.INGRESO,Instant.EPOCH,Instant.EPOCH.minusNanos(1),1));
        assertEquals(Set.of("INGRESO","SALIDA"),new HashSet<>(Arrays.stream(TipoEvento.values()).map(Enum::name).toList()));
    }
    @Test void respaldoValidaIdentidadLecturaYFecha() {
        var l=new LecturaRFID(1,"X",Instant.EPOCH,OrigenDatos.SIMULACION,1L,Map.of());
        assertThrows(IllegalArgumentException.class,()->new EvidenciaEvento(0,1,l,Instant.EPOCH));
        assertThrows(IllegalArgumentException.class,()->new EvidenciaEvento(1,0,l,Instant.EPOCH));
        assertThrows(NullPointerException.class,()->new EvidenciaEvento(1,1,null,Instant.EPOCH));
        assertThrows(IllegalArgumentException.class,()->new EvidenciaEvento(1,1,l,Instant.EPOCH.minusNanos(1)));
    }
    @Test void resultadoNoPuedeAfirmarCreacionSinEvento() {
        assertThrows(IllegalArgumentException.class,()->new ResultadoProcesamiento(1,ResultadoProcesamiento.Estado.EVENTO_CREADO,Optional.empty(),""));
        var e=new EventoOperativo(1,1,1,TipoEvento.INGRESO,Instant.EPOCH,Instant.EPOCH,1);
        assertThrows(IllegalArgumentException.class,()->new ResultadoProcesamiento(1,ResultadoProcesamiento.Estado.SIN_ASOCIACION_VALIDA,Optional.of(e),""));
    }
}
