# Proyecto EMI

Prototipo de trazabilidad RFID UHF pasivo para equipos biomedicos. I1 IMPLEMENTADO Y VERIFICADO TÉCNICAMENTE conforme a DEC-022: bootstrap, tres entidades y esquema relacional disponibles. Los siete casos de uso de asociacion y sus comandos de consola estan implementados y verificados. El bootstrap utiliza Java 21, Maven, SQLite/JDBC, Flyway y JUnit 5. No integra hardware RFID ni cierra P1.

Consultar [fuentes de verdad](docs/FUENTES_DE_VERDAD.md), [reglas de desarrollo](AGENTS.md) y [arquitectura](docs/desarrollo/arquitectura.md). Las referencias institucionales permanecen locales y excluidas de Git; no se distribuyen al clonar.

## Compilar y probar en Windows

Se requiere un JDK 21. Maven Wrapper descarga Maven 3.9.11; la primera ejecucion necesita acceso a Maven Central. En PowerShell, ajustar JAVA_HOME a la instalacion propia:

```powershell
$env:JAVA_HOME = 'C:\Program Files\Eclipse Adoptium\jdk-21.0.12.101-hotspot'
$env:Path = "$env:JAVA_HOME\bin;$env:Path"
.\mvnw.cmd clean verify
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

## Comandos I1

La ruta de la base es el primer argumento; use una base de demostracion nueva para repetir este ejemplo (IDs 1 y 2 corresponden a esa base vacia). No eliminar una base con datos para repetir pruebas.

```powershell
java -jar target/trazabilidad-0.1.0-SNAPSHOT.jar data/demo-i1.db ayuda
java -jar target/trazabilidad-0.1.0-SNAPSHOT.jar data/demo-i1.db crear-equipo EQ-001 true "Monitor"
java -jar target/trazabilidad-0.1.0-SNAPSHOT.jar data/demo-i1.db crear-etiqueta 0001 ACTIVA
java -jar target/trazabilidad-0.1.0-SNAPSHOT.jar data/demo-i1.db crear-etiqueta 0002 ACTIVA
java -jar target/trazabilidad-0.1.0-SNAPSHOT.jar data/demo-i1.db asociar 1 1
java -jar target/trazabilidad-0.1.0-SNAPSHOT.jar data/demo-i1.db vigente 1
java -jar target/trazabilidad-0.1.0-SNAPSHOT.jar data/demo-i1.db buscar-epc 0001
java -jar target/trazabilidad-0.1.0-SNAPSHOT.jar data/demo-i1.db corregir 1 2 "Correccion de etiqueta"
java -jar target/trazabilidad-0.1.0-SNAPSHOT.jar data/demo-i1.db historial 1
```

Descripcion y motivo son opcionales. Usar los IDs devueltos por los comandos de creacion. El historial incluye fechas UTC y conserva asociaciones cerradas. Sin comando se mantiene la inicializacion de persistencia.

Ver [casos de uso y evidencia](docs/desarrollo/casos_de_uso_i1.md): 32 pruebas aprobadas. La prohibicion de asociar etiquetas INACTIVA sigue como propuesta pendiente y no esta implementada. Los comandos muestran errores sin traza interna y retornan 1 para errores del servicio, 2 para argumentos invalidos y 0 para exito.

## Estado técnico y trazabilidad de I1

**I1 = IMPLEMENTADO Y VERIFICADO TÉCNICAMENTE.** El cierre formal queda pendiente de los commits de cierre; todavía no se declara CERRADO.

Estados documentados en el repositorio por solicitud explícita del usuario:

| Requisito | Estado | Alcance |
| --- | --- | --- |
| RF-001 | IMPLEMENTADO | Los siete casos de uso de I1 están implementados y verificados. |
| DAT-003 | EN DESARROLLO | El submodelo I1 está implementado; el modelo mínimo completo del MVP todavía no. |
| TEC-002 | EN DESARROLLO | FuenteLecturasRFID todavía no está implementada. |

No se cambian otros requisitos ni se reescriben los documentos ACTUAL. Su evidencia anterior se conserva como antecedente.

Evidencia técnica: 7 casos de uso implementados; 32 pruebas, 0 fallos, 0 errores y 0 omitidas; rollback completo probado; corrección en una transacción y con un único instante UTC; concurrencia probada; historial ordenado; búsqueda por EPC vigente; JAR ejecutando los siete flujos.

La prohibición de asociar etiquetas INACTIVA no está aprobada: sigue como propuesta pendiente, no implementada y no constituye un requisito.

Después del cierre formal de I1, el siguiente incremento previsto es **FuenteLecturasRFID → FuenteSimulada**. Todavía no se implementa y requiere autorización operativa.
