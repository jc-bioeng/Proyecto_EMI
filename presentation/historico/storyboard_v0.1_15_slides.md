# Storyboard inicial — 15 diapositivas

Versión editorial 0.1 · corte 10/09/2026. Documento de planificación, sin slides producidas. IDs E remiten a `evidence_matrix.md`; notas desarrollan la exposición futura. Los ejemplos didácticos no son datos institucionales ni nuevas pruebas.

## 01 — El proyecto ya cuenta con dos incrementos de software cerrados

- **Mensaje central:** existe avance técnico verificable dentro de la fase formal Diseño.
- **Objetivo pedagógico:** situar al asesor desde el inicio y evitar interpretar el avance como MVP terminado.
- **Evidencia:** E01, E05, E06, E08, E19.
- **Visual recomendado:** apertura tipográfica con I1 e I2 destacados y una línea de estado al pie; no collage.
- **Contenido visible:** título; título formal del proyecto en bloque secundario; Juan José Cortés Fajardo · Bioingeniería · EMI Medellín. «Diseño · RF_REAL y piloto pendientes». Corte de información: 10/09/2026.
- **Notas del presentador:** recordar que la actualización parte de la propuesta aprobada. I1 resuelve asociación/persistencia e I2 entrada simulada. Son incrementos cerrados, no el MVP completo. M p.2 y Q pp.4–5. No usar la fecha del corte como fecha de reunión o aprobación.

## 02 — El compromiso aprobado es construir y validar un prototipo en un punto

- **Mensaje central:** el resultado continúa siendo una intervención evaluable y delimitada.
- **Objetivo pedagógico:** recuperar qué se aprobó y cuáles son sus límites.
- **Evidencia:** E01–E04.
- **Visual recomendado:** cadena editable «MVP → Pruebas → Piloto → Validación» dentro de un perímetro único rotulado; exclusiones en una línea inferior.
- **Contenido visible:** «RFID UHF pasivo · Equipos biomédicos · Un punto autorizado». «AM se conserva». «Fuera del alcance: despliegue institucional, RTLS/GPS, medicamentos e insumos». «Pruebas físicas/piloto condicionados a hardware y permisos».
- **Notas del presentador:** objetivo general y cuatro específicos en P pp.3–4. Recordar caracterización/requisitos como antecedentes de esa cadena. Línea base: 24 semanas, COP 43.724.342; no implica ejecución, gasto ni recursos asignados. Hardware condicionado y desembolso personal $0, P pp.7–9. No prometer conclusión favorable.

## 03 — El cierre de P0 permitió avanzar a Diseño y materializar I1 e I2

- **Mensaje central:** la evolución está gobernada por hitos de proyecto, no por la producción de informes.
- **Objetivo pedagógico:** explicar qué ocurrió desde la aprobación y por qué no se vuelve al diagnóstico general.
- **Evidencia:** E03, E05, E06, E08, E27.
- **Visual recomendado:** línea temporal con aprobación sin fecha supuesta, P0 (03/09), I1 (06/09), evidencia I2 (07/09) y consolidación (09/09); debajo, P1/P2 en curso.
- **Contenido visible:** «Requisitos suficientes → Diseño». «I1: asociación e historial». «I2: fuente común y simulación». «P1 y P2 continúan en paralelo».
- **Notas del presentador:** 06/09 es fecha del informe de cierre I1; 07/09 es ejecución documentada de I2, consolidada como cierre en control del 09/09. No convertir estas fechas en nuevas fases formales. DEC-023 admite evidencia tardía para decisiones de diseño, sin reabrir P0. M pp.2,5; R pp.2–4.

## 04 — Las rutas y los responsables todavía condicionan el punto de lectura

- **Mensaje central:** los hallazgos operativos afinan P2 y muestran qué falta corroborar.
- **Objetivo pedagógico:** enseñar el proceso real sin presentar hipótesis como hechos institucionales.
- **Evidencia:** E13–E15, E27.
- **Visual recomendado:** flujo conceptual editable «Recepción/reingreso → Preparación → Dotación/salida» con etiquetas de evidencia; fotografía del plano solo si pasa revisión de procedencia/legibilidad.
- **Contenido visible:** «Ventanilla/picking: corroborados según DEC-023». «Rutas por activo: pendientes». «Farmacia como operador: preliminar». «No todos los activos tienen paso confirmado».
- **Notas del presentador:** H pp.4–7 orienta el flujo; R p.3 delimita corroboración. Camillas, sillas camilla, raqui, corto espinal y férula espinal requieren confirmar ruta. Equipos instalados pueden no requerir desmontaje; no imponerlo. El diagrama no es un plano medido ni confirma antenas instaladas.

## 05 — El campo de ubicación de AM requiere aclarar su uso antes de integrar

- **Mensaje central:** reconocer una estructura de datos no autoriza escribir sobre ella.
- **Objetivo pedagógico:** explicar lo aprendido de AM y conservar su función institucional.
- **Evidencia:** E02, E12, E27.
- **Visual recomendado:** comparación simple: «Existe: Ubicación Física» y «Pendiente: significado y actualización»; frontera explícita con el MVP, sin flecha de integración activa.
- **Contenido visible:** «Catálogo y campo identificados». «Semántica dinámica pendiente». «Integración automática condicionada a autorización».
- **Notas del presentador:** H figs.1–3, pp.3–4; M §§6,6.3; R DEC-005/023. La coincidencia de campo en ficha y OT no demuestra actualización por movimientos. Si se usa una captura, anonimizar información innecesaria y preservar contexto; no generar una pantalla simulada como evidencia.

## 06 — Una lectura repetida no equivale a un nuevo movimiento

- **Mensaje central:** lectura, contexto y evento ocupan capas diferentes.
- **Objetivo pedagógico:** introducir EPC y explicar la necesidad de deduplicación operativa posterior.
- **Evidencia:** E08, E09, E11.
- **Visual recomendado:** tres observaciones del mismo EPC a la izquierda; bloque discontinuo «Sesión + regla + deduplicación (previsto)»; evento hipotético a la derecha.
- **Contenido visible:** «EPC: identificador de etiqueta». «La entrada conserva repeticiones». «El evento exige contexto». Rótulos «Ejemplo didáctico» y «Motor de eventos pendiente».
- **Notas del presentador:** ejemplo sin umbral temporal inventado: un activo puede generar varias lecturas durante una operación. I2 entrega observaciones; todavía no consolida movimientos. El resultado hipotético de un evento requiere diseño/pruebas posteriores. P p.4; M p.2; I2 §§6–9.

## 07 — I1 permite corregir la asociación sin borrar el historial

- **Mensaje central:** la identidad del activo y su etiqueta puede mantenerse trazable frente a correcciones.
- **Objetivo pedagógico:** mostrar un beneficio concreto de software y su evidencia de integridad.
- **Evidencia:** E06, E07.
- **Visual recomendado:** relación editable Equipo–AsignacionEtiqueta–EtiquetaRFID y dos intervalos de asociación, anterior cerrada y nueva vigente, compartiendo instante de cambio.
- **Contenido visible:** «Crear y asociar». «Consultar vigente, buscar por EPC e historial». «Corregir: cerrar anterior + crear nueva». «Una transacción; rollback probado». «SOFTWARE».
- **Notas del presentador:** siete flujos completos incluyen crear equipo y crear etiqueta por separado. Son tres entidades persistidas; la corrección no sobrescribe la historia. La prueba provoca fallo después de cerrar la asociación y verifica reversión completa. I1 pp.2–5. RF-001 implementado; este historial no reconstruye movimientos operativos.

## 08 — I2 permite probar la entrada de lecturas sin depender del lector

- **Mensaje central:** una interfaz propia desacopla el ingreso de datos del fabricante.
- **Objetivo pedagógico:** mostrar qué se implementó y por qué permite seguir desarrollando.
- **Evidencia:** E08–E09, E25.
- **Visual recomendado:** FuenteSimulada continua y «Adaptador real pendiente» discontinuo convergen en FuenteLecturasRFID → LecturaEntradaRFID; límite de implementación visible.
- **Contenido visible:** «EPC · timestamp · origen · metadata opcional». «FuenteSimulada emite SIMULACION». «Segunda fuente compatible pendiente».
- **Notas del presentador:** I2 §§4–9; M p.2. Metadata vacía en simulador, sin inventar RSSI/antena. Timestamp de aplicación, no instante RF físico. El puerto no importa tipos Chainway. TEC-002 permanece en desarrollo. Stack DEC-022 en notas; no presentar PptxGenJS como parte del MVP.

## 09 — Las 57 pruebas documentadas sustentan software y simulación

- **Mensaje central:** la evidencia es concreta y tiene un límite físico claro.
- **Objetivo pedagógico:** explicar qué prueban los resultados sin sobredimensionarlos.
- **Evidencia:** E10, E19.
- **Visual recomendado:** suma tipográfica editable «32 + 25 = 57» con etiquetas I1/I2; debajo resultado de fallos/errores/omitidas y frontera de evidencia.
- **Contenido visible:** «I1: persistencia, historial, rollback, concurrencia». «I2: contrato, orden, repeticiones, origen». «0 fallos · 0 errores · 0 omitidas». «Evidencia previa; desempeño RF pendiente».
- **Notas del presentador:** I2 §9 y Q p.4. Cotejo de XML existentes, sin ejecución nueva en la tarea editorial. No sumar 32+57, ni afirmar 57 pruebas nuevas. Pruebas automatizadas no cierran la fase formal de Pruebas, ni miden tasa física de lectura, cobertura de código o efectividad institucional.

## 10 — La persistencia de lecturas y la sesión son el siguiente diseño necesario

- **Mensaje central:** I1/I2 sostienen el avance, pero falta el procesamiento operativo del MVP.
- **Objetivo pedagógico:** delimitar implementado y pendiente y explicar el foco de I3.
- **Evidencia:** E06, E08, E11, E20, E25.
- **Visual recomendado:** ruta de incrementos en dos filas alineadas: I1/I2 continuos; etapas posteriores discontinuas. Destacar relación LecturaRFID–SesionOperacion como diseño previo.
- **Contenido visible:** «Diseñar LecturaRFID ↔ SesionOperacion antes de migrar». «Después: eventos → historial operativo → verificación → sustitución → contingencia». «Adaptador real cuando P1 lo habilite».
- **Notas del presentador:** M p.5 §9; R p.3; AGENTS secuencia. El vínculo de diseño no implementa sesión anticipadamente. No reabrir I1/I2 ni modificar V1/V2 por la presentación. DAT-003/RF-002/TEC-002 siguen en desarrollo; faltan persistencia de lecturas y procesamiento operativo.

## 11 — P1 sigue abierto hasta obtener un EPC UHF reproducible

- **Mensaje central:** la inspección del SDK reduce incertidumbre, pero no prueba el lector físico.
- **Objetivo pedagógico:** distinguir identificación de recurso, software inspeccionado e integración real.
- **Evidencia:** E17–E19.
- **Visual recomendado:** escalera de evidencia «SDK estático completado → prueba funcional pendiente → EPC RF_REAL reproducible pendiente»; U300 rotulado candidato.
- **Contenido visible:** «U300: candidato principal provisional». «Java 21 funcional, unidad y configuración: pendientes». «RC522: auxiliar HF; excluido como UHF». «P1 EN CURSO».
- **Notas del presentador:** M pp.3–4 y R DEC-019/021/022. R3S alternativa. SDK no acredita compatibilidad de firmware, parada física, autorización o región. No repetir inspección general; faltan prueba funcional aislada y evidencia de hardware/configuración. `AdaptadorU300` no implementado.

## 12 — P2 debe demostrar que la configuración corresponde a un único punto

- **Mensaje central:** dos antenas siguen siendo una hipótesis condicionada, no dos puntos autorizados.
- **Objetivo pedagógico:** explicar el trabajo físico y operativo sin congelar una arquitectura.
- **Evidencia:** E02, E14–E16, E22.
- **Visual recomendado:** esquema conceptual sin escala de banda/puerta bajo un contorno discontinuo «Único punto por demostrar»; no conos de cobertura. Ruta de análisis abreviada debajo.
- **Contenido visible:** «Límites, rutas y roles por confirmar». «Antena ≠ ingreso/salida». «Plano + fichas → modelo teórico → prepruebas RF_REAL». «Hipótesis P2; configuración pendiente».
- **Notas del presentador:** M §§6.2,9.1 y Q §7. Detallar coordenadas, dimensiones/alturas, materiales/metal, trayectorias/orientación, patrón/polarización, pérdidas de cable y enlace, huella/solapamientos/zonas ciegas, energía/red y montaje. Ninguna medida se extrae aquí de fotografías. Geometría ≠ modelo RF ≠ RF_REAL. No validar todo activo por estar en el catálogo.

## 13 — La contingencia permitirá confirmar operaciones sin inventar lecturas

- **Mensaje central:** el diseño contempla omisiones persistentes y activos fuera de ruta.
- **Objetivo pedagógico:** explicar la respuesta operativa prevista sin confundirla con software terminado.
- **Evidencia:** E11, E21.
- **Visual recomendado:** flujo editable principal «Lectura → Verificación → Reintento → Contingencia manual → Confirmación» y ruta separada «Fuera de ruta → Manual/Contingencia → Confirmación».
- **Contenido visible:** «Reintento controlado cuando corresponda». «Equipo, actor, fecha/hora, sesión y motivo». «Diseño pendiente de implementación · RF-006 Should/Aprobado».
- **Notas del presentador:** M p.3 y Q p.5. El diagrama destaca el camino de excepción; una verificación satisfactoria no obliga a contingencia. Omisión inicial no equivale a falla final. Selección por código institucional/equipo; origen manual explícito sin añadir ahora MANUAL a OrigenDatos. No exige lector manual ni tercer SDK.

## 14 — La validación requiere prepruebas y criterios definidos antes del piloto

- **Mensaje central:** el resultado físico debe construirse con un protocolo y verdad de terreno.
- **Objetivo pedagógico:** mostrar qué todavía debe demostrarse y cómo se evaluará.
- **Evidencia:** E03, E19, E22–E24, E26.
- **Visual recomendado:** secuencia «Prepruebas → P3 → Pruebas → Piloto → Validación», con prerrequisitos P1/P2/MVP explícitos; pie de criterios mínimos.
- **Contenido visible:** «≥10 repeticiones por escenario prioritario». «Verdad de terreno». «Cero eventos duplicados almacenados». «Metas RF y tiempo: tras prepruebas». «Resultados limitados a configuración ensayada».
- **Notas del presentador:** P pp.5–7; Q p.3. Muestra intencional por material/geometría/condición; categorías críticas cuando apliquen. Piloto orientado a ≥2 perfiles, ≥20 eventos y ≥3 sesiones; ajustes justificados antes de iniciar. Medir correctas/omitidas/externas, tiempo, correcciones/incidencias, facilidad de uso e interferencia operativa; valoración económica. Metas futuras no son resultados logrados. La fase formal de Pruebas sigue pendiente.

## 15 — El acompañamiento del asesor puede concretar diseño y condiciones de validación

- **Mensaje central:** la siguiente conversación debe ayudar a producir evidencia y resolver condiciones específicas.
- **Objetivo pedagógico:** terminar con peticiones concretas y diferenciadas de decisiones ya aprobadas.
- **Evidencia:** E15, E19–E20, E23–E26; las peticiones son propuestas editoriales, no hechos.
- **Visual recomendado:** tres líneas «Producto siguiente / Acompañamiento propuesto»: diseño I3; condiciones P1/P2; protocolo P3.
- **Contenido visible:** «Revisar LecturaRFID–SesionOperacion». «Concretar responsables y condiciones de P1/P2». «Revisar criterios tras prepruebas». Rótulo «Propuestas para la reunión».
- **Notas del presentador:** preguntar si el diseño dirigido cubre trazabilidad y evita rehacer migraciones; qué acompañamiento facilita confirmar hardware, único punto y actores; y cómo revisar el protocolo cuando existan prepruebas. No solicitar reaprobación general del proyecto ni prometer fechas. La orientación del asesor se registra con fuente/fecha antes de incorporarla al control.
