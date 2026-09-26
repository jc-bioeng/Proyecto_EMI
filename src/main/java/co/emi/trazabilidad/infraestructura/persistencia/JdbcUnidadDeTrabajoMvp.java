package co.emi.trazabilidad.infraestructura.persistencia;

import co.emi.trazabilidad.aplicacion.puertos.*;
import java.sql.*;
import java.util.Objects;
import java.util.function.Function;

/** Unidad aditiva I6-I8: conserva transaccion/rollback sin modificar la API previa. */
public final class JdbcUnidadDeTrabajoMvp implements UnidadDeTrabajoMvp {
    private final BaseDatos base;
    public JdbcUnidadDeTrabajoMvp(BaseDatos base) { this.base=Objects.requireNonNull(base); }
    @Override public <T> T ejecutarI6(Function<RepositorioI6,T> operacion) { return transaccion(c -> operacion.apply(new JdbcRepositorioI6(c))); }
    @Override public <T> T ejecutarI7(Function<RepositorioI7,T> operacion) { return transaccion(c -> operacion.apply(new JdbcRepositorioI7(c))); }
    @Override public <T> T ejecutarI8(Function<RepositorioI8,T> operacion) { return transaccion(c -> operacion.apply(new JdbcRepositorioI8(c))); }
    private <T> T transaccion(Function<Connection,T> operacion) {
        try(var c=base.abrir()) {
            c.setAutoCommit(false);
            try { T resultado=operacion.apply(c); c.commit(); return resultado; }
            catch(SQLException | RuntimeException | Error fallo) {
                try { c.rollback(); } catch(SQLException rollback) { fallo.addSuppressed(rollback); }
                throw fallo;
            }
        } catch(SQLException e) { throw JdbcSoporteMvp.error(e); }
    }
}
