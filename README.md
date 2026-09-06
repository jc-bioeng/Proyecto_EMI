# Proyecto EMI

Prototipo de trazabilidad RFID UHF pasivo para equipos biomedicos. I1 en desarrollo conforme a DEC-022: bootstrap, tres entidades y esquema relacional disponibles. Los casos de uso y comandos de negocio siguen pendientes. El bootstrap utiliza Java 21, Maven, SQLite/JDBC, Flyway y JUnit 5. No integra hardware RFID ni cierra P1.

Consultar [fuentes de verdad](docs/FUENTES_DE_VERDAD.md), [reglas de desarrollo](AGENTS.md) y [arquitectura](docs/desarrollo/arquitectura.md). Las referencias institucionales permanecen locales y excluidas de Git; no se distribuyen al clonar.

## Compilar y probar en Windows

Se requiere un JDK 21. Maven Wrapper descarga Maven 3.9.11; la primera ejecucion necesita acceso a Maven Central. En PowerShell, ajustar JAVA_HOME a la instalacion propia:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\mvnw.cmd verify
java -jar target/trazabilidad-0.1.0-SNAPSHOT.jar data/emi.db
```

La configuracion de esas variables afecta solo esta sesion. Maven exige Java 21 y rechaza Java 8. El JAR incluye dependencias; el equipo de ejecucion necesita Java 21, sin Maven.

El arranque migra la base antes de usarla. La base debe residir en disco local, fuera del control de versiones. Antes de actualizar una instalacion con datos, detener la aplicacion y respaldar la base y sus archivos auxiliares existentes. No ejecutar migraciones concurrentemente. Flyway clean esta deshabilitado.

## Enlace manual con la cuenta personal

Git local y la cuenta del alojamiento son independientes. La identidad existente se conserva. El usuario controla el remoto y la publicacion; estos pasos no son necesarios para ejecutar el proyecto.

Crear un repositorio remoto vacio en la cuenta personal y reemplazar USUARIO y REPOSITORIO:

```powershell
git remote -v
# Si no existe origin:
git remote add origin https://github.com/USUARIO/REPOSITORIO.git
git status --short --ignored
git log --oneline -5
git push -u origin main
```

Completar la autenticacion cuando Git la solicite. No guardar tokens en archivos ni URLs del repositorio. Revisar los commits antes de publicar: gitignore no retira informacion ya versionada. `docs/control_local/`, `docs/soporte/` y `docs/anexos/` estan excluidas.
