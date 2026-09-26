# I6–I8 — software independiente de hardware

## Alcance autorizado y diseño

Encargo del usuario del 14/09/2026. Referencia: FUENTES_DE_VERDAD v1.9.3,
Maestro §§4 y estado MVP, Matriz §2 RF-005/006/007 y §7, Registro DEC-011/014/015/022.
Los editables ACTUAL se consultaron sin modificar el control formal.

- I6: instantáneas inmutables de verificación en sesión VERIFICACION abierta.
  Una cadena por sesión; reintento explícito con los mismos esperados, sin sobrescribir
  resultados. Cada intento conserva las lecturas disponibles, su atribución histórica
  [inicio, fin), o el motivo de exclusión. Un equipo tiene un único resultado por intento.
  Orígenes derivados de evidencia, sin asignar RF_REAL a faltantes ni mezclar etiquetas.
- I7: sustitución independiente, una activa por equipo; cierre único, historial conservado.
  Origen/destino son contexto textual aportado por el usuario, sin catálogo institucional nuevo.
- I8: registro manual por equipo, con confirmación explícita y actor/fecha de confirmación.
  Omisión exige FALTANTE en el último reintento; fuera de ruta permite contexto sin sesión RFID.
  No se añade MANUAL a OrigenDatos ni se crean lecturas o eventos automáticamente.
- Nuevos puertos y unidad JDBC para I6–I8: interfaces públicas I1–I5 intactas.
  Transacciones BEGIN IMMEDIATE con la configuración BaseDatos existente.
- Fechas de operaciones nuevas en milisegundos UTC como sesiones/I1; timestamps crudos
  conservan segundos y nanos. No se modifica el stack ni V1–V5.

## Verificación

Línea base ejecutada: 169 pruebas, 0 fallos, 0 errores, 0 omitidas;
`tmp/i6-i8-baseline.log`. Suite integrada: 243/0/0/0; ejecución final y detalle a continuación.

## Límites

Evidencia SOFTWARE/SIMULACION exclusivamente. No cierra TEC-001, TEC-002,
RF_REAL, hardware, Pruebas formales, P1/P2/P3, piloto ni validación final.
No crea DEC ni modifica prioridades o estados formales de la Matriz.

## Casos de uso y decisiones mínimas

| Incremento | Entrada pública | Resultado persistido |
|---|---|---|
| I6 / RF-005 | ServicioI6.verificar, reintentar | Verificacion, VerificacionItem, EvidenciaVerificacion |
| I6 / RF-005 | consultar, historial, items, evidencias | Consulta del intento y sus respaldos |
| I7 / RF-007 | ServicioI7.abrir, cerrar | SustitucionTemporal; fechaCierre determina activa/devuelta |
| I7 / RF-007 | consultar, activa, historial | Contexto original e historial por equipo |
| I8 / RF-006 | ServicioI8.registrarOmision | Registro manual vinculado al último reintento faltante |
| I8 / RF-006 | registrarFueraDeRuta | Registro por equipo y contexto, sesión opcional |
| I8 / RF-006 | confirmar, consultar, historial | Confirmación explícita con actor/fecha, preservando el registro |

- Un conjunto de esperados corresponde a una cadena de intentos por sesión. Otra
  comparación con esperados distintos requiere otra sesión; no reescribe intentos.
- El reintento vuelve a evaluar todas las lecturas disponibles en esa sesión en una
  transacción. Conserva resultados y evidencias de intentos previos. No ejecuta hardware
  ni temporizadores: el llamador solicita nuevas lecturas y luego invoca reintentar.
- Las lecturas anteriores a la sesión o posteriores al instante del intento se conservan
  como evidencia excluida. La sesión debe estar abierta para crear intentos. Se permite
  consultar todo el historial después del cierre. El reloj de operación usa milisegundos;
  una lectura con nanos posteriores al corte puede entrar en un intento posterior.
- EPC y origen no se duplican en el item: se obtienen de cada LecturaRFID enlazada por
  verificacion_lectura, con equipo/asignacion históricos. Así se conservan varios EPC de
  un mismo equipo y el origen individual, sin declarar RF_REAL para un faltante.
- EPC sin asociación única no implica equipo NO_ESPERADO: permanece como evidencia
  SIN_ASOCIACION/ASOCIACION_AMBIGUA. No se impone una nueva regla sobre INACTIVA.
- Sustitución: una activa por equipo, índice único parcial y devolución una sola vez.
  Otra apertura conserva el historial y no puede retroceder antes de una devolución.
  Origen/destino/responsable son valores literales; no confirman actores institucionales.
- Omisión: no se permite registrar dos contingencias para el mismo equipo/reintento.
  Si aparece otro intento antes de confirmar, se exige revisar su resultado; el registro
  anterior permanece sin confirmación y no se borra. Confirmar no crea EventoOperativo.
- Fuera de ruta: declaración explícita del caso de uso, identificada por ausencia de
  verificacionId y contexto obligatorio. No añade clasificación permanente al equipo.
- Una confirmación puede completarse después de cerrar la sesión: audita la confirmación
  manual, no habilita ni infiere un movimiento en la sesión cerrada.
- Restricciones/índices/triggers protegen referencias, unicidad, cierres y actualizaciones.
  El servicio construye el resultado completo en una transacción. Los puertos de escritura
  son internos a los casos de uso: no constituyen una API para editar informes a mano.

### Uso desde Java

Las funcionalidades se exponen mediante servicios, siguiendo el estilo de I3–I5.
No se añade frontend ni nuevos comandos de consola por este encargo.

```java
var base = new BaseDatos(rutaBase);
base.migrar();
var unidadMvp = new JdbcUnidadDeTrabajoMvp(base);
var verificacion = new ServicioI6(unidadMvp, Clock.systemUTC());
var sustitucion = new ServicioI7(unidadMvp, Clock.systemUTC());
var contingencia = new ServicioI8(unidadMvp, Clock.systemUTC());
// Reutilizar ServicioI5.buscarEquipoPorCodigo para seleccionar equipoId por código.
// ServicioI3 conserva la sesión y las lecturas suministradas por FuenteLecturasRFID.
```

## Migraciones añadidas

- V6__persistir_verificaciones.sql: verificacion, verificacion_item y verificacion_lectura.
- V7__persistir_sustituciones_temporales.sql: sustitucion_temporal.
- V8__persistir_contingencias_manuales.sql: contingencia_manual.

V1–V5 permanecen intactas. Las nuevas migraciones no escriben en las tablas anteriores.
Pruebas desde cero, V5 con datos e historial, V6 y V7; migración repetida e integridad
SQLite. El ensayo V5→V8 coteja todos los campos de las ocho tablas previas y checksums
Flyway V1–V5. No se migró la base de trabajo del usuario en data/; los ensayos usan TempDir.

## Pruebas añadidas

| Clase | Pruebas | Cobertura principal |
|---|---:|---|
| ServicioI6Test | 22 | Comparación, vacíos, repeticiones, desconocidos, asociación histórica, reintentos, concurrencia, rollback |
| ServicioI7Test | 16 | Apertura, devolución, activa, historial, validación, concurrencia, rollback, reapertura de base |
| ServicioI8Test | 17 | Omisión/reintento, fuera de ruta, confirmación, historial, validación, concurrencia, rollback, ninguna lectura sintética |
| LogicaMvpTest | 8 | Lógica sin JDBC, asociación ambigua, límites temporales, modelos |
| MigracionMvpTest | 8 | Cero/V5/V6/V7→V8, checksums, datos previos, integridad, SQL directo, tiempos negativos |
| IntegracionMvpTest | 3 | Flujo I1–I8 SOFTWARE/SIMULACION, recuperación por reintento, consultas sin escritura |
| **Nuevas** | **74** | **0 fallos, 0 errores, 0 omitidas** |
| Anteriores | 169 | Todas conservadas y aprobadas |
| **Total** | **243** | **0 fallos, 0 errores, 0 omitidas** |

El único archivo previo modificado es EsquemaI5Test.java: dos preparaciones se fijan a
Flyway target(5), igual que los fixtures históricos I3/I4. Se conservan todas sus
aserciones exactas de cinco migraciones, nueve tablas, índices, datos y checksums.
No se eliminó, ignoró ni debilitó ninguna prueba. Los otros ensayos I5 siguen ejecutándose
sobre la base actual V8. No se modificó ningún archivo de producción anterior ni pom.xml.

### Evidencia end-to-end

IntegracionMvpTest.softwareSimuladoIntegraI1AI8SinConvertirVerificacionOManualEnMovimiento:
20 callbacks SIMULACION de ingreso → 20 lecturas → 1 evento → 20 respaldos.
Otra sesión VERIFICACION recibe A, A, C y EPC desconocido; resultado A DETECTADO,
B FALTANTE, C NO_ESPERADO. Otro callback A y reintento conservan B FALTANTE.
Contingencia B y confirmación explícita, sustitución y devolución B, y contingencia
fuera de ruta por código/equipo. Total final: 25 lecturas originales, un evento,
dos verificaciones, dos contingencias. Los registros se vuelven a consultar tras
reabrir la base. Ninguna operación I6–I8 crea lecturas ni eventos de movimiento.

## Requisitos y pendientes

RF-005, RF-007 y RF-006 implementados y verificados técnicamente en SOFTWARE/SIMULACION
por sus casos de uso, persistencia y pruebas anteriores. Esta evaluación no modifica
los estados APROBADO del control formal v1.9.3 ni declara cerrada la fase Pruebas.

No se consideran cerrados TEC-001 ni TEC-002: falta una fuente real compatible y hardware
UHF autorizado. RF-002/RF-003, DAT-002/DAT-003, VAL/OPE y los demás requisitos no se cierran
por inferencia de este incremento. RF_REAL, distancias, interferencias, porcentaje físico
de lectura, Pruebas formales, piloto y validación final continúan pendientes.
MVP completo NO CERRADO por la integración real y condiciones formales restantes.

Sin regresiones conocidas en la suite ejecutada. P1/P2 deben resolver hardware, actores,
regla operativa y configuración; P3 y validación física siguen su secuencia vigente.
El trabajo no introduce RF_REAL falsa, adaptador simulado como real, integración AM,
GPS/RTLS, autenticación, frontend, dependencias ni ampliación de alcance.

## Inventario exacto de archivos creados

- `docs/desarrollo/incrementos_i6_i8.md`
- `src/main/java/co/emi/trazabilidad/aplicacion/ServicioI6.java`
- `src/main/java/co/emi/trazabilidad/aplicacion/ServicioI7.java`
- `src/main/java/co/emi/trazabilidad/aplicacion/ServicioI8.java`
- `src/main/java/co/emi/trazabilidad/aplicacion/ValidacionMvp.java`
- `src/main/java/co/emi/trazabilidad/aplicacion/puertos/RepositorioI6.java`
- `src/main/java/co/emi/trazabilidad/aplicacion/puertos/RepositorioI7.java`
- `src/main/java/co/emi/trazabilidad/aplicacion/puertos/RepositorioI8.java`
- `src/main/java/co/emi/trazabilidad/aplicacion/puertos/UnidadDeTrabajoMvp.java`
- `src/main/java/co/emi/trazabilidad/dominio/ContingenciaManual.java`
- `src/main/java/co/emi/trazabilidad/dominio/EvidenciaVerificacion.java`
- `src/main/java/co/emi/trazabilidad/dominio/ResultadoVerificacion.java`
- `src/main/java/co/emi/trazabilidad/dominio/SustitucionTemporal.java`
- `src/main/java/co/emi/trazabilidad/dominio/Verificacion.java`
- `src/main/java/co/emi/trazabilidad/dominio/VerificacionItem.java`
- `src/main/java/co/emi/trazabilidad/infraestructura/persistencia/JdbcRepositorioI6.java`
- `src/main/java/co/emi/trazabilidad/infraestructura/persistencia/JdbcRepositorioI7.java`
- `src/main/java/co/emi/trazabilidad/infraestructura/persistencia/JdbcRepositorioI8.java`
- `src/main/java/co/emi/trazabilidad/infraestructura/persistencia/JdbcSoporteMvp.java`
- `src/main/java/co/emi/trazabilidad/infraestructura/persistencia/JdbcUnidadDeTrabajoMvp.java`
- `src/main/resources/db/migration/V6__persistir_verificaciones.sql`
- `src/main/resources/db/migration/V7__persistir_sustituciones_temporales.sql`
- `src/main/resources/db/migration/V8__persistir_contingencias_manuales.sql`
- `src/test/java/co/emi/trazabilidad/aplicacion/IntegracionMvpTest.java`
- `src/test/java/co/emi/trazabilidad/aplicacion/LogicaMvpTest.java`
- `src/test/java/co/emi/trazabilidad/aplicacion/MigracionMvpTest.java`
- `src/test/java/co/emi/trazabilidad/aplicacion/ServicioI6Test.java`
- `src/test/java/co/emi/trazabilidad/aplicacion/ServicioI7Test.java`
- `src/test/java/co/emi/trazabilidad/aplicacion/ServicioI8Test.java`
- `src/test/java/co/emi/trazabilidad/aplicacion/SoporteMvpTest.java`

## Archivos previos modificados

- `src/test/java/co/emi/trazabilidad/infraestructura/persistencia/EsquemaI5Test.java`: fixture histórico V5; aserciones intactas.

## Ejecución final y revisión del diff

Comando final: `mvn test`, Maven 3.9.11 del wrapper existente, Java 21.
Resultado del 14/09/2026, 17:41:51 -05:00; duración 58.006 s; código de salida 0.

```text
Tests run: 243, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

Cotejo independiente de 24 XML Surefire: 243/0/0/0. Log íntegro local:
`C:/Proyecto_EMI/tmp/i6-i8-mvn-test-final.log`.

Diff de producción, modelos, puertos, migraciones, pruebas e informe revisado.
30 archivos nuevos y un único archivo previo modificado; `git diff --check` sin errores.
Ningún cambio en producción I1–I5, interfaces previas, V1–V5, dependencias Maven ni control
formal. No se alteró data/ ni se hizo commit. Diff completo local para revisión:
`C:/Proyecto_EMI/tmp/i6-i8-diff-completo.patch`.
