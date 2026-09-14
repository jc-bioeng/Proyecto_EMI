# Revisión de fuentes para la presentación

Corte editorial: 10/09/2026. Control formal: v1.9.2 del 09/09/2026. Esta revisión no cambia decisiones ni documentos fuente. IDs C identifican discrepancias editoriales, no DEC.

## Inspección y vigencia

Se inventarió el repositorio incluyendo archivos ignorados. Se leyeron la Propuesta (11 páginas), Maestro (6), Matriz (6) y Registro (4) v1.9.2; el informe AM/remodelación (9) y cierre I1 v0.2 (6); documentos Markdown de estado/desarrollo e informe I2. Se consultaron contrato/fuente simulada y resultados XML Surefire existentes. Soportes P0, entrevista y checklist se inspeccionaron por extracción de texto/tablas; históricos y avance I1/P1 mediante búsqueda de pasajes de estado y autoridad. No se hizo auditoría visual completa de anexos, reejecución de pruebas Java ni reinspección del SDK. Las fotografías no se han aprobado aún como assets de diapositivas.

`source_manifest.json` registra 66 rutas con tamaño y SHA-256 de documentos y fuentes técnicas de apoyo; `repository_inventory.json` registra 652 archivos del espacio de trabajo durante la configuración, excluyendo metadatos internos `.git`. Es una instantánea, no un índice que se actualiza automáticamente. Inventariar no significa leer o ejecutar todos los binarios. `Java For PC/` ya estaba sin seguimiento: se identifica como SDK externo; no se modifica ni distribuye.

Vigentes: Propuesta `docs/control_local/Propuesta_Juan_Cortes_20262.pdf` y los tres PDF v1.9.2 ACTUAL en la misma carpeta. Históricos: doce PDF de Maestro/Matriz/Registro v1.8, v1.8.1, v1.8.2 y v1.9.1 bajo `docs/control_local/historico/`. Adenda DEC-022 preservada bajo `decisiones_individuales/`. El informe I1 v0.1 es antecedente del cierre v0.2, aunque esté en soporte y no en histórico. Los datos de contacto/identificación de la Propuesta no se transcriben al material editorial.

## Discrepancias y tratamiento

| ID | Evidencia de la discrepancia | Tratamiento para la presentación |
|---|---|---|
| C-01 | Encargo actual: observaciones formales del asesor entre Maestro y Matriz. Maestro v1.9.2 p.1 y FUENTES: no contemplan ese nivel. | Aplicar la precedencia editorial posterior y dejar constancia; no cambiar jerarquía dentro de PDF. No se localizó un acta independiente que active ese nivel. |
| C-02 | README y `docs/desarrollo/{arquitectura,modelo_datos,casos_de_uso_i1}.md`, sección «Estado técnico»: I1 aún no cerrado, fuente no implementada. Maestro v1.9.2 p.2: I1 e I2 cerrados. | Estados de Markdown son anteriores y no describen el corte actual. Usar v1.9.2; conservar archivos sin reescribirlos en esta tarea. |
| C-03 | `INFORME_TECNICO_I2_FUENTE_RFID_SIMULADA.md` §13 dice «técnicamente cerrable» y propone cambios; Matriz v1.9.2 p.4 conserva fila «cerrable». Maestro p.2 y Matriz p.5 §6 consolidan cierre. | Presentar I2 cerrado técnicamente; distinguir criterio de suficiencia de estado formal. Las propuestas finales del informe ya son antecedentes de consolidación. |
| C-04 | Checklist P1 v0.1, hoja de decisiones, REC-P1-003: Java 17 + Spring Boot provisional. Registro v1.9.2 p.2 DEC-022 y `pom.xml`: Java 21/Maven/JDBC/SQLite/Flyway/JUnit 5. | Prevalece DEC-022. No presentar recomendación antigua como decisión ni añadir Spring. |
| C-05 | Checklist P1 v0.1, P1-06: SDK pendiente; cierre I1 v0.2 p.6 pide inspeccionarlo. Maestro v1.9.2 p.3 y Registro p.3: inspección A4 completada. | Inspección estática completada; prueba funcional y RF_REAL pendientes. No repetir inspección general. |
| C-06 | Entrevista, encabezado: «Dispositivos Médicos e Insumos»; §6 describe un proyecto institucional con bodegas e insumos. Propuesta pp.2–3 y Maestro p.2 delimitan equipos biomédicos y un punto. | Título formal de Propuesta. Relato institucional como contexto preliminar; insumos, múltiples puntos y despliegue general excluidos. |
| C-07 | Informe AM/remodelación p.8 recomienda dejar interfaz/adaptador AM preparado. Maestro pp.3–4 y Registro DEC-005 condicionan integración. | No convertir recomendación auxiliar en componente aprobado/implementado. Campo AM existe; semántica e integración pendientes. |
| C-08 | FUENTES, sección I2/SDK, termina remitiendo a PDF v1.9.1; portada/inventario y control actual son v1.9.2. Documentos de desarrollo también conservan rutas v1.8.1 que ya son históricas. | Usar rutas v1.9.2 de cabecera y manifest. Referencias antiguas solo como antecedentes; no seguir rutas antiguas como fuente vigente. |

Las divergencias C-02 a C-05 y C-08 son principalmente desfases temporales de soportes; no justifican reabrir incrementos. C-06 y C-07 son riesgos de trasladar contexto/recomendación a alcance. No se detectó en los pasajes revisados una modificación aprobada de objetivos, presupuesto o estrategia de validación que deba aplicarse al deck.

## Vacíos de evidencia y datos editoriales

- El Registro p.1 exige preservar v1.9, pero el inventario no contiene esa versión exacta. No confundir con v1.9.1; conservar referencia como ausencia documental.
- AP-021 del 31/07/2026 aparece citado en Propuesta pp.5 y 11; original no encontrado en el repositorio. Puede explicarse la recomendación de simulación según Propuesta, sin afirmar lectura directa del acta. Tampoco se localizó registro independiente de fecha exacta de aprobación; no usar fecha de entrega como aprobación.
- El Anexo 1 presupuestal Excel citado en Propuesta p.8 no está localizado. Se dispone de presupuesto global en p.9. No reconstruir partidas detalladas.
- Fichas de antenas/etiquetas y plano dimensional original aparecen referenciados, pero no se localizaron como archivos independientes. Fotos dentro del informe no reemplazan plano a escala ni ficha. La documentación API del SDK sí existe y tiene análisis previo.
- Hardware autorizado, firmware/configuración, prueba funcional Java 21, licencia del SDK y EPC RF_REAL reproducible pendientes; no hay demostración física nueva.
- P2: punto, dimensiones/materiales, rutas por activo, roles de Farmacia/Biomédica, energía/red, montaje, antenas y solapamientos por confirmar. No ejecutar análisis RF especializado en esta tarea.
- P3: metas de lecturas correctas/externas y tiempo máximo tras prepruebas; no sustituir por valores inventados. Se mantienen mínimos y cero duplicados de la Propuesta.
- Para producir el deck: fecha/duración efectiva de reunión y plantilla/logos institucionales autorizados pendientes. Se usa propuesta editorial sobria y 15 slides; esto no bloquea la preparación actual. Las preguntas de cierre son propuestas de acompañamiento, no solicitudes ya formuladas.

## Estado construido

Diseño; P0 cerrado; I1 cerrado y RF-001 implementado; I2 cerrado técnicamente con entrada simulada; 57 pruebas documentadas (32+25) sin fallos/errores/omitidas. P1/P2 en curso, P3/Pruebas formales/Piloto/RF_REAL pendientes. TEC-001, TEC-002, RF-002, DAT-001, DAT-002 y DAT-003 en desarrollo. No hay cierre global del MVP.

## Actualización de revisión editorial 0.3

Por solicitud posterior se incorpora stack visible (slide 07) y se reservan imágenes del plano para 04/12. El usuario indicó que enviará una imagen de mejor calidad con explicación: **pendiente de recepción**; no generar mejora ni arte final ahora. Se inspeccionaron visualmente las imágenes que el PDF H dibuja en pp.5–7, sin alterar el documento. Esto complementa la revisión inicial, que no incluía QA visual de esos assets.

**C-09 — Rótulo del plano frente a descripción del informe.** H p.7, figura 6, permite leer «Túnel con lectores RFID»; H/Registro DEC-023 describen «torre RFID». Además, el detalle de H p.6 no muestra completos todos los elementos nombrados en su pie. Para transcribir un rótulo visual, usar lo realmente visible; para estado formal, conservar DEC-023. No deducir que son recursos distintos ni corregir el control. Denominación/interpretación pendientes de aclarar con el nuevo material del usuario; el storyboard usa «zona RFID» cuando no necesita dirimirlo. Ninguna versión acredita instalación física.

Las 66 fuentes del manifest permanecen sin cambios respecto a la inspección anterior. El inventario JSON sigue siendo una instantánea histórica de autoconfiguración, no el listado dinámico de nuevos archivos editoriales.
