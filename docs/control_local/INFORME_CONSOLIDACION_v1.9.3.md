# Diagnóstico previo de consolidación v1.9.3

Fecha: 13/09/2026. Antecedente inmediato: v1.9.2. Análisis previo a generación final de los tres documentos.

## Inconsistencias detectadas y resolución

| Hallazgo | Fuente anterior | Evidencia posterior | Resolución |
| --- | --- | --- | --- |
| I3/I4/I5 ausentes; total vigente 57 | Maestro v1.9.2 §2 | Informes I3-I5 y 18 XML Surefire | Cierres técnicos SOFTWARE / SIMULACION; total 169/0/0/0. Conservar progresión 32 -> 57 -> 82 -> 134 -> 169. |
| RF-004 Aprobado | Matriz v1.9.2 §2 | I5, escenario RF-004 | IMPLEMENTADO en SOFTWARE; sin estado físico ni RF_REAL. |
| DAT-001 En desarrollo | Matriz v1.9.2 §2 | I3-I4, evento_lectura y pruebas | IMPLEMENTADO en SOFTWARE; lecturas originales preservadas. |
| VAL-003 Aprobado y deduplicación futura | Matriz v1.9.2 §2 | I4, 20 lecturas -> 1 evento -> 20 respaldos | EN DESARROLLO — criterio software verificado; Pruebas formales pendientes. |
| Diseño formal frente a I1-I5 construidos | Maestro v1.9.2 §2; DEC-018 | Propuesta p.6; I3-I5; P2 abierto | Mantener DISEÑO; reconocer software del MVP en construcción. No cerrar diseño físico/operativo por inferencia. |
| RF-003 Aprobado; AC-004 Aprobado | Matriz v1.9.2 §2 | I3-I5 | Ambos EN DESARROLLO; avance técnico parcial, P2 y funciones restantes pendientes. |
| Registro compacto no transcribe revisión de DEC-018 | Registro v1.9.2 §2 | Registro histórico v1.8.2 p.11 | Revisión explícita del gate; no inventar condición automática. |
| Encabezados históricos v1.8/v1.7 en archivo v1.8.2 | Registro histórico p.11-12 | Contenido y ruta | Conservar discrepancia; citar ubicación precisa. |

## Evaluación de fase y decisiones

Se mantiene la fase formal DISEÑO. La construcción del software del MVP está materializada mediante I1-I5; no se presenta como trabajo futuro. La Propuesta aprobada, p. 6, define Diseño con arquitectura física, lógica y operativa, modelo, flujos y reglas de evento. I3-I5 acreditan persistencia, sesiones, eventos, deduplicación y consultas, pero no acreditan cierre del diseño físico ni aprobación de la regla operativa: P2 sigue pendiente de punto único, actores, geometría, rutas, red, energía y permisos. El núcleo lógico construido no basta para declarar terminada la fase completa.

Se revisó DEC-018, cuya fecha de revisión es «Al cerrar Diseño / iniciar MVP» (Registro histórico v1.8.2, p. 11). Esa condición obliga a evaluar el estado; no impone una transición automática por número de incrementos. La alternativa MVP EN CONSTRUCCIÓN describe correctamente el avance técnico, pero usarla como sustituto de la fase formal y declarar Diseño cerrado, sin resolver sus entregables físicos y operativos, ampliaría lo demostrado. La falta de hardware no impide seguir desarrollando software conforme a DEC-009/017; no se convierte P1 en un nuevo prerrequisito de programación.

No se crea DEC-024 ni se formaliza el gate Diseño -> MVP en esta consolidación. I3 ejecuta DEC-014/015/022; I4 ejecuta DEC-014/015; I5 materializa las consultas RF-004 conforme a DEC-015/022. Son actualizaciones de evidencia y estado, sin nueva regla transversal. Una futura formalización del gate deberá acreditar o delimitar expresamente la deuda de diseño y revisar DEC-018; solo si introduce una condición transversal no cubierta se justificará una nueva DEC. No se reserva ni aprueba ahora esa numeración.

Convención adoptada para VAL-003: EN DESARROLLO — criterio software verificado (alternativa B). La Matriz v1.9.2 usa Aprobado, En desarrollo, Implementado y Cerrado, pero no define una reserva explícita de Implementado para los requisitos de Validación. La alternativa A sería defendible para el criterio lógico aislado; se elige B, coherente con la recomendación técnica I4, porque la evidencia corresponde a pruebas de desarrollo del motor y permanece pendiente su comprobación integrada con las funciones restantes del MVP en Pruebas formales. No se añade una exigencia RF_REAL al criterio de cero duplicados ni se aplaza el reconocimiento del resultado software. Es una convención de reporte de estado, no una nueva meta de aceptación ni una DEC.

## Estados revisados individualmente

| ID | Antes | Después | Motivo y evidencia |
| --- | --- | --- | --- |
| AC-001 | Cerrado | Cerrado | Propuesta aprobada. |
| AC-002 | Cerrado | Cerrado | Cronograma. |
| AC-003 | Cerrado | Cerrado | COP $43.724.342. |
| AC-004 | Aprobado | EN DESARROLLO | I1-I5: núcleo software construido; funciones restantes y pruebas pendientes. SOFTWARE / SIMULACION. Evidencia incluida en suite total 169/0/0/0. No cierra MVP. |
| AC-005 | Aprobado | Aprobado | Piloto. |
| RF-001 | Implementado | IMPLEMENTADO | I1: asociación e historial equipo-etiqueta, rollback y concurrencia. Suite I1 de 32 pruebas SOFTWARE; no es historial operativo de I5. |
| RF-002 | En desarrollo | EN DESARROLLO | I2-I4: fuente simulada, captura, persistencia, recuperación y consumo explícito. SOFTWARE / SIMULACION; evidencia incluida en suite total 169/0/0/0. Fuente real pendiente. |
| RF-003 | Aprobado | EN DESARROLLO | I3-I4: tiempos, sesión, punto textual, actor/contexto, tipo de evento y respaldo. SOFTWARE / SIMULACION; evidencia incluida en suite total 169/0/0/0. P2 debe confirmar roles, punto y semántica institucional. |
| RF-004 | Aprobado | IMPLEMENTADO | I5: última lectura, último evento e historiales por equipo con asociación histórica. SOFTWARE / SIMULACION; escenario RF-004 y suite I5 de 35 pruebas nuevas. Implementado en SOFTWARE; no equivale a estado físico actual ni RF_REAL. |
| RF-005 | Aprobado | APROBADO | Siguiente incremento software: esperados vs detectados. Sin implementación iniciada; prioridad Should. El tipo de sesión VERIFICACION no implementa esta función. |
| RF-006 | Aprobado | APROBADO | Diseño conceptual de contingencia mínima y activos fuera de ruta (§7); sin implementación. Prioridad Should. Nunca crear lectura RFID ficticia. |
| RF-007 | Aprobado | APROBADO | Must pendiente: abrir/cerrar sustitución temporal con equipo, origen, destino, responsable, motivo, fecha/hora y devolución. |
| TEC-001 | En desarrollo | EN DESARROLLO | Inspección estática A4 conservada; unidad/firmware, runtime, configuración y EPC UHF físico reproducible desde hardware autorizado pendientes. No hay RF_REAL. Condiciones detalladas en §7. |
| TEC-002 | En desarrollo | EN DESARROLLO | I2: FuenteLecturasRFID y FuenteSimulada probadas en SOFTWARE / SIMULACION (25 pruebas nuevas de I2, no exclusivas del requisito). Falta segunda implementación real/compatible; consumo I3-I5 no la sustituye. |
| DAT-001 | En desarrollo | IMPLEMENTADO | I3-I4: LecturaRFID separada de EventoOperativo, vínculo auditable y deduplicación solo de eventos. I5 conserva separación al consultar. SOFTWARE / SIMULACION; evidencia incluida en suite total 169/0/0/0. Sin validación física. |
| DAT-002 | En desarrollo | EN DESARROLLO | I2-I5: origen SIMULACION/RF_REAL preservado por lectura y auditable por respaldo, incluidos conjuntos mixtos. SOFTWARE / SIMULACION; evidencia incluida en suite total 169/0/0/0. RF_REAL sintético no es evidencia física; estructuras posteriores pendientes. |
| DAT-003 | En desarrollo | EN DESARROLLO | I1-I5: modelo materializado hasta V5; faltan Verificación, Sustitución temporal y Contingencia. SOFTWARE / SIMULACION; evidencia incluida en suite total 169/0/0/0. V5 añade solo índices. |
| VAL-001 | Aprobado | Aprobado | Solo RF_REAL. |
| VAL-002 | Aprobado | Aprobado | Pruebas. |
| VAL-003 | Aprobado | EN DESARROLLO | Criterio software verificado por I4: 20 callbacks -> 20 lecturas -> 1 evento -> 20 respaldos; reejecución y concurrencia sin duplicado. SOFTWARE / SIMULACION; evidencia incluida en suite total 169/0/0/0. Pruebas formales pendientes; convención B explicitada. |
| VAL-004 | Aprobado | Aprobado | P3. |
| VAL-005 | Aprobado | Aprobado | RF_REAL + verdad de terreno. |
| VAL-006 | Aprobado | Aprobado | Piloto. |
| OPE-001 | Aprobado | Aprobado | Piloto. |
| OPE-002 | Aprobado | Aprobado | P2 incorpora recepción/dotación y AM como contexto. Cobertura, actores y único punto: §7. |
| ECO-001 | Aprobado | Aprobado | Validación económica. |
| ECO-002 | Aprobado | Aprobado | Permanente. |
| RSK-001 | Aprobado | Aprobado | SDK lifecycle/Java21 + AM + confinamiento + punto/antena. Riesgos operativos adicionales: §7. |
| RSK-002 | Aprobado | Aprobado | Permanente. |

## Alcance de la ejecución

Tres DOCX editables y sus PDF con la misma versión. Copias v1.9.2 conservadas íntegras. Sin cambios de software, nuevas pruebas, commits, propuesta, soporte, anexos ni fuentes institucionales. Se sincroniza exclusivamente el índice docs/FUENTES_DE_VERDAD.md para identificar la nueva vigencia; AGENTS.md, README y presentación preexistentes se preservan como contexto anterior.

## Tabla de cambios

| Documento | Sección | Antes | Después | Evidencia |
| --- | --- | --- | --- | --- |
| Maestro | § / p. 1 | - La v1.9 y la v1.9.1 se conservan como históricos. La consolidación previa añadió evidencia técnica y estado de implementación; no reescribe ni elimina antecedentes. | - v1.9.2 es el antecedente inmediato conservado íntegramente como histórico, junto con las versiones anteriores localizadas. v1.9 está referenciada históricamente, pero no se localizó un archivo independiente; no se inventa su ruta. La consolidación v1.9.3 incorpora evidencia posterior sin atribuirla a v1.9.2. | Inventario de fuentes y copias SHA-256 |
| Maestro | § / p. 1 | - Los hallazgos del SDK son evidencia de compatibilidad lógica potencial, no RF_REAL. El cierre I2 es evidencia SOFTWARE / SIMULACION. | - Los hallazgos del SDK son evidencia de compatibilidad lógica potencial, no RF_REAL. Los cierres I2-I5 son evidencia SOFTWARE / SIMULACION. | I2-I5 |
| Maestro | §2 | [['Elemento', 'Estado vigente', 'Condición'], ['Propuesta', 'APROBADA / congelada', 'Línea base formal sin cambios.'], ['P0 Requisitos->Diseño', 'CERRADO 03/09/2026', 'Baseline Must congelada.'], ['Fase formal', 'DISEÑO', 'No volver a diagnóstico general.'], ['I1 asociación/persistencia', 'CERRADO', '32 pruebas; RF-001 Implementado.'], ['I2 fuente común/simulación', 'CERRADO técnicamente', 'FuenteLecturasRFID + FuenteSimulada + LecturaEntradaRFID; 57 pruebas totales, 0 fallos/errores/omitidas.'], ['P1 hardware UHF', 'EN CURSO', 'SDK A4 inspeccionado; cierre exige EPC UHF RF_REAL reproducible desde hardware autorizado.'], ['P2 punto/arquitectura física', 'EN CURSO', 'Recepción/dotación mejor definidas; punto piloto aún no congelado.'], ['P3 criterios de aceptación', 'PENDIENTE', 'Después de prepruebas, antes del piloto.'], ['Piloto', 'PENDIENTE / condicionado', 'Un único punto autorizado; nunca antes de Pruebas.']] | Estado común post-I5; 169/0/0/0; Diseño; P1/P2 en curso; P3 pendiente. | I3-I5 y análisis del gate |
| Maestro | § / p. 2 | - Las repeticiones de EPC se preservan en la entrada. La deduplicación permanece en un incremento posterior. | - Las repeticiones EPC se preservan en entrada y persistencia. I4 materializó deduplicación exclusivamente en eventos; las lecturas originales no se eliminan. | I3 Verificación; I4 Evidencia VAL-003 |
| Maestro | § / p. 2 | - Pipeline vigente: Fuente -> LecturaEntradaRFID -> persistencia LecturaRFID -> asociación EPC-equipo -> sesión/contexto -> motor de eventos/deduplicación -> Evento/Verificación/Sustitución/Contingencia. | - Pipeline materializado: FuenteLecturasRFID -> LecturaEntradaRFID -> LecturaRFID -> asociación histórica EPC-equipo -> SesionOperacion/contexto -> motor explícito de eventos y deduplicación -> EventoOperativo -> consultas históricas. La sesión proporciona contexto al motor; esta secuencia lógica no implica crear la sesión después de cada lectura. I3 permite lectura sin sesión, que se conserva sin justificar movimiento. Verificación, Sustitución temporal y Contingencia siguen pendientes. | I3 Diseño; I4 Diseño; I5 Diseño |
| Maestro | § / p. 3 | Las repeticiones de EPC se conservan en la entrada; la deduplicación y la consolidación de eventos permanecen en su capa e incremento previstos. Estas reglas no modifican ahora OrigenDatos ni materializan nuevas entidades persistidas; RF-006 conserva prioridad Should y estado Aprobado hasta evidencia de software y pruebas. | Las repeticiones se conservan. I4 ya materializó la deduplicación y consolidación en la capa de eventos. La contingencia continúa como diseño conceptual: estas reglas no añaden un valor manual a OrigenDatos ni una entidad de contingencia persistida; RF-006 conserva Should / APROBADO. | I4; Matriz v1.9.2 §7 |
| Maestro | § / p. 5 | - I2 queda cerrado técnicamente y no debe reabrirse salvo defecto demostrado. | - I1-I5 están cerrados técnicamente en su alcance; no se reabren salvo defecto demostrado. El núcleo implementado no equivale a MVP completo. | I1-I5 |
| Maestro | § / p. 5 | - Antes de programar I3, realizar diseño dirigido de persistencia de LecturaRFID y resolver su relación con SesionOperacion para evitar migraciones que deban rehacerse. | - Próxima acción software: RF-005 — Verificación de esperados vs detectados; después sustitución temporal y contingencia. El diseño y la persistencia LecturaRFID/SesionOperacion ya se materializaron en I3, seguidos por I4 e I5. | I3-I5, deuda y siguiente incremento |
| Maestro | § / p. 5 | - Después: prepruebas -> P3 -> Pruebas -> Piloto -> Validación. | - Completar el MVP y efectuar Pruebas controladas antes del Piloto y la Validación. Para la ruta física: prepruebas RF_REAL -> P3 -> ensayos/piloto condicionado, con P1/P2 satisfechos según corresponda. | Propuesta pp.6-8; gates vigentes |
| Maestro | § / p. 6 | 11. Control de cambios v1.9.2 | 11. Antecedente preservado del control de cambios v1.9.2 | Preservación temporal |
| Maestro | § / p. 6 | Fuentes de esta consolidación | Fuentes históricas de la consolidación v1.9.2 | Preservación temporal |
| Matriz | § / p. 1 | - Por evidencia I2, RF-002, DAT-001 y DAT-002 pasan de Aprobado a En desarrollo: existe materialización parcial, no cumplimiento integral. | - Antecedente I2 en v1.9.2: RF-002, DAT-001 y DAT-002 estaban En desarrollo por materialización parcial. En v1.9.3, DAT-001 pasa a IMPLEMENTADO por I3-I4; RF-002 y DAT-002 continúan EN DESARROLLO. | I3-I4 |
| Matriz | §2 AC-004 | Aprobado; MVP + pruebas. | EN DESARROLLO; I1-I5: núcleo software construido; funciones restantes y pruebas pendientes. SOFTWARE / SIMULACION. Evidencia incluida en suite total 169/0/0/0. No cierra MVP. | Informes I3-I5 / evidencia específica |
| Matriz | §2 RF-001 | Implementado; I1: 32 pruebas; historial/rollback/concurrenci a. | IMPLEMENTADO; I1: asociación e historial equipo-etiqueta, rollback y concurrencia. Suite I1 de 32 pruebas SOFTWARE; no es historial operativo de I5. | Informes I3-I5 / evidencia específica |
| Matriz | §2 RF-002 | En desarrollo; I2 captura/entrega simulada implementada; persistencia LecturaRFID pendiente. | EN DESARROLLO; I2-I4: fuente simulada, captura, persistencia, recuperación y consumo explícito. SOFTWARE / SIMULACION; evidencia incluida en suite total 169/0/0/0. Fuente real pendiente. | Informes I3-I5 / evidencia específica |
| Matriz | §2 RF-003 | Aprobado; Sesión/contexto; dirección no inferida por lectura aislada. | EN DESARROLLO; I3-I4: tiempos, sesión, punto textual, actor/contexto, tipo de evento y respaldo. SOFTWARE / SIMULACION; evidencia incluida en suite total 169/0/0/0. P2 debe confirmar roles, punto y semántica institucional. | Informes I3-I5 / evidencia específica |
| Matriz | §2 RF-004 | Aprobado; MVP. | IMPLEMENTADO; I5: última lectura, último evento e historiales por equipo con asociación histórica. SOFTWARE / SIMULACION; escenario RF-004 y suite I5 de 35 pruebas nuevas. Implementado en SOFTWARE; no equivale a estado físico actual ni RF_REAL. | Informes I3-I5 / evidencia específica |
| Matriz | §2 RF-005 | Aprobado; Verificación previa a confirmación. | APROBADO; Siguiente incremento software: esperados vs detectados. Sin implementación iniciada; prioridad Should. El tipo de sesión VERIFICACION no implementa esta función. | Informes I3-I5 / evidencia específica |
| Matriz | §2 RF-006 | Aprobado; Ruta alternativa/falla de lectura. Contingencia mínima y activos fuera de ruta: §7. | APROBADO; Diseño conceptual de contingencia mínima y activos fuera de ruta (§7); sin implementación. Prioridad Should. Nunca crear lectura RFID ficticia. | Informes I3-I5 / evidencia específica |
| Matriz | §2 RF-007 | Aprobado; MVP. | APROBADO; Must pendiente: abrir/cerrar sustitución temporal con equipo, origen, destino, responsable, motivo, fecha/hora y devolución. | Informes I3-I5 / evidencia específica |
| Matriz | §2 TEC-001 | En desarrollo; SDK A4 inspeccionado; U300/firmware/configuración y EPC RF_REAL pendientes. Confirmación de antenas/configuración y RF_REAL: §7. | EN DESARROLLO; Inspección estática A4 conservada; unidad/firmware, runtime, configuración y EPC UHF físico reproducible desde hardware autorizado pendientes. No hay RF_REAL. Condiciones detalladas en §7. | Informes I3-I5 / evidencia específica |
| Matriz | §2 TEC-002 | En desarrollo; FuenteLecturasRFID + FuenteSimulada implementadas/probadas; segunda fuente compatible pendiente. | EN DESARROLLO; I2: FuenteLecturasRFID y FuenteSimulada probadas en SOFTWARE / SIMULACION (25 pruebas nuevas de I2, no exclusivas del requisito). Falta segunda implementación real/compatible; consumo I3-I5 no la sustituye. | Informes I3-I5 / evidencia específica |
| Matriz | §2 DAT-001 | En desarrollo; I2 separa entrada cruda; relación persistida lectura- evento pendiente. | IMPLEMENTADO; I3-I4: LecturaRFID separada de EventoOperativo, vínculo auditable y deduplicación solo de eventos. I5 conserva separación al consultar. SOFTWARE / SIMULACION; evidencia incluida en suite total 169/0/0/0. Sin validación física. | Informes I3-I5 / evidencia específica |
| Matriz | §2 DAT-002 | En desarrollo; OrigenDatos implementado; SIMULACION probado; RF_REAL y estructuras posteriores pendientes. | EN DESARROLLO; I2-I5: origen SIMULACION/RF_REAL preservado por lectura y auditable por respaldo, incluidos conjuntos mixtos. SOFTWARE / SIMULACION; evidencia incluida en suite total 169/0/0/0. RF_REAL sintético no es evidencia física; estructuras posteriores pendientes. | Informes I3-I5 / evidencia específica |
| Matriz | §2 DAT-003 | En desarrollo; I1 + LecturaEntradaRFID materializados; entidades persistidas posteriores pendientes. | EN DESARROLLO; I1-I5: modelo materializado hasta V5; faltan Verificación, Sustitución temporal y Contingencia. SOFTWARE / SIMULACION; evidencia incluida en suite total 169/0/0/0. V5 añade solo índices. | Informes I3-I5 / evidencia específica |
| Matriz | §2 VAL-003 | Aprobado; I2 preserva repeticiones; deduplicación se prueba después. | EN DESARROLLO; Criterio software verificado por I4: 20 callbacks -> 20 lecturas -> 1 evento -> 20 respaldos; reejecución y concurrencia sin duplicado. SOFTWARE / SIMULACION; evidencia incluida en suite total 169/0/0/0. Pruebas formales pendientes; convención B explicitada. | Informes I3-I5 / evidencia específica |
| Matriz | § / p. 4 | 3. Actualización de evidencia por SDK e I2 | 3. Antecedente de evidencia SDK e I2 en v1.9.1 | Preservación temporal |
| Matriz | § / p. 4 | 4. Evidencia de cierre I2 | 4. Evidencia histórica de cierre I2 | 57 es el total histórico I2, no el vigente |
| Matriz | § / p. 6 | 8. Integridad de la actualización v1.9.2 | 8. Antecedente de integridad de la actualización v1.9.2 | Preservación temporal |
| Matriz | § / p. 6 | Fuentes de esta consolidación | Fuentes históricas de la consolidación v1.9.2 | Preservación temporal |
| Registro | § / p. 1 | - La v1.9 permanece como histórico y la v1.8.2 como registro histórico detallado de DEC-001 a DEC-022. | - v1.9.2 se conserva íntegra como antecedente inmediato; v1.8.2 conserva el registro histórico detallado de DEC-001 a DEC-022. v1.9 se menciona como antecedente en el control anterior, pero no se localizó un archivo independiente. | Inventario de fuentes |
| Registro | § / p. 1 | - La v1.9.2 conserva identidad, estado y significado de DEC-001 a DEC-023. | - v1.9.3 conserva íntegramente el índice y la ficha DEC-023 de v1.9.2, sin cambiar identidad, estado ni significado de DEC-001 a DEC-023. | Cotejo literal de las tablas |
| Registro | § / p. 3 | 5. Por qué no se crea DEC-024 | 5. Antecedente de no creación de DEC-024 en v1.9.1 | Preservación temporal |
| Registro | § / p. 4 | 9. Evaluación de suficiencia de decisiones vigentes | 9. Evaluación histórica de suficiencia en v1.9.2 | Preservación temporal |
| Registro | § / p. 4 | Fuentes de esta consolidación | Fuentes históricas de la consolidación v1.9.2 | Preservación temporal |
| Los tres | Portada, encabezado y control de cambios | v1.9.2; 09/09/2026; antecedente v1.9.1 | v1.9.3; 13/09/2026; antecedente inmediato v1.9.2 | Encargo de consolidación post-I5 |
| Los tres | Fase y evidencia | Diseño; I1/I2; 57 pruebas | Diseño formal; software del MVP en construcción; I1-I5; 169/0/0/0; sin nueva DEC | Propuesta p.6; DEC-018 histórico p.11; I3-I5 |

## Resumen ejecutivo

Se consolidan persistencia de sesiones y lecturas, motor de eventos y deduplicación, y consultas históricas por equipo. RF-004 y DAT-001 quedan IMPLEMENTADOS en SOFTWARE. VAL-003 tiene criterio software verificado y estado EN DESARROLLO por comprobación integrada pendiente. La evidencia automatizada no es RF_REAL ni cierra Pruebas formales.

Pendientes: cierre de diseño físico/operativo, hardware e integración real condicionados, RF-005, RF-006, RF-007, cierre MVP, Pruebas controladas, Piloto y Validación. Se mantiene DISEÑO sin DEC-024. Próxima acción: RF-005 — Verificación de esperados vs detectados; después sustitución temporal y contingencia. P1/P2 continúan en paralelo; P3 después de prepruebas RF_REAL y antes del piloto.

## Verificación cruzada final

| Control | Maestro | Matriz | Registro |
| --- | --- | --- | --- |
| Versión y fecha | v1.9.3 / 13/09/2026 | v1.9.3 / 13/09/2026 | v1.9.3 / 13/09/2026 |
| Fase formal | DISEÑO; software del MVP en construcción | DISEÑO; software del MVP en construcción | DISEÑO; software del MVP en construcción |
| I1-I5 | Cerrados en su alcance técnico | Cerrados en su alcance técnico | Cerrados en su alcance técnico |
| Suite | 169/0/0/0 | 169/0/0/0 | 169/0/0/0 |
| P1 / P2 / P3 | EN CURSO / EN CURSO / PENDIENTE | EN CURSO / EN CURSO / PENDIENTE | EN CURSO / EN CURSO / PENDIENTE |
| RF_REAL | PENDIENTE | PENDIENTE | PENDIENTE |
| Pruebas formales / Piloto / Validación | PENDIENTES; piloto condicionado | PENDIENTES; piloto condicionado | PENDIENTES; piloto condicionado |
| Próxima acción | RF-005 — esperados vs detectados | RF-005 — esperados vs detectados | RF-005 — esperados vs detectados |
| DEC-024 | NO CREADA | NO CREADA | NO CREADA |

Los 29 identificadores y prioridades se preservan. Los estados comunes coinciden literalmente; el índice DEC-001 a DEC-023 y la ficha DEC-023 coinciden con v1.9.2. Se cotejaron 18 XML Surefire existentes y los dos escenarios identificados de VAL-003/RF-004. No se volvió a ejecutar Maven.

Se exportaron los tres DOCX con Word: Maestro 10 páginas, Matriz 10 páginas apaisadas y Registro 8 páginas. Se revisaron las 28 páginas y se corrigieron borde heredado del título, saltos de párrafo y etiquetas aisladas. Se normalizaron separaciones tipográficas del PDF fuente (RF_RE AL, Operacion al y Riesgo/Vali dación), sin cambio semántico. Los originales, código, migraciones, pruebas y fuentes se cotejaron por SHA-256 antes de activar la nueva vigencia.

## Activación y custodia

Los seis entregables están en docs/control_local. Los tres PDF v1.9.2 se trasladaron a docs/control_local/historico sin cambiar sus bytes; el índice previo se conservó íntegro allí. docs/FUENTES_DE_VERDAD.md identifica v1.9.3 como única versión vigente. El manifiesto de consolidacion_v1.9.3 registra hashes y rutas.

La Propuesta y los archivos fuente, migraciones, pruebas, soporte y anexos conservan su hash inicial. No hubo desarrollo, ejecución nueva de pruebas, commit ni publicación externa. Se observó un cambio concurrente en presentation/build/powerpoint_text_bounds.json, archivo regenerable ajeno a esta tarea; se dejó intacto y se excluyó de la afirmación de preservación. AGENTS.md, README y .gitignore no se modificaron por esta consolidación.
