package co.emi.trazabilidad.infraestructura.persistencia;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import org.flywaydb.core.Flyway;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

/** Configuracion comun para conexiones de aplicacion y migraciones. */
public final class BaseDatos {
    private final SQLiteDataSource source;

    public BaseDatos(Path archivo) throws IOException {
        Path absoluto = archivo.toAbsolutePath().normalize();
        Files.createDirectories(absoluto.getParent());
        SQLiteConfig config = new SQLiteConfig();
        config.enforceForeignKeys(true);
        config.setBusyTimeout(5000);
        config.setTransactionMode(SQLiteConfig.TransactionMode.IMMEDIATE);
        source = new SQLiteDataSource(config);
        source.setUrl("jdbc:sqlite:" + absoluto);
    }

    public void migrar() {
        Flyway.configure().dataSource(source).locations("classpath:db/migration")
            .cleanDisabled(true).validateMigrationNaming(true).load().migrate();
    }

    public Connection abrir() throws SQLException {
        return source.getConnection();
    }
}
