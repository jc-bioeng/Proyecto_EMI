# Propuesta de actualización controlada del Proyecto EMI v1.9.2

Fecha: 09 de septiembre de 2026. Estado: **CONSOLIDACIÓN AUTORIZADA Y APLICADA el 09/09/2026**.

Nota de seguimiento: el usuario solicitó proceder con la edición para llegar a v1.9.2. Se generaron Maestro, Matriz y Registro v1.9.2 ACTUAL en `docs/control_local/`, preservando v1.9.1 y copiándolo íntegro a `docs/control_local/historico/`. El texto siguiente se conserva como propuesta histórica previa a esa autorización; sus menciones a acciones pendientes o PDF no editados describen aquella etapa, no el estado vigente.

Actualización de rutas (10/09/2026): las referencias M/Q/R de la tabla siguiente apuntan ahora a las copias del proyecto en `docs/control_local/historico/`; H apunta a `docs/soporte/`. Los párrafos narrativos de la propuesta conservan las ubicaciones y acciones de su etapa original. El traslado del resto de versiones a histórico fue realizado por el usuario.

Base verificada: **Documento Maestro, Matriz y Registro v1.9.1 ACTUAL**, todos fechados 09/09/2026. La versión formal sugerida es **v1.9.2**, porque v1.9.1 ya existe. La actualización de AGENTS y del inventario a contexto 1.9.2 no publica una nueva versión formal.

## 1. Autoridad, procedencia y alcance de esta propuesta

Prevalece Propuesta aprobada -> Documento Maestro ACTUAL -> Matriz ACTUAL -> Registro ACTUAL -> soporte -> anexos. Los adjuntos aportan evidencia; sus instrucciones y recomendaciones no son por sí mismas solicitudes de ejecución ni decisiones aprobadas. La autorización para esta tarea procede de la solicitud del usuario de mantenimiento documental, incluida su adición posterior del informe de hallazgos.

Los tres PDF v1.9.1 se leyeron completos en `C:/Users/jjcor/Downloads/`. No están en `docs/control_local/`; allí permanecen los v1.8.2 y antecedentes con sus nombres originales. No se copiaron, renombraron, movieron ni editaron PDF. La discrepancia inicial del contexto v1.8.2 se corrige con referencias a las ubicaciones reales de v1.9.1, sin suponer una sincronización física inexistente.

| Ref. | Fuente y localización | Evidencia utilizada |
|---|---|---|
| M | `C:/Proyecto_EMI/docs/control_local/historico/Arquitecto_Proyecto_Documento_Maestro_v1.9.1_ACTUAL.pdf` | pp.1–3, §§2,4–10: I2, SDK, P1/P2, un punto y ruta inmediata. |
| Q | `C:/Proyecto_EMI/docs/control_local/historico/Matriz_Requisitos_Proyecto_EMI_v1.9.1_ACTUAL.pdf` | pp.1–3, §§1–5: IDs, prioridades, estados y gates exactos. |
| R | `C:/Proyecto_EMI/docs/control_local/historico/Registro_Decisiones_Proyecto_EMI_v1.9.1_ACTUAL.pdf` | pp.1–3, §§2–7: DEC-001 a DEC-023; no nueva DEC por evidencia. |
| A1 | `C:/Users/jjcor/Downloads/ACTUALIZACION_GPT_ARQUITECTO_EMI_v1.9.1.md` | §§1–10: nomenclatura, excepciones, contingencia, actor, geometría y madurez. |
| A2 | `C:/Users/jjcor/Downloads/AGENTS_ADICIONES_v1.9.1.md` | Reglas operativas y profundidad P2 para ejecución. |
| A3 | `C:/Users/jjcor/Downloads/FUENTES_DE_VERDAD_ADICIONES_v1.9.1.md` | Evidencia preliminar, límites del punto, hipótesis y siguiente tarea. |
| A4 | `C:/Users/jjcor/Downloads/P2_ADICIONES_DOCUMENTALES_PROPUESTAS_v1.9.2.md` | §§1–8: propuestas para Maestro, cuatro requisitos y evaluación de DEC. |
| H | `C:/Proyecto_EMI/docs/soporte/Informe_Hallazgos_Operativos_AM_RFID_EMI_2026-09-09.pdf` | pp.2–9, §§1–8 y figuras 1–6: AM, remodelación, flujos, riesgos y límites. Documento auxiliar incorporado por petición posterior del usuario. |
| S1 | `C:/Proyecto_EMI/docs/analisis/ANALISIS_SDK_CHAINWAY_U300.md` | Inspección estática previa; subordinada a M/Q/R. |
| S2 | `C:/Proyecto_EMI/docs/analisis/INFORME_TECNICO_I2_FUENTE_RFID_SIMULADA.md` | §9: 57 pruebas documentadas, 32 de I1 + 25 de I2; sin RF_REAL. |

No se ejecutan las recomendaciones de H sobre integración, hardware o piloto. DEC-023 ya acepta la evidencia tardía de AM/remodelación; no se registra otra vez como una decisión nueva.

## 2. Estado que debe conservarse

Diseño; P0 cerrado; I1 cerrado; I2 cerrado técnicamente; P1 y P2 en curso; P3, Pruebas formales y Piloto pendientes; MVP completo no cerrado; RF_REAL pendiente. La suite de 57 pruebas sin fallos/errores/omitidas es evidencia documental existente, no pruebas ejecutadas en esta actualización.

RF-001 Implementado; TEC-001, TEC-002, RF-002, DAT-001, DAT-002 y DAT-003 En desarrollo. No modificar otros estados por estas adiciones. Mantener Java 21 LTS/Maven/JDBC/SQLite/Flyway/JUnit 5 y la secuencia vigente; antes de I3, diseñar persistencia LecturaRFID y relación con SesionOperacion. SDK A4 inspeccionado no equivale a compatibilidad funcional U300 ni a RF_REAL.

## 3. Documento Maestro — textos exactos sugeridos

### 3.1 Adición a §6, AM y P2: cobertura y nomenclatura

Evidencia: A1 §§1–3, A4 §1; H §§3,5–6. Impacto: explicitar universo de cobertura y condiciones de P2, sin ampliar el piloto ni declarar rutas institucionales confirmadas.

> Cobertura del circuito instrumentable. Información operativa preliminar indica que la mayoría de equipos del circuito de dotación/reingreso atravesaría el área candidata a instrumentación RFID. Usar raqui como término operativo único y férula espinal como denominación corregida, sin duplicar categorías por sinónimos.
>
> Camillas, sillas camilla, raqui, corto espinal y férula espinal se identifican preliminarmente como activos con ruta fuera del punto o pendiente de confirmación. Bala central de oxígeno, flujómetros, reguladores de bala central y termohigrómetros de ambulancia presentan condiciones de instalación o remoción rutinaria potencialmente ineficiente o innecesaria. Debe corroborarse con Farmacia/Biomédica si la bala central se cambia allí y si el desmontaje de estos activos es necesario y viable. Estas afirmaciones no constituyen todavía hechos institucionales cerrados.
>
> El MVP no afirmará cobertura automática de activos que no atraviesen el punto instrumentado. EN_RUTA_RFID, FUERA_RUTA_RFID y RUTA_PENDIENTE_CONFIRMAR son categorías conceptuales para el análisis operativo; no autorizan nuevos campos o tablas. Las conclusiones físicas del piloto se limitarán a los activos, trayectorias y configuración realmente ensayados. Los activos fuera de ruta usarán gestión manual/contingencia explícita.

### 3.2 Adición a §3, alcance: camillas

Evidencia: A1 §2.3 y A4 §1. Impacto: aclaración de exclusión vigente de localización activa, sin requisito, compra o partida nueva.

> La posible estrategia institucional futura de GPS/localización activa de camillas para recuperación en hospitales se registra exclusivamente como contexto preliminar y mejora futura FUERA DEL ALCANCE del Proyecto EMI. No se incorpora GPS, RTLS o localización activa al MVP, piloto, arquitectura, requisitos ni presupuesto.

### 3.3 Adición a §6: arquitectura candidata y actor

Evidencia: M §6, R DEC-008/DEC-023, A1 §§5–6, A4 §1, H §§3,6. Impacto: mantener la selección de un punto; evaluar la hipótesis sin aprobar simultáneamente recepción y salida.

> Se considera como hipótesis P2 una antena asociada a banda transportadora y otra a puerta/salida, potencialmente conectadas al mismo lector/hub UHF. Esta disposición solo permanecerá dentro del piloto si se demuestra y autoriza que forma un único punto de control operativo: misma operación/piloto, arquitectura de control única, límites físicos documentados, roles de antena definidos y ausencia o control de solapamientos relevantes. No autoriza pilotear recepción y salida como puntos distintos. La ubicación, modelo final, puertos, potencia, orientación y configuración siguen pendientes de confirmación.
>
> Una antena no equivale a un evento. Su identificador aporta contexto y no determina automáticamente INGRESO/SALIDA; la dirección requiere sesión/contexto y reglas operativas validadas.
>
> Farmacia se perfila preliminarmente como actor principal para operar sesiones. La ventanilla está corroborada en DEC-023, pero no acredita responsabilidades operativas: P2 debe confirmar quién inicia, confirma, corrige y ejecuta contingencia y cuándo interviene Biomédica.

### 3.4 Adición a §4: contingencia mínima

Evidencia: A1 §4, A2, A4 §2; H §5.1; R DEC-014. Impacto: concreción mínima de diseño trazable a RF-006; no implementación ni cambio de prioridad/secuencia.

> La estrategia mínima de diseño del MVP es: Lectura -> Verificación -> Reintento -> Contingencia manual -> Confirmación. La omisión inicial no genera automáticamente un evento final de falla. Cuando un activo esperado no sea detectado, se permitirá un reintento controlado cuando corresponda; si persiste la omisión, se registrará contingencia manual con equipo, actor/usuario, fecha/hora, sesión/contexto, motivo y condición de origen manual explícito.
>
> Para activos fuera de ruta se usará directamente Manual/Contingencia -> Confirmación. Nunca se creará una lectura RFID ficticia para justificar un movimiento. Se preferirá seleccionar código institucional/equipo a escribir EPC hexadecimal. No se incorporará lector manual ni tercer SDK al MVP sin autorización explícita.
>
> Las repeticiones de EPC se conservan en la entrada; la deduplicación y la consolidación de eventos permanecen en su capa e incremento previstos. Estas reglas no modifican ahora OrigenDatos ni materializan nuevas entidades persistidas; RF-006 conserva prioridad Should y estado Aprobado hasta evidencia de software y pruebas.

### 3.5 Ampliación de §9: siguiente tarea P2 geométrica/RF

Evidencia: A1 §§7–9, A2, A3 y A4 §6; H figuras 4–6 y §§4–6. Impacto: exigir profundidad y trazabilidad; salida de diseño candidata, sin cifras inventadas ni cierre físico.

> El análisis especializado P2 debe usar planos y fichas técnicas, documentar fuentes, unidades, supuestos e incertidumbre y cubrir: sistema de coordenadas; escala; dimensiones; alturas y altura libre; materiales, muros/divisiones y mobiliario/metal; banda transportadora; puerta; ventanilla; trayectorias, distancias y orientación de etiquetas; posiciones candidatas de antena; altura, orientación/inclinación; polarización; patrón; banda/frecuencia RF; pérdidas de cable/conectores; presupuesto de enlace teórico; huella teórica; solapamientos; zonas ciegas; energía/red; restricciones constructivas y de montaje; y plan de prepruebas RF_REAL.
>
> La secuencia será Plano -> coordenadas -> trayectorias -> posiciones de antena -> patrón/polarización -> pérdidas -> presupuesto de enlace teórico -> huella aproximada -> solapamientos/zonas ciegas -> configuraciones candidatas -> prepruebas RF_REAL. Si faltan dimensiones, fichas o parámetros se declararán pendientes, sin inventarlos. El plan de prepruebas especificará configuración y escenarios, verdad de terreno y observación de lecturas correctas, omitidas y externas, orientación, metal y múltiples etiquetas. P3 fijará los umbrales después de prepruebas y antes del piloto; no se cambian los criterios vigentes de validación.
>
> GEOMETRIA != MODELO_RF_TEORICO != RF_REAL. Los cálculos seleccionan candidatos y orientan prepruebas; no demuestran desempeño físico, confinamiento, cobertura, omisiones o interferencia. Clasificar cada afirmación como CONFIRMADA (plano/ficha/medición/observación directa o aprobación identificable), CORROBORADA (fuentes independientes coincidentes), PRELIMINAR (reporte sin verificación), PENDIENTE (evidencia insuficiente), CALCULADA (resultado matemático con supuestos) o PENDIENTE_RF_REAL (requiere ensayo físico RFID).

### 3.6 Adición de evidencia a §6 y §8: informe AM/remodelación

Evidencia: H pp.2–8 y R DEC-023; M §6 ya incorpora el núcleo del hallazgo. Impacto: añadir procedencia y riesgo, evitando duplicar una decisión o autorizar un adaptador AM.

> El Informe_Hallazgos_Operativos_AM_RFID_EMI_2026-09-09.pdf se incorpora como evidencia auxiliar de DEC-023. Documenta el catálogo y campo Ubicación Física en AM y su presencia en ficha maestra y orden de trabajo; su semántica dinámica y regla institucional de actualización siguen pendientes. No se autoriza escritura automática desde el MVP ni implementación de integración AM por este hallazgo; se mantiene DEC-005.
>
> El informe aporta fotografías de ventanilla, picking y referencia a torre RFID y describe recepción/reingreso, preparación y dotación/salida. Estos insumos orientan P2, sin sustituir plano dimensional o medición. La jaula de Faraday reportada, el lector manual y la segunda antena siguen preliminares o pendientes: no prueban infraestructura disponible, confinamiento ni desempeño RF_REAL. La confirmación de roles y rutas efectivas queda abierta. Deben controlarse la detección demasiado tardía y el reingreso por rutas diferentes, permitiendo verificación/corrección antes de confirmar.

## 4. Matriz — cambios exactos sugeridos

Conservar identificadores, prioridades, estados y criterios de validación de Q. Solo ampliar evidencia/condiciones; no crear requisitos nuevos. A4 ofrece una alternativa genérica de estado para OPE-002: se resuelve usando el valor real **Aprobado** de Q, no cambiándolo a En desarrollo por una recomendación auxiliar.

| ID | Prioridad y estado que se conservan | Evidencia e impacto |
|---|---|---|
| OPE-002 | Must / Aprobado | A1–A4, H §§3,6,8 y R DEC-023. Ampliar pendientes P2, sin declararlo cumplido. |
| RF-006 | Should / Aprobado | A1 §4, A4 §4, H §5 y DEC-014. Precisar contingencia, sin declararla implementada ni elevarla a Must. |
| RSK-001 | Should / Aprobado | A4 §5, H §5. Añadir riesgos a los ya vigentes de SDK/Java21/AM/confinamiento. |
| TEC-001 | Must / En desarrollo | M §5, A4 §6, H §4. Mantener inspección SDK completada y separar pendientes físicos/funcionales. |

### OPE-002 — añadir a evidencia/condición

> P2 debe confirmar si banda y puerta constituyen un único punto de control operativo autorizado, con límites físicos y roles de antena documentados. Confirmar actor principal Farmacia y rol de Biomédica; inicio, confirmación, corrección y contingencia de sesiones; ruta por tipo de activo, activos fuera de ruta o pendientes de confirmar; horario, equipos, geometría, energía/red, permisos, posiciones de antena y solapamientos. El análisis geométrico/RF seguirá la profundidad y clasificación de evidencia del Maestro. La cobertura automática y las conclusiones del piloto se limitan a los activos y trayectorias realmente incluidos. Mantener Aprobado; cierre P2 pendiente.

### RF-006 — añadir a evidencia/condición

> Diseñar Lectura -> Verificación -> Reintento -> Contingencia manual -> Confirmación. Las omisiones persistentes tras reintento controlado cuando aplique se gestionarán con contingencia explícita; para activos fuera de ruta se usará Manual/Contingencia -> Confirmación. Conservar equipo, actor, fecha/hora, sesión/contexto, motivo y origen manual. Nunca generar una lectura RFID artificial para justificar el movimiento. Preferir selección por código institucional/equipo; no añadir lector manual ni tercer SDK sin autorización. Mantener Should / Aprobado hasta implementación y pruebas, sin adelantar el incremento previsto.

### RSK-001 — añadir a evidencia/condición

> Añadir riesgos de cobertura incompleta por activos instalados o rutas alternativas, desmontaje operacionalmente ineficiente, detección demasiado tardía, confusión entre dos antenas y dos puntos piloto, solapamiento de zonas, inferencia incorrecta de dirección por antena e incorporación accidental de GPS/RTLS fuera de alcance. Controles: corroboración operativa; límites de cobertura explícitos; contingencia auditable; verificación/corrección antes de confirmar; único punto autorizado; dirección por sesión/contexto; geometría y modelo RF separados de RF_REAL. Mantener los riesgos vigentes de SDK, Java 21 funcional, AM y confinamiento.

### TEC-001 — añadir a evidencia/condición

> SDK A4 inspeccionado estáticamente; la compatibilidad funcional con Java 21 y unidad/firmware U300 permanece pendiente. Confirmar número de puertos/antenas efectivamente usados, existencia y función de antena de banda y puerta, compatibilidad del SDK con esa configuración, cableado/conectores y pérdidas, ubicación, altura, orientación/inclinación, polarización, patrón, potencia, región/banda, red/energía y disponibilidad/autorización real. Usar estos insumos en presupuesto de enlace y huella teóricos; verificar desempeño con prepruebas RF_REAL. Identificado no equivale a disponible, configurado, autorizado o probado. P1 permanece en curso hasta EPC UHF reproducible desde hardware autorizado con configuración/SDK funcional documentados.

No modificar VAL ni umbrales por el informe auxiliar: conservar VAL-001/003/004/005/006 y condiciones vigentes. Un modelo teórico o una fotografía no satisfacen validación física.

## 5. Registro de Decisiones — evaluación y texto sugerido

Se verificó directamente R, no se reconstruyeron DEC a partir de informes. DEC-023 ya está aprobada y acepta evidencia operativa tardía de AM/remodelación como insumo P2; DEC-008 conserva un punto; DEC-012/021 separan evidencia de RF_REAL y cierre P1; DEC-014 separa lectura/evento/verificación/sustitución/contingencia; DEC-005 condiciona AM; DEC-016 congela Must; DEC-022 conserva stack.

**No crear DEC-024 ni otra DEC nueva.** No se identificó una necesidad que exija cambiar decisiones vigentes. La hipótesis de dos antenas no queda aprobada por esta propuesta. Incluso al confirmar hardware o geometría, evaluar primero si se ejecuta DEC-008/023; una nueva DEC solo se justificaría si una necesidad no puede resolverse con las decisiones vigentes y exige un cambio o regla transversal aprobada. El lenguaje de “Decisión” de H es una recomendación auxiliar, no un acto adicional de aprobación.

Texto exacto sugerido como nota de actualización de evidencia, sin ID nuevo ni cambio al texto/estado histórico de las DEC:

> Actualización de evidencia v1.9.2. Se incorporan las adiciones operativas posteriores a v1.9.1 y la referencia al informe complementario AM/remodelación como desarrollo de DEC-023, DEC-008 y DEC-014, manteniendo DEC-005, DEC-012, DEC-016, DEC-021 y DEC-022. Se precisan nomenclatura, límites de cobertura, contingencia mínima, actores pendientes y profundidad del análisis geométrico/RF. La configuración de dos antenas permanece como hipótesis condicionada a un único punto operativo autorizado. No se aprueba otro punto piloto, GPS/RTLS, lector manual, tercer SDK ni integración automática AM. No se crea DEC nueva. P1 permanece en curso y RF_REAL pendiente; no cambian objetivos, alcance, metodología, cronograma, presupuesto, stack, piloto o validación. DEC-001 a DEC-023 conservan identidad, estado y significado.

## 6. Evidencia y madurez de las adiciones

| Afirmación | Clasificación | Fuente / condición de avance |
|---|---|---|
| Nomenclatura raqui y férula espinal | CONFIRMADA como instrucción terminológica del usuario | Solicitud y A1–A4; no implica observación de ruta. |
| Mayoría pasa por el área | PRELIMINAR | A1/A3/A4; corroborar flujo efectivo. |
| Excepciones de ruta e instalación/remoción de activos | PRELIMINAR / PENDIENTE | A1–A4; confirmar con Farmacia/Biomédica por tipo de activo. |
| GPS futuro de camillas | PRELIMINAR; exclusión explícita del alcance | A1/A4 y solicitud del usuario. |
| Campo/catálogo AM | CONFIRMADA según capturas documentadas en H | H pp.2–4; no confirma semántica dinámica. |
| Campo AM visible también en OT | CORROBORADA según H | H pp.2–4; regla de actualización PENDIENTE. |
| Ventanilla/picking/torre RFID en plano | CORROBORADA según H y R DEC-023 | H pp.5–7; no acredita montaje físico ejecutado ni medidas definitivas. |
| Banda/flujo de recepción | CORROBORADA en el alcance que declara H | H p.2; rutas por activo, roles y operación oficial siguen por confirmar. |
| Farmacia opera sesiones | PRELIMINAR / PENDIENTE | A1/A4; recepción por Farmacia no prueba permisos de sesión. |
| Jaula de Faraday/confinamiento | PRELIMINAR / PENDIENTE_RF_REAL | H §§3,5; confirmar materiales y ensayar desempeño. |
| Lector manual y segunda antena | PRELIMINAR / PENDIENTE | H §§3–4; no incorporados al MVP por este informe. |
| Dos antenas componen un único punto | PENDIENTE | A1/A4; requiere demostración y autorización P2. |
| Dimensiones/configuraciones candidatas | PENDIENTE | Obtener plano/ficha/medición suficiente. |
| Huella y presupuesto de enlace futuros | CALCULADA cuando existan entradas y cálculo | Esta tarea no realiza el análisis ni produce cifras. |
| Alcance, omisiones, externas, solapamiento e interferencia efectivos | PENDIENTE_RF_REAL | Prepruebas con hardware autorizado; no deducirlos del modelo. |

Las adiciones A1–A4 repiten un mismo reporte y no constituyen cuatro corroboraciones independientes. Las referencias internas F1–F8 de H se mantienen como procedencia declarada del informe, no como nuevas fuentes consultadas directamente en esta tarea.

## 7. Impacto, límites y consolidación posterior

El cambio concreta evidencia y trabajo P2 sin reabrir P0. Mantiene el punto pendiente de congelación y evita atribuir trazabilidad automática a activos fuera de ruta. La contingencia conserva prioridad y estado existentes. No se añaden requisitos, entidades persistidas, software, migraciones, dependencias o hardware.

Antes de emitir PDF v1.9.2, revisar/aprobar estos textos conforme a la jerarquía documental. Conservar íntegramente los históricos v1.9/v1.8.2 y DEC-001 a DEC-023. No sustituir los PDF por esta propuesta ni confundir el contexto 1.9.2 con control formal ya consolidado. La ubicación externa de v1.9.1 queda inventariada; esta tarea no mueve archivos a carpetas de custodia.

Validación de esta entrega: comparación de huellas contra el estado previo, revisión de los tres Markdown y `git diff --check`/`git diff --stat`. Los cambios previos del repositorio se distinguen de esta tarea; no se ejecuta la suite de software al tratarse exclusivamente de mantenimiento documental. No hacer commit automáticamente.
