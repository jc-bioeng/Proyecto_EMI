> Actualización de producción autorizada: 29 diapositivas (18 principales + 11 anexos), plano ampliado con recorte editable y definiciones de códigos. Véase [production_revision.md](production_revision.md) para la correspondencia con el storyboard aprobado. Las referencias anteriores a producción pendiente son antecedentes.

# Brief de presentación EMI

Versión editorial 0.3 · corte 10/09/2026 · control v1.9.2. Preparación documental; no hay PPTX generado. Fuentes y discrepancias en `evidence_matrix.md` y `source_review.md`.

## Propósito y audiencia

Actualizar a Javier Hernando García Ramos, asesor interno de Bioingeniería de la Universidad de Antioquia (Propuesta p.1), sobre la evolución desde la aprobación. Presenta Juan José Cortés Fajardo. Según el encargo, la última reunión se centró en aprobar la propuesta; no se inventa una fecha de esa aprobación.

Título formal: **Desarrollo y validación de un prototipo de trazabilidad basado en RFID pasivo para equipos biomédicos administrados por EMI Medellín**. Subtítulo de la Propuesta: «Implementación de un producto mínimo viable y prueba piloto en un punto de control operativo».

Mensaje rector: el proyecto avanzó a Diseño y materializó dos incrementos de software; dispone de evidencia automatizada de asociación y entrada simulada, mientras continúa preparando las condiciones físicas y operativas para probar y validar el prototipo.

## Qué se aprobó

Objetivo general de la Propuesta p.3: desarrollar y validar un prototipo funcional de trazabilidad basado en RFID pasivo para apoyar el registro y control de movimientos de equipos biomédicos administrados por EMI Medellín. Objetivos específicos (síntesis pp.3–4): caracterizar y establecer requisitos/línea base; diseñar arquitectura física, lógica y operativa; implementar asociación, captura/almacenamiento, eventos e historial; validar técnica y operativamente y estimar costos.

Secuencia: Diagnóstico → Requisitos → Diseño → MVP → Pruebas → Piloto → Validación. La fase formal actual es Diseño; los incrementos técnicos no cambian por sí solos la fase. El resultado previsto conserva MVP funcional, pruebas, piloto delimitado y validación. La Propuesta pp.6–9 condiciona pruebas físicas/piloto a entrega y autorización oportuna del hardware; una restricción se documenta sin convertir simulación en validación física ni eliminar el objetivo del piloto.

Un único punto autorizado, RFID UHF pasivo y equipos biomédicos. AM mantiene su función institucional. Exclusiones: sustitución/integración automática no autorizada de AM, RTLS/GPS/localización en tiempo real, medicamentos/insumos, múltiples puntos, compra masiva y despliegue institucional.

Cronograma base (Propuesta p.8): 24 semanas; caracterización 1–4, requisitos 5–6, diseño 7–9, MVP 10–15, pruebas 16–18, piloto 19–21, validación/cierre 22–24. No se traduce a avance porcentual o cumplimiento de fechas sin evidencia. Presupuesto global: COP 43.724.342 (p.9); recursos tecnológicos condicionados, desembolso personal del estudiante $0. No significa compra ejecutada o disponibilidad confirmada.

## Evolución sustentada

| Hito | Qué permite explicar | Evidencia |
|---|---|---|
| Propuesta aprobada | Compromiso de intervención evaluable, con alcance acotado | E01–E04 |
| P0 cerrado, 03/09/2026 | Requisitos suficientes y entrada a Diseño | E05 |
| I1 cerrado, informe 06/09/2026 | Identidad equipo-etiqueta, siete flujos y corrección con historial | E06–E07 |
| I2: ejecución documentada 07/09; cierre consolidado en control 09/09 | Fuente común y simulación independiente del proveedor | E08–E10 |
| DEC-023 y control 09/09/2026 | Hallazgos AM/remodelación refinan P2 sin reabrir P0 | E12–E16 |
| Corte 10/09/2026 | Dos incrementos cerrados; gates físicos/operativos aún abiertos | E17–E22 |

No organizar el deck por informes. Los hitos describen evolución y las fuentes la sustentan.

## Aprendizaje operativo y diseño

AM tiene «Ubicación Física»; su semántica dinámica y regla de actualización siguen pendientes. La evidencia de ventanilla/picking/torre RFID está corroborada según DEC-023, pero no confirma un sistema RFID instalado. Banda y recepción orientan la comprensión del flujo; rutas completas por activo, condiciones de remoción y operador de sesiones requieren corroboración.

Farmacia es candidato preliminar para operar sesiones. Usar `raqui` y `férula espinal`; no afirmar que todos los activos atraviesan el punto. Banda + puerta como dos antenas es hipótesis condicionada a demostrar un único punto operativo autorizado. Identificador de antena no determina ingreso/salida.

La lectura es una observación; el evento requiere contexto y reglas. Verificación compara esperados/detectados; sustitución temporal registra origen/destino y cierre; contingencia documenta excepción. Estos componentes posteriores no están implementados por I2. El flujo mínimo de diseño es Lectura → Verificación → Reintento → Contingencia manual → Confirmación; fuera de ruta, Manual/Contingencia → Confirmación. Nunca inventar lectura RFID.

## Implementación y evidencia

I1: Equipo, EtiquetaRFID y AsignacionEtiqueta persistidos; crear equipo, crear etiqueta, asociar, consultar vigente, buscar por EPC, corregir e historial. Corrección cierra e inserta con una transacción y un instante UTC, preserva historia y revierte ante fallo. Historial de asociaciones, no historial operativo de movimientos.

I2: FuenteLecturasRFID, FuenteSimulada, LecturaEntradaRFID y OrigenDatos; EPC literal, timestamp de generación/recepción, origen y metadata opcional. Preserva repeticiones. No persiste LecturaRFID ni genera eventos. El puerto está desacoplado del fabricante; TEC-002 sigue en desarrollo porque falta segunda fuente compatible.

57 pruebas previas documentadas = 32 de I1 + 25 nuevas; 0 fallos, 0 errores, 0 omitidas. Se cotejaron siete XML Surefire existentes con esos totales, sin ejecutar Maven en esta tarea. Software/persistencia e ingreso simulado no prueban alcance RF, metal, interferencia, antenas, omisiones o lecturas externas. La inspección estática SDK completada tampoco lo demuestra.

Stack DEC-022: Java 21 LTS, Maven, JDBC, SQLite, Flyway y JUnit 5. PptxGenJS es herramienta de producción editorial; no cambia ese stack.

## Próximos productos y acompañamiento propuesto

- Diseño previo a I3: persistencia LecturaRFID y relación con SesionOperacion; después respetar secuencia de incrementos, sin reabrir I1/I2.
- P1: prueba funcional aislada del SDK y primera lectura EPC UHF reproducible desde unidad autorizada, con firmware/configuración documentados.
- P2: único punto, rutas, actores, geometría, antenas, energía/red y permisos; análisis plano → coordenadas → trayectorias → antenas/patrón → pérdidas → enlace → huella teórica → solapamientos → candidatos → prepruebas.
- P3 tras prepruebas: fijar metas antes de piloto; luego Pruebas → Piloto → Validación. El análisis teórico no acredita RF_REAL.
- Acompañamiento propuesto: revisar coherencia del diseño del siguiente incremento; ayudar a concretar responsables/condiciones para P1/P2; revisar protocolo y criterios de aceptación cuando existan prepruebas. No son nuevas decisiones ni permisos otorgados.

Conservar Propuesta pp.5–7: mínimo diez repeticiones por escenario prioritario y verdad de terreno; cero eventos duplicados almacenados. Piloto orientado a dos perfiles autorizados, veinte eventos completos y tres sesiones; ajustes por restricciones deben justificarse antes de iniciar. Resultados limitados a configuración y muestra ensayadas. Valoración final técnica, operativa y económica: favorable, condicionada o no favorable; ninguna se presume hoy.

Plan editorial: 15 slides; diseño sobrio 16:9 con diagramas editables. Duración y fecha de reunión pendientes; no comprimir evidencia para una duración inventada.


Actualización editorial: storyboard v0.3 de 15 slides, con stack explicado visualmente en 07. Imágenes de plano en 04/12 reservadas para el aporte de mejor calidad y explicación del usuario, aún pendientes; no se generan imágenes ni PPTX. Notas completas en `speaker_notes.md`; reservas y tratamiento de evidencia visual en `visual_assets_plan.md`.
