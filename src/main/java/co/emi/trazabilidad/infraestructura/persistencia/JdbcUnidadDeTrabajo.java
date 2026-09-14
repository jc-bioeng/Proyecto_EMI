package co.emi.trazabilidad.infraestructura.persistencia;

import co.emi.trazabilidad.aplicacion.ErrorAplicacion;
import co.emi.trazabilidad.aplicacion.puertos.*;
import java.sql.SQLException;
import java.sql.Connection;
import java.util.Objects;
import java.util.function.Function;

public final class JdbcUnidadDeTrabajo implements UnidadDeTrabajo {
    private final BaseDatos base;

    public JdbcUnidadDeTrabajo(BaseDatos base) { this.base = Objects.requireNonNull(base); }

    @Override public <T> T ejecutar(Function<RepositorioI1, T> operacion) {
        return transaccion(c -> operacion.apply(new JdbcRepositorioI1(c)));
    }

    @Override public <T> T ejecutarI3(Function<RepositorioI3, T> operacion) {
        return transaccion(c -> operacion.apply(new JdbcRepositorioI3(c)));
    }

    @Override public <T> T ejecutarI4(Function<RepositorioI4, T> operacion) {
        return transaccion(c -> operacion.apply(new JdbcRepositorioI4(c)));
    }

    @Override public <T> T ejecutarI5(Function<RepositorioI5, T> consulta) {
        return transaccion(c -> {
            // Conexion exclusiva de esta llamada: una consulta I5 no puede escribir datos.
            try (var s = c.createStatement()) { s.execute("PRAGMA query_only = ON"); }
            catch (SQLException e) {
                throw new ErrorAplicacion(ErrorAplicacion.Codigo.PERSISTENCIA, "No se pudo proteger la consulta", e);
            }
            Throwable falloConsulta = null;
            try {
                return consulta.apply(new JdbcRepositorioI5(c));
            } catch (RuntimeException | Error fallo) {
                falloConsulta = fallo;
                throw fallo;
            } finally {
                // El driver inicia otro BEGIN IMMEDIATE al completar commit/rollback.
                // Restaurar el modo antes de finalizar; todas las consultas ya terminaron.
                try (var s = c.createStatement()) { s.execute("PRAGMA query_only = OFF"); }
                catch (SQLException fallo) {
                    if (falloConsulta != null) falloConsulta.addSuppressed(fallo);
                    else throw new ErrorAplicacion(ErrorAplicacion.Codigo.PERSISTENCIA,
                        "No se pudo finalizar la consulta protegida", fallo);
                }
            }
        });
    }

    private <T> T transaccion(Function<Connection, T> operacion) {
        try (var c = base.abrir()) {
            c.setAutoCommit(false);
            try {
                T resultado = operacion.apply(c);
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
