package co.emi.trazabilidad.aplicacion;

import co.emi.trazabilidad.aplicacion.puertos.*;
import co.emi.trazabilidad.dominio.*;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static co.emi.trazabilidad.aplicacion.ErrorAplicacion.Codigo.*;

/** Casos de uso I1. La entrada no decide reglas ni administra transacciones. */
public final class ServicioI1 {
    private final UnidadDeTrabajo unidad;
    private final Clock reloj;

    public ServicioI1(UnidadDeTrabajo unidad, Clock reloj) {
        this.unidad = Objects.requireNonNull(unidad);
        this.reloj = Objects.requireNonNull(reloj);
    }

    public Equipo crearEquipo(String codigo, String descripcion, boolean activoPiloto) {
        obligatorio(codigo, "codigo_institucional");
        return unidad.ejecutar(r -> {
            if (r.equipoPorCodigo(codigo).isPresent()) {
                throw new ErrorAplicacion(CONFLICTO, "El codigo institucional ya existe");
            }
            return r.crearEquipo(codigo, descripcion, activoPiloto);
        });
    }

    public EtiquetaRFID crearEtiqueta(String epc, EstadoEtiqueta estado) {
        obligatorio(epc, "epc");
        if (estado == null) throw new ErrorAplicacion(DATO_INVALIDO, "estado es obligatorio");
        return unidad.ejecutar(r -> {
            if (r.etiquetaPorEpc(epc).isPresent()) {
                throw new ErrorAplicacion(CONFLICTO, "El EPC ya existe");
            }
            return r.crearEtiqueta(epc, estado);
        });
    }

    public AsignacionEtiqueta asociar(long equipoId, long etiquetaId) {
        identificador(equipoId); identificador(etiquetaId);
        return unidad.ejecutar(r -> {
            exigirEquipo(r, equipoId);
            exigirEtiqueta(r, etiquetaId);
            if (r.vigenteDeEquipo(equipoId).isPresent()) {
                throw new ErrorAplicacion(CONFLICTO, "El equipo ya tiene una asociacion vigente");
            }
            exigirEtiquetaLibre(r, etiquetaId);
            return r.crearAsignacion(equipoId, etiquetaId, ahora());
        });
    }

    public Optional<AsignacionEtiqueta> consultarVigente(long equipoId) {
        identificador(equipoId);
        return unidad.ejecutar(r -> {
            exigirEquipo(r, equipoId);
            return r.vigenteDeEquipo(equipoId);
        });
    }

    public Optional<Equipo> buscarEquipoPorEpc(String epc) {
        obligatorio(epc, "epc");
        return unidad.ejecutar(r -> r.etiquetaPorEpc(epc)
            .flatMap(e -> r.vigenteDeEtiqueta(e.etiquetaId()))
            .flatMap(a -> r.equipo(a.equipoId())));
    }

    public AsignacionEtiqueta corregir(long equipoId, long nuevaEtiquetaId, String motivo) {
        identificador(equipoId); identificador(nuevaEtiquetaId);
        return unidad.ejecutar(r -> {
            exigirEquipo(r, equipoId);
            AsignacionEtiqueta anterior = r.vigenteDeEquipo(equipoId)
                .orElseThrow(() -> new ErrorAplicacion(NO_ENCONTRADO, "El equipo no tiene asociacion vigente"));
            exigirEtiqueta(r, nuevaEtiquetaId);
            exigirEtiquetaLibre(r, nuevaEtiquetaId);
            Instant cambio = ahora();
            if (cambio.isBefore(anterior.fechaInicio())) {
                throw new ErrorAplicacion(DATO_INVALIDO, "El instante de cambio precede al inicio de la asociacion");
            }
            if (!r.cerrarAsignacion(anterior.asignacionId(), cambio, motivo)) {
                throw new ErrorAplicacion(CONFLICTO, "La asociacion vigente cambio durante la operacion");
            }
            return r.crearAsignacion(equipoId, nuevaEtiquetaId, cambio);
        });
    }

    public List<AsignacionEtiqueta> historial(long equipoId) {
        identificador(equipoId);
        return unidad.ejecutar(r -> {
            exigirEquipo(r, equipoId);
            return r.historial(equipoId);
        });
    }

    private Instant ahora() {
        // Una lectura del reloj, con la precision de milisegundos del esquema existente.
        return Instant.ofEpochMilli(reloj.instant().toEpochMilli());
    }
    private static void obligatorio(String valor, String campo) {
        if (valor == null || valor.isBlank()) throw new ErrorAplicacion(DATO_INVALIDO, campo + " es obligatorio");
    }
    private static void identificador(long id) {
        if (id <= 0) throw new ErrorAplicacion(DATO_INVALIDO, "El identificador debe ser positivo");
    }
    private static void exigirEquipo(RepositorioI1 r, long id) {
        if (r.equipo(id).isEmpty()) throw new ErrorAplicacion(NO_ENCONTRADO, "El equipo no existe");
    }
    private static void exigirEtiqueta(RepositorioI1 r, long id) {
        if (r.etiqueta(id).isEmpty()) throw new ErrorAplicacion(NO_ENCONTRADO, "La etiqueta no existe");
        // No se impone una prohibicion para INACTIVA sin aprobacion funcional.
    }
    private static void exigirEtiquetaLibre(RepositorioI1 r, long id) {
        if (r.vigenteDeEtiqueta(id).isPresent()) {
            throw new ErrorAplicacion(CONFLICTO, "La etiqueta ya tiene una asociacion vigente");
        }
    }
}
