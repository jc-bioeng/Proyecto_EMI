# I3 — Persistencia de sesiones y lecturas crudas

Fecha de ejecución: 12/09/2026. **I3 CERRADO técnicamente**, ámbito SOFTWARE / SIMULACION.
Este informe es evidencia técnica subordinada; no modifica los PDF ACTUAL v1.9.2 ni crea una DEC.
No cierra MVP, P1, P2, P3, Pruebas formales, Piloto ni Validación. RF_REAL físico sigue pendiente.

## Inspección, autoridad y discrepancias

Se inspeccionaron el módulo Maven, dominio, puertos, servicios, JDBC, unidad de trabajo, consola, migraciones V1/V2 y convenciones de las pruebas I1/I2. Se verificó primero la suite histórica: 57 pruebas, cero fallos, errores y omitidas.
No se repitió la inspección estática del SDK A4: no hay una pregunta nueva de hardware en este encargo.

Fuentes consultadas, en orden de autoridad:

- `docs/control_local/Propuesta_Juan_Cortes_20262.pdf`, pp. 3, 5 y 7: alcance delimitado, simulación como soporte de desarrollo y separación de validación física.
- `docs/control_local/Arquitecto_Proyecto_Documento_Maestro_v1.9.2_ACTUAL.pdf`, §4 p. 2 y §9 p. 5: pipeline, entrada propia, repeticiones y diseño dirigido de relación lectura/sesión antes de migrar.
- `docs/control_local/Matriz_Requisitos_Proyecto_EMI_v1.9.2_ACTUAL.pdf`, §2 p. 2 y §§3–5 pp. 4–5: requisitos, evidencia parcial y gates.
- `docs/control_local/Registro_Decisiones_Proyecto_EMI_v1.9.2_ACTUAL.pdf`, §2 p. 2 y §§4–6 p. 3: DEC-014/015/017/022, diseño previo y separación de evidencia.
- `docs/FUENTES_DE_VERDAD.md`, `AGENTS.md` y el encargo posterior de I3 aportado por el usuario.
- Código y documentos de desarrollo I1; informe I2 en `docs/analisis/INFORME_TECNICO_I2_FUENTE_RFID_SIMULADA.md`.

Discrepancia histórica: README y los documentos `arquitectura.md`, `modelo_datos.md` y `casos_de_uso_i1.md` describen I1 antes de su cierre y la fuente como pendiente; algunas rutas citan v1.8.1. Son antecedentes subordinados. Prevalece v1.9.2: I1 e I2 cerrados, 57 pruebas previas. No se reabren ni se reescriben esos antecedentes.
La secuencia anterior separaba persistencia de lectura y sesión. El encargo actual autoriza explícitamente ambas en I3; se diseñan conjuntamente antes de V3, sin adelantar eventos.

## Diseño adoptado antes de V3

### Relación y supuestos conservadores

Una sesión tiene cero o muchas lecturas; una lectura tiene cero o una sesión. El control vigente consultado no fija obligatoriedad de FK para toda detección y el contrato I2 carece de sesión. Se permite `sesion_id = NULL` para no fabricar contexto ni perder lecturas. Si se proporciona ID, la sesión debe existir; no se elige automáticamente una sesión abierta.
Una lectura sin sesión se recupera por su ID. No tiene contexto suficiente para justificar un movimiento. No hay reasignación ni actualización de evidencia persistida en I3; una asociación contextual posterior requerirá diseño explícito, preservando la evidencia original.

`punto_control` y `actor_contexto` son texto obligatorio proporcionado por el llamador, sin catálogo institucional ni FK ficticia a entidades inexistentes. No confirman ubicación, permisos, Farmacia como operador ni múltiples puntos autorizados. `contexto` es texto adicional nullable. El origen reside en cada lectura: la sesión no obliga a que todas compartan origen.

Tipos explícitos: `INGRESO`, `SALIDA`, `VERIFICACION`. Estados técnicos: `ABIERTA`, `CERRADA`. Crear abre; cerrar fija fecha de fin conservando los demás datos. Segundo cierre: `CONFLICTO`; reloj anterior al inicio: `DATO_INVALIDO`; ID inexistente para cerrar/listar/persistir con sesión: `NO_ENCONTRADO`. Consultar un ID válido ausente devuelve `Optional.empty()`.

Se admite una entrega tardía con ID de sesión explícito aunque esté cerrada, conservando su timestamp literal. La entrada cruda no demuestra pertenencia temporal a una operación; no se filtra por intervalo, no se reabre la sesión ni se calcula un evento. Esta es una política técnica de conservación, no una regla institucional aprobada. La capa posterior deberá validar contexto, intervalos y confirmación sin convertir automáticamente estas lecturas en eventos.

### Tiempo y metadata

Dominio con `Instant`/UTC. Sesiones: INTEGER de milisegundos Unix UTC, como I1; el reloj se consulta dentro de la transacción y se ajusta explícitamente a esa precisión.
Lecturas: `timestamp_segundos` + `timestamp_nanos`, ambos INTEGER; reconstrucción mediante `Instant.ofEpochSecond`. La diferencia es deliberada: I2 entrega nanosegundos y truncarlos perdería evidencia/orden. Se soporta el rango de `Instant`, incluyendo instantes negativos. Orden total: segundos, nanos, `lectura_id`; empates no se eliminan.

La metadata `Map<String,String>` usa tabla auxiliar normalizada `lectura_rfid_metadata`, PK `(lectura_id, clave)`. Conserva claves/valores vacíos, Unicode, comillas y saltos de línea; no admite null. Mapa vacío = ninguna fila. Se guarda junto a la lectura en una sola transacción. No es una entidad operativa adicional ni introduce biblioteca de serialización.

### Integridad y arquitectura

V3 añade tablas STRICT, PK positivas, FK activas con RESTRICT, NOT NULL y CHECK de tipos, estados y tiempo. Índice `(sesion_id, timestamp_segundos, timestamp_nanos, lectura_id)` para consultas de sesión; la PK compuesta indexa metadata. No existe UNIQUE sobre EPC ni timestamp; no hay FK a etiqueta/equipo que impida capturar EPC desconocidos.
Triggers impiden borrar sesiones, lecturas y metadata, sobrescribir lecturas/metadata, alterar contexto de sesión y reabrir/modificar sesiones cerradas. La API no ofrece mutaciones históricas ni inserciones de metadata independientes; las protecciones SQL no sustituyen el control de acceso al archivo SQLite.

`ServicioI3` depende de `UnidadDeTrabajo` y `RepositorioI3`. Se añade `ejecutarI3` como extensión compatible y ambos caminos JDBC reutilizan el mismo procedimiento de conexión/transacción/commit/rollback existente. No se introduce ORM ni arquitectura paralela. El repositorio mantiene SQL parametrizado y traduce errores con `ErrorAplicacion`; una restricción SQLite es CONFLICTO, otros errores SQL son PERSISTENCIA.
Cada llamada a persistir confirma una lectura y toda su metadata o revierte ambas. Un lote de FuenteSimulada sigue siendo una secuencia de entregas, no una transacción global: ante fallo, las entregas anteriores confirmadas permanecen, conforme al contrato I2.

## Uso desde Java

```java
var base = new BaseDatos(Path.of("data/ensayo-i3.db"));
base.migrar();
var servicio = new ServicioI3(new JdbcUnidadDeTrabajo(base), Clock.systemUTC());
var sesion = servicio.crearSesion("PUNTO-PRUEBA", "contexto de ensayo", TipoOperacion.INGRESO, "SIMULACION de software");
var fuente = new FuenteSimulada(Clock.systemUTC());
fuente.iniciar(entrada -> servicio.persistirLectura(entrada, sesion.sesionId()));
fuente.emitir(List.of("0001", "0001"));
fuente.detener();
servicio.cerrarSesion(sesion.sesionId());
var lecturas = servicio.lecturasDeSesion(sesion.sesionId()); // dos lecturas
```

La consola conserva los comandos I1. I3 se ofrece mediante servicio Java, sin nueva UI o API REST.

## Archivos

Rutas relativas al repositorio:

| Archivo | Función |
| --- | --- |
| `src/main/java/co/emi/trazabilidad/dominio/SesionOperacion.java` | Record de sesión y coherencia estructural. |
| `src/main/java/co/emi/trazabilidad/dominio/TipoOperacion.java` | Tres tipos de contexto. |
| `src/main/java/co/emi/trazabilidad/dominio/EstadoSesion.java` | Estado de sesión. |
| `src/main/java/co/emi/trazabilidad/dominio/LecturaRFID.java` | Lectura persistida, metadata inmutable y contrato I2 reutilizado. |
| `src/main/java/co/emi/trazabilidad/aplicacion/ServicioI3.java` | Casos de uso y errores. |
| `src/main/java/co/emi/trazabilidad/aplicacion/puertos/RepositorioI3.java` | Puerto de datos I3. |
| `src/main/java/co/emi/trazabilidad/aplicacion/puertos/UnidadDeTrabajo.java` | Modificado: extensión I3 compatible. |
| `src/main/java/co/emi/trazabilidad/infraestructura/persistencia/JdbcRepositorioI3.java` | SQL y mapeo de sesiones, lecturas y metadata. |
| `src/main/java/co/emi/trazabilidad/infraestructura/persistencia/JdbcUnidadDeTrabajo.java` | Modificado: reutilización del procedimiento transaccional. |
| `src/main/resources/db/migration/V3__persistir_sesiones_y_lecturas.sql` | Esquema e integridad incremental. |
| `src/test/java/co/emi/trazabilidad/aplicacion/ServicioI3Test.java` | 17 pruebas de aplicación/integración/concurrencia/rollback. |
| `src/test/java/co/emi/trazabilidad/infraestructura/persistencia/EsquemaI3Test.java` | 6 pruebas de migración e integridad SQL. |
| `src/test/java/co/emi/trazabilidad/dominio/ModeloI3Test.java` | 2 pruebas de invariantes de dominio. |
| `docs/desarrollo/incremento_i3.md` | Diseño, supuestos, evidencia y trazabilidad. |
| `README.md` | Añadido enlace a I3 y aviso de antecedentes históricos. |

V1/V2, clases de negocio I1, fuente/contrato I2, pruebas históricas y `pom.xml` permanecen intactos. Los cambios previos del usuario en `.gitignore`, `AGENTS.md`, presentación, skills y SDK se preservan.

## Verificación

- Base anterior a I3: 57 pruebas aprobadas, ejecutadas con `mvnw.cmd test`.
- Suite con I3: 57 históricas + 25 nuevas = **82 pruebas; 0 fallos; 0 errores; 0 omitidas**.
- `mvnw.cmd verify`: compilación Java 21, suite completa y empaquetado exitosos.
- Verificación final independiente: `mvnw.cmd clean verify`, BUILD SUCCESS, 12/09/2026 20:57:38 America/Bogota; 82/0/0/0, cotejado con los 10 XML de Surefire. Log local: `tmp/i3-clean-verify.log`; reportes regenerables: `target/surefire-reports/`.
- JAR final ejecutado sobre una base temporal nueva: V1, V2 y V3 aplicadas, tablas I3 vacías y arranque correcto. Log local: `tmp/i3-jar-smoke.log`. No se migraron bases operativas del usuario.
- Pruebas con SQLite temporal real y migraciones de producción, desde cero y desde V2 con datos/historial. Los checksums V1/V2 permanecen iguales.
- Fallo SQL deliberado al insertar la segunda fila de metadata, después de lectura y primera metadata: rollback completo; conserva la lectura previa confirmada.
- Reapertura, orden nanosegundo/empates, mismo EPC en misma sesión, origen, metadata, contexto, cierre inválido, dos cierres concurrentes y dos escrituras idénticas concurrentes verificados.
- Integración FuenteSimulada → callback → ServicioI3 → SQLite verificada.
- El test que persiste `RF_REAL` usa un valor construido en software. No hay lectura física ni evidencia RF_REAL.
- Los avisos de Shade sobre manifiestos/licencias/module-info no impiden el empaquetado; no se modifican dependencias para este incremento.

## Trazabilidad y deuda

| ID | Evidencia I3 | Recomendación, sin editar control formal |
| --- | --- | --- |
| RF-002 | Entrada simulada conectada a almacenamiento, repeticiones y recuperación. | Mantener EN DESARROLLO: integración real y operación completa pendientes. |
| RF-003 | Fecha/hora/punto/actor y tipo de sesión persistidos. | Recomendar APROBADO → EN DESARROLLO; tipo de sesión no sustituye tipo de evento. |
| DAT-001 | Lecturas crudas independientes, sin deduplicación ni eventos. | Mantener EN DESARROLLO; falta relación/capa de eventos. |
| DAT-002 | Origen preservado y restringido en DB para ambos valores. | Mantener EN DESARROLLO; estructuras futuras y RF_REAL físico pendientes. |
| DAT-003 | Dos entidades mínimas persistidas adicionales. | Mantener EN DESARROLLO; modelo completo del MVP pendiente. |

Siguiente incremento recomendado: **motor de eventos + deduplicación**, con diseño de reglas y trazabilidad antes de implementar. Resolver consumo de lecturas, contexto suficiente, asociación EPC-equipo históricamente correcta, ventana/regla de deduplicación y relación evidencia-evento. No inferir dirección por antena ni inventar umbrales operativos. Mantener `LecturaRFID != EventoOperativo`.
Después siguen última lectura/historial operativo por equipo, verificación, sustitución temporal, contingencia y adaptador real cuando P1 lo permita. Listar lecturas de sesión en I3 no implementa RF-004 ni historial operativo por equipo. La prohibición INACTIVA continúa pendiente.
