# I5 — Consulta de última lectura e historial por equipo

Estado: **I5 CERRADO técnicamente**, 13/09/2026. Evidencia SOFTWARE / SIMULACION. No cierra el MVP, la fase formal de Pruebas ni RF_REAL.

## Autoridad e inspección

Se mantiene la Propuesta aprobada y control v1.9.2: Maestro §4 p.2, Matriz §2 p.2 (RF-004), Registro §2 p.2 (DEC-014/015/022), docs/FUENTES_DE_VERDAD.md y el encargo posterior I5. El control describe incrementos anteriores; el encargo reconoce I1–I4 cerrados y 134 pruebas. No se reescriben los PDF ni estados formales por inferencia.
Se inspeccionaron Equipo, AsignacionEtiqueta, LecturaRFID, EventoOperativo, EvidenciaEvento, ServicioI1/I3/I4, puertos, JDBC, V1–V4 y pruebas. Base ejecutada: 134 pruebas, 0 fallos/errores/omitidas (tmp/i5-baseline.log).

## Diseño congelado

- Identidad principal: equipo_id existente; resolución auxiliar por código institucional literal mediante el mapeo I1. No se duplican cuatro APIs por código ni se crea otra identidad.
- Lecturas: todas las atribuibles por EPC literal e intervalo histórico [fecha_inicio,fecha_fin), con fin null abierto. Se exige una única asociación aplicable, como I4; solapamientos ambiguos quedan excluidos de cualquier atribución arbitraria. No se consulta solo la etiqueta vigente.
- No se aplican criterios de elegibilidad de eventos a evidencia cruda: lecturas sin sesión, en VERIFICACION, en sesión cerrada o sin evento pertenecen al historial si la atribución histórica es válida. No se descartan timestamps futuros por comparación con un reloj de consulta: se muestra lo persistido, sin acreditar un movimiento.
- Última lectura: máximo (timestamp_segundos,timestamp_nanos,lectura_id), no orden de inserción ni último evento. Historial ascendente por las mismas claves. Una lectura insertada tarde se ubica según su timestamp.
- Último evento: máximo (timestamp operativo,evento_id) entre eventos cuyo equipo_id ya quedó fijado por I4. No reinterpretar los eventos consultando la asociación actual. Historial ascendente.
- LecturaAtribuida y EventoConContexto son proyecciones de aplicación, no entidades/tablas Historial. La primera conserva lectura original y AsignacionEtiqueta usada; la segunda, evento, SesionOperacion y conjunto de OrigenDatos de sus fuentes. La consulta detallada de respaldos reutiliza EvidenciaEvento. LecturaRFID no cambia.
- Equipo existente sin evidencia: Optional.empty/listas vacías. ID inválido: DATO_INVALIDO; equipo inexistente: NO_ENCONTRADO. Las consultas corren en una conexión/transacción del patrón existente; ruta I5 protegida con PRAGMA query_only. No escriben ni invocan el motor de eventos.
- Joins y agrupación en memoria de filas de un resultado evitan consultas N+1 de metadata, sesiones y origen. Consultas de último resultado limitan antes de unir metadata. Sin paginación compleja ni copias persistidas.

## Decisión de V5

Se compararon planes con V1–V4 en SQLite aislado y dos índices candidatos (tmp/i5-planes-preliminares.json). Sin ellos: lectura recorre ix_lectura_sesion_tiempo y evento recorre toda la tabla, con ordenación temporal. Con índices: búsqueda de lectura por EPC/rango temporal y búsqueda ordenada de eventos por equipo.
V5 añade exclusivamente ix_lectura_epc_tiempo(epc,timestamp_segundos,timestamp_nanos,lectura_id) e ix_evento_equipo_tiempo(equipo_id,timestamp_segundos,timestamp_nanos,evento_id). No hay nuevas tablas, campos ni restricciones de negocio. Se acepta la ordenación temporal al combinar varias etiquetas históricas; no se promete eliminar toda ordenación ni rendimiento físico RFID. Los planes reales se comprobaron con el driver SQLite del proyecto.
Las pruebas de esquema I4 fijan versión 4; su fixture se limita explícitamente a target 4 manteniendo las aserciones. V4→V5 tiene pruebas propias. V1–V4 son inmutables.


## API Java y significado de los resultados

```java
var consultas = new ServicioI5(new JdbcUnidadDeTrabajo(base));
var ultimaLectura = consultas.ultimaLecturaPorEquipo(equipoId); // Optional<LecturaAtribuida>
var ultimoEvento = consultas.ultimoEventoPorEquipo(equipoId);   // Optional<EventoOperativo>
var lecturas = consultas.historialLecturasPorEquipo(equipoId); // List<LecturaAtribuida>
var eventos = consultas.historialEventosPorEquipo(equipoId);   // List<EventoConContexto>
var respaldos = consultas.evidenciasDeEvento(eventoId);        // List<EvidenciaEvento>
var equipo = consultas.buscarEquipoPorCodigo(codigo);          // Optional<Equipo>
```

`base` debe estar migrada mediante el arranque existente. La búsqueda auxiliar por código ausente devuelve Optional.empty; no transforma un EPC en código institucional. Las cuatro consultas por equipo y la de respaldos validan que exista la identidad solicitada. Cada llamada constituye su propia transacción: varias llamadas independientes no prometen una instantánea común ante escrituras concurrentes.

La lectura conserva ID, EPC, timestamp con nanosegundos, origen, sesión nullable y mapa completo de metadata; la proyección añade el intervalo de asociación consultado. El evento conserva su equipo, sesión, tipo, timestamp operativo, fecha de creación y lectura base. Su historial incluye el contexto persistido de sesión (incluido su estado de cierre actual) y todos los orígenes distintos de sus respaldos; un evento puede tener fuentes mixtas. Los respaldos detallados se solicitan por ID de evento. No se transforma el conjunto de orígenes en evidencia física.

Las listas y mapas devueltos son inmutables. Las lecturas desconocidas, anteriores a una asociación, en huecos o con asociación ambigua continúan persistidas, pero no se atribuyen arbitrariamente a un equipo. No se infieren ubicación, disponibilidad, dirección por antena ni estado físico actual.

## Persistencia y planes comprobados

V5 contiene únicamente dos CREATE INDEX. No añade tablas, columnas, historial duplicado, estados ni reglas de negocio. Se probó migración desde cero, repetición y actualización de una base V4 con lecturas, metadata, evento, respaldos y sesión cerrada. Los checksums anteriores y los datos se conservan.

Extractos de EXPLAIN QUERY PLAN sobre el SQL de producción, obtenidos con SQLite JDBC 3.50.3.0:

| Consulta | V4 | V5 |
| --- | --- | --- |
| Última lectura | SCAN l USING INDEX ix_lectura_sesion_tiempo | SEARCH l USING INDEX ix_lectura_epc_tiempo (epc=? AND (timestamp_segundos,timestamp_nanos)>(?,?)) |
| Último evento | SCAN evento_operativo; USE TEMP B-TREE FOR ORDER BY | SEARCH evento_operativo USING INDEX ix_evento_equipo_tiempo (equipo_id=?) |
| Historial de eventos | Evaluado en V5 | SEARCH e mediante ix_evento_equipo_tiempo |

La notación del plan no sustituye el predicado inclusivo >= del SQL. Sigue existiendo ordenación temporal para combinar intervalos/etiquetas y metadata. Los índices justifican búsquedas selectivas, no un benchmark de latencia ni garantía de desempeño a cualquier volumen.

Cada consulta principal de repositorio usa una sentencia preparada, independientemente del número de resultados; se comprobó con 20 lecturas, tres entradas de metadata por lectura y 20 respaldos. El servicio agrega una consulta fija para comprobar existencia, sin N+1. El historial completo se materializa en memoria; paginación queda como mejora futura si el volumen medido lo exige.

La ruta I5 activa PRAGMA query_only durante la ejecución de consultas. Lo restaura antes de commit/rollback porque el controlador inicia otro BEGIN IMMEDIATE al finalizar una transacción; dejarlo activo produjo SQLITE_READONLY en una ejecución intermedia. La corrección se limita a I5 y preserva excepciones originales con fallos de restauración suprimidos. Se comprobó igualdad byte a byte del archivo antes/después de ejecutar todas las consultas repetidamente, ausencia de eventos nuevos y rechazo de INSERT con query_only activo. Conserva el modo transaccional existente: una consulta puede competir por la reserva de escritura de SQLite; no se promete concurrencia de lectura ilimitada.

## Archivos creados y modificados

Rutas relativas a la raíz del proyecto.

Creados:

- src/main/java/co/emi/trazabilidad/aplicacion/ServicioI5.java
- src/main/java/co/emi/trazabilidad/aplicacion/consultas/LecturaAtribuida.java
- src/main/java/co/emi/trazabilidad/aplicacion/consultas/EventoConContexto.java
- src/main/java/co/emi/trazabilidad/aplicacion/puertos/RepositorioI5.java
- src/main/java/co/emi/trazabilidad/infraestructura/persistencia/JdbcRepositorioI5.java
- src/main/resources/db/migration/V5__indexar_consultas_por_equipo.sql
- src/test/java/co/emi/trazabilidad/aplicacion/ServicioI5Test.java
- src/test/java/co/emi/trazabilidad/aplicacion/ConsultasI5Test.java
- src/test/java/co/emi/trazabilidad/aplicacion/AislamientoI5Test.java
- src/test/java/co/emi/trazabilidad/infraestructura/persistencia/EsquemaI5Test.java
- docs/desarrollo/incremento_i5.md

Modificados:

- src/main/java/co/emi/trazabilidad/aplicacion/puertos/UnidadDeTrabajo.java: extensión aditiva ejecutarI5.
- src/main/java/co/emi/trazabilidad/infraestructura/persistencia/JdbcUnidadDeTrabajo.java: ejecución protegida I5 con la misma transacción.
- src/test/java/co/emi/trazabilidad/infraestructura/persistencia/EsquemaI4Test.java: fixture explícito target 4, conservando sus aserciones históricas; V5 tiene pruebas propias.
- src/test/java/co/emi/trazabilidad/aplicacion/AislamientoI4Test.java: incluir proyecciones Java puras al compilar todos los puertos sin dependencias externas; no se elimina la prueba de aislamiento.
- README.md: acceso al estado y documentación I5.

Comparación SHA-256 frente a la instantánea previa: solo cambian los cuatro archivos Java anteriores (más README al documentar). V1–V4, dominio, servicios/repositorios I1–I4 y pom.xml se preservan. No se modifican PDF ACTUAL, decisiones, anexos, soporte ni archivos de presentación. Los cambios previos del usuario permanecen. No se realiza commit ni push.

## Pruebas y verificación final

Comando reproducible: `.\mvnw.cmd clean verify` con Java 21.

| Grupo | Pruebas |
| --- | ---: |
| Históricas I1–I4 | 134 |
| ServicioI5Test | 27 |
| EsquemaI5Test | 5 |
| ConsultasI5Test | 2 |
| AislamientoI5Test | 1 |
| Nuevas I5 | 35 |
| Total | **169** |
| Fallos / errores / omitidas | **0 / 0 / 0** |

BUILD SUCCESS, 13/09/2026 12:11:58 -05:00, 31,006 s. Compilación limpia, pruebas y empaquetado completados. Maven Shade informa recursos LICENSE/NOTICE/MANIFEST compartidos entre dependencias, sin impedir el empaquetado. No se cambiaron dependencias.

Cobertura: inexistencia/vacíos/errores de entrada; sin sesión/evento; sesiones cerradas/VERIFICACION; cambio y reasignación de etiquetas; huecos, EPC desconocido y solapamientos; límites [inicio,fin) a nanosegundos; orden fuera de inserción, empates, épocas negativas y extremos de Instant; metadata completa; resultados inmutables; orígenes mixtos; contexto y respaldo; reapertura; migración e índices; cero escrituras; número fijo de consultas; aislamiento Java sin SDK.

Los valores RF_REAL usados en fixtures verifican conservación del enumerado exclusivamente. Son datos sintéticos de prueba: no son lecturas físicas ni evidencia RF_REAL del proyecto.

Evidencia local regenerable (tmp y target están excluidos de Git): tmp/i5-baseline.log, tmp/i5-fixed-test.log, tmp/i5-clean-verify.log, tmp/i5-before-hashes.json, tmp/i5-changed-existing.json y target/surefire-reports. El informe conserva resultados y las pruebas versionables permiten reproducirlos sin esos archivos temporales.

## Evidencia RF-004 solicitada

Prueba `ServicioI5Test.rf004EscenarioCompletoDosEtiquetasCuatroLecturasYDosEventos`, 13/09/2026, horas UTC de ensayo, origen SIMULACION:

| Hora | Etiqueta | Lectura | Evento persistido |
| --- | --- | --- | --- |
| 08:00 | A | Conservada | INGRESO, timestamp 08:00 |
| 08:01 | A | Conservada | Respalda el mismo INGRESO; no duplica |
| 12:00 | B, tras cambio histórico A→B | Conservada | SALIDA, timestamp 12:00 |
| 12:05 | B | Conservada, sesión ya cerrada | Ninguno nuevo |

Resultado comprobado: historial de cuatro lecturas y dos eventos; última lectura B 12:05; último evento SALIDA 12:00. El INGRESO tiene dos respaldos y la SALIDA uno. Consultar no procesa la lectura de 12:05 ni altera eventos. El cambio de etiqueta preserva las lecturas históricas A. Una prueba adicional demuestra que reasignar A a otro equipo no transfiere evidencia anterior.

**Última lectura != último evento != estado físico actual del equipo.**

## Requisitos: recomendación técnica, sin editar ACTUAL

| Requisito | Recomendación | Límite |
| --- | --- | --- |
| RF-004 | IMPLEMENTADO en SOFTWARE | Consulta de evidencia persistida; sin acreditar estado físico ni RF_REAL. |
| RF-002 / RF-003 | EN DESARROLLO | El procesamiento software previo no completa la evidencia operacional/física. |
| DAT-001 | Mantener sólidamente demostrado | Preservación de identidad, cambios y reasignaciones en las consultas históricas. |
| DAT-002 | EN DESARROLLO | Estructuras y evidencia física pendientes según control. |
| DAT-003 | EN DESARROLLO | Aún faltan entidades y funciones del MVP. |

No se crea DEC ni se modifica la fase formal Diseño, la jerarquía, el presupuesto o la validación. El cierre técnico de I5 no cierra el MVP.

## Deuda y siguiente incremento

- Software: RF-005 esperados vs detectados; después sustitución temporal y contingencia. No se implementan en I5. Evaluar paginación o transacción de lectura dedicada solo ante necesidad medida.
- P1: lector UHF autorizado, unidad/firmware/runtime, configuración y compatibilidad funcional; adaptador real aún pendiente.
- P2: corroborar actor/roles, activos/rutas, geometría y condiciones de un único punto; dos antenas no determinan dirección ni validan cobertura.
- P3: umbrales tras prepruebas y antes del piloto; Pruebas formales, piloto y validación siguen pendientes.
- RF_REAL: no existe evidencia física producida por este incremento. SIMULACION y tests no demuestran desempeño RFID.

Siguiente incremento recomendado: **verificación de esperados vs detectados (RF-005)**. I5 no reveló una dependencia técnica que impida diseñarlo; su implementación requiere el siguiente encargo. Mantener la secuencia Lectura → Verificación → Reintento → Contingencia manual → Confirmación sin lecturas ficticias.
