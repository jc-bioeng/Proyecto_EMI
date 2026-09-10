# Informe técnico I2 — FuenteLecturasRFID y FuenteSimulada

Fecha: 07 de septiembre de 2026. Documento auxiliar del Proyecto EMI. Evidencia: **SOFTWARE / SIMULACION**. **RF_REAL: NO**. No sustituye ni modifica Propuesta, Documento Maestro, Matriz o Registro ACTUAL.

# 1. Objetivo

Materializar el contrato independiente del fabricante y una fuente simulada determinística que entregue observaciones propias EMI. I2 implementa el ingreso de datos, sin persistir lecturas, buscar equipos asociados ni interpretar operaciones. Una lectura RFID cruda no equivale a un evento operativo.

Se verificaron `AGENTS.md`, `docs/FUENTES_DE_VERDAD.md`, los documentos de desarrollo y el código/pruebas existentes antes de modificar código. Los PDF v1.8.2, inicialmente ausentes, fueron incorporados por el usuario y leídos antes de implementar. Referencias de autoridad, con páginas físicas del PDF (contadas desde 1):

| Fuente en docs/control_local | Evidencia utilizada |
|---|---|
| `Arquitecto_Proyecto_Documento_Maestro_v1.8.2_ACTUAL.pdf` | p.6: `LecturaEntradaRFID`; p.11–12, §18.2–18.5: separación lectura/evento, dominio de origen y contrato lógico |
| `Matriz_Requisitos_Proyecto_EMI_v1.8.2_ACTUAL.pdf` | p.8: RF-002; p.11: TEC-002, DAT-001; p.12: DAT-002/DAT-003; p.22: condición explícita TEC-002; p.24–25: cierre I1 y estados vigentes |
| `Registro_Decisiones_Proyecto_EMI_v1.8.2_ACTUAL.pdf` | p.10–11: DEC-017; p.16: DEC-022; p.17: no crear nueva DEC cuando la implementación sigue DEC-017 |

Los PDF incluyen contenido histórico con encabezados v1.8/v1.8.1; se consultó la actualización v1.8.2 al interpretar estados. No se elevó a decisión la recomendación histórica Java17/Spring: sigue vigente DEC-022, Java21/Maven/JDBC/SQLite/Flyway/JUnit5.

Contexto auxiliar: `docs/analisis/ANALISIS_SDK_CHAINWAY_U300.md`, especialmente §§6–7,12,14. Informa la independencia del receptor, semántica temporal y límites de parada; no introduce dependencias del proveedor.

# 2. Alcance de I2

Implementación nueva, sin modificar clases anteriores:

| Archivo productivo bajo src/main/java/co/emi/trazabilidad | Responsabilidad |
|---|---|
| `aplicacion/puertos/FuenteLecturasRFID.java` | Puerto común; receptor, inicio, parada y estado local |
| `dominio/LecturaEntradaRFID.java` | Record inmutable propio; EPC, Instant, origen y mapa opcional |
| `dominio/OrigenDatos.java` | Dominio aprobado SIMULACION / RF_REAL |
| `infraestructura/rfid/FuenteSimulada.java` | Implementación síncrona controlada explícitamente mediante emitir |

Pruebas nuevas bajo `src/test/java/co/emi/trazabilidad`: `dominio/LecturaEntradaRFIDTest.java`, `infraestructura/rfid/FuenteSimuladaTest.java`, `infraestructura/rfid/AislamientoI2Test.java`.

El código mantiene el módulo Maven único, los records del dominio, puertos en aplicación, implementaciones en infraestructura y Clock inyectable ya utilizado por ServicioI1. No se añadieron dependencias Maven. El enum de estado está contenido en el propio contrato, sin una máquina de estados adicional.

# 3. Fuera de alcance

No se implementaron LecturaRFID persistida, migraciones, SesionOperacion, eventos, deduplicación, historial operativo, ubicación, verificación, sustitución, contingencia, UI, TCP, serial, configuración RF ni adaptadores de hardware. No se importaron ReaderAPI, UHFTAGInfo ni paquetes Chainway. No se conectó ningún dispositivo.

I1 permanece cerrado; ninguna de sus clases, pruebas o migraciones V1/V2 fue modificada. No se cambió la regla pendiente sobre etiquetas INACTIVA. No se modificaron `docs/control_local/`, `docs/soporte/`, `docs/anexos/`, históricos ni documentos ACTUAL. Los cambios previos de AGENTS y FUENTES_DE_VERDAD se conservaron.

# 4. Diseño

```text
FuenteSimulada (infraestructura)
    implementa FuenteLecturasRFID (puerto de aplicación)
    genera LecturaEntradaRFID (valor propio del dominio)
    entrega al Consumer registrado por el consumidor de aplicación/prueba
```

La entidad de entrada se llama **LecturaEntradaRFID**, exactamente como fija el Maestro §18.5 y DEC-017. No es `LecturaRFID`, entidad persistida prevista para un incremento posterior. No posee ID, sesión, equipo, evento ni conexión a repositorios.

Se eligió `Consumer<LecturaEntradaRFID>` de Java estándar frente a una nueva interfaz funcional idéntica. El contrato no presupone Swing, un executor, un hilo concreto ni callback de proveedor. La simulación entrega inmediatamente en el hilo del llamador; una implementación posterior podrá entregar desde otro hilo y deberá documentar su sincronización. El receptor no debe asumir afinidad de hilo a partir del puerto.

El contrato lógico P0.4 (`iniciar`, `detener`, `obtener_lecturas`, `estado`) se concreta en Java mediante `iniciar(receptor)`, `detener()` y `estado()`: **obtener lecturas es la recepción por callback**, no un segundo canal polling. Unir registro e inicio impide tener una fuente iniciada sin receptor. Esto concreta tipos y mecanismo de entrega dentro de DEC-017; no elimina la capacidad de obtener lecturas ni cambia un requisito. No se exponen controles RF.

# 5. FuenteLecturasRFID

```java
public interface FuenteLecturasRFID {
    enum Estado { NUEVA, INICIADA, DETENIDA }
    void iniciar(Consumer<LecturaEntradaRFID> receptor);
    void detener();
    Estado estado();
}
```

Las operaciones de ciclo retornan void porque el éxito se representa por el estado y los usos inválidos producen excepciones visibles. No se añade un boolean ambiguo de éxito físico. `estado()` describe la instancia local; DETENIDA no acredita detención RF del U300.

El contrato permite que una entrega ya en curso termine al detener, pero impide iniciar nuevas entregas estando DETENIDA. La simulación comprueba el estado antes de cada emisión, incluso dentro de un lote. La política de ejecución concurrente de un adaptador real queda para ese adaptador; FuenteSimulada exige que sus llamadas se confinen a un hilo. No ofrece seguridad entre hilos ni crea hilos propios.

# 6. Modelo de lectura cruda

| Campo | Tipo | Semántica / validación |
|---|---|---|
| epc | String | No nulo/no blanco; copia literal, incluidos ceros iniciales, capitalización y espacios en una cadena no blanca |
| timestamp | Instant | No nulo; instante en que EMI genera/recibe la observación, nunca tiempo físico RF inferido |
| origenDatos | OrigenDatos | No nulo; enum SIMULACION / RF_REAL aprobado; la fuente simulada siempre fija SIMULACION |
| metadata | Map<String,String> | Vacío válido; copia defensiva inmutable; mapa, claves y valores no nulos |

**EPC:** `EtiquetaRFID`, `ServicioI1` y `docs/desarrollo/modelo_datos.md` ya aceptan cadenas no blancas sin fijar longitud ni normalización. Por ello I2 no convierte a mayúsculas/minúsculas, no hace trim, no convierte a número y no impone validación hexadecimal estricta. Ejemplos de prueba: `000aBC`, ` 00aB `, `epc-prueba`, `A`. La representación de un lector físico puede ser hexadecimal, pero eso no autoriza cambiar ahora la regla genérica existente. Una validación canónica hexadecimal global requeriría revisión trazable posterior.

**Tiempo:** FuenteSimulada recibe un Clock obligatorio y consulta `instant()` por emisión. Las pruebas usan reloj fijo y secuencial; no dependen del reloj real, sleeps ni zona del computador. Se conserva precisión de Instant, sin truncar a milisegundos: aún no hay persistencia de lecturas. El esquema temporal de I1 queda intacto. El timestamp técnico de parseo de un SDK futuro puede conservarse separadamente como metadata; no se confunde con recepción EMI.

**Metadata:** se elige un mapa textual pequeño para no modelar anticipadamente RSSI, antena, TID, USER, PC o atributos de un fabricante. La fuente simulada no permite al llamador escoger origen ni inyecta metadata física ficticia: produce `Map.of()`. El constructor genérico sí admite metadata opcional propia, cuya copia defensiva se prueba con un campo de prueba no físico. La interpretación de claves/unidades físicas queda pendiente de integración real.

La declaración del símbolo `RF_REAL` en el enum no produce evidencia RF_REAL. Ninguna prueba de I2 construye o emite una observación etiquetada RF_REAL.

# 7. FuenteSimulada

API específica adicional: `emitir(String)` y `emitir(List<String>)`. No se exponen en el puerto porque controlar manualmente EPC es propio de la simulación.

Ejemplo de uso software, equivalente al flujo probado:

```java
var recibidas = new ArrayList<LecturaEntradaRFID>();
var simulada = new FuenteSimulada(Clock.fixed(
    Instant.parse("2026-09-07T12:00:00Z"), ZoneOffset.UTC));
FuenteLecturasRFID fuente = simulada;
fuente.iniciar(recibidas::add);
simulada.emitir(List.of("0001", "0001", "00aB"));
fuente.detener();
// recibidas contiene tres observaciones, en ese orden y todas SIMULACION.
```

El lote se copia antes del recorrido para evitar que modificaciones de su lista original desde el receptor alteren el orden pendiente. Cada elemento se valida/genera/entrega secuencialmente. Un lote vacío no genera observaciones, pero exige estado INICIADA.

No hay acumulador de tags, deduplicación, reintento, aleatoriedad ni temporizadores. Dos EPC idénticos generan dos entregas aunque también coincidan los timestamps de un reloj fijo. El receptor decide qué hacer con ellas; la fuente no escribe en base de datos ni asocia equipos.

# 8. Lifecycle

| Situación | Resultado comprobado |
|---|---|
| Instancia nueva | NUEVA; sin receptor |
| iniciar(receptor no nulo) desde NUEVA | INICIADA; un único receptor registrado |
| iniciar(null) | NullPointerException, sin cambio de estado ni receptor; validación de null precede al estado |
| iniciar dos veces | IllegalStateException; conserva el receptor original |
| detener desde NUEVA | DETENIDA; permitido, terminal |
| detener desde INICIADA | DETENIDA; libera referencia al receptor |
| detener dos veces | Sin error, sigue DETENIDA |
| iniciar desde DETENIDA | IllegalStateException; para otra captura se crea otra instancia |
| emitir antes de iniciar / después de detener | IllegalStateException; no entrega |
| EPC nulo o blanco | IllegalArgumentException; no entrega ese EPC |
| Lista nula / reloj nulo | NullPointerException |
| Receptor lanza excepción | Se propaga la misma excepción; no se oculta, reintenta ni cambia estado automáticamente |
| Fallo en mitad de lote | Se interrumpe; entregas anteriores no se revierten; restantes no se intentan |
| Receptor solicita detener | La entrega actual termina; siguiente elemento del lote falla por DETENIDA |
| Receptor intenta emitir reentrantemente | IllegalStateException; evita intercalar emisiones recursivas |
| Nueva emisión tras excepción de receptor | Permitida si la fuente sigue INICIADA; guardia de emisión se libera en finally |

No se reutilizó `ErrorAplicacion`: sus códigos actuales modelan los casos de uso/persistencia de I1. I2 utiliza las excepciones estándar de argumento/estado y propaga el fallo original del consumidor, sin ampliar errores de negocio ni introducir otra jerarquía.

# 9. Pruebas

Ejecución final: **`.\mvnw.cmd verify`**, Java21, BUILD SUCCESS, terminada el **07/09/2026 20:56:20 -05:00**. Se ejecutó toda la suite, no solo I2. La primera ejecución pasó con 56 pruebas; tras añadir la prueba explícita de compilación aislada se repitió la suite completa sobre el código final.

| Grupo / clase | Previas | Nuevas | Total | Fallos | Errores | Omitidas |
|---|---:|---:|---:|---:|---:|---:|
| ServicioI1Test | 22 | 0 | 22 | 0 | 0 | 0 |
| ConsolaEmiTest | 2 | 0 | 2 | 0 | 0 | 0 |
| BaseDatosTest | 1 | 0 | 1 | 0 | 0 | 0 |
| EsquemaI1Test | 7 | 0 | 7 | 0 | 0 | 0 |
| FuenteSimuladaTest | 0 | 19 | 19 | 0 | 0 | 0 |
| LecturaEntradaRFIDTest | 0 | 5 | 5 | 0 | 0 | 0 |
| AislamientoI2Test | 0 | 1 | 1 | 0 | 0 | 0 |
| **Total** | **32** | **25** | **57** | **0** | **0** | **0** |

Evidencia local regenerable: `target/i2-verify.log` y `target/surefire-reports/TEST-*.xml`. Maven también generó el JAR del proyecto; no se ejecutó hardware. Los avisos de Shade se refieren a module-info, manifiestos y licencias superpuestos de dependencias ya presentes. No se cambió el empaquetado ni se añadieron dependencias para silenciarlos.

| Comportamiento | Prueba JUnit representativa |
|---|---|
| Contrato, inicio, EPC, origen, timestamp y parada | `contratoEntregaObservacionPropiaYSeDetiene` |
| Orden, repeticiones, origen solo SIMULACION y metadata vacía | `conservaOrdenYRepeticionesSinDeduplicar` |
| Emisión fuera de lifecycle | `rechazaEmisionAntesDeIniciarIncluidoLoteVacio`, `rechazaEmisionDespuesDeDetener` |
| Doble inicio conserva receptor | `segundoInicioNoReemplazaReceptor` |
| Doble parada, parada desde NUEVA y no reinicio | `detenerDosVecesEsIdempotenteYNoPermiteReinicio`, `detenerSinIniciarEsTerminalEIdempotente` |
| Receptor ausente y recuperación | `receptorNuloNoIniciaLaFuente` |
| Validación EPC y regla I1 preservada | `rechazaEpcObligatorioSinEntregarNiInutilizarFuente`, `noIntroduceNormalizacionNiRestriccionHexAjenaAI1` |
| Timestamp por observación, precisión y reloj inyectado | `timestampSeObtienePorObservacionDelRelojInyectado` |
| Excepción sin reintento y lote parcial | `excepcionDelReceptorEsVisibleSinReintentoYPermiteOtraEmision`, `datoInvalidoInterrumpeLoteSinRevertirEntregasPrevias` |
| Parada y reentrancia desde receptor | `paradaDesdeReceptorImpideRestoDelLote`, `rechazaEmisionReentranteSinOcultarla` |
| Copia del lote, vacío/nulo, instancias independientes | `loteEsUnaCopiaEstableFrenteACambiosDelReceptor`, `loteVacioNoEmiteYLoteNuloFalla`, `instanciasNoCompartenEstadoNiReceptor` |
| Metadata inmutable y campos obligatorios | Cinco pruebas de `LecturaEntradaRFIDTest` |
| Aislamiento real de compilación | `i2CompilaSoloConJavaEstandarSinSdkNiDependenciasDelProyecto` |

La prueba de aislamiento usa JavaCompiler del JDK con classpath y sourcepath apuntando a un directorio temporal vacío, sin procesadores de anotaciones, y compila únicamente los cuatro fuentes productivos nuevos. Demuestra que no requieren SDK, JDBC, framework ni clases productivas ajenas a I2 para compilar. La inspección de `src` y `pom.xml` tampoco encontró referencias a `com.rscja`, ReaderAPI, UHFTAGInfo o AdaptadorU300. No equivale a probar un lector futuro.

# 10. Trazabilidad

Cadena: necesidad de avanzar sin hardware → TEC-002/DEC-017 → puerto y estructura de entrada → FuenteSimulada → pruebas del contrato con Consumer → XML Surefire y log Maven. No se inventan IDs ni se cambian estados de control.

| Requisito | Necesidad | Implementación | Prueba | Resultado |
|---|---|---|---|---|
| TEC-002 | Fuente intercambiable y estructura independiente del SDK | FuenteLecturasRFID + LecturaEntradaRFID + FuenteSimulada | Flujo tipado por puerto + AislamientoI2Test | **PARCIAL**: falta demostrar procesamiento desde segunda fuente real/stub compatible, condición expresa Matriz p.11 y p.22 |
| RF-002 | Capturar y almacenar lecturas; fuente simulada durante desarrollo | Captura/entrega simulada; almacenamiento excluido de I2 | Orden, lotes y recepción | **PARCIAL**: no persiste LecturaRFID |
| DAT-001 | Distinguir lectura de evento y conservar repeticiones | DTO sin evento ni interpretación; no deduplicación | Repeticiones preservadas | **PARCIAL**: falta relación auditable lectura/evento en persistencia posterior |
| DAT-002 | Distinguir origen de cada dato | OrigenDatos y SIMULACION fijo en cada emisión | Contrato y todas las lecturas del lote | **PARCIAL** global: probado para entrada simulada; sesiones, eventos y exportaciones aún no implementados |
| DAT-003 | Modelo mínimo coherente | Estructura de entrada definida formalmente; no nuevas entidades persistidas | DTO/validaciones e I1 regresión | **PARCIAL** global; no se cierra el modelo mínimo de persistencia |

La cobertura parcial de requisitos más amplios no impide cerrar técnicamente el incremento acotado I2. No se añade una segunda fuente o stub para declarar TEC-002 completo: eso no forma parte de la implementación solicitada. RF-001 permanece implementado y protegido por sus 32 pruebas previas y archivos intactos.

# 11. Límites de evidencia

Este incremento demuestra:

- lógica de entrada;
- contrato común;
- producción de observaciones simuladas;
- lifecycle;
- entrega de EPC;
- repetición de EPC;
- timestamps de aplicación;
- aislamiento respecto al hardware.

NO demuestra:

- funcionamiento del U300;
- conexión TCP real;
- lectura física;
- RSSI real;
- antena real;
- alcance RFID;
- omisiones;
- lecturas externas;
- comportamiento sobre metal;
- interferencia;
- desempeño físico.

**Tipo de evidencia: SOFTWARE / SIMULACION. RF_REAL: NO.** P1 sigue EN CURSO. Fase formal Diseño, MVP completo no cerrado, Pruebas formales y piloto pendientes. Las pruebas unitarias de este incremento no adelantan las puertas de validación física.

# 12. Riesgos

| Riesgo / límite | Tratamiento |
|---|---|
| Consumidor lento bloquea emisión síncrona | Explícito y apropiado para simulación determinística; un adaptador asíncrono deberá gestionar su entrega |
| Acceso desde múltiples hilos | No soportado por FuenteSimulada; llamador confina el uso a un hilo. Puerto no fija hilo SDK |
| Fallo de consumidor tras efectos parciales | Excepción visible, sin reintento/rollback; las pruebas muestran la entrega parcial |
| EPC no hexadecimal o no canónico aceptado | Conserva regla I1. No introducir normalización global hasta regla aprobada |
| Metadata textual sin esquema físico | Vacía en simulación; contrato de unidades/valores pendiente de hardware; mapa defensivo inmutable |
| DETENIDA interpretada como acuse físico | Estado exclusivamente local; no afirma parada de radiofrecuencia |
| Cierre I2 interpretado como cierre TEC-002/MVP/P1 | Matriz de cobertura PARCIAL y límites explícitos; documentos de control no modificados |

# 13. Resultado del incremento

**I2 es técnicamente cerrable dentro del alcance solicitado.** Cuatro archivos productivos nuevos, tres clases de pruebas y este informe. 57 pruebas aprobadas: 32 previas intactas + 25 nuevas. Sin dependencias nuevas, migraciones, hardware, RF_REAL ni commits.

Verificación Git: `git diff` sobre V1/V2, ServicioI1, sus pruebas y pom.xml no mostró diferencias; el conjunto de archivos previamente seguidos modificado sigue siendo el preexistente AGENTS.md/FUENTES_DE_VERDAD.md. Los archivos nuevos de I2 permanecen sin seguimiento porque no se hizo add ni commit.

Salida `git status --short` al consolidar I2:

```text
 M AGENTS.md
 M docs/FUENTES_DE_VERDAD.md
?? "Java For PC/"
?? docs/analisis/
?? src/main/java/co/emi/trazabilidad/aplicacion/puertos/FuenteLecturasRFID.java
?? src/main/java/co/emi/trazabilidad/dominio/LecturaEntradaRFID.java
?? src/main/java/co/emi/trazabilidad/dominio/OrigenDatos.java
?? src/main/java/co/emi/trazabilidad/infraestructura/rfid/
?? src/test/java/co/emi/trazabilidad/dominio/
?? src/test/java/co/emi/trazabilidad/infraestructura/rfid/
```

Salida `git diff --stat`:

```text
 AGENTS.md                 | 157 ++++++++-----
 docs/FUENTES_DE_VERDAD.md | 550 +++++++++-------------------------------------
 2 files changed, 204 insertions(+), 503 deletions(-)
```

Ese diff refleja **cambios preexistentes**, no trabajo I2. Git diff no incluye archivos sin seguimiento; la lista completa de altas I2 está en §2 y este informe. `docs/analisis/` también contiene el análisis SDK anterior, que no se modificó.

# 14. Próximo incremento autorizado

El siguiente elemento de la secuencia es **persistencia de LecturaRFID**. No se implementa aquí. Su diseño deberá respetar los campos y relaciones formales —incluida la relación con sesión— y el orden aprobado, sin crear anticipadamente SesionOperacion ni eventos dentro de I2. Cualquier dependencia que obligue a alterar esa secuencia deberá explicarse antes de implementarla.

Después permanecen SesionOperacion, eventos/deduplicación, historial operativo, verificación, sustitución, contingencia y el adaptador UHF real cuando P1 lo permita. I2 no autoriza conectar ReaderAPI ni avanzar a RF_REAL.

## CAMBIOS DOCUMENTALES SUGERIDOS

### Documento Maestro

Sección: §18.5 Interfaz común de lecturas y sección de estado de incrementos vigente.

Cambio sugerido: registrar materialización técnica de I2 sin alterar el contrato lógico ni declarar cierre global de MVP/P1.

Contenido propuesto: “FuenteLecturasRFID y FuenteSimulada implementadas. LecturaEntradaRFID conserva EPC literal, Instant de recepción/generación, origen SIMULACION y metadata opcional. La obtención de lecturas se concreta con Consumer registrado al iniciar y estado local explícito. I2 verificado con 25 pruebas nuevas; suite completa de 57 pruebas sin fallos, errores u omitidas. Evidencia SOFTWARE / SIMULACION, RF_REAL NO. TEC-002 conserva cobertura parcial hasta segunda fuente compatible.”

Motivo: actualización de evidencia de implementación dentro de DEC-017; no modificación de alcance, stack, presupuesto, metodología o validación.

### Matriz de Requisitos

Requisito: TEC-002.

Estado anterior: EN DESARROLLO.

Estado sugerido: EN DESARROLLO; añadir cobertura parcial de I2, no Implementado.

Evidencia: FuenteLecturasRFID, LecturaEntradaRFID, FuenteSimulada, pruebas de contrato y compilación aislada; este informe y Surefire.

Motivo: Matriz p.11/p.22 exige procesamiento desde FuenteSimulada y FuenteLectorReal/stub; este incremento implementa solo la primera.

Requisito: RF-002 / DAT-001 / DAT-002.

Estado anterior: APROBADO.

Estado sugerido: EN DESARROLLO si se aprueba actualizar control; cobertura PARCIAL, nunca Implementado por I2.

Evidencia: entrega simulada ordenada, repeticiones conservadas y origen explícito; faltan persistencia, relación con eventos y cobertura de origen en estructuras posteriores.

Motivo: existe materialización parcial, no cumplimiento integral de sus criterios. La sugerencia no cambia sus estados actuales automáticamente.

Requisito: DAT-003.

Estado anterior: EN DESARROLLO.

Estado sugerido: EN DESARROLLO, sin avance de entidades persistidas por I2.

Evidencia: DTO de entrada formal e I1 preservado; no migración nueva.

Motivo: LecturaEntradaRFID no reemplaza LecturaRFID persistida ni materializa el modelo mínimo completo.

### Registro de Decisiones

¿Nueva decisión requerida?: NO

Justificación: se implementa DEC-017 con el nombre formal y campos aprobados, dentro del stack DEC-022. Consumer, firma Java, copia de metadata y lifecycle son concreciones técnicas del contrato lógico. El Registro v1.8.2 p.17 indica no crear una DEC por una implementación que siga DEC-017. No se cambia ninguna decisión ni se incorpora una nueva fuente hardware.

| Criterio | Resultado |
|---|---|
| FuenteLecturasRFID implementada | SÍ |
| Independiente de Chainway | SÍ |
| FuenteSimulada implementada | SÍ |
| EPC probado | SÍ |
| Repeticiones preservadas | SÍ |
| Timestamp probado | SÍ |
| Origen SIMULACION probado | SÍ |
| Lifecycle probado | SÍ |
| Suite completa pasa | SÍ |
| I1 permanece intacto | SÍ |
| AdaptadorU300 implementado | NO |
| RF_REAL producida | NO |
| I2 técnicamente cerrable | SÍ |
