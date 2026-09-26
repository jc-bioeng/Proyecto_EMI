package co.emi.trazabilidad.aplicacion;

import co.emi.trazabilidad.aplicacion.puertos.*;
import co.emi.trazabilidad.dominio.*;
import java.time.*;
import java.util.*;
import static co.emi.trazabilidad.aplicacion.ErrorAplicacion.Codigo.*;

public final class ServicioI8 {
    private final UnidadDeTrabajoMvp unidad;
    private final Clock reloj;
    public ServicioI8(UnidadDeTrabajoMvp unidad,Clock reloj) {
        this.unidad=Objects.requireNonNull(unidad); this.reloj=Objects.requireNonNull(reloj);
    }
    /** La omision inicial no basta: referencia al ultimo reintento que conserva FALTANTE. */
    public ContingenciaManual registrarOmision(long equipo,long reintento,String actor,String contexto,String motivo) {
        validar(equipo,actor,contexto,motivo); ValidacionMvp.id(reintento);
        return unidad.ejecutarI8(r -> {
            exigirEquipo(r,equipo);
            var v=exigirReintentoFaltante(r,equipo,reintento);
            var fecha=ValidacionMvp.ahora(reloj);
            if(fecha.isBefore(v.fecha())) throw new ErrorAplicacion(DATO_INVALIDO,"Registro anterior a verificacion");
            exigirSesionAbierta(r,v.sesionId(),fecha);
            return r.registrar(equipo,actor,fecha,v.sesionId(),contexto,motivo,reintento);
        });
    }
    /** Declaracion explicita del operador; no clasifica permanentemente el activo ni exige EPC. */
    public ContingenciaManual registrarFueraDeRuta(long equipo,String actor,Long sesion,String contexto,String motivo) {
        validar(equipo,actor,contexto,motivo); if(sesion!=null) ValidacionMvp.id(sesion);
        return unidad.ejecutarI8(r -> {
            exigirEquipo(r,equipo); var fecha=ValidacionMvp.ahora(reloj);
            if(sesion!=null) exigirSesionAbierta(r,sesion,fecha);
            return r.registrar(equipo,actor,fecha,sesion,contexto,motivo,null);
        });
    }
    public ContingenciaManual confirmar(long id,String actor) {
        ValidacionMvp.id(id); ValidacionMvp.texto(actor);
        return unidad.ejecutarI8(r -> {
            var c=r.contingencia(id).orElseThrow(() -> new ErrorAplicacion(NO_ENCONTRADO,"La contingencia no existe"));
            if(c.confirmada()) throw new ErrorAplicacion(CONFLICTO,"La contingencia ya fue confirmada");
            var fecha=ValidacionMvp.ahora(reloj);
            if(fecha.isBefore(c.fecha())) throw new ErrorAplicacion(DATO_INVALIDO,"Confirmacion anterior al registro");
            if(c.verificacionId()!=null) {
                // Si hubo otro intento, el operador debe revisar ese resultado antes de confirmar.
                exigirReintentoFaltante(r,c.equipoId(),c.verificacionId());
            }
            if(!r.confirmar(id,fecha,actor)) throw new ErrorAplicacion(CONFLICTO,"La contingencia cambio");
            return r.contingencia(id).orElseThrow();
        });
    }
    public Optional<ContingenciaManual> consultar(long id) { ValidacionMvp.id(id); return unidad.ejecutarI8(r -> r.contingencia(id)); }
    public List<ContingenciaManual> historial(long equipo) {
        ValidacionMvp.id(equipo); return unidad.ejecutarI8(r -> { exigirEquipo(r,equipo); return r.historial(equipo); });
    }
    private static Verificacion exigirReintentoFaltante(RepositorioI8 r,long equipo,long id) {
        var v=r.verificacion(id).orElseThrow(() -> new ErrorAplicacion(NO_ENCONTRADO,"La verificacion no existe"));
        if(v.anteriorId()==null || r.verificaciones(v.sesionId()).getLast().verificacionId()!=id
            || r.items(id).stream().noneMatch(i -> i.equipoId()==equipo && i.resultado()==ResultadoVerificacion.FALTANTE))
            throw new ErrorAplicacion(CONFLICTO,"Se requiere FALTANTE en el ultimo reintento");
        return v;
    }
    private static void exigirSesionAbierta(RepositorioI8 r,long id,Instant fecha) {
        var s=r.sesion(id).orElseThrow(() -> new ErrorAplicacion(NO_ENCONTRADO,"La sesion no existe"));
        if(s.estado()!=EstadoSesion.ABIERTA) throw new ErrorAplicacion(CONFLICTO,"Sesion cerrada");
        if(fecha.isBefore(s.fechaInicio())) throw new ErrorAplicacion(DATO_INVALIDO,"Registro anterior a sesion");
    }
    private static void exigirEquipo(RepositorioI8 r,long id) {
        if(r.equipo(id).isEmpty()) throw new ErrorAplicacion(NO_ENCONTRADO,"El equipo no existe");
    }
    private static void validar(long equipo,String actor,String contexto,String motivo) {
        ValidacionMvp.id(equipo); ValidacionMvp.texto(actor); ValidacionMvp.texto(contexto); ValidacionMvp.texto(motivo);
    }
}
