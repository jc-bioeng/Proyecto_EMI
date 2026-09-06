# Base tecnica I1

DEC-022: Java 21, Maven, JDBC, SQLite, Flyway y JUnit 5. Un modulo Maven, sin Spring ni ORM. El dominio y esquema I1 se incorporaron despues del commit 320b069 del bootstrap. Los servicios y comandos de negocio siguen pendientes.

La migracion V1 prueba el mecanismo de migraciones sin crear entidades ficticias. V2 incorpora el esquema de asociaciones, conservando V1 inmutable. Las conexiones activan claves foraneas, espera de bloqueo de 5 segundos y transacciones inmediatas. El archivo es local; no compartirlo entre computadores.

Validacion inicial: migracion a SQLite temporal en disco, reapertura, ausencia de repeticion y claves foraneas activadas. No demuestra integracion RFID ni cierra P1.

## Verificacion ejecutada

Bootstrap: commit 320b069, primera prueba aprobada y JAR ejecutado en Windows con Java 21. Paso entidades: mvnw.cmd verify completo, 8 pruebas sin fallos ni errores. El JAR actualizo la base de prueba del bootstrap de V1 a V2 correctamente. Las entidades y V2 se versionan en un incremento posterior al commit del bootstrap; las pruebas y la documentacion se registran en commits separados. No se ha hecho push.
