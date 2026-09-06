# Base tecnica I1

DEC-022: Java 21, Maven, JDBC, SQLite, Flyway y JUnit 5. Un modulo Maven, sin Spring ni ORM. El dominio y esquema I1 se incorporaron despues del commit 320b069 del bootstrap. Los servicios y comandos de negocio estan implementados; ver casos_de_uso_i1.md.

La migracion V1 prueba el mecanismo de migraciones sin crear entidades ficticias. V2 incorpora el esquema de asociaciones, conservando V1 inmutable. Las conexiones activan claves foraneas, espera de bloqueo de 5 segundos y transacciones inmediatas. El archivo es local; no compartirlo entre computadores.

Validacion inicial: migracion a SQLite temporal en disco, reapertura, ausencia de repeticion y claves foraneas activadas. No demuestra integracion RFID ni cierra P1.

## Antecedente de verificacion

Bootstrap: commit 320b069, primera prueba aprobada y JAR ejecutado en Windows con Java 21. Paso entidades: mvnw.cmd verify completo, 8 pruebas sin fallos ni errores. El JAR actualizo la base de prueba del bootstrap de V1 a V2 correctamente. Las entidades y V2 se versionan en un incremento posterior al commit del bootstrap; las pruebas y la documentacion se registran en commits separados.

## Servicios I1

ServicioI1 depende de UnidadDeTrabajo y RepositorioI1. JDBC implementa ambos contratos; ConsolaEmi invoca los casos de uso. La correccion cierra e inserta en una misma transaccion y con un solo instante UTC. Evidencia actual: 32 pruebas aprobadas; el detalle y las decisiones pendientes se encuentran en casos_de_uso_i1.md. No hay cambios en V1/V2 ni dependencias nuevas.

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
