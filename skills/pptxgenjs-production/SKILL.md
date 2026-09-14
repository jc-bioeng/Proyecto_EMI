---
name: pptxgenjs-production
description: Produce posteriormente el PPTX editable de EMI con PptxGenJS, componentes reutilizables, trazabilidad en notas y verificación de geometría, cuando se solicite generar diapositivas.
---

# Producción PptxGenJS EMI

Este skill no habilita producción en el encargo de autoconfiguración. Al solicitar el usuario la generación, leer AGENTS, los cuatro documentos de presentación y `src/presentation/README.md`.

Usar PptxGenJS por elección explícita del usuario. Es una herramienta editorial separada de DEC-022: no cambiar Maven, Java ni arquitectura del MVP. La versión encontrada en el runtime local el 10/09/2026 es 4.0.1; verificar la disponible al producir. Resolver dependencia mediante el runtime de artefactos o instalación local aislada; no asumir instalación global ni rutas de otro equipo.

Organizar el futuro generador en `src/presentation/`: configuración, componentes, datos y ensamblaje. Reutilizar `theme.mjs` y `geometry.mjs`. Un componente recibe rectángulo, contenido y estilo; devuelve objetos editables y sus cajas de control. No incrustar una diapositiva completa como PNG/SVG.

Usar 16:9, medidas en pulgadas y tipografía en puntos. Mantener texto con `addText`, tablas con `addTable` y diagramas con formas/conectores nativos. SVG solo para assets auxiliares: viewBox explícito, proporción preservada, sin fuentes externas ni scripts. Los elementos dentro de un SVG no son editables individualmente en PowerPoint; no usarlo para el diagrama explicativo principal.

Cada slide incorpora notas con `addNotes`: explicación oral, IDs E, rutas/versiones/localizadores y límites. Los avisos que cambian la conclusión también deben verse en la slide. No llevar notas de QA ni narración de producción al contenido del asesor.

Antes de exportar, verificar cajas dentro del lienzo/área útil, intersecciones no intencionales y conectores sin texto atravesado. El control rectangular no estima anchura de glifos ni reemplaza render. Registrar solapamientos intencionales (texto dentro de tarjeta o conectores) con motivo.

En producción: crear borrador en `presentation/build/`, renderizar todas las slides mediante herramienta disponible (PowerPoint/LibreOffice o runtime), revisar imágenes a tamaño de lectura y corregir. Repetir solo las verificaciones afectadas. Inspeccionar editabilidad y notas en el PPTX; si se usa PowerPoint, distinguir apertura real de validación de XML.

Guardar PPTX revisado en `presentation/output/` solo después de QA. Mantener esos directorios fuera de Git y no copiar PDF institucionales ni SDK al entregable. Una exportación exitosa no demuestra ausencia de overflow ni calidad factual. Usar `skills/slide-qa/SKILL.md` para el cierre.
