> Actualización de producción autorizada: 29 diapositivas (18 principales + 11 anexos), plano ampliado con recorte editable y definiciones de códigos. Véase [production_revision.md](production_revision.md) para la correspondencia con el storyboard aprobado. Las referencias anteriores a producción pendiente son antecedentes.

# Storyboard — evolución del Proyecto EMI

Versión editorial 0.3 · 15 diapositivas · corte 10/09/2026 · control v1.9.2. Revisión editorial completada; no implica aprobación del asesor ni autoriza generar PPTX. Antecedente preservado en `historico/storyboard_v0.1_15_slides.md`.

Pregunta conductora: ¿cómo se pasó de la propuesta aprobada a arquitectura e incrementos con evidencia de software, y qué falta para probarlos físicamente y ejecutar el piloto?

Audiencia: asesor interno con capacidad técnica, conocedor de la propuesta general pero no de los nombres internos. Una slide = una conclusión. Los esquemas siguientes son instrucciones de composición, no diapositivas generadas. El texto de «Contenido visible» y las etiquetas de esquemas forman un único contenido: no duplicarlos al producir. Notas completas en `speaker_notes.md`; assets y cajas en `visual_assets_plan.md`; fuentes E en `evidence_matrix.md`.

## Gramática común y primera aparición

Estados describen objetos: CERRADO para incrementos/gates, IMPLEMENTADO para capacidades concretas, EN DESARROLLO para requisitos parciales, EN CURSO para P1/P2, PENDIENTE para evidencia o trabajo faltante, CONDICIONADO para selección/configuración y piloto. APROBADA/congelada describe la propuesta. Ningún color sustituye al rótulo.

Madurez aparte: Confirmada, Corroborada, Preliminar, Pendiente. Confirmada documentalmente no significa desempeño físico confirmado. Conservar CALCULADA y PENDIENTE_RF_REAL cuando haya resultados de esas clases; aquí no se producen cálculos RF. SOFTWARE = persistencia/lógica; SIMULACIÓN = datos de entrada sintéticos; RF_REAL = evidencia obtenida de hardware RFID físico autorizado. En código, SIMULACION conserva su identificador sin tilde.

Primera aparición visible: RF_REAL en 01; producto mínimo viable (MVP), P0 e incrementos I1/I2 en 02; EPC y sesión en 06; SDK/P1 en 11; P2 en 12; P3 en 13. Explicación antes del código o junto a él, nunca solo en notas. «Gate» se expresa como condición de paso; no es un porcentaje de avance.

Línea continua = componente/relación implementada; discontinua + «previsto/pendiente» = diseño aún no materializado. Flechas de flujo y relaciones de implementación llevan etiquetas distintas. No usar AM como nodo conectado al MVP.

## 01 — El proyecto ya tiene avances de software verificables

**Mensaje central:** existen resultados técnicos, con validación física todavía pendiente.

**Objetivo pedagógico:** establecer el balance que organizará la conversación.

**Evidencia:** E01, E19; Propuesta p.2; Maestro v1.9.2 pp.1–2.

**Composición:** apertura tipográfica, alineación izquierda. Título conclusivo arriba; título formal del proyecto en el centro, en tres líneas de 22–24 pt; autor y afiliación abajo. Una única línea de estado, sin logotipos inventados ni fotografía decorativa.

**Contenido visible:**
- «Desarrollo y validación de un prototipo de trazabilidad basado en RFID pasivo para equipos biomédicos administrados por EMI Medellín».
- «Juan José Cortés Fajardo · Bioingeniería · Universidad de Antioquia».
- «Fase formal: Diseño · Validación física (RF_REAL) y piloto: PENDIENTES».
- «Corte de información: 10/09/2026».

**Notas del presentador:** presentar avance verificable sin anticipar validación ni fecha de aprobación. Guion, pregunta y transición: N01.

## 02 — Los requisitos habilitaron Diseño y dos incrementos ya están cerrados

**Mensaje central:** el avance técnico ocurre dentro de Diseño y no cierra el producto completo.

**Objetivo pedagógico:** explicar P0, I1 e I2 y reconstruir la evolución sin convertir informes en etapas.

**Evidencia:** E01, E03, E05, E06, E08, E19; Maestro p.2; Registro p.2.

**Composición:** línea temporal superior con cuatro hitos; banda inferior de fases. La zona «Diseño» contiene una llave hacia I1/I2 para mostrar que son incrementos técnicos, no cambios automáticos de fase.

```text
Propuesta            Requisitos suficientes       Asociación             Entrada simulada
APROBADA/congelada →  P0: CERRADO (03/09)     →     I1: CERRADO (06/09) →  I2: CERRADO técnicamente
                                                                         evidencia 07/09;
                                                                         cierre consolidado 09/09

Diagnóstico → Requisitos → [DISEÑO: fase formal actual] → MVP → Pruebas → Piloto → Validación
                                                   producto mínimo viable: aún incompleto
```

**Contenido visible:** etiquetas del esquema; «Incremento = bloque técnico acotado y verificable». Sin fecha supuesta para la aprobación.

**Notas del presentador:** distinguir fecha de informe/ejecución/consolidación y fase formal; mostrar por qué las pruebas automatizadas tempranas no representan la fase formal de Pruebas terminada. N02.

## 03 — El prototipo aborda el registro de movimientos en un único punto

**Mensaje central:** la brecha aprobada es la correspondencia entre movimiento físico y registro oportuno.

**Objetivo pedagógico:** recordar problema, intervención y límites de validez.

**Evidencia:** E01, E02, E04, E12, E28; Propuesta pp.3–4,7–9; Registro DEC-005/008.

**Composición:** arriba, dos elementos «Equipo se mueve» y «Registro disponible», unidos por una brecha rotulada «Correspondencia a verificar». Centro: perímetro del prototipo y sus resultados previstos. Pie: límites de alcance, sin diagrama de integración activa con AM.

**Contenido visible:**
- «Equipo físico ↔ Identificación ↔ Registro de movimiento».
- «RFID UHF pasivo · Equipos biomédicos · Un punto autorizado».
- «AM se conserva; integración automática NO autorizada».
- «Fuera del alcance: RTLS/GPS, medicamentos/insumos y despliegue institucional».

**Notas del presentador:** no cuantificar pérdidas, ahorros ni incumplimientos; AM sí tiene campo de ubicación y se desconoce su semántica dinámica. Presupuesto y cronograma aprobados permanecen como línea base, no ejecución. N03.

## 04 — La caracterización orientó el diseño; aún faltan rutas y roles por confirmar

**Mensaje central:** se comprende mejor el flujo, con grados de evidencia diferentes.

**Objetivo pedagógico:** explicar qué se aprendió sin generalizar los relatos ni reabrir diagnóstico.

**Evidencia:** E12, E13, E14, E15, E27, E28; Propuesta p.3; Maestro §§6.1–6.3; Registro DEC-023; H pp.2–7.

**Composición:** reservar el tercio izquierdo para la imagen de mejor calidad y explicación que aportará el usuario (asset P-01, PENDIENTE DE RECEPCIÓN). La fotografía H p.5, figura 4, permanece como antecedente documental; no producir ni mejorar imágenes ahora. A la derecha, flujo conceptual recepción → preparación → dotación y hallazgos con madurez escrita. Posiciones y llamadas se ajustarán cuando llegue el material; el esquema no reconstruye geometría. Distinguir «Caracterización inicial» de «Hallazgos posteriores que refinan Diseño» mediante un subtítulo, sin atribuirles idéntica fecha.

**Contenido visible:**
- «Recepción/reingreso → Preparación → Dotación/salida: flujo en estudio».
- «Confirmada: existencia de “Ubicación Física” en AM».
- «Corroborada según control: ventanilla/picking en el plano».
- «Preliminar: rutas por activo y Farmacia como operador».
- «Hallazgos posteriores afinan Diseño; P0 permanece cerrado».

**Notas del presentador:** no afirmar infraestructura RFID instalada. Semántica AM, roles y pasos efectivos por equipo siguen pendientes. Usar raqui/férula espinal y mantener reportes de remoción como preliminares. N04.

## 05 — Los requisitos convierten necesidades del proceso en comportamientos verificables

**Mensaje central:** cada pieza del sistema tiene una necesidad y una condición observable.

**Objetivo pedagógico:** hacer visible la trazabilidad hallazgo/necesidad → requisito → diseño → evidencia o pendiente.

**Evidencia:** E05, E06, E11, E21, E27, E28; Propuesta pp.3–6; Matriz v1.9.2 pp.2,5; Registro DEC-014/015/016/023.

**Composición:** tres carriles horizontales con flechas y cuatro estaciones. Fila 1 principal; filas 2–3 más compactas. Códigos junto a significado, no lista de IDs sin explicación. Son correspondencias trazables, no afirmación de que todos los hallazgos recientes originaron requisitos nuevos.

```text
Vincular activo-etiqueta → RF-001: asociar → Relación histórica → IMPLEMENTADO
Interpretar movimiento  → RF-003: contexto → Sesión y evento   → APROBADO; implementación pendiente
Gestionar excepción    → RF-006: manual   → Contingencia      → Should/APROBADO; pendiente
```

**Contenido visible:** etiquetas de carriles; «Requisitos existentes; los hallazgos posteriores refinan condiciones». «APROBADO ≠ IMPLEMENTADO».

**Notas del presentador:** una necesidad de diseño no demuestra frecuencia institucional del problema. RF-006 conserva Should; no elevarla a Must por el diagrama. Requisitos parciales RF-002/TEC-002/DAT se explican después. N05.

## 06 — La arquitectura separa identidad, entrada de lecturas y operación

**Mensaje central:** hay módulos materializados y una ruta lógica diseñada que aún debe completarse.

**Objetivo pedagógico:** proporcionar un mapa del MVP antes de entrar a cada incremento.

**Evidencia:** E06, E08, E11, E20, E21, E25; Maestro §4 y §9; Matriz pp.2,4; Registro DEC-014/015/017/022.

**Composición:** dos zonas existentes a la izquierda: I1 (identidad/persistencia) e I2 (entrada). A la derecha, una gran zona discontinua «Procesamiento operativo previsto», con tres capas. Conexiones entre incrementos y esta zona discontinuas: no existe pipeline integral demostrado. AM queda fuera, como texto de contexto de la slide 03.

```text
I1 IMPLEMENTADO                         PROCESAMIENTO OPERATIVO PENDIENTE
Equipo ↔ Asociación ↔ Etiqueta ······>   Consulta EPC-equipo y contexto
SQLite/JDBC                             ↑
                                        |
I2 IMPLEMENTADO                         |
Fuente común → LecturaEntradaRFID ···> LecturaRFID persistida + SesionOperacion
                                        ···> Eventos / historial operativo
                                        ···> Verificación / sustitución / contingencia
```

**Contenido visible:** etiquetas del esquema; «EPC = identificador de etiqueta». «Sesión = contexto de una operación». «Discontinuo: conexión o función prevista». Fuente común e identidad son módulos disponibles; la continuidad completa es pendiente.

**Notas del presentador:** no inferir que la fuente ya escribe en SQLite. El esquema organiza responsabilidades, no ordena todos los subprocesos; la secuencia de construcción se conserva en N06/N14. Verificación, sustitución y contingencia son conceptos separados, no clases de lectura. N06.

## 07 — El stack elegido permite desarrollar y probar el MVP local

**Mensaje central:** cada herramienta del stack aprobado tiene una función concreta en la implementación.

**Objetivo pedagógico:** explicar la elección tecnológica sin confundir herramientas de desarrollo con hardware o arquitectura institucional.

**Evidencia:** E25; Registro v1.9.2 p.2 DEC-022; `pom.xml`; I1 v0.2 pp.3–4. La motivación se describe como adecuación a lo implementado, no como evaluación comparativa de todos los stacks.

**Composición:** esquema de ejecución en el centro «Java 21 LTS → JDBC → SQLite». En la franja superior, Maven enmarca compilar/empaquetar; debajo de la base, Flyway se conecta mediante «versiona esquema»; JUnit 5 ocupa un carril de verificación que apunta a lógica y persistencia. Nombres de herramientas con verbos breves, sin seis logos decorativos ni seis tarjetas iguales.

**Contenido visible:**
- «Java 21 LTS: lógica y casos de uso».
- «JDBC: acceso SQL → SQLite: persistencia local».
- «Maven: compilar y empaquetar».
- «Flyway: versionar migraciones».
- «JUnit 5: pruebas automatizadas».
- «DEC-022: stack elegido y materializado; hardware RF pendiente».

**Notas del presentador:** explicar flujo ejecución/desarrollo, módulo Maven único, ausencia de SDK en dominio y aplicación, y límites de SQLite local. No promover Java17/Spring del checklist histórico ni presentar PptxGenJS como stack del MVP. N07.

## 08 — Varias lecturas del mismo EPC no prueban varios movimientos

**Mensaje central:** la entrada conserva observaciones; la interpretación operativa exige contexto y reglas.

**Objetivo pedagógico:** distinguir evidencia cruda de evento, verificación y excepción.

**Evidencia:** E09, E11, E21; Propuesta p.4; Maestro §§4–4.1; I2 §§6–9.

**Composición:** tres observaciones repetidas a la izquierda; al centro bloque discontinuo de interpretación; a la derecha un evento hipotético. Debajo, dos ramas pequeñas: verificación y contingencia, sin presentarlas como eventos RFID. No inventar ventana temporal ni algoritmo de deduplicación.

```text
EPC A    EPC A    EPC A  →  Entrada conserva las tres observaciones
                          ···> Sesión + regla + deduplicación [PENDIENTE]
                          ···> Posible evento [ejemplo, no resultado implementado]

Verificación: esperados vs. detectados     Contingencia: excepción manual explícita
```

**Contenido visible:** etiquetas; «Ejemplo didáctico». «Lectura ≠ evento ≠ verificación ≠ sustitución temporal ≠ contingencia». Sustitución: préstamo/reemplazo temporal con cierre, explicado oralmente.

**Notas del presentador:** repetir EPC en entrada es correcto; almacenar eventos duplicados no lo sería. Activos fuera de ruta pueden seguir vía manual sin lectura artificial. N08.

## 09 — I1 corrige asociaciones y conserva el historial incluso ante fallos

**Mensaje central:** el primer incremento aporta integridad comprobada a la identidad equipo-etiqueta.

**Objetivo pedagógico:** conectar una operación de usuario con un mecanismo y una prueba significativa.

**Evidencia:** E06, E07, E10; I1 v0.2 pp.2–5; Matriz p.2 RF-001.

**Composición:** dos intervalos editables para un equipo didáctico, separados por un instante t. Abajo, transacción «Cerrar anterior + crear nueva» con ruta de rollback a la asociación original. En el lateral derecho, cifra 32 con ámbito SOFTWARE. No colocar los siete comandos como captura de consola.

**Contenido visible:**
- «Asociación anterior: cerrada en t → Nueva asociación: vigente desde t».
- «Una transacción · Un instante UTC · Rollback probado».
- «Crear, asociar, consultar y corregir con historial».
- «I1 CERRADO · RF-001 IMPLEMENTADO».
- «32 pruebas automatizadas · SOFTWARE · Evidencia previa».

**Notas del presentador:** siete flujos concretos en notas; caso de fallo después del cierre, concurrencia y búsqueda vigente. «Historial» aquí es de asociaciones, no de movimientos. N09.

## 10 — I2 genera lecturas simuladas mediante un contrato independiente del fabricante

**Mensaje central:** el ingreso de datos puede probarse sin depender del lector físico.

**Objetivo pedagógico:** mostrar la interfaz común, su implementación actual y la futura sustitución de fuente.

**Evidencia:** E08, E09, E10, E25; Maestro §4; Matriz pp.2,4; I2 §§4–9.

**Composición:** interfaz común centrada arriba; debajo dos implementaciones, una continua y otra discontinua. Usar flechas huecas hacia la interfaz rotuladas «implementa/implementará», y flechas independientes para entrega de observaciones. No usar las mismas flechas para herencia y flujo.

```text
                       FuenteLecturasRFID [IMPLEMENTADA]
                         ↑ implementa        ↑ implementará
          FuenteSimulada [IMPLEMENTADA]    Fuente UHF real [PENDIENTE]
                         | entrega             : entrega futura
                         └──> LecturaEntradaRFID <···┘
                              EPC · timestamp · origen · metadata opcional
                              ···> Persistencia y lógica operativa [PENDIENTES]
```

**Contenido visible:** etiquetas; «I2 CERRADO técnicamente». «SIMULACIÓN: conserva orden y repeticiones». «TEC-002 EN DESARROLLO: segunda fuente compatible pendiente». Las flechas desde la fuente real permanecen discontinuas.

**Notas del presentador:** 25 pruebas nuevas; total posterior 57. Timestamp de aplicación, metadata vacía en simulador, no RSSI físico. Interfaz disponible no significa lector intercambiable ya probado. N10.

## 11 — El SDK inspeccionado aún debe probarse con hardware UHF autorizado

**Mensaje central:** la compatibilidad estática conocida no cierra la integración física.

**Objetivo pedagógico:** explicar la condición de paso P1 y por qué RC522 no resuelve la necesidad UHF.

**Evidencia:** E17, E18, E19; Maestro §§5,7; Registro DEC-019/021/022.

**Composición:** recorrido horizontal de tres niveles: SDK estático, compatibilidad funcional/configuración, EPC reproducible. U300 como candidato en un rótulo secundario; RC522 en recuadro de exclusión pequeño. No usar fotos de catálogo como prueba de disponibilidad.

**Contenido visible:**
- «P1 = hardware e integración UHF · EN CURSO».
- «SDK: librerías del fabricante · inspección estática completada».
- «Java 21 funcional + unidad/firmware + configuración: PENDIENTES».
- «EPC UHF RF_REAL reproducible: PENDIENTE».
- «U300: candidato provisional; selección final CONDICIONADA».
- «RC522: HF; incompatible con etiquetas UHF del proyecto».

**Notas del presentador:** R3S alternativa; no volver a inspección general; confirmar dependencias, licencia, región, antenas/cables y autorización. Parada lógica no demuestra parada física. N11.

## 12 — El punto de control debe delimitarse antes de congelar la configuración

**Mensaje central:** la arquitectura física depende de confirmar operación, geometría y hardware.

**Objetivo pedagógico:** explicar P2 y la hipótesis de dos antenas sin ampliar el piloto.

**Evidencia:** E02, E13, E14, E15, E16, E22; Maestro §§6.1–6.3,9.1; Matriz §7.

**Composición:** reservar mitad izquierda para el detalle de mejor calidad que enviará el usuario con su explicación (asset P-02, PENDIENTE DE RECEPCIÓN). H p.7, figura 6, es antecedente: posibles llamadas a ventanilla, picking y zona RFID, todavía sin posición final ni transcripción fijada. A la derecha, reservar esquema conceptual separado de banda/puerta bajo contorno discontinuo «Único punto operativo por demostrar», más dependencias rutas/roles → geometría → configuración → prepruebas. Ajustar interpretación a lo recibido y su madurez; no generar imagen ahora ni deducir medidas, antenas o cobertura.

**Contenido visible:**
- «P2 = punto y arquitectura física/operativa · EN CURSO».
- «Banda + puerta: hipótesis de dos antenas, CONDICIONADA a un único punto autorizado».
- «Rutas, actores, límites y solapamientos: PENDIENTES».
- «Antena ≠ ingreso/salida».
- Pie previsto de procedencia del plano/imagen, a completar al recibir el material; «Esquema interpretativo, sin escala» si aplica. Esta indicación es editorial y no se proyecta como texto pendiente.
- «Geometría ≠ modelo RF ≠ RF_REAL».

**Notas del presentador:** mismo lector no prueba único punto. P2 debe demostrar misma operación/control, límites y solapamientos. Fotografías del plano solo como contexto. Explicar análisis especializado pendiente y roles no confirmados. N12.

## 13 — La evidencia demuestra software; el desempeño físico sigue pendiente

**Mensaje central:** hay resultados verificables y una frontera explícita de lo que aún no se puede concluir.

**Objetivo pedagógico:** responder de forma directa «¿qué puedo afirmar hoy?».

**Evidencia:** E06, E07, E08, E09, E10, E17, E19, E22, E23; Maestro pp.2–5; Matriz pp.3–5; I2 §9.

**Composición crítica:** comparación 38%/62%, título común y separador vertical; fondo sobrio. Izquierda con dos pruebas visuales (intervalo de asociación y secuencia de lecturas) y cifra 57. Derecha con tres grupos tipográficos, sin diez tarjetas ni párrafos. Conservar todos los pendientes visibles, en etiquetas ≥18 pt. Fuente de números en pie. No usar una barra de avance.

**Contenido visible — YA DEMOSTRADO:**
- «SOFTWARE: asociación, historial, rollback».
- «SIMULACIÓN: contrato, orden, repeticiones y origen».
- «32 + 25 = 57 pruebas documentadas».
- «0 fallos · 0 errores · 0 omitidas».

**Contenido visible — TODAVÍA PENDIENTE:**
- «Captura: EPC UHF RF_REAL reproducible».
- «Desempeño físico: omisiones, lecturas externas, interferencia y superficies/materiales relevantes».
- «Preparación: arquitectura física congelada; P3 = criterios de aceptación».
- «Ejecución: pruebas controladas físicas y piloto».

**Límite visible al pie:** «Pruebas automatizadas ≠ fase formal de Pruebas cerrada. La simulación no acredita desempeño RF».

**Notas del presentador:** los grupos no esconden pendientes en las notas. Lecturas omitidas/externas son fenómenos físicos por medir, no pruebas Java fallidas. No mezclar el análisis SDK con evidencia simulada ni declarar cero eventos duplicados logrado. N13.

## 14 — El piloto requiere completar el MVP y pasar por pruebas controladas

**Mensaje central:** el siguiente avance exige productos concretos y condiciones de paso, sin saltos.

**Objetivo pedagógico:** conectar el trabajo inmediato con la estrategia aprobada de validación.

**Evidencia:** E03, E19, E20, E22, E23, E24, E26; Propuesta pp.5–9; Maestro §9; Matriz §§2,5.

**Composición:** dos carriles que convergen antes de las pruebas formales: software y condiciones físicas. En el carril físico, prepruebas llevan a P3; ambos carriles desembocan en Pruebas → Piloto → Validación. No dibujar MVP → Piloto sin nodo intermedio. Los estados pendientes y condiciones deben acompañar los nodos.

```text
Software: diseño LecturaRFID–SesionOperacion → completar MVP ─────────────┐
Físico: P1/P2 → prepruebas RF_REAL → P3: criterios de aceptación ─────────┤
                                                                      ↓
                        Pruebas controladas → Piloto único → Validación
                            PENDIENTES        CONDICIONADO    PENDIENTE
```

**Contenido visible:** etiquetas; «≥10 repeticiones por escenario prioritario + verdad de terreno». «Cero eventos duplicados almacenados: criterio». «Metas RF/tiempo: tras prepruebas, antes del piloto». «Piloto PENDIENTE; sujeto a hardware y permisos».

**Notas del presentador:** P1/P2 se resuelven coordinadamente, no como fases que bloquean todo software. Prepruebas no sustituyen pruebas controladas; no crear criterio nuevo de gate. Conservar orientación ≥2 perfiles/≥20 eventos/≥3 sesiones y posibles ajustes justificados antes del piloto. N14.

## 15 — El siguiente paso es completar el diseño y concretar condiciones de validación

**Mensaje central:** el acompañamiento solicitado se enfoca en producir evidencia y resolver decisiones delimitadas.

**Objetivo pedagógico:** terminar con peticiones específicas, sin pedir una nueva aprobación general ni prometer viabilidad.

**Evidencia:** E15, E19, E20, E26; Maestro §9; Propuesta pp.6,9. Las peticiones son propuestas para la reunión, no decisiones ya tomadas.

**Composición:** tres filas «Producto siguiente / Acompañamiento propuesto», con verbos claros; cierre tipográfico en la zona inferior. No resumir todo el deck en una tabla de estados.

**Contenido visible:**
- «Diseño del siguiente incremento (I3): revisar LecturaRFID–SesionOperacion».
- «P1/P2: concretar responsables, hardware y único punto».
- «Validación: revisar protocolo y criterios tras prepruebas».
- «Propuestas para la reunión; sin nuevas autorizaciones implícitas».
- «Hay avance de software verificable; la validez física y operativa aún debe demostrarse».

**Notas del presentador:** preguntas accionables y respuestas ceñidas a evidencia; no atribuir al asesor universitario permisos operativos de EMI ni fijar fechas. N15.

## Revisión crítica final como profesor evaluador

Autoevaluación editorial bajo la perspectiva solicitada; no evaluación independiente ni aprobación académica.

1. **¿Demuestra avance real? Sí.** 09–10 muestran operación, mecanismo y evidencia; 13 presenta 32 + 25 = 57. La slide 07 explica el stack materializado. No se usa volumen documental como progreso.
2. **¿Hay salto metodológico? No en el relato.** 02 separa fase formal e incrementos; 14 conserva prepruebas/P3 y Pruebas antes de Piloto. Pruebas Java tempranas no cierran la fase formal.
3. **¿Hay afirmaciones mayores que la evidencia? Se corrigieron los riesgos.** 06 marca conexiones futuras; 10 mantiene segunda fuente pendiente; 04/12 separan plano, interpretación y madurez. 13 enumera toda la evidencia física faltante. La discrepancia «Túnel»/«torre» observada en el antecedente queda para aclarar con el nuevo material, sin afirmar equipo instalado.
4. **¿Se amplía alcance? No.** 03 conserva AM y exclusiones; 12 condiciona dos antenas a un punto. Los medicamentos visibles en el plano no entran al MVP. No se crean decisiones ni prioridades.
5. **¿Parece un informe pegado? No por su composición planificada.** Se usan diagramas de relaciones, corrección, contrato y stack; se prevé lectura guiada de los planos cuando se reciban. La comparación 13 agrupa pendientes en dos columnas. El futuro render debe verificar legibilidad, densidad y overflow: no se presume validación visual del PPTX.
6. **¿Qué debe recordar el asesor?** El proyecto mantiene el compromiso aprobado y ya tiene arquitectura lógica, stack e incrementos de software comprobados; falta completar procesamiento operativo y producir evidencia física/operativa antes de culminar Pruebas, Piloto y Validación.

Correcciones aplicadas: glosario visible; trazabilidad en 05; conexiones futuras identificadas en 06; stack dedicado en 07 por ampliación editorial solicitada; espacios reservados para planos mejorados y explicación del usuario en 04/12; comparación completa en 13; notas con pregunta/respuesta/transición para cada slide. Se conservan 15 slides totales, máximo autorizado. Storyboard listo para revisión del usuario, sin generar PPTX.


Revisión posterior autorizada: nueva diapositiva 18 de red de precedencias del proyecto. Cierre 19 y anexos 20–30. Correspondencia y alcance en production_revision.md.
