---
name: emi-source-of-truth
description: Identifica fuentes vigentes, discrepancias y límites de evidencia del Proyecto EMI para preparar afirmaciones trazables en una presentación académica.
---

# Fuentes y evidencia EMI

Trabajar desde la raíz del repositorio. Leer `docs/FUENTES_DE_VERDAD.md` primero, luego `AGENTS.md` y `presentation/source_review.md`. Consultar el contenido de Propuesta y tres PDF v1.9.2 ACTUAL; si cambia el inventario, verificar autorización de nueva vigencia. El nombre ACTUAL de un histórico no basta.

La precedencia editorial solicitada es Propuesta → Maestro → observaciones formales del asesor identificables → Matriz → Registro → anexos/informes → entrevistas/preliminares. La diferencia respecto a la jerarquía formal está registrada como C-01; no cambiar PDF por ese motivo. Toda DEC se verifica en Registro ACTUAL y toda prioridad/estado de requisito en Matriz ACTUAL.

Inventariar archivos incluyendo fuentes ignoradas por Git. Usar `presentation/source_manifest.json` para detectar cambios por SHA-256; un hash acredita identidad de archivo, no veracidad. Leer completos los documentos que fundamentan conclusiones. Para antecedentes, localizar pasajes pertinentes y declarar el nivel real de revisión. No repetir inspección SDK general: usar el análisis estático ya consolidado salvo pregunta nueva.

Para cada afirmación registrar en `presentation/evidence_matrix.md`: ID E, enunciado limitado, fuente, versión, página física (desde 1) o sección/método, madurez, ámbito, condición de uso como conclusión y diapositivas. No copiar un título de informe como afirmación de implementación.

Tres dimensiones independientes:

- Estado: APROBADO, IMPLEMENTADO, EN DESARROLLO, CERRADO, EN CURSO, PENDIENTE, según el objeto controlado.
- Madurez: CONFIRMADA, CORROBORADA, PRELIMINAR, PENDIENTE, CALCULADA, PENDIENTE_RF_REAL; definiciones en AGENTS.
- Ámbito: SOFTWARE, SIMULACION, RF_REAL o NO APLICA. SDK estático se identifica como análisis estático de software, nunca emisión simulada ni RF_REAL.

La Propuesta p.5 relaciona documentada/observada con confirmada; probable con preliminar; no comprobada con pendiente. Conservar el origen de esa clasificación. Un informe que repite otro no corrobora de manera independiente.

Ante contradicción, añadir C en `source_review.md` con los pasajes opuestos, autoridad, aplicación temporal y tratamiento. Aplicar fuente superior sin borrar la inferior. Si falta fuente superior, suspender solo la conclusión afectada y continuar lo sustentado. No inventar v1.9, AP-021, planos, fichas o aprobaciones ausentes.

Actualizar fuentes y matriz antes de storyboard. Las 57 pruebas documentadas son 32 previas + 25 nuevas, no 89 ni 57 pruebas físicas. Leer XML existentes es revisión de evidencia previa; solo una ejecución nueva permite decir «ejecutadas en esta tarea».
