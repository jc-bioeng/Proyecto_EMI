# Fuentes de verdad del Proyecto EMI

Versión de inventario/contexto: 1.9.2

Fecha de actualización de rutas: 10 de septiembre de 2026

Base de control interno vigente: **v1.9.2 ACTUAL**, consolidada por autorización del usuario del 09/09/2026. La Propuesta aprobada sigue siendo la línea base formal sin cambios.

## Jerarquía documental vigente

1. `control_local/Propuesta_Juan_Cortes_20262.pdf` — línea base formal aprobada.
2. `control_local/Arquitecto_Proyecto_Documento_Maestro_v1.9.2_ACTUAL.pdf` — control interno vigente.
3. `control_local/Matriz_Requisitos_Proyecto_EMI_v1.9.2_ACTUAL.pdf` — requisitos, estados, evidencia y gates vigentes.
4. `control_local/Registro_Decisiones_Proyecto_EMI_v1.9.2_ACTUAL.pdf` — decisiones vigentes e histórico preservado.
5. Documentos de soporte.
6. Anexos y evidencia contextual.

Ante contradicción, aplicar este orden y registrar el impacto. No editar silenciosamente la línea base ni los documentos de control.

Los tres PDF v1.9.2 se consolidaron desde v1.9.1 y la propuesta autorizada, con revisión de contenido y presentación. Están en `control_local/`. Los originales v1.9.1 permanecen en `C:/Users/jjcor/Downloads/` y sus copias verificadas por SHA-256 están en `control_local/historico/` con el mismo nombre. El usuario trasladó las versiones anteriores a `control_local/historico/`; se verificó allí la presencia de v1.8, v1.8.1, v1.8.2 y v1.9.1 de los tres documentos. Aunque algunos nombres conservan el sufijo ACTUAL, son históricos y no prevalecen sobre v1.9.2.

## Estado vigente

- Fase formal: **Diseño**.
- P0: **CERRADO**.
- P1: **EN CURSO**.
- P2: **EN CURSO**.
- P3: **PENDIENTE**.
- I1: **CERRADO como incremento técnico/documental**.
- I2: **CERRADO técnicamente** (SOFTWARE / SIMULACION).
- MVP completo: **NO cerrado**.
- Fase formal de Pruebas: **PENDIENTE**.
- Piloto: **PENDIENTE**.
- Evidencia RF_REAL: **PENDIENTE**.

## I1 cerrado

I1 cubre:

- persistencia y migraciones;
- `Equipo`;
- `EtiquetaRFID`;
- `AsignacionEtiqueta`;
- crear equipo;
- crear etiqueta;
- asociar;
- consultar asociación vigente;
- buscar equipo por EPC;
- corregir conservando historial;
- consultar historial;
- atomicidad, rollback y concurrencia;
- entrada temporal por consola.

Evidencia de cierre:

- 32 pruebas;
- 0 fallos;
- 0 errores;
- 0 omitidas;
- V1/V2 intactas;
- corrección en una transacción y un único instante UTC;
- rollback completo demostrado mediante fallo real posterior al cierre;
- concurrencia, historial ordenado y búsqueda por EPC vigente verificados;
- JAR ejecutando los siete flujos sobre una base nueva.

## Estados de requisitos afectados

- `RF-001`: **IMPLEMENTADO**.
- `DAT-003`: **EN DESARROLLO**. El submodelo I1 está implementado; faltan las entidades posteriores del modelo mínimo del MVP.
- `TEC-002`: **EN DESARROLLO**. `FuenteLecturasRFID` y `FuenteSimulada` implementadas y probadas; segunda fuente compatible pendiente.
- `TEC-001`: **EN DESARROLLO**. SDK inspeccionado; unidad/firmware/configuración y RF_REAL pendientes.
- `RF-002`, `DAT-001`, `DAT-002`: **EN DESARROLLO** por materialización parcial de I2, según Matriz v1.9.1.
- `OPE-002`: **Must / Aprobado**; `RF-006` y `RSK-001`: **Should / Aprobado**. Las nuevas condiciones no cambian estos estados ni prioridades.
- No cambiar otros requisitos por el cierre de I1.

## Decisiones

El Registro v1.9.2 conserva DEC-001 a DEC-023, sin nueva DEC.

Antecedente v1.8.2: conservaba DEC-001 a DEC-022.

Antecedente de cierre I1 (v1.8.2): no se creó DEC-023 por ese cierre; es una actualización de estado sustentada en evidencia, no un cambio de alcance, arquitectura, metodología, cronograma, presupuesto, piloto o validación.

Decisiones críticas:

- DEC-015: modelo lógico mínimo + historial equipo-etiqueta.
- DEC-016: baseline Must congelada.
- DEC-017: interfaz común `FuenteLecturasRFID`.
- DEC-019: RC522/MFRC522 excluido como lector UHF del MVP y como evidencia RF_REAL.
- DEC-020: documentos auxiliares redundantes subordinados.
- DEC-021: P1 permanece abierto hasta lectura EPC UHF reproducible desde hardware autorizado.
- DEC-022: Java 21 LTS + Maven + JDBC + SQLite + Flyway + JUnit 5.
- DEC-023: evidencia operativa tardía de AM/remodelación como insumo de P2, sin reabrir P0 ni cambiar línea base.

No se crea DEC-024: estas adiciones desarrollan DEC-008, DEC-012, DEC-014 y DEC-023, preservando DEC-005, DEC-016 y DEC-021.

## Documentos actuales

### Control

- `control_local/Arquitecto_Proyecto_Documento_Maestro_v1.9.2_ACTUAL.pdf`
- `control_local/Matriz_Requisitos_Proyecto_EMI_v1.9.2_ACTUAL.pdf`
- `control_local/Registro_Decisiones_Proyecto_EMI_v1.9.2_ACTUAL.pdf`

### Soporte

- `soporte/Informe_Hallazgos_Operativos_AM_RFID_EMI_2026-09-09.pdf` — evidencia auxiliar AM/remodelación y P2; copia íntegra del archivo aportado, incorporada el 10/09/2026.

- `soporte/Informe_Cierre_P0_Proyecto_EMI_2026-09-03.docx`
- `soporte/Informe_Tecnico_Avance_P1_v0.1_2026-09-05.pdf`
- `soporte/Checklist_P1_v0.1_ACTUALIZABLE.xlsx`
- `soporte/Informe_Tecnico_Cierre_I1_Proyecto_EMI_v0.2_2026-09-06.pdf`

### Histórico

El inventario anterior era v1.8.2 (06/09/2026). Tras el traslado realizado por el usuario, las rutas históricas verificadas el 10/09/2026 son:

- `control_local/historico/Arquitecto_Proyecto_Documento_Maestro_v1.8.1_ACTUAL.pdf`
- `control_local/historico/Arquitecto_Proyecto_Documento_Maestro_v1.8.2_ACTUAL.pdf`
- `control_local/historico/Arquitecto_Proyecto_Documento_Maestro_v1.8.pdf`
- `control_local/historico/Arquitecto_Proyecto_Documento_Maestro_v1.9.1_ACTUAL.pdf`
- `control_local/historico/Matriz_Requisitos_Proyecto_EMI_v1.8.1_ACTUAL.pdf`
- `control_local/historico/Matriz_Requisitos_Proyecto_EMI_v1.8.2_ACTUAL.pdf`
- `control_local/historico/Matriz_Requisitos_Proyecto_EMI_v1.8.pdf`
- `control_local/historico/Matriz_Requisitos_Proyecto_EMI_v1.9.1_ACTUAL.pdf`
- `control_local/historico/Registro_Decisiones_Proyecto_EMI_v1.8.1_ACTUAL.pdf`
- `control_local/historico/Registro_Decisiones_Proyecto_EMI_v1.8.2_ACTUAL.pdf`
- `control_local/historico/Registro_Decisiones_Proyecto_EMI_v1.8.pdf`
- `control_local/historico/Registro_Decisiones_Proyecto_EMI_v1.9.1_ACTUAL.pdf`

Las versiones v1.8, v1.8.1, v1.8.2 y v1.9.1 de Maestro, Matriz y Registro están en esa carpeta. Los únicos documentos de control ACTUAL en `control_local/` son v1.9.2, junto con la Propuesta aprobada. Se mantienen nombres y contenido de los históricos; el sufijo ACTUAL de una versión anterior no determina vigencia. El Registro exige conservar v1.9 como antecedente, pero no se encontró un archivo v1.9 en este inventario: no se confunde con v1.9.1 ni se inventa su ruta.

La antigua `Matriz_Requisitos_Proyecto_EMI_v1.8.1_ACTUALIZACION_CONTROLADA.pdf` está reemplazada por la Matriz completa y, si se conserva, debe quedar como artefacto histórico no vigente.

La adenda individual DEC-022 debe conservarse como fuente histórica de consolidación.

## Próximo incremento lógico

Antecedente de secuencia v1.8.2, ya materializado y cerrado en I2:

`FuenteLecturasRFID` → `FuenteSimulada`

Siguiente paso: diseño dirigido de persistencia `LecturaRFID` y su relación con `SesionOperacion` antes de programar I3; no invertir ni adelantar la secuencia:

`LecturaRFID` → `SesionOperacion` → eventos/deduplicación → historial operativo → verificación → sustitución temporal → contingencia → adaptador UHF real cuando P1 lo habilite.

La condición anterior de inspeccionar el SDK ya se cumplió como análisis estático. El adaptador U300 permanece pendiente; no se integra por esta actualización.

## P1 — inspección completada y trabajo pendiente

Antecedente v1.8.2: la revisión RAR/SDK se solicitó para determinar los siguientes aspectos. La inspección estática A4 ya está completada; los extremos funcionales/físicos siguen sujetos a verificación:

- plataforma: Windows / Android / Linux;
- API: Java / .NET / C/C++ / JNI / red / serial;
- JAR, DLL, SO, EXE o drivers incluidos;
- ejemplos de conexión e inventario EPC;
- arquitectura x64/x86;
- runtimes y dependencias;
- configuración regional, potencia y antena;
- metadata disponible;
- licencia y restricciones de redistribución.

La revisión del SDK no cierra P1. El cierre sigue exigiendo una lectura EPC UHF `RF_REAL` reproducible desde hardware autorizado con configuración documentada.

## Regla pendiente

La existencia de `ACTIVA | INACTIVA` no implica por sí sola que una etiqueta INACTIVA tenga prohibida una nueva asociación.

La prohibición permanece como **propuesta pendiente** hasta que exista una regla aprobada.

## Regla de evidencia

- Software/persistencia ≠ SIMULACION RFID.
- SIMULACION ≠ RF_REAL.
- Solo RF_REAL puede sustentar alcance, omisiones, lecturas externas, interferencia, comportamiento sobre metal, antenas o desempeño físico.

## Custodia

No borrar versiones históricas.

`control_local/`, `soporte/` y `anexos/` permanecen excluidos de Git salvo decisión expresa distinta.

La propuesta aprobada sigue siendo la línea base formal.

## I2 y SDK — evidencia consolidada en v1.9.1

Maestro §§2, 4, 5, 9–10; Matriz §§1, 3–5; Registro §§3–7:

- I2 cerrado: `FuenteLecturasRFID`, `FuenteSimulada`, `LecturaEntradaRFID` y `OrigenDatos`; EPC literal, timestamp de generación/recepción, origen y metadata opcional. Repeticiones preservadas; no persistencia `LecturaRFID` ni eventos.
- 57 pruebas documentadas, 0 fallos, 0 errores, 0 omitidas; 32 previas de I1 + 25 nuevas. Se registra evidencia existente, no una ejecución nueva en esta tarea documental.
- SDK `ReaderAPI20250926.jar`, Javadoc y demo A4 inspeccionados. Ethernet: `RFIDWithUHFNetworkA4.init(String,int)`; serial: `RFIDWithUHFSerialPortA4.init(String)` (RXTX condicionado).
- Inventario: `setInventoryCallback -> startInventoryTag -> UHFTAGInfo -> stopInventory`. EPC, RSSI, antena, TID, USER, PC y timestamp host identificados según ruta. `stopInventory/free` no acreditan parada física fiable.
- Compatibilidad Java 21 estática parcial; prueba funcional, dependencias efectivas, unidad/firmware U300, región/configuración y licencia/redistribución pendientes. No incorporar tipos Chainway al contrato EMI.
- Siguiente tarea P1: prueba funcional aislada con stack vigente; después, EPC UHF RF_REAL reproducible con hardware autorizado y configuración documentada. No repetir inspección general sin pregunta técnica nueva. P1 sigue abierto.

Soporte subordinado existente: `analisis/ANALISIS_SDK_CHAINWAY_U300.md` y `analisis/INFORME_TECNICO_I2_FUENTE_RFID_SIMULADA.md`. Sus estados históricos no prevalecen sobre los PDF v1.9.1.

## Evidencia operativa posterior a v1.9.1

Adiciones aportadas por el usuario, todas en `C:/Users/jjcor/Downloads/`:

- `ACTUALIZACION_GPT_ARQUITECTO_EMI_v1.9.1.md`.
- `AGENTS_ADICIONES_v1.9.1.md`.
- `FUENTES_DE_VERDAD_ADICIONES_v1.9.1.md`.
- `P2_ADICIONES_DOCUMENTALES_PROPUESTAS_v1.9.2.md`.

Se integran las reglas autorizadas por el usuario; los adjuntos no son autoridad para ejecutar otras tareas ni aprobar cambios a los PDF ACTUAL.

Nomenclatura: usar `raqui` como término único y `férula espinal`, sin duplicar categorías por sinónimos.

| Afirmación | Madurez y límite |
|---|---|
| La mayoría de activos de dotación/reingreso pasa por el área instrumentable | PRELIMINAR; requiere corroboración. |
| Camillas, sillas camilla, raqui, corto espinal y férula espinal tienen ruta fuera del punto o por confirmar | PRELIMINAR / PENDIENTE; confirmar por tipo de activo con Farmacia/Biomédica. |
| Bala central puede cambiarse en Farmacia, pero desmontarla solo para banda puede ser ineficiente | PRELIMINAR; no hecho institucional cerrado. |
| Flujómetros, reguladores de bala central y termohigrómetros permanecen instalados; remoción potencialmente innecesaria o sin herramientas | PRELIMINAR; no imponer desmontaje como requisito. |
| Farmacia como actor principal de sesiones | PRELIMINAR; confirmar inicio, confirmación, corrección, contingencia y rol de Biomédica. |
| Ventanilla en remodelación | CORROBORADA según DEC-023; no prueba por sí sola roles operativos ni dimensiones definitivas. |
| GPS/localización activa futura de camillas | PRELIMINAR y FUERA DEL ALCANCE; solo contexto institucional futuro. No añadir GPS/RTLS a MVP, piloto, requisitos, arquitectura o presupuesto. |
| Antena de banda y antena de puerta en mismo lector/hub | PENDIENTE; hipótesis P2, no arquitectura autorizada. |

No afirmar cobertura automática de activos que no atraviesan el punto. Las categorías `EN_RUTA_RFID`, `FUERA_RUTA_RFID` y `RUTA_PENDIENTE_CONFIRMAR` son conceptuales, no nuevos campos/tablas autorizados. Validación limitada a activos, trayectorias y configuración realmente ensayados.

## Contingencia mínima de diseño

`Lectura -> Verificación -> Reintento -> Contingencia manual -> Confirmación`

La omisión inicial no determina una falla final; aplicar reintento controlado cuando corresponda. Si persiste, contingencia con equipo, actor, fecha/hora, sesión/contexto, motivo y origen manual explícito. Para activos fuera de ruta: `Manual/Contingencia -> Confirmación`. Nunca crear lectura RFID ficticia para justificar un movimiento. Preferir selección por código institucional/equipo a escritura de EPC hexadecimal. No incorporar lector manual ni tercer SDK sin autorización.

Este flujo es una directriz de diseño; no declara RF-006 implementado, no modifica su prioridad Should ni adelanta la contingencia en la secuencia de incrementos.

## Siguiente tarea P2 — análisis geométrico/RF especializado

Usar planos y fichas técnicas para producir configuraciones candidatas y un plan de prepruebas. Cubrir sistema de coordenadas, escala, dimensiones, alturas/altura libre, materiales y metal, banda transportadora, puerta, ventanilla, trayectorias/distancias/orientación de tags, posiciones candidatas de antena, orientación/inclinación, polarización, patrón, banda/frecuencia RF, pérdidas de cable/conectores, presupuesto de enlace, huella teórica, solapamientos, zonas ciegas, energía/red y restricciones constructivas/de montaje.

Documentar entradas, unidades, fuentes e incertidumbre. Terminar con plan de prepruebas RF_REAL y verdad de terreno; mantener prepruebas -> P3 -> Pruebas -> Piloto -> Validación. No inventar medidas, potencia o patrones si faltan fichas/mediciones.

Dos antenas solo permanecen en el piloto si forman un único punto de control operativo autorizado: misma operación/piloto, arquitectura única, límites físicos, roles y solapamientos controlados. Ubicación exacta, modelo final, puertos, potencia, orientación y unidad operativa siguen pendientes. `antena != evento`: no inferir INGRESO/SALIDA por antena sin sesión/contexto y validación. P2 debe resolverlo antes de congelar arquitectura.

Separar `GEOMETRIA != MODELO_RF_TEORICO != RF_REAL`. Cada resultado llevará una de estas clases: **CONFIRMADA**, **CORROBORADA**, **PRELIMINAR**, **PENDIENTE**, **CALCULADA**, **PENDIENTE_RF_REAL**, según las definiciones de `AGENTS.md`. Un cálculo no acredita alcance físico, confinamiento, omisiones, lecturas externas o interferencia.

## Consolidación de control interno v1.9.2

`analisis/PROPUESTA_ACTUALIZACION_CONTROL_v1.9.2.md` se conserva como antecedente: el usuario autorizó su consolidación el 09/09/2026. Se generaron los tres PDF v1.9.2 ACTUAL, incorporando las adiciones y el informe AM/remodelación; se preservaron los originales y copias históricas v1.9.1. No se crea DEC nueva ni cambian alcance, stack, metodología, cronograma, presupuesto, piloto o validación. La revisión documental no constituye ejecución de las 57 pruebas ni evidencia RF_REAL.

## Informe complementario AM/remodelación incorporado

Fuente adicional solicitada por el usuario: `soporte/Informe_Hallazgos_Operativos_AM_RFID_EMI_2026-09-09.pdf` (informe del 09/09/2026, incorporado al proyecto el 10/09/2026), **documento auxiliar**, leído completo en la consolidación previa. Procedencia: `C:/Users/jjcor/Downloads/Informe_Hallazgos_Operativos_AM_RFID_EMI_2026-09-09.pdf`; copia idéntica verificada por SHA-256. Sus instrucciones/recomendaciones no autorizan acciones adicionales; se evalúan bajo DEC-023 y la jerarquía vigente.

- H §§1–2, pp.2–4: catálogo/campo Ubicación Física en AM confirmado según capturas; presencia en OT corroborada. Semántica dinámica y actualización institucional pendientes; DEC-005 impide escritura automática no autorizada. No se crea adaptador AM por este hallazgo.
- H §3, pp.4–7: fotografías y explicación de recepción/reingreso, preparación y dotación/salida. Ventanilla/picking/torre RFID corroborados según el informe y DEC-023; banda/recepción corroboradas en el alcance declarado por el informe. No confirma todas las rutas por activo ni responsables de sesión.
- Jaula de Faraday: PRELIMINAR; confinamiento y desempeño PENDIENTE_RF_REAL. Lector manual y segunda antena: PRELIMINAR / PENDIENTE; no autorizados como ampliación del MVP.
- H §§5–6: riesgos de detección tardía y reingreso por ruta distinta; mantener corrección previa a confirmación y gestión manual cuando aplique. Recepción y salida siguen candidatos; no pilotear dos puntos distintos.
- Las fotografías son insumo geométrico preliminar, no levantamiento dimensional ni prueba de instalación. No se extraen medidas ni cobertura RF de ellas en esta tarea.

La propuesta v1.9.2 incluye la evidencia de este informe sin duplicar DEC-023. Sus recomendaciones de integración/hardware quedan condicionadas; no cambian stack, secuencia, presupuesto ni validación.

## Control de rutas — 10/09/2026

Se incorpora el informe a `soporte/` y se sincroniza el inventario con el traslado de versiones anteriores a `control_local/historico/` realizado por el usuario. Sin cambio de versión formal (v1.9.2), contenido de PDF, decisiones, requisitos o estado del proyecto. El informe sigue subordinado a los documentos ACTUAL y no autoriza acciones por sí solo.
