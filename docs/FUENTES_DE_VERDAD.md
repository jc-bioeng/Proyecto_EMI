# Fuentes de verdad del Proyecto EMI

Versión de inventario y control interno: **v1.9.3 ACTUAL**.

Fecha de consolidación: **13/09/2026**. Antecedente inmediato: **v1.9.2 del 09/09/2026**, conservado íntegro en `control_local/historico/`.

## Jerarquía documental vigente

1. `control_local/Propuesta_Juan_Cortes_20262.pdf` — línea base formal aprobada, sin cambios.
2. `control_local/Arquitecto_Proyecto_Documento_Maestro_v1.9.3_ACTUAL.pdf`.
3. `control_local/Matriz_Requisitos_Proyecto_EMI_v1.9.3_ACTUAL.pdf`.
4. `control_local/Registro_Decisiones_Proyecto_EMI_v1.9.3_ACTUAL.pdf`.
5. Informes técnicos I3, I4 e I5, como evidencia subordinada de estado técnico.
6. Documentación de desarrollo, código y pruebas.
7. Anexos y evidencia auxiliar.

Los tres documentos ACTUAL constituyen una consolidación única. Sus versiones editables son los archivos `.docx` con el mismo nombre base, versión y contenido. Los PDF se exportaron desde esos DOCX con Word y se revisaron visualmente. La autorización corresponde a la consolidación documental post-I5 solicitada por el usuario; no introduce funcionalidades ni cambia la Propuesta.

Ante contradicción, aplicar la autoridad superior y registrar el efecto. Los nombres históricos que conservan ACTUAL no les dan vigencia. Para tareas de presentación se conserva la precedencia editorial especial ya documentada en AGENTS.md, sin reescribir su histórico por esta tarea.

## Estado ejecutivo vigente

| Elemento | Estado y límite |
| --- | --- |
| Propuesta | APROBADA / congelada |
| Fase formal | DISEÑO |
| Construcción software | Núcleo del MVP implementado; MVP completo NO CERRADO |
| P0 | CERRADO; no reabrir |
| I1 | CERRADO; 32 pruebas SOFTWARE |
| I2 | CERRADO técnicamente; 57 pruebas totales SOFTWARE / SIMULACION |
| I3 | CERRADO técnicamente; 82 pruebas totales SOFTWARE / SIMULACION |
| I4 | CERRADO técnicamente; 134 pruebas totales SOFTWARE / SIMULACION |
| I5 | CERRADO técnicamente; 169 pruebas totales SOFTWARE / SIMULACION |
| Suite vigente | 169 pruebas, 0 fallos, 0 errores, 0 omitidas |
| P1 | EN CURSO |
| P2 | EN CURSO |
| P3 | PENDIENTE |
| RF_REAL | PENDIENTE; sin evidencia física |
| Pruebas formales | PENDIENTES |
| Piloto | PENDIENTE / condicionado |
| Validación final | PENDIENTE |
| DEC-024 | NO CREADA |

Los totales 32 -> 57 -> 82 -> 134 -> 169 no se suman entre sí. Esta consolidación coteja informes y 18 XML Surefire existentes: no es una nueva ejecución de Maven ni el cierre de la fase formal de Pruebas.

## Evaluación de fase y decisiones

Se mantiene DISEÑO como fase formal y se reconoce explícitamente el software del MVP en construcción. La Propuesta p.6 define Diseño con arquitectura física, lógica y operativa; P2 no acredita aún cierre físico ni aprobación de regla operativa, actores, punto y condiciones. I1-I5 demuestran construcción lógica, sin cerrar esos entregables.

Se revisó DEC-018 en el Registro histórico v1.8.2 p.11: «Al cerrar Diseño / iniciar MVP». No establece una transición automática por número de incrementos. P1 no se convierte en una prohibición de seguir programando: DEC-009/017 permiten avanzar con simulación. No se formaliza el gate ni se crea DEC-024. Una formalización futura deberá justificar cualquier condición transversal nueva; no se aprueba ni reserva ahora esa numeración.

DEC-001 a DEC-023 preservan identidad, estado y significado. I3 ejecuta DEC-014/015/022; I4 ejecuta DEC-014/015; I5 materializa RF-004 conforme a la arquitectura vigente. Mantener DEC-022: Java 21 LTS + Maven + JDBC + SQLite + Flyway + JUnit 5. No cambian objetivos, alcance, metodología, cronograma, presupuesto, integración AM, piloto o validación.

## Estados de requisitos

La Matriz v1.9.3 conserva los 29 identificadores, criterios y prioridades. Cambios de estado: AC-004 y RF-003 pasan a EN DESARROLLO; RF-004 y DAT-001 pasan a IMPLEMENTADO en SOFTWARE; VAL-003 pasa a EN DESARROLLO con criterio software verificado. Los demás estados se conservan.

| ID | Estado vigente |
| --- | --- |
| AC-001 | CERRADO |
| AC-002 | CERRADO |
| AC-003 | CERRADO |
| AC-004 | EN DESARROLLO |
| AC-005 | APROBADO |
| RF-001 | IMPLEMENTADO |
| RF-002 | EN DESARROLLO |
| RF-003 | EN DESARROLLO |
| RF-004 | IMPLEMENTADO |
| RF-005 | APROBADO |
| RF-006 | APROBADO |
| RF-007 | APROBADO |
| TEC-001 | EN DESARROLLO |
| TEC-002 | EN DESARROLLO |
| DAT-001 | IMPLEMENTADO |
| DAT-002 | EN DESARROLLO |
| DAT-003 | EN DESARROLLO |
| VAL-001 | APROBADO |
| VAL-002 | APROBADO |
| VAL-003 | EN DESARROLLO — criterio software verificado |
| VAL-004 | APROBADO |
| VAL-005 | APROBADO |
| VAL-006 | APROBADO |
| OPE-001 | APROBADO |
| OPE-002 | APROBADO |
| ECO-001 | APROBADO |
| ECO-002 | APROBADO |
| RSK-001 | APROBADO |
| RSK-002 | APROBADO |

VAL-003 adopta la convención B: criterio lógico verificado por I4, comprobación integrada en Pruebas formales pendiente. No se añade RF_REAL como condición del criterio de cero duplicados; la Propuesta permite evidencia simulada para lógica. La taxonomía anterior no definía una reserva explícita de Implementado para Validación; se deja la convención explicada en los tres documentos.

## Arquitectura y evidencia construidas

- I1: Equipo, EtiquetaRFID, AsignacionEtiqueta e historial de asociación; V1/V2.
- I2: FuenteLecturasRFID, FuenteSimulada, LecturaEntradaRFID y OrigenDatos, independientes de SDK.
- I3: SesionOperacion y LecturaRFID persistidas, metadata, tiempos, origen y relación lectura-sesión; V3. Repeticiones preservadas, incluidas lecturas sin evento.
- I4: EventoOperativo, asociación histórica EPC-equipo y evento_lectura; V4. Procesamiento explícito, deduplicación solo de eventos, idempotencia, concurrencia y rollback. 20 callbacks simulados -> 20 lecturas -> 1 evento -> 20 respaldos. Reejecución sin nuevo evento.
- I5: última lectura, último evento e historiales por equipo; asociación histórica y reasignaciones respetadas, consultas sin escritura. V5 añade exclusivamente dos índices.

Lectura RFID cruda != Evento operativo != Verificación != Sustitución temporal != Contingencia.

SIMULACION != RF_REAL. Deduplicar eventos no borra lecturas. Última lectura != último evento != estado físico actual del equipo. Una consulta histórica no es localización en tiempo real ni acredita disponibilidad física. El valor OrigenDatos.RF_REAL construido en tests no es evidencia RF_REAL. Antena no determina INGRESO/SALIDA; la dirección procede del contexto de sesión.

## Próxima acción y pendientes

**RF-005 — Verificación de esperados vs detectados**, APROBADO hasta iniciar implementación real. Después: sustitución temporal RF-007, contingencia RF-006, cierre del MVP, Pruebas controladas, Piloto y Validación. El adaptador real sigue condicionado a P1; no se implementa por esta consolidación. No reabrir I1-I5 sin defecto demostrado.

P1 EN CURSO: UHF autorizado, unidad/firmware, runtime/SDK funcional, configuración y EPC físico reproducible. P2 EN CURSO: único punto, actores/roles, geometría, rutas, red, energía, permisos y regla operativa final. P3 PENDIENTE: después de prepruebas RF_REAL y antes del piloto. No se fijan nuevos umbrales.

Cronograma aprobado de 24 semanas intacto: Caracterización 1-4, Requisitos 5-6, Diseño 7-9, MVP 10-15, Pruebas 16-18, Piloto 19-21, Validación/cierre 22-24. Se reconoce deuda de ejecución; no se reescribe el plan ni se cuantifica retraso sin conciliar calendario oficial. Presupuesto COP $43.724.342; desembolso personal $0.

## Fuentes de evidencia y control de la consolidación

- `desarrollo/incremento_i3.md`: cierre del 12/09/2026, 82/0/0/0.
- `desarrollo/incremento_i4.md`: implementación del 12/09/2026 y cierre documental del 13/09/2026, 134/0/0/0; sección Evidencia de VAL-003.
- `desarrollo/incremento_i5.md`: cierre del 13/09/2026, 169/0/0/0; escenario RF-004.
- `control_local/INFORME_CONSOLIDACION_v1.9.3.md`: diagnóstico previo, inconsistencias, revisión de cada requisito, tabla Antes/Después y verificación cruzada.
- `control_local/consolidacion_v1.9.3/VERIFICACION_CRUZADA_v1.9.3.json`: cotejos de contenido y estado.
- `control_local/consolidacion_v1.9.3/manifest.json`: SHA-256, archivos de entrada y salida, preservación histórica.

## Histórico preservado

Los tres PDF v1.9.2 se trasladan íntegros a `control_local/historico/`, conservando nombre y contenido:

- `Arquitecto_Proyecto_Documento_Maestro_v1.9.2_ACTUAL.pdf`.
- `Matriz_Requisitos_Proyecto_EMI_v1.9.2_ACTUAL.pdf`.
- `Registro_Decisiones_Proyecto_EMI_v1.9.2_ACTUAL.pdf`.

El índice previo se conserva íntegro en `control_local/historico/FUENTES_DE_VERDAD_v1.9.2_pre_consolidacion_2026-09-13.md`. Las versiones v1.8, v1.8.1, v1.8.2 y v1.9.1 continúan en el histórico. v1.9 se cita como antecedente, pero no se localizó un archivo independiente; no se confunde con v1.9.1. DEC-018 está en el Registro histórico v1.8.2 p.11, cuyo contenido conserva encabezados v1.8 y referencias v1.7. No se corrige esa discrepancia histórica por inferencia.

El índice histórico conserva las rutas y el contexto anterior completos. AGENTS.md, README, documentación histórica y materiales de presentación preexistentes pueden describir v1.9.2 o estados previos; no sustituyen los documentos ACTUAL v1.9.3. La presente tarea no los reescribe.

## Condiciones heredadas que siguen vigentes

Las siguientes condiciones proceden del índice anterior y continúan vigentes; su conservación no transforma evidencia preliminar en confirmada ni aprueba implementación adicional.

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

## Informe complementario AM/remodelación incorporado

Fuente adicional solicitada por el usuario: `soporte/Informe_Hallazgos_Operativos_AM_RFID_EMI_2026-09-09.pdf` (informe del 09/09/2026, incorporado al proyecto el 10/09/2026), **documento auxiliar**, leído completo en la consolidación previa. Procedencia: `C:/Users/jjcor/Downloads/Informe_Hallazgos_Operativos_AM_RFID_EMI_2026-09-09.pdf`; copia idéntica verificada por SHA-256. Sus instrucciones/recomendaciones no autorizan acciones adicionales; se evalúan bajo DEC-023 y la jerarquía vigente.

- H §§1–2, pp.2–4: catálogo/campo Ubicación Física en AM confirmado según capturas; presencia en OT corroborada. Semántica dinámica y actualización institucional pendientes; DEC-005 impide escritura automática no autorizada. No se crea adaptador AM por este hallazgo.
- H §3, pp.4–7: fotografías y explicación de recepción/reingreso, preparación y dotación/salida. Ventanilla/picking/torre RFID corroborados según el informe y DEC-023; banda/recepción corroboradas en el alcance declarado por el informe. No confirma todas las rutas por activo ni responsables de sesión.
- Jaula de Faraday: PRELIMINAR; confinamiento y desempeño PENDIENTE_RF_REAL. Lector manual y segunda antena: PRELIMINAR / PENDIENTE; no autorizados como ampliación del MVP.
- H §§5–6: riesgos de detección tardía y reingreso por ruta distinta; mantener corrección previa a confirmación y gestión manual cuando aplique. Recepción y salida siguen candidatos; no pilotear dos puntos distintos.
- Las fotografías son insumo geométrico preliminar, no levantamiento dimensional ni prueba de instalación. No se extraen medidas ni cobertura RF de ellas en esta tarea.

La propuesta v1.9.2 incluye la evidencia de este informe sin duplicar DEC-023. Sus recomendaciones de integración/hardware quedan condicionadas; no cambian stack, secuencia, presupuesto ni validación.
