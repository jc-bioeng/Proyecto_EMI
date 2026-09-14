# I4 — Motor de eventos y deduplicación

Estado: **I4 CERRADO técnicamente**. Implementación y clean verify: 12/09/2026; smoke test y cierre documental: 13/09/2026. Evidencia: **SOFTWARE / SIMULACION**. Control formal v1.9.2 intacto. MVP, Pruebas formales, Piloto, Validación y RF_REAL físico siguen pendientes.

## Diseño congelado antes del código y V4

Fuentes: Propuesta aprobada pp. 3, 5, 7 (alcance/simulación); Maestro v1.9.2 §4 p. 2 (pipeline); Matriz v1.9.2 §2 pp. 2–3 (RF/DAT y VAL-003); Registro v1.9.2 §2 p. 2 (DEC-014/015/017/022); `docs/FUENTES_DE_VERDAD.md`; `docs/desarrollo/incremento_i3.md`; encargo posterior I4 del usuario. No existe en estas fuentes una ventana de segundos ni una política de admisión retrospectiva de eventos de sesiones cerradas.
El control formal aún describe I3 pendiente; el encargo posterior reconoce su cierre técnico con 82 pruebas. Se conserva esa diferencia temporal, sin actualizar automáticamente PDF, estados o DEC.

1. Asociación histórica por EPC literal y `fechaInicio <= timestamp < fechaFin` (fin null = sin límite superior). El intervalo semiabierto concreta el cierre y la nueva asociación en el mismo instante de I1 sin doble atribución. Se compara `Instant`, sin redondear lecturas a milisegundos. Cero o múltiples asociaciones aplicables: sin evento. No filtrar por ACTIVA/INACTIVA ni condición de piloto sin regla aprobada.
2. Contexto elegible para nueva evidencia de evento: sesión existente ABIERTA de tipo INGRESO/SALIDA, lectura con timestamp >= inicio y <= reloj del procesamiento. No se interpreta antena, RSSI ni orden de llegada como dirección. Sesión cerrada: ninguna nueva interpretación automática, incluso para lectura dentro de su intervalo; VERIFICACION: ningún movimiento. La FK no basta. I3 sigue conservando todo.
3. `EventoOperativo` inmutable: ID, equipo, sesión, tipo INGRESO/SALIDA, timestamp operativo, fecha de creación y lectura base explícita. El origen no es un escalar del evento: cada lectura fuente conserva su `OrigenDatos`; un conjunto mixto se audita como tal, nunca se presenta como RF_REAL homogéneo.
4. UNIQUE(sesion_id, equipo_id, tipo_evento). I4 trata cada sesión como una operación delimitada por tipo. Un segundo movimiento equivalente requiere otra sesión; es un supuesto técnico explícito del encargo, no confirmación institucional. No se inventan ventanas temporales.
5. Relación `evento_lectura`: lectura única globalmente, evento y asignación histórica utilizada. Un evento tiene 1..N respaldos; una lectura 0..1 evento. FK diferida desde evento a su vínculo de lectura base impide confirmar eventos huérfanos. No borrar ni actualizar eventos/vínculos.
6. Timestamp operativo = menor (timestamp, lectura_id) entre lecturas elegibles persistidas del mismo equipo/sesión disponibles al crear el evento. Esa lectura base se vincula aunque se haya solicitado procesar otra posterior. Su identidad y timestamp se conservan. Una lectura cronológicamente anterior persistida después puede añadirse como evidencia elegible sin modificar el timestamp histórico: el evento describe la evidencia disponible en su creación, no un mínimo global sobre datos futuros. FechaCreacion = Clock/UTC, distinta del timestamp operativo. Procesar una sesión ordena por timestamp e ID y toma un reloj por transacción.
7. Idempotencia: consultar primero vínculo existente; devolver evento original sin reinterpretar equipo ni exigir que la sesión siga abierta. Una lectura nueva elegible se vincula al evento equivalente; nunca crea otro. Resultados de no elegibilidad son transitorios y no generan nuevas entidades. Se pueden reevaluar, sin reintentos automáticos.
8. Aplicación y DB: servicio I4, puerto y JDBC reutilizando la transacción IMMEDIATE de la UnidadDeTrabajo. Evento + vínculos atómicos; procesamiento completo de sesión en una transacción y snapshot. UNIQUE y PK protegen contra concurrencia. No se crea UI, REST, historial por equipo, verificación, contingencia, sustitución ni hardware.

## Inspección inicial

Se revisaron AsignacionEtiqueta, EPC literal, consultas vigentes, SesionOperacion, LecturaRFID, timestamps, RepositorioI1/I3, UnidadDeTrabajo, ErrorAplicacion, JDBC, V1–V3 y pruebas. Suite base ejecutada: 82/0/0/0 (`tmp/i4-baseline.log`).
La prueba histórica EsquemaI3Test fija exactamente tres migraciones y las tablas de I3. Se limitó su fixture a target 3 para conservar sus seis pruebas y aserciones de I3; el esquema más reciente y la transición V3→V4 tienen pruebas propias. No se modifican V1–V3 ni las reglas de I1–I3.


## Implementación y API Java

ServicioI4 usa UnidadDeTrabajo.ejecutarI4 y RepositorioI4. JDBC reutiliza la misma conexión y transacción IMMEDIATE del proyecto. Las consultas de lectura/sesión se delegan al repositorio I3 sobre esa conexión, sin transacciones anidadas ni cambios en sus reglas. La caché de contexto/asociaciones solo vive dentro de una transacción.

- `procesarLectura(id)`: procesa la lectura indicada; si crea evento, incorpora también su lectura base más antigua disponible. Por ello una llamada puede vincular dos lecturas. No dispara el motor desde callbacks.
- `procesarSesion(id)`: procesa el snapshot completo ordenado por timestamp e ID, con una lectura del reloj y una transacción para todo el lote. Un fallo revierte todas las escrituras de esa llamada; los eventos previamente confirmados permanecen.
- `consultarEvento(id)`: Optional; un ID válido ausente devuelve vacío.
- `eventosDeSesion(id)`: consulta delimitada a una sesión, orden por timestamp e ID.
- `evidenciasDeEvento(id)`: lecturas originales, ID de asignación histórica utilizado y fecha de vinculación, ordenadas por timestamp e ID. Incluye origen y metadata originales.

Los ID no positivos producen DATO_INVALIDO. Una lectura/sesión requerida o un evento requerido para consultar respaldos ausente produce NO_ENCONTRADO. Una referencia corrupta de sesión se informa sin inventar contexto. Las escrituras SQL que violan integridad producen CONFLICTO; otros errores SQL producen PERSISTENCIA, conforme al patrón existente.

| Estado de resultado | Significado |
| --- | --- |
| EVENTO_CREADO | Evento y respaldos iniciales confirmados. |
| EVIDENCIA_AGREGADA | Evento equivalente existente; se agrega una lectura elegible. |
| LECTURA_YA_PROCESADA | Vínculo confirmado previamente; sin escritura ni reinterpretación. |
| CONTEXTO_INSUFICIENTE | SIN_SESION; no se elige otra sesión abierta por inferencia. |
| LECTURA_NO_ELEGIBLE | SESION_CERRADA, SESION_VERIFICACION, ANTERIOR_A_SESION, LECTURA_FUTURA o RELOJ_ANTERIOR_A_EVENTO. |
| SIN_ASOCIACION_VALIDA | ASOCIACION_AUSENTE o ASOCIACION_AMBIGUA. |

La fecha de vinculación distingue cada incorporación posterior de la creación del evento. No puede preceder al timestamp de lectura ni a la creación del evento. Si el reloj retrocede, una nueva incorporación anterior a la creación se rechaza; una lectura ya vinculada sigue siendo idempotente. Los rechazos no se persisten como entidades: pueden reevaluarse después, sin reintento automático.

### Ejemplo de uso

Con base migrada, equipo/etiqueta asociados por I1 y una sesión abierta creada por I3:

```java
var motor = new ServicioI4(new JdbcUnidadDeTrabajo(base), Clock.systemUTC());
var fuente = new FuenteSimulada(Clock.systemUTC());
fuente.iniciar(l -> servicioI3.persistirLectura(l, sesion.sesionId()));
fuente.emitir(Collections.nCopies(20, "0001"));
fuente.detener();
var resultados = motor.procesarSesion(sesion.sesionId());
var eventos = motor.eventosDeSesion(sesion.sesionId());
var respaldos = motor.evidenciasDeEvento(eventos.getFirst().eventoId());
servicioI3.cerrarSesion(sesion.sesionId());
```

La consola conserva I1; I4 se ofrece como servicio Java, sin nueva GUI o API REST. El ejemplo no autoriza un punto físico, un actor institucional ni hardware.

## Base de datos — V4

`V4__persistir_eventos_y_respaldo.sql` crea exclusivamente:

| Tabla | Integridad |
| --- | --- |
| evento_operativo | PK positiva; FK a equipo, sesión y lectura base; tipo INGRESO/SALIDA; timestamp y creación separados en segundos/nanos INTEGER; creación >= timestamp; UNIQUE(sesion_id,equipo_id,tipo_evento). |
| evento_lectura | lectura_id PK/FK: máximo un evento por lectura; FK evento y asignación histórica; fecha de vinculación en segundos/nanos; UNIQUE(evento_id,lectura_id). |

Una FK compuesta **DEFERRABLE INITIALLY DEFERRED** desde `(evento_id,lectura_base_id)` al vínculo `(evento_id,lectura_id)` exige el respaldo base al COMMIT. Permite insertar evento y luego respaldos en una transacción, pero impide confirmar un evento huérfano o sustituir su lectura base por otra arbitraria. Las demás relaciones usan ON DELETE RESTRICT; no hay cascadas destructivas.

Las tablas son STRICT y usan NOT NULL, CHECK de ID/rango Instant/nanosegundos/tipo/tiempo. Índices adicionales: `ix_evento_sesion_tiempo` e `ix_evento_lectura_asignacion`; los UNIQUE también indexan deduplicación y recuperación de respaldos.

Triggers implementados:

- `validar_contexto_evento`: sesión abierta, tipo coincidente y timestamp idéntico a la lectura base de esa sesión, no anterior al inicio.
- `validar_respaldo_evento`: misma sesión/equipo, EPC literal e intervalo histórico único; fecha de vinculación compatible. Impide nuevos respaldos tras el cierre.
- `impedir_borrado_evento`, `impedir_cambio_evento`, `impedir_borrado_evento_lectura`, `impedir_cambio_evento_lectura`: preservan eventos y respaldos.
- `impedir_reemplazo_evento`, `impedir_reemplazo_respaldo`: bloquean también INSERT OR REPLACE, sin depender de recursive_triggers.

SQLite compara pares INTEGER, sin REAL ni redondeo. Se ajustan cociente y resto de los milisegundos negativos de I1/I3 para mantener exactamente la semántica de Instant. El servicio selecciona la primera evidencia disponible y consulta el Clock; los triggers protegen la integridad de referencias y tiempos almacenados. Esto no acredita autenticidad institucional del actor, del reloj ni de la procedencia física.

## Archivos creados/modificados

Rutas relativas a C:/Proyecto_EMI:

| Acción | Archivo | Función |
| --- | --- | --- |
| Creado | src/main/java/co/emi/trazabilidad/dominio/TipoEvento.java | Tipos de movimiento. |
| Creado | src/main/java/co/emi/trazabilidad/dominio/EventoOperativo.java | Entidad inmutable. |
| Creado | src/main/java/co/emi/trazabilidad/dominio/EvidenciaEvento.java | Lectura, asignación y fecha de respaldo. |
| Creado | src/main/java/co/emi/trazabilidad/aplicacion/ResultadoProcesamiento.java | Resultado estructurado. |
| Creado | src/main/java/co/emi/trazabilidad/aplicacion/ServicioI4.java | Elegibilidad, eventos, deduplicación e idempotencia. |
| Creado | src/main/java/co/emi/trazabilidad/aplicacion/puertos/RepositorioI4.java | Puerto de datos. |
| Modificado | src/main/java/co/emi/trazabilidad/aplicacion/puertos/UnidadDeTrabajo.java | Extensión compatible ejecutarI4. |
| Creado | src/main/java/co/emi/trazabilidad/infraestructura/persistencia/JdbcRepositorioI4.java | SQL parametrizado e historial. |
| Modificado | src/main/java/co/emi/trazabilidad/infraestructura/persistencia/JdbcUnidadDeTrabajo.java | Ruta I4 al procedimiento transaccional existente. |
| Creado | src/main/resources/db/migration/V4__persistir_eventos_y_respaldo.sql | Tablas, restricciones y triggers. |
| Creado | src/test/java/co/emi/trazabilidad/aplicacion/ServicioI4Test.java | 36 pruebas funcionales, negativas, temporales, concurrentes y rollback. |
| Creado | src/test/java/co/emi/trazabilidad/aplicacion/AislamientoI4Test.java | 1 prueba de compilación sin JDBC/Flyway/SDK. |
| Creado | src/test/java/co/emi/trazabilidad/dominio/ModeloI4Test.java | 3 pruebas de invariantes. |
| Creado | src/test/java/co/emi/trazabilidad/infraestructura/persistencia/EsquemaI4Test.java | 12 pruebas de migración e integridad SQL. |
| Modificado | src/test/java/co/emi/trazabilidad/infraestructura/persistencia/EsquemaI3Test.java | Solo fixture target 3; seis pruebas y aserciones históricas conservadas. |
| Creado | docs/desarrollo/incremento_i4.md | Diseño, evidencia y trazabilidad. |
| Modificado | README.md | Enlace a I4 y estado técnico posterior. |

No se modificaron los archivos de negocio I1/I2/I3, V1–V3 ni pom.xml. Se cotejaron sus hashes con la captura anterior a I4 (`tmp/i4-before-hashes.json`). Entre los archivos fuente preexistentes solo cambiaron UnidadDeTrabajo, JdbcUnidadDeTrabajo y el fixture EsquemaI3Test. Los cambios previos en AGENTS.md, .gitignore, SDK, presentación y skills se preservan. No se editaron PDF ACTUAL ni se hicieron commits/publicaciones.

## Verificación reproducible

| Conjunto | Pruebas | Fallos | Errores | Omitidas |
| --- | ---: | ---: | ---: | ---: |
| Históricas I1–I3 | 82 | 0 | 0 | 0 |
| Nuevas I4 | 52 | 0 | 0 | 0 |
| Total final | **134** | **0** | **0** | **0** |

- Suite inicial: `mvnw.cmd test`, 82/0/0/0 (`tmp/i4-baseline.log`).
- Suite ampliada previa: 132/0/0/0; se añadieron después las dos pruebas finales de referencia corrupta y aislamiento.
- **Final: `mvnw.cmd clean verify`, BUILD SUCCESS**, 12/09/2026 22:10:55 America/Bogota. 134/0/0/0, cotejado con los 14 XML de `target/surefire-reports/`. Log local: `tmp/i4-clean-verify.log`.
- El 13/09/2026 se comprobó que los fuentes no tenían modificaciones posteriores al build. Smoke del JAR final sobre base temporal nueva: V1–V4 aplicadas, sin migrar bases operativas. Log: `tmp/i4-jar-migration.log`.
- Smoke Java contra las clases del JAR: creación/asociación I1, sesión y fuente simulada I3, procesamiento I4, consultas de respaldo, reejecución idempotente y cierre de sesión. Log: `tmp/i4-smoke.log`; ejecutor reproducible local: `tmp/I4Smoke.java`.
- Avisos de Shade por manifiestos/licencias/module-info; no impidieron empaquetado ni smoke. No se alteraron dependencias.

### Evidencia de VAL-003

**20 callbacks simulados → 20 LecturaRFID persistidas → 0 eventos antes de invocar el motor → 1 EventoOperativo SALIDA → 20 respaldos.** Reejecución: 0 eventos nuevos. Todas las lecturas originales permanecen intactas. Ver `veinteCallbacksConservanVeinteLecturasYUnEventoSoloAlInvocarMotor` y smoke del JAR.

Pruebas reales sobre SQLite temporal verifican además:

- Reasignación EPC: lectura anterior al cambio resuelve el equipo histórico; en el límite exclusivo y después resuelve el nuevo. Huecos/solapamientos no producen interpretación arbitraria.
- Dos hilos y conexiones independientes procesan lecturas idénticas o la misma lectura: un evento. UNIQUE/PK/triggers también se prueban con SQL directo.
- VERIFICACION, ausencia de sesión, cierre, timestamp incompatible, EPC desconocido y referencia corrupta no crean movimientos.
- Metadata idéntica de antena/RSSI en sesiones INGRESO y SALIDA produce tipos definidos exclusivamente por sesión.
- Primera lectura disponible y empate por ID; evidencia anterior recibida después no reescribe el evento.
- Fallo real tras insertar evento y antes del vínculo: rollback completo. Fallo al segundo respaldo: revierte evento y primer vínculo. Fallo al segundo evento del lote: revierte el lote. Fallo al ampliar un evento existente: preserva lo previamente confirmado.
- V4 desde cero y desde V3 con datos, metadata, sesiones cerradas y checksums previos; FK diferida impide COMMIT huérfano.
- Orígenes mixtos se conservan por lectura; el valor RF_REAL construido en test es exclusivamente evidencia SOFTWARE.

## Trazabilidad recomendada

No se actualizan automáticamente los documentos ACTUAL. Las recomendaciones son:

| ID | Evidencia y límite | Estado recomendado |
| --- | --- | --- |
| RF-002 | Captura/persistencia intacta y consumo explícito; fuente real pendiente. | Mantener EN DESARROLLO. |
| RF-003 | Movimiento, equipo, sesión, punto, actor y tiempos; reglas institucionales/P2 pendientes. | EN DESARROLLO, con avance técnico sustancial. |
| RF-004 | Consultas por sesión/evento sirven a auditoría; no última lectura ni historial completo por equipo. | Mantener APROBADO; pendiente de I5. |
| DAT-001 | Lectura/evento separados con vínculo explícito y deduplicación solo de eventos. | Recomendar IMPLEMENTADO para separación lectura/evento en software; no equivale a validación física. |
| DAT-002 | Origen por respaldo, mezcla auditable; entidades futuras y RF físico pendientes. | Mantener EN DESARROLLO. |
| DAT-003 | Evento y relación materializados; modelo mínimo todavía incompleto. | Mantener EN DESARROLLO. |
| VAL-003 | Cero duplicados en repetición, reejecución, concurrencia y SQL. | Recomendar EN DESARROLLO, con criterio software verificado; no cerrar validación/piloto. |

## Deuda restante

- **Software:** I5 de última lectura + historial operativo por equipo (RF-004), con asociación histórica y origen. Posteriormente verificación, sustitución temporal y contingencia. Admisión retrospectiva de sesiones cerradas y anulación/reversión de eventos requieren diseño explícito futuro. Antes del piloto, contrastar el supuesto de sesión como operación delimitada con los flujos/roles aprobados.
- **P1 hardware:** lector autorizado, compatibilidad funcional SDK/runtime/unidad/configuración y EPC físico reproducible pendientes. Sin Chainway ni nueva inspección estática.
- **P2 punto físico:** confirmar único punto, actores, rutas, geometría y reglas operativas. Texto libre de punto/actor no acredita permisos ni cobertura.
- **P3 criterios:** después de prepruebas y antes del piloto; I4 no fija metas RF.
- **RF_REAL:** ninguna evidencia física nueva; no se acreditan alcance, interferencia, omisiones, metal ni lecturas externas.

**Siguiente incremento recomendado: I5 — consulta de última lectura + historial operativo por equipo (RF-004).** Mantener LecturaRFID != EventoOperativo y la secuencia formal del proyecto.
