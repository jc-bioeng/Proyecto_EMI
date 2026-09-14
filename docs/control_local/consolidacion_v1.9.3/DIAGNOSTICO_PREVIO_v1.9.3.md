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
