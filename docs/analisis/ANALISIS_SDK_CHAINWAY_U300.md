# Inspección técnica del SDK Chainway candidato para U300

Fecha de inspección: 06–07 de septiembre de 2026. Proyecto EMI. Estado: análisis estático, sin ejecución del lector. Documento auxiliar de análisis; no constituye decisión aprobada ni modifica la línea base.

# 1. Resumen ejecutivo

1. [JAR_SIGNATURE] Se inspeccionó el SDK local `Java For PC/API/ReaderAPI20250926.jar`: 247 clases, bytecode major 52. Las dos copias del JAR son idénticas por SHA-256; las 247 clases del SDK incluidas en la app también coinciden byte a byte (§2).
2. [SDK_DOC][DEMO_SOURCE][JAR_SIGNATURE] La ruta Ethernet documentada y utilizada es `new RFIDWithUHFNetworkA4()` → `init(String,int)`; la serial es `new RFIDWithUHFSerialPortA4()` → `init(String)`. La denominación A4 no demuestra por sí sola compatibilidad con la unidad U300 y su firmware concretos (§4–5).
3. [DEMO_SOURCE][JAR_SIGNATURE] El demo registra `IUHFInventoryCallback.callback(UHFTAGInfo)` y arranca con `startInventoryTag()`. Existe polling alternativo; el fabricante pide elegir un mecanismo, no ambos (§6).
4. [JAR_SIGNATURE] Los callbacks de inventario de la ruta examinada se invocan desde un hilo interno que consume un búfer. El callback debe ser breve y aislar excepciones; el demo bloquea su entrega mientras actualiza Swing (§6).
5. [SDK_DOC][JAR_SIGNATURE] EPC es una cadena hexadecimal; también hay bytes, TID, USER, PC, RSSI y antena. Fase y frecuencia requieren revisar parámetros y soporte efectivo (§7).
6. [JAR_SIGNATURE] **Existe timestamp y, en el parser A4 trazado, se asigna mediante `System.currentTimeMillis()` del host.** No es evidencia de un reloj físico de captura en el lector (§7).
7. [JAR_SIGNATURE] **`stopInventory()` descarta el resultado del envío y devuelve `true` en las bases A4 de red y serial.** No confirma parada física. `free()` tampoco constituye acuse del lector (§12).
8. [SDK_DOC][JAR_SIGNATURE][INFERENCIA] El fabricante declara suficiente ReaderAPI para Ethernet, pero el parser compartido referencia JSON externo. No se ha demostrado el conjunto mínimo de dependencias mediante ejecución aislada (§10).
9. [SDK_DOC][JAR_SIGNATURE] El fabricante declara JDK 1.8.0_131 para desarrollar el demo. Las DLL RXTX entregadas son Windows AMD64. La librería serial Linux mencionada no aparece como archivo entregado (§10).
10. [INFERENCIA][PENDIENTE] La compatibilidad con Java 21 es **parcial**: inspección estática satisfactoria, sin prueba de carga funcional, conexión ni recuperación. El wrapper Gradle 6.8 del demo no tiene soporte para ejecutarse sobre Java 21; EMI conserva Maven (§11).
11. [PENDIENTE] **LICENCIA / REDISTRIBUCIÓN: PENDIENTE DE CONFIRMACIÓN.** Los avisos de terceros de la app no otorgan permisos sobre ReaderAPI (§17).
12. [INFERENCIA][PENDIENTE] Es viable preparar un diseño preliminar de wrapper independiente del fabricante, sujeto a resolver compatibilidad, configuración y ciclo de vida. No se implementó `AdaptadorU300`; P1 sigue EN CURSO y RF_REAL PENDIENTE.

## Alcance, fuentes y cómo reproducir la inspección

Se leyeron primero `docs/FUENTES_DE_VERDAD.md` y `AGENTS.md`, ambos versión de contexto 1.8.2. Se respeta su jerarquía: propuesta → Maestro ACTUAL → Matriz ACTUAL → Registro ACTUAL → soporte → anexos. No se usan los hallazgos para aprobar decisiones, cerrar requisitos ni reabrir I1. No se cotejaron aquí los PDF ACTUAL: cualquier futura modificación de una DEC exige verificar directamente el Registro ACTUAL.

Se detectaron cambios preexistentes en `AGENTS.md` y `docs/FUENTES_DE_VERDAD.md`, y la carpeta `Java For PC/` sin seguimiento en Git. No son cambios de esta inspección. La única entrega nueva es este informe. No se hicieron commits ni cambios de código, migraciones, configuración, documentos de control o históricos.

Convenciones de evidencia: `[SDK_DOC]` documentación entregada por el fabricante; `[DEMO_SOURCE]` fuentes entregadas del demo; `[JAR_SIGNATURE]` estructura, firmas o bytecode inspeccionado del JAR; `[APP_CONFIG]` contenido del paquete APP; `[INFERENCIA]` deducción o recomendación que no es confirmación; `[PENDIENTE]` ausencia de evidencia suficiente. Se distingue expresamente firma de comportamiento observado en bytecode. Las fuentes oficiales externas sobre Java/Gradle se citan aparte y se clasifican como apoyo a una inferencia de compatibilidad, no como documentación Chainway.

Rutas abreviadas, todas relativas a `C:/Proyecto_EMI/`:

| Alias | Ruta |
|---|---|
| S | `Java For PC/` |
| J | `Java For PC/API/ReaderAPI20250926.jar` |
| D | `Java For PC/demo source code/A4JavaDemo/A4JavaDemo/` |
| C | `D/src/main/java/com/uhf/` — sustituir D por su ruta completa |
| H | `Java For PC/API/doc/doc/` |
| A | `Java For PC/APP/URA4DemoV1.4.jar` |
| W | `Java For PC/开发包说明文档.docx` |

Cuando se indica `H:Clase`, corresponde al HTML del nombre plenamente cualificado, sustituyendo puntos por `/`. Para clases internas del JAR se usa `$`. Las líneas de fuentes son de los archivos originales; los offsets de bytecode son offsets de instrucciones, **no líneas Java**. No se inventan líneas para fuentes no entregadas. La etiqueta y la referencia de cada fila cubren sus afirmaciones técnicas.

Herramientas: PowerShell, `rg`, ZIP de .NET, hashes SHA-256 y `jar`, `javap`, `jdeps` del JDK local Eclipse Adoptium en `C:/Program Files/Eclipse Adoptium/jdk-21.0.12.101-hotspot/bin/`. Se inspeccionaron cabeceras PE sin cargar DLL. Los volcados de trabajo se guardaron en `%TEMP%/emi-sdk-inspection-20260907/`; son auxiliares regenerables y no se requieren para usar el informe.

Comandos reproducibles de solo inspección desde la raíz del proyecto:

```powershell
Get-FileHash -Algorithm SHA256 -LiteralPath 'Java For PC/API/ReaderAPI20250926.jar'
jar tf 'Java For PC/API/ReaderAPI20250926.jar'
javap -classpath 'Java For PC/API/ReaderAPI20250926.jar' -public com.rscja.deviceapi.RFIDWithUHFNetworkA4
javap -classpath 'Java For PC/API/ReaderAPI20250926.jar' -public com.rscja.deviceapi.RFIDWithUHFSerialPortA4
javap -classpath 'Java For PC/API/ReaderAPI20250926.jar' -c -p 'com.rscja.deviceapi.RFIDWithUHFNetworkAx$InventoryThread'
javap -classpath 'Java For PC/API/ReaderAPI20250926.jar' -c -p com.rscja.deviceapi.RFIDWithUHFNetworkAx
javap -classpath 'Java For PC/API/ReaderAPI20250926.jar' -c -p com.rscja.deviceapi.RFIDWithUHFSerialPortAxBase
javap -classpath 'Java For PC/API/ReaderAPI20250926.jar' -c -p com.rscja.deviceapi.h
jdeps --ignore-missing-deps -verbose:class 'Java For PC/API/ReaderAPI20250926.jar'
jdeps --missing-deps --class-path 'Java For PC/demo source code/A4JavaDemo/A4JavaDemo/libs/*' 'Java For PC/API/ReaderAPI20250926.jar'
```

No se ejecutaron demo, código SDK, inventario, conexión, descubrimiento UDP, actualización de firmware ni pruebas físicas. No se recompiló el proyecto: esta inspección no añade pruebas a las 32 de I1.

# 2. Inventario del SDK

[JAR_SIGNATURE][DEMO_SOURCE][SDK_DOC][APP_CONFIG] El árbol entregado contiene 15 JAR, 2 DLL externas, 18 fuentes Java, 256 HTML, 1 DOCX, 1 RAR, scripts Gradle y archivos auxiliares. No se hallaron EXE, instaladores de drivers ni SO externos en el árbol. Sí hay binarios nativos embebidos en JNA y en la app. Los archivos XML y properties incluyen metadatos de IDE/caché y wrapper, no una configuración EMI aprobada.

```text
Java For PC/
  API/ReaderAPI20250926.jar
  API/doc/doc/                  JavaDoc HTML
  demo source code/A4JavaDemo.rar
  demo source code/A4JavaDemo/A4JavaDemo/
    src/main/java/com/uhf/      fuente del demo
    src/main/resources/META-INF/MANIFEST.MF
    libs/                      ReaderAPI + 11 JAR auxiliares
    rxtxSerial.dll, rxtxParallel.dll
    build.gradle, settings.gradle, gradlew, gradlew.bat
    gradle/wrapper/, ip.txt, .gradle/, metadatos del IDE
  APP/URA4DemoV1.4.jar
  开发包说明文档.docx
```

En las columnas de necesidad, «condicional» exige la función indicada; «pendiente» no significa obligatorio. La app completa y el SDK mínimo son productos diferentes.

| Archivo | Tipo | Función probable o confirmada | Necesario para Ethernet | Necesario para serial | Relevancia EMI | Evidencia |
|---|---|---|---|---|---|---|
| J y `D/libs/ReaderAPI20250926.jar` | JAR | API A4 y otros lectores/protocolos | Sí | Sí | Núcleo candidato | [JAR_SIGNATURE] firmas y hashes iguales |
| `H/**/*.html` | JavaDoc | Contrato y ejemplos; algunas clases internas conservan nombres anteriores a ofuscación | Consulta | Consulta | ALTA | [SDK_DOC] HTML |
| `C/**/*.java` | Fuente | Demo Swing; conexión, inventario y configuración | Referencia | Referencia | ALTA; no dependencia del wrapper | [DEMO_SOURCE] `UHFMainForm`, `InventoryForm` |
| `D/libs/RXTXcomm.jar` | JAR | `gnu.io`, acceso a puerto serial | No en la ruta A4 de sockets examinada; el demo referencia `gnu.io` | Sí | Condicional serial | [SDK_DOC] W; [JAR_SIGNATURE] `jdeps`, `deviceapi.b` |
| `D/rxtxSerial.dll` | DLL AMD64 | Capa nativa RXTX serial Windows | No en ruta examinada | Sí según W | Condicional serial | [SDK_DOC] W; [JAR_SIGNATURE] PE `0x8664` |
| `D/rxtxParallel.dll` | DLL AMD64 | RXTX paralelo; entregada en conjunto Windows | No | Fabricante la enumera; necesidad estricta serial no probada | Baja/condicional | [SDK_DOC] W; [JAR_SIGNATURE] PE `0x8664` |
| `librxtxSerial.so` | SO mencionado, ausente | RXTX serial Linux | No | Sí según W, falta archivo | Riesgo Linux | [SDK_DOC] W; [PENDIENTE] binario |
| `D/libs/jna-5.4.0.jar` | JAR + nativos embebidos | JNA para otras rutas USB/BLE/Linux/Windows | No llamada directa identificada en A4 TCP | No requisito A4 RXTX demostrado | Condicional; evitar copiar por inercia | [JAR_SIGNATURE] `jdeps`; [DEMO_SOURCE] build:29 |
| `D/libs/jna-platform-5.4.0.jar` | JAR | APIs de plataforma JNA | Pendiente según funcionalidades; no en flujo básico trazado | Igual | Baja/condicional | [DEMO_SOURCE] build:30 |
| `D/libs/json-lib-2.4-jdk15.jar` | JAR | `net.sf.json`, referenciado por parser compartido `g` | Condicional; mínimo no demostrado | Condicional por mismo parser | ALTA para resolver classpath | [JAR_SIGNATURE] `jdeps`, `g`; [DEMO_SOURCE] build:37 |
| `D/libs/ezmorph-1.0.6.jar` | JAR | Apoyo al conjunto JSON del demo | Condicional | Condicional | No asumir mínimo | [DEMO_SOURCE] build:36; [INFERENCIA] papel en JSON |
| `D/libs/commons-beanutils-1.8.0.jar` | JAR | Utilidades Java del conjunto demo | Condicional | Condicional | Riesgo de dependencia antigua | [DEMO_SOURCE] build:32 |
| `D/libs/commons-collections-3.2.1.jar` | JAR | Colecciones del conjunto demo | Condicional | Condicional | Igual | [DEMO_SOURCE] build:33 |
| `D/libs/commons-lang-2.6.jar` | JAR | Utilidades del conjunto demo | Condicional | Condicional | Igual | [DEMO_SOURCE] build:34 |
| `D/libs/commons-logging-1.1.1.jar` | JAR | Logging del conjunto demo | Condicional | Condicional | Igual | [DEMO_SOURCE] build:35 |
| `D/libs/xom-1.2.6.jar` | JAR | XML; contiene paquete compartido con `java.xml` | No demostrado para EPC básico | Igual | Evitar arrastre completo | [DEMO_SOURCE] build:38; [JAR_SIGNATURE] `jdeps` |
| `D/libs/jxl.jar` | JAR | Exportación de resultados del demo | No para recibir EPC | No para recibir EPC | FUERA_DEL_MVP en esta integración | [DEMO_SOURCE] `utils/ExportUtils.java`, build:31 |
| `D/ip.txt` | Texto | Último endpoint del demo: `192.168.99.100,9160` | No; la aplicación puede proporcionar endpoint | No | Ejemplo, no configuración autorizada | [DEMO_SOURCE] `UHFMainForm`:59–62,190 |
| `D/build.gradle`, wrapper y scripts | Build | Empaquetado del demo y Gradle 6.8 | No para SDK en Maven | No para SDK en Maven | Solo referencia | [DEMO_SOURCE] build:14–23, wrapper properties |
| A | Fat JAR ejecutable | Demo + dependencias; entrada `com.uhf.UHFMainForm` | No para wrapper | No para wrapper | Herramienta posterior, no ejecutada | [APP_CONFIG] manifiesto y entradas ZIP |
| W | DOCX | Instrucciones de API, dependencias y runtime | Consulta | Consulta | ALTA | [SDK_DOC] párrafos «API接口说明», «执行程序说明» |
| `S/demo source code/A4JavaDemo.rar` | Archivo RAR | Distribución archivada del demo | No | No | Custodia; no se volvió a extraer | [JAR_SIGNATURE] tamaño/hash del archivo; [PENDIENTE] cotejo interno RAR/extracción |
| `.gradle`, IDE, `.jfd`, recursos visuales | Caché/metadatos/vistas | Desarrollo y presentación del demo | No | No | FUERA_DEL_MVP | [DEMO_SOURCE] inventario del árbol |

## Identidad y custodia

| Archivo | Bytes | SHA-256 |
|---|---:|---|
| J y `D/libs/ReaderAPI20250926.jar` | 444433 cada uno | `BC585B2249E6C036907218408A8053AB26E5D07166C4B51A88347253145BB183` |
| A | 5761693 | `1C85895F5B12E2FBECB4A4E45F67FE71AA8CF7EA61C7D9A4608AEECFBF134DA6` |
| RAR | 5230288 | `415A6C3BB343480A85C4E2198CB6CFF186DFA5C9D0E03C5447DAE276C2049BD0` |
| W | 309962 | `36E7E106AF5E6076EC3143E852CA3D97D29386B94D024B03B7CF8A7BCEEC3937` |
| `D/rxtxSerial.dll` | 129536 | `993BEAE12D71DDAB9E5D0139131A562DFB3A560044886E677E332ED56574D1D3` |
| `D/rxtxParallel.dll` | 84992 | `2F3BF859A5581204F9F62CB87A4C772CAD9B21A33EB6AB5BE1C0FA2E2DBFFC4C` |

[JAR_SIGNATURE][APP_CONFIG] Comparación de entradas de clase J→A: **247 iguales, 0 diferentes, 0 ausentes**. Esto no certifica que todo el fuente del demo corresponda al build APP. Los hashes permiten identificar las copias inspeccionadas; no autentican por sí solos su procedencia. [PENDIENTE] No se aportó firma del distribuidor ni comprobación de autenticidad con Chainway.

[DEMO_SOURCE] Identidad SHA-256 de los archivos que sustentan las líneas citadas; rutas relativas a D. Estas huellas distinguen una revisión posterior del demo de la inspeccionada aquí.

| Archivo en D | SHA-256 |
|---|---|
| `src/main/java/com/uhf/UHFMainForm.java` | `C25B989AEA8D95C7B9D1BF7C2C311260C5454FBB54920E2ADF735E2A1692D8B4` |
| `src/main/java/com/uhf/form/InventoryForm.java` | `CFAE5753FFD4ABA148EDAAC7B4EC89E1B3E99C3D923DDD0F67350BE5C336386C` |
| `src/main/java/com/uhf/model/InventoryTableModel.java` | `32C816BB840A4D0C40C22F7EF4A8029392AD7D766831838A51E8B20CA986A707` |
| `src/main/java/com/uhf/form/ConfigForm.java` | `86E293698DF0EE2A675724A9E031585842CB706025E5B4A4B094670350476A2C` |
| `src/main/java/com/uhf/form/ConfigForm2.java` | `DC9F7956FB11A0823F314844C94A7D67F0EBF88754161F52B0892D2AA672D824` |
| `build.gradle` | `28CE28C11E0098F02E653BADFB8E64ECBA870A2568693DC916FE21B226900AF6` |
| `ip.txt` | `1BD01CDD28DD4523089306B0C9E7C1614CFFE54A0FA223E01619D0882312CA11` |

# 3. Arquitectura relevante

| Paquete | Clase o grupo | Tipo | Responsabilidad comprobada o probable | Métodos relevantes | Relevancia EMI / evidencia |
|---|---|---|---|---|---|
| `com.rscja.deviceapi` | `RFIDWithUHFNetworkA4` | Clase pública | Fachada A4 Ethernet | `init`, `free`, `getConnectStatus`, inventario y configuración | ALTA [SDK_DOC][JAR_SIGNATURE] J, H |
| mismo | `RFIDWithUHFSerialPortA4` | Clase pública | Fachada A4 serial | `init(String)`, `free`, inventario y configuración | ALTA si serial [SDK_DOC][JAR_SIGNATURE] |
| mismo | `RFIDWithUHFNetworkAx` | Clase abstracta no pública | Implementación heredada de sockets, inventario y callbacks | `startInventoryTag`, `readTagFromBuffer`, `stopInventory` | Interna; no envolver su nombre ofuscable [JAR_SIGNATURE] |
| mismo | `RFIDWithUHFSerialPortAxBase` | Base interna | RXTX, recepción y ciclo serial | `init`, `send`, `receiveTagData`, inventario | Interna [JAR_SIGNATURE] |
| mismo | `SocketManageAx`, `c`, `d` | Internas | Gestión TCP; `d` conserva `Compiled from SocketTcpIp.java` | `connect`, `receiveTagData`, `close`; llamadas `java.net.Socket` | Internas, sin API EMI expuesta [JAR_SIGNATURE] |
| mismo | `g`, `i`, `h` | Parser y bases | `g` conserva `UHFProtocolParseAxFromJava.java`; `i`, `UHFProtocolParseFromJava.java`; `h` decodifica tag | `a(...,TagInfoRule)` y generación de comandos | Explican transformación; no dependencia directa propuesta [JAR_SIGNATURE] |
| `com.rscja.deviceapi.interfaces` | `IUHF`, `IUHFAx`, `IUHFA4`, `IMultipleAntenna` | Interfaces | Contratos del fabricante, no contratos EMI | Inventario, configuración y antenas | Referencia de wrapper [SDK_DOC][JAR_SIGNATURE] |
| mismo | `IUHFInventoryCallback` | Interfaz | Entrega de `UHFTAGInfo` | `void callback(UHFTAGInfo)` | ALTA [SDK_DOC][JAR_SIGNATURE] |
| mismo | `ConnectionStateCallback` | Interfaz | Notificación de estado | `void getState(ConnectionState,Object)` | ALTA; significado del Object no documentado [JAR_SIGNATURE][PENDIENTE] |
| mismo | `IObserver` | Interfaz interna de notificación | Propagación de cambios desde transporte | `update` | No es el contrato de tags del demo [JAR_SIGNATURE] |
| `com.rscja.deviceapi` | `ConnectionState` | Enum | Estado de conexión | `CONNECTED`, `DISCONNECTED` utilizados por demo | ALTA [DEMO_SOURCE] C/UHFMainForm:261–277 |
| `com.rscja.deviceapi.entity` | `UHFTAGInfo` | POJO mutable | Datos de tag | `getEPC`, `getRssi`, `getAnt`, `getTimestamp` | ALTA [JAR_SIGNATURE] |
| mismo | `InventoryParameter`, `TagInfoRule` | POJO/configuración | Solicitud y decodificación de metadata | `isNeedPhase`, `isNeedFrequencyPoint` y setters | Opcional [JAR_SIGNATURE] |
| mismo | `AntennaState`, `AntennaNameEnum`, `AntennaPowerEntity`, `ReaderInfo` | POJO/enum | Habilitación, potencia y cantidad declarada | `isEnable`, `getAntennaNumber`, getters de potencia | ALTA [SDK_DOC][JAR_SIGNATURE] |
| mismo | `Gen2Entity` | POJO | Session, Q y otros parámetros Gen2 | `get/setQuerySession`, `get/setQ`, Start/Min/MaxQ | Consulta y configuración posterior [JAR_SIGNATURE] |
| mismo | `AfterNetworkDisconnectedEntity`, `DuplicateTagFilterEntity`, `RssiFilterEntity`, `TagReportingModeEntity` | POJO | Caché tras desconexión y filtros/reporte | Getters/setters A4 correspondientes | Revisar antes de interpretar observaciones [SDK_DOC][DEMO_SOURCE] ConfigForm2 |
| mismo y otros | M775, HF/PSAM, BLE, USB, AccessGate, MQTT, JSON, firmware | Varias | Otras funciones de la distribución | Escritura, kill, lock, upgrade, otras comunicaciones | FUERA_DEL_MVP para esta inspección; no activar [JAR_SIGNATURE] listado J |

[JAR_SIGNATURE] Herencia confirmada: A4 red → NetworkAx → UHFBase; A4 serial → SerialPortAxBase. La clase serial también declara `java.util.Observer`; la red utiliza el `IObserver` del fabricante. El demo expone ambas mediante `IUHFA4`. [INFERENCIA] Es un antecedente útil para un wrapper, pero ninguna clase de Chainway debe filtrarse hacia dominio o aplicación EMI.

# 4. Flujo Ethernet identificado

Respuestas a preguntas 1–10:

| Pregunta | Respuesta y evidencia |
|---|---|
| 1. Clase Ethernet | [SDK_DOC][DEMO_SOURCE] `com.rscja.deviceapi.RFIDWithUHFNetworkA4`, indicada por W y utilizada en `C/UHFMainForm.java`:256. [PENDIENTE] confirmación de modelo/firmware de la unidad U300 entregada; no inferirla del nombre A4. |
| 2. Instanciación | [JAR_SIGNATURE][DEMO_SOURCE] Constructor público sin argumentos; `new RFIDWithUHFNetworkA4()`. Demo conserva una instancia reutilizable. |
| 3–5. Parámetros, IP, puerto | [SDK_DOC] `boolean init(String host,int port)`. [DEMO_SOURCE] campos IP/puerto, validación IPv4 y `Integer.parseInt`, líneas 173–183. UI inicial `192.168.1.100:9160` (501,513); `D/ip.txt` contiene `192.168.99.100,9160` y sobrescribe esos campos. Son ejemplos, no dirección de hardware constatada. |
| 6. Timeout | [JAR_SIGNATURE] No hay parámetro timeout en la firma pública A4 examinada. `d.a(String,int)` usa 20000 ms de conexión y 100 ms de SO_TIMEOUT en la rama de creación de socket; otra rama de reutilización contiene 5000/500 ms. No representan un plazo total garantizado para `init`, una política configurable ni una medida de hardware. |
| 7. Apertura | [SDK_DOC][JAR_SIGNATURE] `init(host,port)` conecta; la alternativa `setPort(int)` → `init(host)` está documentada. No hace falta inventar `connect/open`. |
| 8. Estado | [SDK_DOC][JAR_SIGNATURE] `ConnectionState getConnectStatus()` y `setConnectionStateCallback(ConnectionStateCallback)`. Estado del transporte no demuestra recepción UHF. |
| 9. Desconexión | [DEMO_SOURCE] demo llama `inventoryForm.stopInventory()` si visible y luego `ur4.free()` (199–202). [JAR_SIGNATURE] `free()` cierra transporte, solicita parada de hilos y limpia búfer; límites en §12. |
| 10. Pérdida | [DEMO_SOURCE] callback `DISCONNECTED` reenvía estado, intenta parar/liberar y habilita reconexión manual (261–277). No se encontró bucle de reconexión automática en ese flujo. [PENDIENTE] latencia de detección y continuidad real de inventario. |

Flujo efectivamente utilizado, con distinción entre acciones automáticas y manuales:

```text
main → new UHFMainForm → initUI
  → selección network (índice 1)
  → new RFIDWithUHFNetworkA4
  → asignación a IUHFA4 ur4
  → registro de ConnectionStateCallback
acción del botón Connect
  → validar IP/puerto → init(ip,port)
  → callback CONNECTED → InventoryForm.getState
      → setInventoryCallback
  → setAutoCheckConnectStatus(false)
  → comprobar resultado boolean → guardar ip.txt
  → ConfigForm.onConnected / ConfigForm2.onConnected
      → consultas solo cuando el formulario corresponde
acciones separadas de configuración, si el operador las solicita
  → setAntenna / setPower / setFrequencyMode / modo EPC
acción AUTO → startInventoryTag → callback de tags
acción STOP → stopInventory
acción Disconnect → stopInventory si formulario visible → free
```

[DEMO_SOURCE] Fuente: `UHFMainForm`:33–35,56–70,120–121,153–207,211–279; `ConfigForm`:45–56; `InventoryForm`:153–205,266–286. La selección de antenas y potencia **no se ejecuta obligatoriamente al conectar**: los setters pertenecen a acciones de usuario (§8). [JAR_SIGNATURE] La comunicación de inventario sigue sockets TCP; UDP existe para descubrimiento, no es la vía de tags de este flujo. [INFERENCIA] Proporcionar una IPv4 validada y un puerto configurado, sin trasladar direcciones de ejemplo a configuración EMI.

# 5. Flujo serial identificado

[SDK_DOC][DEMO_SOURCE][JAR_SIGNATURE] `new RFIDWithUHFSerialPortA4()` (C/UHFMainForm:226) → `setConnectionStateCallback(...)` (229) → `init(com)` (159). En Windows el demo enumera puertos con `gnu.io.CommPortIdentifier.getPortIdentifiers()` (284–288); en otros sistemas ofrece `/dev/ttyS1` y `/dev/ttyUSB0`, entre otros ejemplos (123–130). No se detectó puerto conectado durante esta tarea.

[JAR_SIGNATURE] `RFIDWithUHFSerialPortAxBase.init(String)` delega en `com.rscja.deviceapi.b`, cuya apertura llama `CommPortIdentifier.open(String,2000)` y `SerialPort.setSerialPortParams(115200,8,1,0)`: configuración RXTX convencional 115200, 8 bits, 1 stop, sin paridad. Es lo que programa el software; no prueba que coincida con el puerto del U300. No hay baud rate en la firma A4 pública `init(String)`.

[JAR_SIGNATURE][DEMO_SOURCE] Tras apertura aparecen `ReceiveDataThread` y `InventoryThread`, un `SerialPortEventListener`, estado CONNECTED y el mismo callback de tags. Inicio, recepción y parada usan `IUHFA4` como en Ethernet. `free()` envía deshabilitación de envío RS232, cierra el puerto y solicita parada de recepción/inventario. No hay implementación serial en EMI ni carga de DLL realizada aquí.

[SDK_DOC][PENDIENTE] W distingue dependencias Windows y Linux (§2). Faltan confirmar cable/interfaz eléctrica/conversor, driver de la unidad, permisos seriales Linux si aplicara y correspondencia de firmware. La inspección de PC no autoriza integración Android.

# 6. Inventario EPC

Respuestas a preguntas 11–17:

| Pregunta | Resultado y evidencia |
|---|---|
| 11. Inicio | [SDK_DOC][JAR_SIGNATURE][DEMO_SOURCE] `boolean startInventoryTag()`; usado en InventoryForm:156. Sobrecarga `boolean startInventoryTag(InventoryParameter)` confirmada; no usada en ese botón del demo. |
| 12. Continuo o bloqueante | [SDK_DOC] Inventario continuo hasta parada. [JAR_SIGNATURE] A4 envía comando, registra estado y retorna; no bloquea la llamada durante todo el inventario. Eso no garantiza que envío, conexión o comandos sean instantáneos: hay sincronización y operaciones de E/S. |
| 13. Single-shot | [SDK_DOC][JAR_SIGNATURE] `UHFTAGInfo inventorySingleTag()`, dato o null. Es alternativa síncrona, no el flujo AUTO observado. Null no separa inequívocamente ausencia de etiqueta de error de comunicación. |
| 14. Parada | [SDK_DOC][DEMO_SOURCE] `boolean stopInventory()`, InventoryForm:205; retorno no fiable como confirmación física, §12. |
| 15. Mecanismo | [SDK_DOC] Callback o polling excluyentes. [DEMO_SOURCE] el demo elige callback. [JAR_SIGNATURE] búfer interno y un hilo que llama `readTagFromBuffer()`; no se usa Observer como interfaz de tags del demo. |
| 16. Hilo de llegada | [JAR_SIGNATURE] `RFIDWithUHFNetworkAx$InventoryThread.run`, invocación `callback` offset 325; serial `RFIDWithUHFSerialPortAxBase$InventoryThread.run`, offset 163. No es el hilo principal ni una garantía pública sobre el nombre asignado al hilo. |
| 17. Bloqueo | [DEMO_SOURCE] botones realizan llamadas en manejadores Swing; callback ejecuta `EventQueue.invokeAndWait` (InventoryForm:274–281). [INFERENCIA] E/S puede congelar UI y UI lenta puede frenar entrega. Un wrapper debe desacoplar consumidor, persistencia y UI del callback. |

[SDK_DOC] `H/com/rscja/deviceapi/interfaces/IUHF.html`, detalle `startInventoryTag`: registrar callback antes del inicio; después del inicio el módulo solo responde a `stopInventory`. Su detalle `readTagFromBuffer` describe extracción no bloqueante con null si no hay datos. El ejemplo de polling del JavaDoc duerme si recibe null, pero luego intenta acceder al objeto sin `continue` ni `else`: **discrepancia/defecto del ejemplo**, no patrón que deba copiarse.

[JAR_SIGNATURE] Ruta trazada: `SocketManageAx$ReceiveDataThread` → manejador `com.rscja.deviceapi.c.g` / base `c.b` → búfer → `NetworkAx$InventoryThread` → `readTagFromBuffer` → parser `g/i/h` → callback. `c.b` construye `LinkedBlockingQueue(20480)` y extrae mediante `poll()`; `c.g` contiene una rama que hace `offer()` y descarta su booleano. [INFERENCIA] Existe riesgo de pérdida silenciosa cuando se llena esa cola; no se midieron saturación, caudal ni omisiones RF. La rama de entrega directa del manejador también existe: no extrapolar una política de cola idéntica a todas las modalidades del SDK.

[JAR_SIGNATURE] El hilo de inventario de red se inicia después de `init` exitoso; si no recibe tag espera hasta 100 ms. El callback se llama fuera del bloque que maneja `InterruptedException`; no hay envoltura general que proteja esa llamada frente a una excepción del consumidor. [INFERENCIA] El wrapper debe copiar datos a una estructura propia, entregar sin bloquear y contener sus errores. No depender de que el SDK recupere un hilo terminado por una excepción del consumidor.

[DEMO_SOURCE] El hilo creado en `InventoryForm.startInventory` (173–198) es un temporizador que actualiza segundos y puede detener por duración; **no es el hilo de recepción de tags**. `InventoryTableModel.addData` (23–32) agrupa por EPC y aumenta `count`; `getInsertIndex` (107–136) compara bytes EPC. No es deduplicación operativa EMI ni evidencia de movimiento. Lectura RFID cruda ≠ evento operativo.

# 7. Modelo de datos de lectura

Respuestas a preguntas 18–27. Clase: `com.rscja.deviceapi.entity.UHFTAGInfo`, mutable, con constructores vacío y de nueve parámetros (§19). «Disponible» indica API, no que toda lectura del U300 entregue ese campo.

| Campo / pregunta | Firma y representación | Procedencia / interpretación | Evidencia |
|---|---|---|---|
| EPC (18–20) | `String getEPC()`; `byte[] getEpcBytes()` | SDK: hexadecimal y bytes; conservar ceros iniciales, no convertir a entero | [SDK_DOC] H/UHFTAGInfo:406; [JAR_SIGNATURE]; [DEMO_SOURCE] tabla:73,119 |
| RSSI (21) | `String getRssi()` | SDK: representación textual; guardar valor original. JavaDoc no precisa unidad, locale ni calibración | [SDK_DOC] H/UHFTAGInfo:489; [PENDIENTE] interpretación física |
| Antena (22) | `String getAnt()` | SDK: identificador ANT; no localización espacial | [SDK_DOC] H/UHFTAGInfo:512; [DEMO_SOURCE] tabla:83 |
| TID (23) | `String getTid()`, `byte[] getTidBytes()` | SDK; hexadecimal, condicionado al modo de inventario | [SDK_DOC][JAR_SIGNATURE]; [DEMO_SOURCE] ConfigForm:541 |
| USER (24) | `String getUser()`, `byte[] getUserBytes()` | SDK; memoria USER opcional, depende de modo/dirección/longitud | [SDK_DOC][JAR_SIGNATURE]; [DEMO_SOURCE] ConfigForm:559 |
| Count (25) | `int getCount()` / `setCount(int)` | POJO inicializa 1; demo incrementa por EPC. No contador de eventos ni prueba de conteo físico independiente | [JAR_SIGNATURE] constructor UHFTAGInfo offsets 5–6; [DEMO_SOURCE] tabla:24,29 |
| Timestamp (26) | `long getTimestamp()` / `setTimestamp(long)` | En parser A4 inspeccionado: milisegundos de reloj del PC al parsear, asignados por SDK. No fecha emitida por lector | [JAR_SIGNATURE] cadena de parser descrita abajo; [SDK_DOC] firma H/UHFTAGInfo:668 sin semántica |
| PC (27) | `String getPc()` | SDK: PC hexadecimal del tag | [SDK_DOC][JAR_SIGNATURE] UHFTAGInfo |
| Fase (27) | `int getPhase()` | API documenta 0–359; `InventoryParameter.setNeedPhase(boolean)` solicita inclusión. Valor por defecto no prueba metadata válida | [SDK_DOC] H/UHFTAGInfo:383; [JAR_SIGNATURE] InventoryParameter y parser |
| Frecuencia (27) | `float getFrequencyPoint()` | Solicitud `setNeedFrequencyPoint(boolean)`; unidad y soporte efectivo deben confirmarse antes de uso físico | [JAR_SIGNATURE]; [PENDIENTE] unidad/firmware |
| Otros (27) | `getReserved()`, `getIndex()`, `getM775AuthenticationInfo()` | API genérica; no necesarios para contrato mínimo | [JAR_SIGNATURE] UHFTAGInfo; FUERA_DEL_MVP en esta integración |
| `origen_datos` | No equivalente EMI identificado | Campo agregado por EMI. El modo simulado y el hardware real deben mantenerse separados | [PENDIENTE] no campo SDK equivalente; [INFERENCIA] mapeo propio |
| `timestampRecepcion` propio | Reloj UTC de aplicación | Agregado por EMI al aceptar callback. Distinto de tiempo de parseo SDK y de tiempo físico RF | [INFERENCIA] propuesta de contrato |

**Trazabilidad del timestamp.** [JAR_SIGNATURE] `NetworkAx.readTagFromBuffer()` invoca `g.a(c.c,TagInfoRule)` en offset 68. El método está heredado de `i`, que toma el payload y llama al parser estático `a(byte[],TagInfoRule)` heredado de `h` (offset 22). En `h.a(byte[],TagInfoRule)`, offsets **383 y 386**: `System.currentTimeMillis()` → `UHFTAGInfo.setTimestamp(long)`. La serial utiliza el mismo parser. Por tanto, su origen host queda demostrado para esa ruta de decodificación, aunque el JavaDoc del getter no lo explique. No generalizar a todas las rutas USB/BLE/JSON de la librería.

[INFERENCIA] Propuesta: conservar `timestampSdkParseo` como metadata técnica opcional y generar `timestampRecepcion` explícito en la aplicación. Un eventual `timestamp` común puede representar recepción UTC, siempre documentando esa decisión. No usar `getReaderDateTime()` como si fuera el timestamp de cada tag: es una consulta separada de configuración, utilizada en `ConfigForm2`:190,208.

# 8. Antenas y potencia

| Pregunta | Resultado |
|---|---|
| 28. Cantidad A4 | [JAR_SIGNATURE] `AntennaNameEnum` tiene ANT1–ANT16: enum compartido, no prueba de 16 puertos A4. [DEMO_SOURCE] `ConfigForm`:54–90 pregunta `getReaderInfo().getAntennaNumber()` y oculta controles 5–8 cuando devuelve 4; el demo contempla 4 u 8 controles. [PENDIENTE] máximo realmente admitido por la combinación U300/firmware; debe confirmarse con ficha/unidad y consulta real. No inferir exactamente cuatro por el sufijo. |
| 29. Habilitación | [SDK_DOC][JAR_SIGNATURE] `boolean setAntenna(List<AntennaState>)`, `List<AntennaState> getAntenna()`; `new AntennaState(AntennaNameEnum,boolean)`. [DEMO_SOURCE] ConfigForm:294–304 construye ANT1–ANT8 según checkboxes y envía lista. No copiar antenas inexistentes. |
| 30. Origen de lectura | [SDK_DOC][DEMO_SOURCE] `UHFTAGInfo.getAnt()`; tabla del demo:83. [PENDIENTE] correspondencia observada con conector físico. |
| 31. Potencia por antena | [SDK_DOC][JAR_SIGNATURE] `boolean setPower(AntennaNameEnum,int)`, `int getPower(AntennaNameEnum)`, `List<AntennaPowerEntity> getPowerAll()`. [DEMO_SOURCE] ConfigForm:164–234 envía índice seleccionado + 1; no es recomendación de potencia para EMI. |

[SDK_DOC][JAR_SIGNATURE] **Discrepancia de JavaDoc:** `getPowerAll()` está tipado como lista, pero su texto dice «single antenna» y retorno «-1». El demo comprueba una lista y recorre entidades (ConfigForm:112–148). Priorizar firma + uso; no convertir una lista en un entero ni depender de la descripción de error copiada. [PENDIENTE] límites por antena, unidades precisas y rango autorizado del equipo deben confirmarse; no se estableció potencia óptima.

# 9. Configuración RF

Respuestas a preguntas 32–34:

| Configuración | API y alcance demostrado | Criterio para primera lectura / evidencia |
|---|---|---|
| Potencia | `setPower`, `getPower`, `getPowerAll` | [SDK_DOC][DEMO_SOURCE] API disponible. [PENDIENTE] verificar valor real y configuración autorizada; ningún valor óptimo deducible aquí. |
| Región / banda | `int getFrequencyMode()`, `boolean setFrequencyMode(byte)` | [SDK_DOC] lista de perfiles por región, H/NetworkA4:1630. [PENDIENTE] perfil aplicable a Colombia y autorizado para unidad; no equiparar automáticamente Colombia con USA/0x08. |
| Frecuencia / hopping | `boolean setFreHop(float)` | [SDK_DOC] texto «frequency Hop» pero parámetro descrito como punto de frecuencia, H:2194. [PENDIENTE] no se documenta aquí una tabla de hopping, dwell por canal ni unidad inequívoca. No deducir comportamiento completo por nombre. |
| Protocolo / RF link | `setProtocol(int)`, `getProtocol()`, `setRFLink(int)`, `getRFLink()` | [DEMO_SOURCE] ConfigForm:427–493. [INFERENCIA] registrar valores efectivos; no optimizar para el escenario sin RF_REAL. |
| Session Gen2 | `getGen2()`, `setGen2(Gen2Entity)`, `get/setQuerySession(int)` | [JAR_SIGNATURE]; [DEMO_SOURCE] ConfigForm:640–667 consulta/modifica entidad. [INFERENCIA] conservar ajuste conocido inicialmente y documentarlo. |
| Q | `get/setQ`, `get/setStartQ`, `get/setMinQ`, `get/setMaxQ` de Gen2Entity | [JAR_SIGNATURE]. [PENDIENTE] semántica de cada selector/rango en firmware y valores efectivos. |
| Trabajo/espera | `setPwm(int WorkTime,int WaitTime)`, `getPwm()` | [SDK_DOC] ambos 0–255 ms; es ciclo de trabajo/espera. No equivale automáticamente a dwell time por antena/canal. |
| Dwell time | `DeviceAPI.UHFDwell(int,int)` aparece en documentación genérica | [SDK_DOC] H/DeviceAPI:6390; [PENDIENTE] no es método confirmado en interfaz A4 pública ni demo A4. No usar la API nativa general como atajo. |
| Modo de datos | `setEPCMode`, `setEPCAndTIDMode`, `setEPCAndTIDUserMode` | [DEMO_SOURCE] ConfigForm:534,541,559. [INFERENCIA] EPC basta para primera observación; TID/USER son opcionales. |
| Modo de disparo | `get/setTriggerInventoryMode` | [SDK_DOC] 0=comando, 1=GPI; [DEMO_SOURCE] ConfigForm2:127,154. [INFERENCIA] verificar modo coherente con inicio explícito; no activar GPIO como ampliación del MVP. |
| Filtros/reportes/caché | `setFilter`, `get/setDuplicateTagFilter`, `get/setRssiFilter`, `get/setTagReportingMode`, `get/setAfterNetworkDisconnected` | [DEMO_SOURCE] InventoryForm:140; ConfigForm2:250–376. [INFERENCIA] registrar estado antes de interpretar ausencia/repetición/antigüedad de observaciones. |

[INFERENCIA] Mínimo a **verificar**, no una receta de optimización: endpoint/puerto o COM correctos; unidad y firmware compatibles; región autorizada; antena físicamente conectada y habilitada; potencia documentada; modo de inventario por comando y EPC; callback registrado; filtros y caché conocidos; manejo de parada y cierre. Un `init=true` no comprueba todo ello.

[INFERENCIA][PENDIENTE] Session, Q, RF link, fase, TID/USER, PWM y modo rápido no necesitan ajustes experimentales para diseñar el wrapper. Pueden conservar valores efectivos documentados si el fabricante confirma que sirven para la prueba inicial. **No hay valores predeterminados de fábrica verificados aquí**, y no puede dejarse región/potencia sin conocer su estado. Rango, interferencias, metal, antenas y confiabilidad siguen RF_REAL PENDIENTE.

# 10. Dependencias y runtime

| Pregunta | Resultado y evidencia |
|---|---|
| 35. ¿ReaderAPI basta para Ethernet? | [SDK_DOC] W lo afirma explícitamente. [JAR_SIGNATURE] `jdeps` encuentra referencia externa a `net.sf.json.JSONObject` en `g`, parser compartido A4; su constructor es Java simple y las referencias JSON aparecen en métodos específicos, por lo que ello **no demuestra** que inventario EPC básico requiera JSON. [PENDIENTE] mínimo real por ejecución aislada. No declarar ni suficiencia universal ni obligatoriedad universal de JSON. |
| 36. ¿RXTX solo serial? | [SDK_DOC] W lo sitúa en serial Windows/Linux. [JAR_SIGNATURE] `SerialPortAxBase`/`b` usan `gnu.io`; no se identificó referencia RXTX directa en la ruta A4 TCP trazada. [DEMO_SOURCE] el demo completo importa `gnu.io` y lo empaqueta; no confundir esa aplicación con el SDK mínimo Ethernet. |
| 37. ¿Otros nativos? | [JAR_SIGNATURE][APP_CONFIG] JNA incluye `jnidispatch` para distintos sistemas/arquitecturas; A los incorpora. DLL externas solo RXTX AMD64. No se hallaron binarios externos propietarios USB/BLE ni SO serial Linux en este árbol. |
| 38. ¿JNI? | [JAR_SIGNATURE] Sí en distribución general: RXTX/JNA y métodos `native` de DeviceAPI. No se identificó carga JNI obligatoria en el flujo A4 TCP básico. [PENDIENTE] corroborar mediante prueba software aislada; no cargar APIs generales innecesarias. |
| 39. Java demo | [SDK_DOC] W: desarrollo JDK 1.8.0_131 y ejecución 1.8 o superior. [JAR_SIGNATURE] clases ReaderAPI major 52; [APP_CONFIG] `com/uhf/UHFMainForm.class` major 52. [DEMO_SOURCE] build.gradle no fija `sourceCompatibility`/`targetCompatibility`; wrapper 6.8. |
| 40. Java 21 | [INFERENCIA][PENDIENTE] Compatibilidad parcial: formato legible y dependencias analizables; faltan carga, E/S, serial nativo, concurrencia y recuperación. Riesgos concretos en §11/16. |

[APP_CONFIG] APP contiene un fat JAR y manifiesto `Main-Class: com.uhf.UHFMainForm`; no un JRE/JDK incluido. W habla de abrir `URA4Demo_CN`, pero el nombre encontrado es `URA4DemoV1.4.jar`: discrepancia nominal, no error de ejecución probado. No hay script de arranque independiente dentro de APP hallado como archivo externo. Sus nativos JNA soportan varias plataformas; ello no demuestra soporte completo del U300 para cada plataforma.

[JAR_SIGNATURE] `jdeps --missing-deps` con `D/libs/*` no emitió dependencias faltantes del JAR analizado; sí avisó de paquete dividido `org.w3c.dom` entre `java.xml` y `xom-1.2.6.jar`. Ese comando no verifica las dependencias transitivas por reflexión, carga nativa, todos los caminos del demo ni permisos de redistribución. No se alteró `pom.xml` ni se incorporaron JAR al repositorio.

# 11. Compatibilidad con Java 21

[JAR_SIGNATURE][INFERENCIA] El bytecode major 52 del SDK corresponde a Java 8 y fue leído por herramientas del JDK 21 local. Esto favorece viabilidad de compatibilidad binaria, pero **no equivale a ejecutar correctamente el SDK sobre Java 21**. El manifiesto de ReaderAPI solo tiene `Manifest-Version: 1.0`, sin versión de SDK declarada ni matriz formal de soporte.

| Capa | Evidencia | Evaluación |
|---|---|---|
| Firmas y bytecode | [JAR_SIGNATURE] `javap` y cabeceras ZIP | Inspección satisfactoria; no prueba funcional |
| Java de demo | [SDK_DOC] W 1.8.0_131, requisito 1.8+ | Declaración genérica, no certificación explícita Java 21 |
| Build Gradle 6.8 | [DEMO_SOURCE] wrapper properties | [INFERENCIA] No soportado para ejecutar sobre Java 21; soporte oficial desde Gradle 8.5 ([notas oficiales](https://docs.gradle.org/8.5/release-notes.html)). No afecta la elección Maven de EMI ni justifica migrarla. |
| RXTX x64 | [JAR_SIGNATURE] DLL `0x8664`, JAR antiguo | [PENDIENTE] carga JNI y comportamiento con JDK21/Windows AMD64; incompatible una mezcla de arquitecturas |
| JSON/Commons/XOM/JNA | [DEMO_SOURCE][JAR_SIGNATURE] lista y `jdeps` | [INFERENCIA] aislar conjunto mínimo antes de incorporar; versiones viejas, sin auditoría de vulnerabilidades realizada |
| Observer serial | [JAR_SIGNATURE] `java.util.Observer` | [INFERENCIA] obsoleto desde Java 9, todavía disponible en [Java SE 21](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/Observer.html). Obsolescencia no es ausencia ni incompatibilidad demostrada. |
| Módulos Java | [JAR_SIGNATURE] aviso XOM/`java.xml` | [INFERENCIA] evaluar classpath mínimo; no copiar módulo/paquete del demo a EMI por conveniencia |
| Concurrencia y cierre | [JAR_SIGNATURE] hilos, callbacks, flags y sincronización | [PENDIENTE] verificar recuperación, ausencia de bloqueo y fin de callbacks en pruebas software autorizadas |

[INFERENCIA] Siguiente validación software sugerida, fuera de esta entrega: carga aislada sin hardware, classpath mínimo Ethernet frente al conjunto completo, control de métodos empleados y errores de carga; después pruebas autorizadas de lifecycle. Si se usa un transporte artificial en esa validación, etiquetarlo como prueba software, nunca RF_REAL. No se propone cambiar Java 21, Maven, JDBC, SQLite, Flyway ni JUnit 5.

# 12. Manejo de errores y desconexión

[SDK_DOC][JAR_SIGNATURE] Las firmas relevantes no declaran excepciones checked específicas del lector; predominan boolean, null y estado. La ausencia de `throws` no garantiza ausencia de RuntimeException, errores de linkage o fallas nativas. El demo muestra diálogos ante `init=false` y `startInventoryTag=false`; no construye un modelo de errores de dominio.

| Hallazgo | Evidencia trazable | Consecuencia para futuro wrapper |
|---|---|---|
| `init` red valida conexión de socket, no EPC | [JAR_SIGNATURE] NetworkAx.init(String,int), llamada SocketManageAx.connect offset 83, creación de hilo offset 132 | [INFERENCIA] separar conectado de observación recibida y de inventario confirmado |
| `startInventoryTag` retorna éxito de envío | [JAR_SIGNATURE] NetworkAx.startInventoryTag(int,int,int), send offsets 114–118; serial base 64–67 | [INFERENCIA] no es acuse de etiqueta ni desempeño físico |
| **Parada retorna true aun descartando envío** | [JAR_SIGNATURE] NetworkAx.stopInventory: send 54, pop 57, true 87; serial base: send 10, pop 13, true 19 | [INFERENCIA] registrar solicitud de parada; no afirmar confirmación física a partir del boolean |
| `free` cierra recursos y retorna true | [JAR_SIGNATURE] NetworkAx.free: SocketManageAx.close 51, pop 54, stopInventoryThread 61, true 86; pausas 20/500 ms | [INFERENCIA] usar finalmente de cierre, pero verificar postcondiciones localmente; duración total no acotada por esas pausas |
| Hilo inventario no hace join al parar | [JAR_SIGNATURE] NetworkAx.stopInventoryThread llama stopThread; InventoryThread.stopThread establece flag y notifyAll, sin join | [INFERENCIA] puede haber entrega en curso; invalidar generación/sesión de captura y evitar aceptar callbacks tardíos |
| Callback puede lanzar excepción | [JAR_SIGNATURE] InventoryThread.run llama callback directamente, sin catch general alrededor | [INFERENCIA] contener errores de aplicación y evitar perder silenciosamente el hilo |
| Comprobación automática desactivada | [DEMO_SOURCE] UHFMainForm:184 | [PENDIENTE] definir política de detección y probarla; no copiar ciegamente el demo |
| Recuperación manual del demo | [DEMO_SOURCE] UHFMainForm:261–277 | [INFERENCIA] si se diseña reconexión, debe ser explícita, acotada y distinguir observaciones antiguas |
| Caché después de desconexión | [SDK_DOC][JAR_SIGNATURE] `AfterNetworkDisconnectedEntity(boolean cacheTags)`, `isCacheTags`; [DEMO_SOURCE] ConfigForm2:250–267 | [PENDIENTE] persistencia, límite y reenvío exactos en firmware. El timestamp host puede corresponder al parseo posterior de datos viejos. |
| Bloqueo de UI y recursos compartidos | [DEMO_SOURCE] callback cambia modelo antes de invokeAndWait; lista mutable y temporizador separado | [INFERENCIA] no trasladar el modelo Swing a EMI; serializar lifecycle y consumidor propio |

**CONTRADICCIÓN DETECTADA — semántica de éxito de parada.** Fuente: JavaDoc de `stopInventory` (`H/.../RFIDWithUHFNetworkA4.html`:1721, contrato true/false) frente al bytecode indicado. Impacto: retorno positivo no acredita detención física ni ausencia de datos posteriores. Decisión a evaluar posteriormente: cómo expresar «parada solicitada», cierre local y fallo de transporte en el contrato de fuente. Es una discrepancia interna del SDK; no modifica ninguna DEC del proyecto.

[PENDIENTE] Sin ensayo no quedan determinados plazo de detección de cable desconectado, reconexión fiable, orden de callbacks de estado entre hilos, repetición tras reconectar ni garantía de vaciado. El parámetro `Object` del callback de conexión carece de contrato de error suficientemente descrito para mapearlo sin validación.

# 13. Métodos deprecated

[SDK_DOC] `H/deprecated-list.html` lista `com.rscja.utility.StringUtility.isHexNumber(String)` y `com.rscja.deviceapi.RFIDWithUHFNetworkUR4.readTagFromBuffer()`. **No lista el método A4 de igual nombre.** No debe confundirse UR4 con A4 ni convertir una preferencia por callbacks en una anotación deprecada inexistente para A4.

[JAR_SIGNATURE][INFERENCIA] La serial implementa `java.util.Observer`; su obsolescencia en Java 21 se documenta en la fuente oficial citada en §11. Se encapsula dentro del fabricante. La lista local deprecated no basta para garantizar una auditoría exhaustiva de cada dependencia transitiva; alcance de esta sección: APIs relevantes y lista JavaDoc entregada.

# 14. Mapeo SDK → FuenteLecturasRFID

[INFERENCIA] Evaluación preliminar de adaptación, sin implementación y sin adelantar el orden autorizado `FuenteLecturasRFID` → `FuenteSimulada` → incrementos posteriores → adaptador real. El DTO siguiente es conceptual, no una entidad persistida ni una modificación al modelo aprobado.

| Punto | Clasificación | Mapeo / tratamiento | Evidencia |
|---|---|---|---|
| Inicialización | ADAPTACIÓN | Configuración propia de endpoint/COM; fabricante queda dentro del wrapper | [SDK_DOC][JAR_SIGNATURE] init, §4–5 |
| Inicio | ADAPTACIÓN | `startInventoryTag`; estado propio distingue envío de observación | [JAR_SIGNATURE] §12 |
| Parada | PROBLEMÁTICO | Boolean no confirma éxito; gestionar aceptación local y cierre separado | [JAR_SIGNATURE] §12 |
| Entrega asíncrona | ADAPTACIÓN | `setInventoryCallback`; copia inmutable y consumidor desacoplado | [DEMO_SOURCE][JAR_SIGNATURE] §6 |
| EPC | DIRECTO | `getEPC()` a epc propio, validar sin perder ceros | [SDK_DOC] §7 |
| Timestamp | ADAPTACIÓN | Timestamp recepción propio; SDK parseo host opcional, sin fingir tiempo físico | [JAR_SIGNATURE] §7 |
| Origen | ADAPTACIÓN | Etiqueta propia SIMULACION / RF_REAL según fuente efectiva y procedencia comprobada | [INFERENCIA] no equivalente SDK |
| RSSI/antena | ADAPTACIÓN | Preservar valor original; parsear solo con convención confirmada | [SDK_DOC][PENDIENTE] §7 |
| Fase/frecuencia/TID/USER | ADAPTACIÓN | Opcionales condicionados a configuración; no valores ficticios por defecto | [SDK_DOC][JAR_SIGNATURE] §7–9 |
| Errores | PROBLEMÁTICO | Boolean/null y callback de estado; normalización propia de causas conocidas | [SDK_DOC][JAR_SIGNATURE] §12 |
| Cierre de recursos | PROBLEMÁTICO | `free`, fin local y callbacks tardíos; falta garantía de join | [JAR_SIGNATURE][PENDIENTE] §12 |
| Reconexión/cache | DESCONOCIDO | No se ha probado continuidad ni reenvío; requiere política específica | [PENDIENTE] §12 |

```text
LecturaRFIDCruda propia (propuesta)
  epc: cadena hexadecimal validada
  timestamp: instante UTC de recepción en aplicación (semántica explícita)
  origen_datos: procedencia propia de la fuente
  metadataFisicaOpcional:
    antenaSdk, rssiSdk, tid, user, pc
    fase/frecuencia solo cuando solicitadas, presentes y válidas
  metadataTecnicaOpcional:
    timestampSdkParseoHost, identificador de fuente/configuración
```

[INFERENCIA] `count` del demo no genera eventos ni reemplaza observaciones. El wrapper no interpreta entradas/salidas, movimientos, verificación, sustitución o ubicación. La capa posterior aplica deduplicación y reglas operativas. Campos Chainway permanecen fuera de contratos del negocio.

# 15. Pseudocódigo mínimo de integración

**Propuesta conceptual, no ejecutada ni implementada.** Nombres de métodos Chainway usados abajo están confirmados en J y H (§19); acciones descriptivas no son API SDK. No se inventan `connect`, `close`, `awaitStopped`, listeners o excepciones del fabricante.

```text
en controlador propio de lifecycle, fuera de callbacks SDK:
  verificar configuración autorizada y compatibilidad de unidad/firmware
  crear lector = new RFIDWithUHFNetworkA4()
    alternativa serial = new RFIDWithUHFSerialPortA4()
  preparar manejo propio de estados y de errores
  lector.setConnectionStateCallback(callback de estado)
    callback de estado:
      registrar estado; programar recuperación/cierre fuera del callback
      no ejecutar aquí tareas largas ni cierre reentrante
  intentar:
    resultado = lector.init(ipValidada, puertoConfigurado)
      alternativa serial: lector.init(puertoSerial)
    si resultado es false: registrar fallo y terminar por finalmente
    comprobar lector.getConnectStatus() == ConnectionState.CONNECTED
    decidir explícitamente política setAutoCheckConnectStatus(boolean)
    consultar configuración antes del inventario
    si es necesario y está autorizado:
      lector.setAntenna(lista de AntennaState para antenas existentes)
      lector.setPower(antena, potencia autorizada)
      lector.setFrequencyMode(perfil previamente confirmado)
      lector.setEPCMode()
      comprobar cada resultado; error → finalmente
    lector.setInventoryCallback(callback de tag)
      callback de tag:
        capturar instante de recepción de aplicación
        si el ciclo local ya no acepta datos: registrar descarte técnico y salir
        si tag es null o tag.getEPC() no es válido: registrar error y salir
        copiar tag.getEPC(), getAnt(), getRssi() y metadata opcional válida
        conservar getTimestamp() como parseo host del SDK si interesa
        crear LecturaRFIDCruda propia con procedencia comprobada
        entregar sin bloqueo a la capa superior mediante mecanismo propio
        contener excepciones del consumidor
        no crear movimientos ni deduplicar eventos operativos aquí
    habilitar aceptación del ciclo local
    intentar inicio = lector.startInventoryTag()
    si inicio es false: registrar fallo y terminar por finalmente
    mantener captura hasta solicitud de parada o fallo
  finalmente:
    deshabilitar aceptación del ciclo local
    si hubo intento de inventario:
      intentar lector.stopInventory()
      registrar que su true NO confirma parada física
    en un finalmente independiente:
      intentar lector.free() aunque falle la parada
      cerrar recursos propios y descartar callbacks de ciclos anteriores
      documentar postcondiciones locales y fallos
      confirmación de parada física: PENDIENTE_DE_CONFIRMAR
```

[INFERENCIA] Esta secuencia no es una autorización de RF_REAL ni una especificación final. La exclusión de callbacks tardíos, la estrategia de cola y el cierre deben diseñarse y probarse antes de integración. No se configura IP, potencia o región real en esta inspección.

# 16. Riesgos

Probabilidades son estimaciones de ingeniería `[INFERENCIA]`, sin frecuencia empírica; «desconocida» significa falta de prueba. «Bloquea P1» se refiere al cierre reproducible del gate, no a impedir avanzar la inspección. Un «sí» puede ser condicional al transporte elegido.

| ID | Riesgo | Evidencia | Probabilidad | Impacto | Mitigación propuesta | Bloquea P1: sí/no |
|---|---|---|---|---|---|---|
| R01 | Modelo/firmware U300 no correlacionado inequívocamente con paquete A4 | [SDK_DOC] W nombra A4; [PENDIENTE] ficha/unidad/firmware | Desconocida | Alto | Confirmación del proveedor y registro de identidad/configuración | Sí |
| R02 | Runtime Java 21 no verificado funcionalmente | [SDK_DOC] Java8; [JAR_SIGNATURE] major52 | Desconocida | Alto | Prueba aislada del classpath y lifecycle en stack vigente | Sí para integración reproducible |
| R03 | Wrapper demo Gradle6.8 no soporta JDK21 | [DEMO_SOURCE] wrapper; fuente oficial §11 | Alta si se intenta ese build | Medio | Conservar Maven EMI; tratar el build del demo separadamente si se solicita | No, no es dependencia EMI |
| R04 | Confundir declaración «solo ReaderAPI» con mínimo probado | [SDK_DOC] W; [JAR_SIGNATURE] `g` referencia JSON | Desconocida | Alto | Verificar rutas utilizadas con classpath mínimo sin agregar JAR por defecto | Sí si impide ejecutar ruta elegida |
| R05 | RXTX/driver/arquitectura o Linux SO faltante | [JAR_SIGNATURE] DLL AMD64; [SDK_DOC] W; SO ausente | Desconocida; archivo Linux faltante confirmado | Alto para serial | Confirmar plataforma, driver y binarios adecuados con proveedor | Sí si serial; no para Ethernet confirmado |
| R06 | Arrastre de JNA/Commons/XOM y dependencias antiguas | [DEMO_SOURCE] build:28–39; `jdeps` paquete dividido | Media | Medio/alto | Minimizar dependencias, revisar uso/licencias y compatibilidad; no auditoría CVE fingida | No por sí solo |
| R07 | Callback lento, excepción o cola saturada | [JAR_SIGNATURE] InventoryThread/queue; [DEMO_SOURCE] invokeAndWait | Media | Alto | Copia breve, aislamiento de errores, capacidad/contabilidad propias y pruebas software | Sí si causa pérdida no controlada |
| R08 | Falso éxito de stop y cierre con callback en curso | [JAR_SIGNATURE] §12 | Alta para sobreinterpretar el retorno | Alto | Separar solicitud, cierre local y confirmación; probar lifecycle | Sí para ciclo reproducible |
| R09 | Desconexión no detectada / reconexión manual / datos viejos cacheados | [DEMO_SOURCE] autoCheck=false; [JAR_SIGNATURE] caché | Desconocida | Alto | Política explícita, pruebas autorizadas de desconexión y trazabilidad de origen temporal | Sí si impide reproducibilidad |
| R10 | Región/potencia/antena reales sin documentar | [PENDIENTE] hardware/configuración | Desconocida | Alto | Confirmar configuración autorizada; no proponer óptimo físico | Sí |
| R11 | Timestamp host confundido con captura física | [JAR_SIGNATURE] h offsets383–386 | Alta si se mapea sin semántica | Alto | Timestamp recepción explícito y metadata separada | No para inspección; corregir antes de uso |
| R12 | Count o filtro del SDK confundido con eventos | [DEMO_SOURCE] tabla:23–32; [SDK_DOC] filtros | Media | Alto | Solo observaciones; eventos/deduplicación en capas posteriores | No para primera EPC; sí para validez operativa posterior |
| R13 | Permisos de uso y redistribución ReaderAPI desconocidos | [PENDIENTE] §17 | Desconocida | Alto | Confirmar por escrito; no versionar/publicar binarios | No necesariamente para inspección; uso/distribución pendientes |
| R14 | Copiar APIs obsoletas o ejemplos defectuosos | [SDK_DOC] deprecated y ejemplo null; [JAR_SIGNATURE] Observer | Media | Medio | Wrapper sobre interfaces públicas correctas, pruebas de errores | No por sí solo |
| R15 | Suponer número de antenas por sufijo/enum | [DEMO_SOURCE] consulta 4/8; [JAR_SIGNATURE] enum16 | Media | Alto | Consultar modelo/capacidades reales antes de enviar lista | Sí si configuración inválida |

# 17. Licencia / redistribución

**LICENCIA / REDISTRIBUCIÓN: PENDIENTE DE CONFIRMACIÓN**

[JAR_SIGNATURE][SDK_DOC] Búsqueda en árbol de fuentes, JavaDoc, textos de configuración, manifiestos y documentación W: no se identificó licencia o autorización expresa de Chainway para versionar, publicar o redistribuir ReaderAPI. J contiene manifiesto mínimo, sin archivo de licencia hallado en sus entradas. La entrega de código demo no demuestra permiso de redistribución del SDK. El RAR fue identificado por hash, pero su contenido interno no fue auditado de nuevo.

[APP_CONFIG] A incluye avisos JNA y Apache Commons (BeanUtils, Collections, Lang, Logging), con entradas LICENSE/NOTICE incluso repetidas por ensamblado del fat JAR. Identifican componentes de terceros; **no permiten atribuir sus licencias a ReaderAPI**. No se hizo una auditoría jurídica ni se determinó cobertura completa de licencias de RXTX/XOM/JXL y demás transitivas.

[PENDIENTE] Pedir al proveedor condiciones de uso, distribución de aplicaciones que incorporen SDK, publicación en repositorio privado/público, distribución de DLL/JAR y disponibilidad de versiones/correcciones. [INFERENCIA] Conservar custodia local y hashes; no incluir binarios del fabricante en commits mientras no se confirme permiso. En esta tarea no se realizó ningún commit.

# 18. Preguntas pendientes

| Pendiente | Evidencia que falta / forma de resolver |
|---|---|
| Identidad U300 | [PENDIENTE] Confirmación Chainway de este paquete/version/hash para modelo, revisión y firmware concretos |
| Capacidad de antenas | [PENDIENTE] Ficha y `ReaderInfo` del hardware; relacionar `getAnt()` con conector físico |
| Configuración física autorizada | [PENDIENTE] Región aplicable, potencia, antena, cable y configuración del punto; no proponer óptimo |
| Endpoint real | [PENDIENTE] IP/puerto efectivos y modo del lector; valores demo no son prueba |
| Serial | [PENDIENTE] Conector/interfaz, driver, ajuste de puerto y binarios Linux si esa plataforma se elige |
| Java21 mínimo | [PENDIENTE] Prueba de carga y ejecución de ruta básica con ReaderAPI solo y dependencias justificadas |
| Metadata física | [PENDIENTE] Unidad/formato RSSI y frecuencia, disponibilidad de fase/TID/USER según firmware/modo |
| Error y parada | [PENDIENTE] Política fiable para fallas, callbacks tardíos, cierre y confirmación de parada real |
| Detección/reconexión/caché | [PENDIENTE] Detección de pérdida, recuperación, límite/retención de caché y tratamiento de datos previos |
| Saturación | [PENDIENTE] Capacidad efectiva del flujo y contabilización de pérdidas software; diferente de omisiones RF |
| Licencia | [PENDIENTE] Permisos de uso, Git y redistribución; §17 |
| Custodia completa | [PENDIENTE] Cotejo RAR versus extracción y confirmación de procedencia por fabricante |

[INFERENCIA] Estas preguntas delimitan lo que la inspección no demuestra. No se solicita ampliar el MVP, rediseñar arquitectura, integrar AM, introducir RTLS, cloud, microservicios, múltiples puntos o despliegue institucional. Funciones SDK no necesarias se mantienen FUERA_DEL_MVP.

# 19. Archivos y clases críticos encontrados

## Matriz de trazabilidad de métodos

Las firmas omiten `java.lang` y prefijos de entidades para legibilidad; los tipos pertenecen a `com.rscja.deviceapi` y `com.rscja.deviceapi.entity/interfaces` según §3. «A4 ambas» significa fachadas red y serial públicas; la implementación puede ser heredada. Confianza ALTA se limita a firma/uso estático, no a operación física. «Media» señala comportamiento o soporte incompleto.

| Clase | Método | Firma | Fuente | Archivo / localización | Uso observado | Confianza |
|---|---|---|---|---|---|---|
| RFIDWithUHFNetworkA4 | Constructor | `public RFIDWithUHFNetworkA4()` | [JAR_SIGNATURE][DEMO_SOURCE] | J; C/UHFMainForm:256 | Crear lector red | Alta |
| RFIDWithUHFSerialPortA4 | Constructor | `public RFIDWithUHFSerialPortA4()` | [JAR_SIGNATURE][DEMO_SOURCE] | J; C/UHFMainForm:226 | Crear lector serial | Alta |
| Red A4 | init | `synchronized boolean init(String,int)` | [SDK_DOC][JAR_SIGNATURE][DEMO_SOURCE] | H/NetworkA4:1112; C/UHFMainForm:183 | Apertura TCP con endpoint UI | Alta |
| Red A4 | init/setPort | `boolean init(String)`, `void setPort(int)` | [SDK_DOC][JAR_SIGNATURE] | H/NetworkA4:1092; J | Alternativa documentada, no la llamada principal demo | Alta firma |
| Serial A4 | init | `boolean init(String)` | [SDK_DOC][JAR_SIGNATURE][DEMO_SOURCE] | H/SerialPortA4 detalle init; C/UHFMainForm:159 | Apertura de COM | Alta |
| A4 ambas | getConnectStatus | `ConnectionState getConnectStatus()` | [SDK_DOC][JAR_SIGNATURE][DEMO_SOURCE] | H/NetworkA4:1147; C/form/ConfigForm:52 | Consultar estado antes de datos | Alta |
| A4 ambas | setConnectionStateCallback | `void setConnectionStateCallback(ConnectionStateCallback)` | [JAR_SIGNATURE][DEMO_SOURCE] | J; C/UHFMainForm:229,259 | Registrar cambios de conexión | Alta |
| ConnectionStateCallback | getState | `void getState(ConnectionState,Object)` | [JAR_SIGNATURE][DEMO_SOURCE] | J; C/UHFMainForm:261 | CONNECTED/DISCONNECTED, Object sin significado estable identificado | Alta firma / media semántica |
| A4 ambas | setInventoryCallback | `void setInventoryCallback(IUHFInventoryCallback)` | [SDK_DOC][JAR_SIGNATURE][DEMO_SOURCE] | H/NetworkA4:2840; C/form/InventoryForm:269 | Registrar consumidor tras CONNECTED | Alta |
| IUHFInventoryCallback | callback | `void callback(UHFTAGInfo)` | [JAR_SIGNATURE][DEMO_SOURCE] | J; C/form/InventoryForm:271 | Entregar tag y actualizar tabla | Alta |
| A4 ambas | startInventoryTag | `boolean startInventoryTag()` | [SDK_DOC][JAR_SIGNATURE][DEMO_SOURCE] | H/NetworkA4:1656; C/form/InventoryForm:156 | Inicio continuo | Alta |
| A4 ambas | startInventoryTag | `boolean startInventoryTag(InventoryParameter)` | [SDK_DOC][JAR_SIGNATURE] | H/NetworkA4:3162; J | Parámetros fase/frecuencia; no usado por botón observado | Alta firma |
| A4 ambas | readTagFromBuffer | `UHFTAGInfo readTagFromBuffer()` | [SDK_DOC][JAR_SIGNATURE] | H/IUHF detalle; NetworkAx$InventoryThread.run offset76 | Polling interno; null si búfer vacío | Alta |
| A4 ambas | inventorySingleTag | `UHFTAGInfo inventorySingleTag()` | [SDK_DOC][JAR_SIGNATURE] | H/NetworkA4:1838; J | Alternativa single-shot, no flujo AUTO | Alta firma |
| A4 ambas | stopInventory | `boolean stopInventory()` | [SDK_DOC][JAR_SIGNATURE][DEMO_SOURCE] | H/NetworkA4:1721; C/form/InventoryForm:205; offsets §12 | Enviar parada; true no confirma envío/efecto | Alta |
| A4 ambas | free | `boolean free()` | [SDK_DOC][JAR_SIGNATURE][DEMO_SOURCE] | H/NetworkA4:1131; C/UHFMainForm:202; bytecode §12 | Cerrar transporte y solicitar fin de hilos | Alta estática |
| Red A4 | setAutoCheckConnectStatus | `void setAutoCheckConnectStatus(boolean)` | [JAR_SIGNATURE][DEMO_SOURCE] | J; C/UHFMainForm:184 | Desactivado por demo | Alta |
| A4 ambas | setAntenna/getAntenna | `boolean setAntenna(List<AntennaState>)`; `List<AntennaState> getAntenna()` | [SDK_DOC][JAR_SIGNATURE][DEMO_SOURCE] | H/NetworkA4:2593; C/form/ConfigForm:249,304 | Lista de antenas habilitadas | Alta |
| AntennaState | Constructor | `AntennaState(AntennaNameEnum,boolean)` | [JAR_SIGNATURE][DEMO_SOURCE] | J; C/form/ConfigForm:295–302 | Estado por antena | Alta |
| A4 ambas | setPower | `boolean setPower(AntennaNameEnum,int)` | [SDK_DOC][JAR_SIGNATURE][DEMO_SOURCE] | H/NetworkA4:1585; C/form/ConfigForm:164 | Potencia de antena seleccionada | Alta |
| A4 ambas | getPower/getPowerAll | `int getPower(AntennaNameEnum)`; `List<AntennaPowerEntity> getPowerAll()` | [SDK_DOC][JAR_SIGNATURE][DEMO_SOURCE] | H/NetworkA4:1275; C/form/ConfigForm:112 | Leer lista; JavaDoc retorno erróneo | Alta firma |
| A4 ambas | getReaderInfo | `ReaderInfo getReaderInfo()` | [JAR_SIGNATURE][DEMO_SOURCE] | J; C/form/ConfigForm:54 | Consultar cantidad declarada de antenas | Alta |
| A4 ambas | setFrequencyMode | `boolean setFrequencyMode(byte)` | [SDK_DOC][JAR_SIGNATURE][DEMO_SOURCE] | H/NetworkA4:1630; C/form/ConfigForm:408 | Selección perfil de banda | Alta firma |
| A4 ambas | setFreHop | `boolean setFreHop(float)` | [SDK_DOC][JAR_SIGNATURE] | H/NetworkA4:2194 | Frecuencia según doc; hopping completo no confirmado | Media semántica |
| A4 ambas | setPwm | `boolean setPwm(int,int)` | [SDK_DOC][JAR_SIGNATURE] | H/NetworkA4:2473 | Trabajo/espera 0–255ms, no dwell equivalente | Alta doc |
| A4 ambas / Gen2Entity | getGen2/setGen2 | `Gen2Entity getGen2()`; `boolean setGen2(Gen2Entity)` | [JAR_SIGNATURE][DEMO_SOURCE] | J; C/form/ConfigForm:640–667 | Consultar/modificar Gen2 | Alta |
| A4 ambas | modo EPC/TID/USER | `boolean setEPCMode()`; `boolean setEPCAndTIDMode()`; `boolean setEPCAndTIDUserMode(int,int)` | [JAR_SIGNATURE][DEMO_SOURCE] | J; C/form/ConfigForm:534,541,559 | Selección explícita por UI | Alta |
| UHFTAGInfo | getters básicos | `String getEPC()/getTid()/getUser()/getPc()/getRssi()/getAnt()` | [SDK_DOC][JAR_SIGNATURE][DEMO_SOURCE] | H/UHFTAGInfo; C/model/InventoryTableModel:73–83 | Datos de tabla y copia propia propuesta | Alta |
| UHFTAGInfo | timestamp | `long getTimestamp()`; `void setTimestamp(long)` | [SDK_DOC][JAR_SIGNATURE] | H/UHFTAGInfo:668; h.a offsets383–386 | Tiempo host asignado por parser | Alta para ruta trazada |
| UHFTAGInfo | count | `int getCount()`; `void setCount(int)` | [SDK_DOC][JAR_SIGNATURE][DEMO_SOURCE] | H/UHFTAGInfo:558; C/model/InventoryTableModel:29 | Agregación demo, valor inicial1 | Alta |

En referencias H/NetworkA4 y H/UHFTAGInfo se expanden respectivamente `H/com/rscja/deviceapi/RFIDWithUHFNetworkA4.html` y `H/com/rscja/deviceapi/entity/UHFTAGInfo.html`. H/SerialPortA4 e H/IUHF se expanden conforme al paquete de §3.

## Discrepancias adicionales registradas

| Hallazgo | Fuentes | Impacto / tratamiento |
|---|---|---|
| JavaDoc muestra `UHFProtocolParseAxFromJava` / `UHFProtocolParseFromJava`, pero JAR usa `g`/`i` | [SDK_DOC] HTML; [JAR_SIGNATURE] `Compiled from` de g/i | Nombres internos ofuscados; buscar por firmas/herencia, no programar contra esos nombres |
| `setFilterRepeatData(int bank)` documenta parámetro como true/false | [SDK_DOC] H/NetworkA4 detalle; [JAR_SIGNATURE] firma int y lógica de filtro 1–3 en InventoryThread | No inventar conversión booleana ni usar esa descripción como contrato aprobado |
| `getPowerAll` documenta -1 aunque retorna lista | [SDK_DOC][JAR_SIGNATURE][DEMO_SOURCE] §8 | Firma y tratamiento de lista prevalecen para implementación futura |
| Ejemplo polling accede al tag después de null | [SDK_DOC] H/IUHF.readTagFromBuffer; demo usa callback | No copiar el ejemplo; guardia null necesaria |
| Documentación dice solo ReaderAPI para red; parser referencia JSON y demo empaqueta muchos JAR | [SDK_DOC][JAR_SIGNATURE][DEMO_SOURCE] §10 | Distinguir camino básico, otras funciones y carga dinámica; mínimo pendiente |
| W nombra URA4Demo_CN; paquete contiene URA4DemoV1.4.jar | [SDK_DOC][APP_CONFIG] §10 | Registrar identidad real por hash; no asumir versión desde nombres solamente |

# 20. Conclusión para P1

[JAR_SIGNATURE][SDK_DOC][DEMO_SOURCE] Se identificaron las fachadas públicas, la apertura TCP/serial, el inicio continuo, la recepción de `UHFTAGInfo`, la extracción EPC, la parada y el cierre previstos por el SDK. Se siguió la cadena de hilos, búfer y parser relevante y se verificó la procedencia host del timestamp. La inspección permite describir **qué API envolver y qué garantías no ofrece**.

[INFERENCIA] Puede elaborarse un diseño preliminar del adaptador tras revisar este informe, con tratamiento especial de parada, callbacks tardíos, errores, metadata y dependencias. [PENDIENTE] No es aún un diseño listo para integrar la unidad U300 concreta: faltan confirmar firmware/plataforma/configuración, licencia, compatibilidad Java21 funcional y requisitos de P1. Se mantiene el orden autorizado de incrementos; no se implementa adaptador.

**RF_REAL PENDIENTE.** No se demuestra distancia, porcentaje de lectura, omisiones, lecturas externas, interferencia, comportamiento sobre metal, desempeño de antenas, estabilidad del U300 ni confiabilidad física. Análisis de SDK ≠ SIMULACION ≠ RF_REAL. P1 continúa EN CURSO, P2/P3 ABIERTOS, fase formal Diseño, Pruebas/piloto pendientes, I1 cerrado como incremento y MVP completo no cerrado.

## CAMBIOS DOCUMENTALES SUGERIDOS

No se realizan estos cambios. Son sugerencias condicionadas a revisión humana y cotejo del Registro ACTUAL, no decisiones nuevas.

**Documento:** Matriz de Requisitos ACTUAL.

**Sección:** evidencia de inspección SDK dentro de P1, según estructura vigente.

**Evidencia nueva:** hashes, fachadas A4, flujo y limitaciones recogidos en este informe.

**Cambio sugerido:** registrar inspección estática realizada y conservar pendientes de hardware/configuración/Java21/RF_REAL.

**Justificación:** distinguir avance de conocimiento de un cierre de gate; no elevar TEC-002 ni otros requisitos a implementados por inspeccionar un SDK.

**Documento:** Documento Maestro ACTUAL.

**Sección:** riesgos y futura integración de fuente RFID, después de verificar sección vigente.

**Evidencia nueva:** parada devuelve true sin confirmar envío, timestamp host, callback interno, DLL AMD64 y mínimo de dependencias pendiente.

**Cambio sugerido:** incorporar riesgos verificables cuando se autorice actualizar control; mantener abstracción independiente y secuencia de incrementos.

**Justificación:** preparar diseño informado sin cambiar arquitectura, alcance ni stack. No se propone una nueva DEC sin cotejar el Registro ACTUAL.

| Pregunta | Resultado | Evidencia |
|---|---|---|
| SDK real inspeccionado | SÍ | [SDK_DOC][DEMO_SOURCE][JAR_SIGNATURE][APP_CONFIG] Distribución local, §2; RAR identificado, no recotejado internamente |
| ReaderAPI.jar inspeccionado | SÍ | [JAR_SIGNATURE] SHA-256, 247 clases, firmas y bytecode selectivo |
| Clase Ethernet identificada | SÍ | [SDK_DOC][DEMO_SOURCE][JAR_SIGNATURE] RFIDWithUHFNetworkA4; correspondencia de unidad/firmware pendiente |
| Clase serial identificada | SÍ | [SDK_DOC][DEMO_SOURCE][JAR_SIGNATURE] RFIDWithUHFSerialPortA4 |
| Flujo conexión identificado | SÍ | [DEMO_SOURCE][JAR_SIGNATURE] init, estado y free; §4–5 |
| Flujo inventario EPC identificado | SÍ | [SDK_DOC][DEMO_SOURCE][JAR_SIGNATURE] startInventoryTag, callback y stopInventory |
| Forma de recibir EPC identificada | SÍ | [DEMO_SOURCE][JAR_SIGNATURE] IUHFInventoryCallback.callback(UHFTAGInfo) |
| Modelo de lectura identificado | SÍ | [SDK_DOC][JAR_SIGNATURE] UHFTAGInfo y timestamp host; disponibilidad física opcional pendiente |
| Dependencias identificadas | PARCIAL | [SDK_DOC][JAR_SIGNATURE] Distribución inventariada; mínimo funcional y nativos Linux pendientes |
| Compatibilidad Java 21 determinada | PARCIAL | [JAR_SIGNATURE][INFERENCIA][PENDIENTE] Inspección estática; no ejecución funcional |
| SDK puede mapearse a FuenteLecturasRFID | SÍ | [INFERENCIA] Mapeo conceptual viable con wrapper y tratamiento especial; §14 |
| Puede diseñarse AdaptadorU300 después de esta revisión | PENDIENTE | [INFERENCIA][PENDIENTE] Diseño preliminar viable; diseño concreto condicionado a revisión del informe, unidad/firmware/configuración y secuencia autorizada |
| AdaptadorU300 implementado | NO | No se escribió código productivo ni se integró el SDK |
| RF_REAL demostrada | NO | No se conectó hardware ni se ejecutó inventario |
| P1 cerrado | NO | Inspección estática no satisface lectura EPC UHF reproducible desde hardware autorizado |
