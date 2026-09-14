# QA de autoconfiguración

## Producción final autorizada — 10/09/2026

La autorización posterior del usuario dio paso a la producción de 15 diapositivas editables y 15 notas incorporadas. PPTX abierto y exportado mediante Microsoft PowerPoint a PDF y 15 PNG, todos inspeccionados visualmente después de las correcciones. QA final apto, sin bloqueantes ni mayores abiertos. Informe completo: `../output/QA_REPORT.md`. Este estado sustituye las referencias de las secciones históricas siguientes a «no existe PPTX» e imagen pendiente. Se incorporó la imagen aportada por el usuario con límites explícitos de interpretación; no se recibió explicación técnica adicional.

Fecha: 10/09/2026. Alcance: preparación del sistema editorial, sin producir PowerPoint.

| Comprobación | Resultado |
|---|---|
| Cinco skills locales | Validador `quick_validate.py`: válidos los cinco; frontmatter y nombres aceptados |
| Trazabilidad documental | 27 afirmaciones E con fuente/versión/localizador, madurez, ámbito, límite y destino |
| Storyboard | 15 entradas; todas incluyen mensaje, objetivo pedagógico, evidencia, visual, visible y notas |
| Referencias E del storyboard | Todas pertenecen a la matriz; revisadas también las referencias expresadas por rangos |
| Fuentes identificadas | 66 archivos con SHA-256; hashes cotejados después de configurar, sin variaciones desde el manifest |
| Estado vigente | Contrastado con Propuesta y tres PDF v1.9.2; discrepancias C-01 a C-08 registradas |
| Evidencia de pruebas | XML existentes: 57 pruebas, 0 fallos/errores/omitidas; no ejecución nueva de Java |
| Gramática visual | Spec 16:9, Arial, retícula, márgenes, mínimos, estados, fotos y diagramas editables |
| Helpers JavaScript | Importación y casos de límites, dimensiones/valores inválidos, solapamiento y contacto de bordes verificados con Node |
| Cajas de tema | Título, cuerpo y pie dentro del lienzo y sin solapamiento mutuo |
| Codificación | Markdown revisado como UTF-8, sin caracteres de reemplazo |
| Git | `git diff --check` sin errores; cambios seguidos limitados a AGENTS y gitignore; nuevas carpetas editoriales |
| Código de negocio | Sin cambios a `src/main`, `src/test`, V1/V2 o `pom.xml`; sin integración SDK |
| PPTX/render/PowerPoint | NO REALIZADO, por alcance explícito del encargo |

El primer intento de validación de skills encontró PyYAML ausente; se instaló solo bajo `tmp/skill-validation-deps/`, ignorado por Git. El validador requirió modo UTF-8 en Windows para leer los acentos; la ejecución final pasó con `PYTHONUTF8=1`. No se modificaron dependencias Maven ni se instaló un stack de aplicación adicional.

La revisión editorial conserva las condiciones del piloto y evita confundir asociación con movimiento, simulación con RF_REAL, criterio con resultado y recomendación con DEC. No se detectaron bloqueantes para esta preparación. Los pendientes del proyecto y de la futura producción siguen en `source_review.md`; no son logros cerrados.

La inspección visual de fotografías/planos, el render de todas las slides, el control tipográfico de overflow y la editabilidad real del PPTX se harán al producir. Este informe no acredita una presentación renderizada o revisada en PowerPoint. No se hizo commit ni publicación; `Java For PC/` sigue como carpeta preexistente sin seguimiento.


## Revisión del storyboard v0.3 — encargo posterior

Resultado: 15 slides y 15 notas con numeración/títulos coincidentes; composición, mensaje, objetivo, fuente, texto visible y notas en cada slide. Las notas incluyen explicación, sobreafirmación a evitar, transición, pregunta y respuesta basada en fuentes. Matriz actualizada a 28 IDs y nuevas referencias de slides. Stack DEC-022 completo y visible en 07. Los diez pendientes exigidos permanecen explícitos en la comparación 13. La slide 14 conserva Pruebas antes de Piloto.

Las 66 fuentes del manifest coinciden por SHA-256 con el corte anterior; no se ejecutó Maven ni se modificó código de negocio. Se comprobó UTF-8 sin caracteres de reemplazo y `git diff --check` sin errores. No existe PPTX generado. Los resultados de autoconfiguración de la sección anterior son antecedentes, no el número actual de afirmaciones.

Perspectiva de profesor evaluador aplicada a las seis preguntas del encargo y registrada al final del storyboard. Correcciones: glosario visible, correspondencia de requisitos sin causalidad histórica inventada, arquitectura con conexiones futuras discontinuas, stack por funciones, comparación completa y ruta de validación sin salto metodológico. No se ejecutó revisión independiente con otro agente.

Planos: se inspeccionaron imágenes fuente H pp.5–7 antes de la última indicación del usuario, sin editarlas; se registró C-09 (Túnel/torre). Por esa indicación, solo se reservan P-01/P-02 en slides 04/12, pendientes de imagen mejorada y explicación. No se generó ni mejoró imagen alguna. La interpretación espacial y las anotaciones finales quedan abiertas hasta recibir el material; esto no bloquea la revisión narrativa actual.

La revisión no acredita render, legibilidad final, overflow tipográfico o editabilidad de un PPTX. Estos controles corresponden a producción posterior. Se preservó storyboard v0.1 en `presentation/historico/`; brief, spec y skill de narrativa se sincronizaron al conteo de 15 slides. No se modificaron AGENTS ni documentos de control en este encargo de storyboard.

Revisión posterior final: 29 diapositivas y notas (18 principales + 11 anexos). Definiciones de códigos y recorte nativo del plano incorporados por solicitud expresa. QA aprobado tras nueva exportación y revisión; detalles en production_revision.md y ../output/QA_REPORT.md.

Última revisión: conector 09 corregido, stack 10 rediseñado, estilo verde inspirado en UdeA aplicado al conjunto. QA final sin desbordamientos; 654 formas nativas. Véase output/QA_REPORT.md.

Logos incorporados en stack 10; QA visual y estructural aprobado. Ver informe final en ../output/QA_REPORT.md.

Red de proyecto añadida en 18 y guion actualizado a 30 secciones. QA final sin desbordamientos; 30 diapositivas/notas/páginas. Detalle en ../output/QA_REPORT.md.
