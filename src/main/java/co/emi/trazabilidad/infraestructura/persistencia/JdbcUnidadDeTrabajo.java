package co.emi.trazabilidad.infraestructura.persistencia;

import co.emi.trazabilidad.aplicacion.ErrorAplicacion;
import co.emi.trazabilidad.aplicacion.puertos.*;
import java.sql.SQLException;
import java.util.Objects;
import java.util.function.Function;

public final class JdbcUnidadDeTrabajo implements UnidadDeTrabajo {
    private final BaseDatos base;

    public JdbcUnidadDeTrabajo(BaseDatos base) { this.base = Objects.requireNonNull(base); }

    @Override public <T> T ejecutar(Function<RepositorioI1, T> operacion) {
        try (var c = base.abrir()) {
            c.setAutoCommit(false);
            try {
                T resultado = operacion.apply(new JdbcRepositorioI1(c));
                c.commit();
                return resultado;
            } catch (SQLException | RuntimeException | Error fallo) {
                try { c.rollback(); } catch (SQLException rollback) { fallo.addSuppressed(rollback); }
                throw fallo;
            }
        } catch (SQLException fallo) {
            throw new ErrorAplicacion(ErrorAplicacion.Codigo.PERSISTENCIA,
                "No se pudo completar la transaccion de persistencia", fallo);
        }
    }
}
