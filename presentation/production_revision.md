# Revisión de producción autorizada — 10/09/2026

La aprobación del storyboard fue seguida por dos instrucciones expresas: ampliar el plano y quitar bandas negras; explicar los códigos internos a un docente ajeno a la documentación. Estas instrucciones autorizan las siguientes adaptaciones editoriales, sin alterar la fuente de verdad del proyecto.

## Estructura final

29 diapositivas: 18 de exposición principal y 11 anexos de consulta. El relato aprobado se conserva con esta correspondencia:

| Storyboard aprobado | Presentación final |
|---|---|
| 01 | 01 |
| Orientación añadida | 02: P0–P3; 03: I1–I3 y trabajo posterior; 04: conceptos/códigos |
| 02–15 | 05–18, respectivamente |
| Anexos añadidos | 19–23: DEC-001 a DEC-023; 24–29: los 29 requisitos |

Los identificadores E01–E28 y sus fuentes permanecen válidos. Sus referencias numéricas al storyboard se traducen con esta tabla. Las definiciones nuevas proceden de Maestro v1.9.2 §§2/9, Matriz v1.9.2 §§2/5 y Registro v1.9.2 p.2. No se crean P4, DEC-024 ni numeraciones futuras I4+ no establecidas en el control.

Los anexos se leen como requisitos/decisiones y estados, no como resultados ejecutados. Se distingue aprobado de implementado; indispensable corresponde a Must y deseable a Should. DEC-002 y DEC-004 están reemplazadas. La explicación de los códigos forma parte de la presentación proyectable, además de las notas.

## Plano

El PNG aportado se conserva intacto. PowerPoint aplica un recorte nativo, reversible, de la zona aproximadamente x=20..1040, y=115..805 de la imagen de 1122 × 1402 píxeles. Se muestra ventanilla/picking/entrega en una caja de 6,7 × 4,5324 pulgadas, sin deformación apreciable. El recorte excluye bandas negras y la parte inferior no necesaria para la explicación de estas dos diapositivas; el original completo continúa incrustado.

Las diapositivas finales 07 y 15 declaran «detalle» y conservan límites de escala y evidencia. No se redibujó el plano, no se reinterpretaron puntos rojos y no se generaron píxeles mediante IA. Las posiciones no demuestran ubicación de antenas o cobertura física.

## Notas y reproducción

Las 29 notas están incorporadas al PPTX. `output/speaker_notes_final.md` es la extracción completa, ordenada según la numeración final. `speaker_notes.md` conserva el guion aprobado que alimenta las 15 diapositivas originales; el generador incorpora las notas adicionales de orientación y anexos.

Generación: `src/presentation/build.mjs`, con PptxGenJS y el catálogo `catalogs.mjs`. Render: `render.ps1`, mediante PowerPoint. Comprobación estructural: `qa.py`. Resultado y limitaciones: `output/QA_REPORT.md`.

## Ajuste final: conector, stack e inspiración UdeA

Por solicitud del usuario se corrigió el conector central de la diapositiva 09: dos entradas ortogonales discontinuas convergen en una única salida con flecha. Se eliminó el cruce diagonal. La semántica continúa siendo procesamiento pendiente.

La diapositiva 10 se recompuso en dos niveles: Java 21 LTS → JDBC → SQLite en ejecución; Maven, JUnit 5 y Flyway para construir y verificar. Cada tecnología conserva texto nativo y función explicada; LTS se define como soporte prolongado. Se ampliaron seis cajas de texto durante QA sin disminuir fuentes.

Estética editorial inspirada en la Universidad de Antioquia: verde 357A3E, texto verde oscuro 203C2B, fondo FAFCF8 y acentos claros E8F1E2, franja superior y pie académico. Referencia consultada: [Banco oficial de recursos UdeA](https://www.udea.edu.co/wps/portal/udea/web/inicio/somos-udea/empleados/gestion-organizacion/banco-recursos-multimedia/Contenido/asMenuLateral/logosimbolo-udea), que identifica el verde como parte de su paleta institucional. Los valores elegidos son una adaptación editorial para pantalla, no una certificación de equivalencia Pantone ni una plantilla oficial aprobada. No se incorporó ni reconstruyó el escudo.

Se reexportaron las 29 diapositivas y se revisó la continuidad de todas mediante hojas de contacto, además de inspección ampliada de 09 y 10; los controles de texto se ejecutaron sobre el conjunto. Resultado final: 29 diapositivas, 29 notas, 29 páginas PDF, 654 formas nativas, 2 colocaciones documentales con recorte reversible, cero desbordamientos y cero afirmaciones incorrectas de las seis buscadas. Las 66 fuentes conservan su hash. El QA de las cuatro perspectivas permanece válido: estos cambios son de representación y no alteran arquitectura, estados ni alcance.

## Red del proyecto añadida por solicitud del usuario

La diapositiva 18 muestra una red de precedencias tipo PERT/CPM. P0 conduce a dos frentes de preparación: I1/I2 → diseño I3 y resto del MVP; y P1/P2 → prepruebas/P3. Ambos convergen antes de Pruebas, seguidas de Piloto y Validación. Los nombres de fases y estados son explícitos. El esquema es una agregación didáctica de Maestro v1.9.2 §§2/9 y Matriz §§2/5; no establece nuevas dependencias de detalle, autorizaciones ni fechas. No implica que hardware deba esperar a terminar el software.

Se declara en la lámina y las notas que no existen duraciones ni ruta crítica calculada. No se inventan estimaciones PERT, holguras o criticidad. Las notas explican qué se necesitaría para un cálculo cuantitativo y los requisitos comprendidos en cada frente.

Se corrigieron etiquetas largas que provocaban tres líneas y ampliaciones indebidas; el diseño final conserva las fuentes mínimas. PowerPoint exportó 30 diapositivas, 30 notas y 30 páginas PDF. QA del conjunto: 702 formas nativas, ocho imágenes, cero desbordamientos, cero extensiones negativas, las 66 fuentes originales sin cambios y ninguna coincidencia de las seis afirmaciones incorrectas buscadas. Se inspeccionó la nueva diapositiva renderizada tras corregir; el resto mantiene su composición, con renumeración del cierre y anexos.

Estructura vigente: 19 diapositivas principales y 11 anexos. La nueva red ocupa 18; el cierre pasa a 19; los anexos pasan a 20–30. El guion de explicación tiene 30 secciones correlativas y una explicación de cómo recorrer la red, qué significan PERT/CPM y qué no está calculado. Se actualizaron también las notas incorporadas y su extracción complementaria.
