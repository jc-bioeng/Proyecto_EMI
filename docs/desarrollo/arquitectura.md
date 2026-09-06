# Base tecnica I1

DEC-022: Java 21, Maven, JDBC, SQLite, Flyway y JUnit 5. Un modulo Maven, sin Spring ni ORM. Dominio y servicios se incorporan despues del commit del bootstrap, conforme a la secuencia solicitada por el usuario.

La migracion V1 prueba el mecanismo de migraciones sin crear entidades ficticias. El esquema de asociaciones se incorporara en V2, conservando V1 inmutable. Las conexiones activan claves foraneas, espera de bloqueo de 5 segundos y transacciones inmediatas. El archivo es local; no compartirlo entre computadores.

Validacion inicial: migracion a SQLite temporal en disco, reapertura, ausencia de repeticion y claves foraneas activadas. No demuestra integracion RFID ni cierra P1.
