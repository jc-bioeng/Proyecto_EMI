---
name: slide-qa
description: Revisa factualidad, trazabilidad, alcance, claridad y geometría de la presentación EMI; distingue QA documental de render y comprobación real en PowerPoint.
---

# QA EMI

En autoconfiguración, revisar brief/matriz/storyboard/spec y scripts, sin crear PPTX. En producción posterior, añadir revisión visual y editabilidad. Registrar resultado y pendientes en `presentation/qa_report.md`, sin introducirlos en notas del asesor.

## Exactitud antes de estética

- Seguir cada ID E hasta fuente, versión y localizador. Verificar DEC en Registro, requisitos en Matriz y línea base en Propuesta; revisar C abiertas.
- Comprobar Diseño, P0 cerrado, I1/I2 cerrados; P1/P2 en curso; P3, Pruebas formales, Piloto y RF_REAL pendientes; MVP incompleto. Revalidar si cambia el corte.
- Exigir 57 = 32 + 25; cero fallos/errores/omitidas documentados. No equivale a 100% de lectura, cobertura de código ni fase formal de Pruebas terminada.
- Diferenciar SOFTWARE, SIMULACION y RF_REAL; SDK estático no es RF_REAL. «Confirmado» no transfiere certeza a roles, rutas, cobertura o permisos vecinos.
- Mantener verificación, sustitución y contingencia como pendientes. No añadir INACTIVA prohibida, adaptador AM, lector manual, GPS, segundo punto o DEC-024.
- Conservar mínimos y condiciones de validación. No inventar umbrales RF o fechas del piloto.

## Claridad y lenguaje

Revisar una idea por slide, títulos sustentados, progresión desde aprobación y las nueve preguntas del encargo. Comprobar ortografía española, acentos, `raqui`, `férula espinal`, EPC y nombres de clases exactos. Explicar acrónimos; evitar párrafos, redundancia y títulos genéricos. Las notas desarrollan el argumento, no repiten el texto.

## Geometría y revisión visual posterior

Comprobar 16:9, márgenes, tamaños mínimos, contraste, alineación, densidad, proporción de fotografías y conectores. Buscar overflow, texto cortado, líneas huérfanas, tablas comprimidas y superposiciones. Revisar todas las imágenes renderizadas; documentar número de slide, defecto y corrección. Los chequeos de cajas no detectan por sí solos desbordamiento tipográfico.

Abrir el PPTX o inspeccionar su estructura para comprobar que texto, tablas y diagramas principales continúan editables y las notas están presentes. No afirmar revisión en PowerPoint si solo se inspeccionó XML o un render externo.

Clasificar hallazgos: bloqueante (fuente ausente para conclusión, sobreafirmación, alcance alterado, contenido cortado), mayor (ambigüedad, lectura difícil, límite oculto), menor (consistencia editorial). Entregar solo con bloqueantes y mayores resueltos; un pendiente del proyecto bien rotulado no es un defecto de la presentación.
