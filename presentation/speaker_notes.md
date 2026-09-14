> Actualización de producción autorizada: 29 diapositivas (18 principales + 11 anexos), plano ampliado con recorte editable y definiciones de códigos. Véase [production_revision.md](production_revision.md) para la correspondencia con el storyboard aprobado. Las referencias anteriores a producción pendiente son antecedentes.

# Notas del presentador

Versión editorial 0.3 · 15 slides · corte 10/09/2026. Cada N corresponde al mismo número en `storyboard.md`. Fuentes P/M/Q/R/H/I1/I2/S definidas con rutas completas en `evidence_matrix.md`; páginas físicas desde 1. Las preguntas son posibles intervenciones del asesor, no conversaciones ocurridas. No se exportan notas a PPTX todavía.

## N01 — El proyecto ya tiene avances de software verificables

- **Qué explicar:** «La propuesta sigue siendo nuestra línea base. Desde entonces se consolidó el diseño lógico y se implementaron bloques de software que puedo respaldar con pruebas. Mostraré qué está construido y qué falta demostrar físicamente». Definir RF_REAL como evidencia de lectura con hardware RFID físico autorizado.
- **Qué no sobreafirmar:** no presentar MVP, validación ni piloto terminados. No atribuir una fecha a la aprobación.
- **Transición:** «Primero ubico esos avances en la secuencia del proyecto».
- **Posible pregunta:** «¿Este es un resultado final?».
- **Respuesta sugerida:** «Es seguimiento de avance. El control vigente mantiene Diseño; pruebas formales, RF_REAL y piloto están pendientes».
- **Fuente:** E01/E19; P p.2; M pp.1–2.

## N02 — Los requisitos habilitaron Diseño y dos incrementos ya están cerrados

- **Qué explicar:** P0 es la condición de suficiencia de requisitos para pasar a Diseño; cerró el 03/09. I1 significa el primer bloque técnico, asociación y persistencia; I2, el segundo, entrada común y simulación. El informe de cierre I1 es del 06/09; la ejecución I2 se documentó el 07/09 y su cierre se consolidó en el control del 09/09. El MVP es el producto mínimo viable completo, todavía incompleto.
- **Qué no sobreafirmar:** no equiparar incremento cerrado con cambio de fase formal. No llamar fecha de aprobación a fecha de entrega o firma no verificada.
- **Transición:** «Estos incrementos responden al mismo problema y al mismo perímetro que se aprobaron».
- **Posible pregunta:** «¿Por qué hay código si la fase formal sigue siendo Diseño?».
- **Respuesta sugerida:** «El Maestro registra simultáneamente Diseño e I1/I2 cerrados. Son materializaciones técnicas acotadas; no declaran terminadas las demás funciones ni las condiciones de pruebas y piloto».
- **Fuente:** E01/E03/E05/E06/E08/E19; M p.2; R p.2 DEC-013/018; I1 p.1; I2 §9.

## N03 — El prototipo aborda el registro de movimientos en un único punto

- **Qué explicar:** la oportunidad está en vincular el movimiento físico con un registro oportuno y consultable. AM conserva inventario e historial técnico. El prototipo se limita a equipos biomédicos, RFID UHF pasivo y un único punto autorizado.
- **Qué no sobreafirmar:** no afirmar pérdida de equipos cuantificada, ahorro, incumplimiento normativo o ausencia de funciones en AM. No integrar AM ni incorporar RTLS/GPS, medicamentos, insumos o despliegue general.
- **Transición:** «La caracterización ayudó a entender qué movimientos y condiciones debería atender ese punto».
- **Posible pregunta:** «¿Cambió el alcance o el presupuesto después de aprobar?».
- **Respuesta sugerida:** «El control v1.9.2 preserva la línea base: 24 semanas y COP 43.724.342, con recursos tecnológicos condicionados y desembolso personal del estudiante $0. Es planificación, no evidencia de ejecución ni disponibilidad. No se ha autorizado integración automática AM».
- **Fuente:** E01/E02/E04/E12/E28; P pp.3–4,7–9; M pp.2–4; R DEC-005/008/010.

## N04 — La caracterización orientó el diseño; aún faltan rutas y roles por confirmar

- **Qué explicar:** separar lo aprendido inicialmente de los hallazgos posteriores de AM/remodelación. El campo Ubicación Física existe; el control corrobora ventanilla/picking. La secuencia recepción–preparación–dotación orienta el diseño, pero no confirma cada ruta ni quién inicia/confirma una sesión. El usuario aportará una imagen mejor y su explicación; la lectura espacial se ajustará a ese material antes de producir la slide.
- **Qué no sobreafirmar:** una mejor imagen no confirma por sí sola instalación RFID ni responsables. Farmacia como operador es preliminar. Camillas, sillas camilla, raqui, corto espinal y férula espinal conservan rutas fuera del punto o por confirmar. Los equipos instalados no se obligan a desmontarse por inferencia.
- **Transición:** «Estos hallazgos se traducen en necesidades concretas y refinan requisitos que ya estaban aprobados».
- **Posible pregunta:** «¿La nueva información obliga a repetir el diagnóstico?».
- **Respuesta sugerida:** «DEC-023 permite usarla para resolver decisiones de Diseño/P2 sin reabrir P0. La corroboración adicional debe ser dirigida a los bloqueos, como rutas, roles y geometría».
- **Fuente:** E12/E13/E14/E15/E27/E28; P p.3; M §§6.1–6.3; R p.3 DEC-023; H pp.2–7. Nuevo asset/explicación: aún no recibidos.

## N05 — Los requisitos convierten necesidades del proceso en comportamientos verificables

- **Qué explicar:** recorrer una fila completa: vincular activo-etiqueta → RF-001 → asociación histórica → prueba de corrección y consulta. Comparar con RF-003, contexto de evento pendiente, y RF-006, contingencia manual pendiente. Las filas reconstruyen trazabilidad conceptual, no prueban una cronología causal para todos los hallazgos.
- **Qué no sobreafirmar:** «Aprobado» no equivale a «implementado». RF-006 conserva Should; su necesidad de diseño no altera prioridad. Los hallazgos tardíos no crean requisitos ni una DEC nueva.
- **Transición:** «La arquitectura asigna esas responsabilidades a componentes separados».
- **Posible pregunta:** «¿Qué requisito existente respalda esta funcionalidad?».
- **Respuesta sugerida:** «RF-001 respalda la asociación; RF-003, fecha/hora/punto/usuario-contexto/tipo de evento; RF-006, contingencia manual. La Matriz conserva sus estados, y solo RF-001 está implementado entre estos tres ejemplos».
- **Fuente:** E05/E06/E11/E21/E27/E28; Q p.2 y p.5; R DEC-014/015/016/023; P pp.3–6.

## N06 — La arquitectura separa identidad, entrada de lecturas y operación

- **Qué explicar:** I1 conserva identidad/asociaciones; I2 entrega LecturaEntradaRFID. EPC identifica la etiqueta. Una sesión recoge el contexto de una operación. La persistencia de lecturas y la lógica operativa siguen pendientes; las líneas discontinuas expresan conexiones aún no implementadas.
- **Qué no sobreafirmar:** no afirmar que FuenteSimulada persiste lecturas en SQLite o genera movimientos. LecturaEntradaRFID no es LecturaRFID persistida. El mapa de responsabilidades no es un flujo completo de producción en ejecución.
- **Transición:** «Para materializar esta separación se eligió un stack local concreto».
- **Posible pregunta:** «¿Qué queda por construir si ya hay persistencia?».
- **Respuesta sugerida:** «La persistencia actual cubre Equipo, EtiquetaRFID y AsignacionEtiqueta. Antes del siguiente incremento deben diseñarse LecturaRFID y su relación con SesionOperacion; después siguen eventos/deduplicación, historial operativo, verificación, sustitución y contingencia».
- **Fuente:** E06/E08/E11/E20/E21/E25; M §§4,9; Q pp.2,4; R DEC-015/017/022; I2 §§3–4,14.

## N07 — El stack elegido permite desarrollar y probar el MVP local

- **Qué explicar:** Java 21 LTS implementa dominio y casos de uso. JDBC ejecuta SQL sobre SQLite local. Maven compila, ejecuta la construcción y empaqueta. Flyway versiona las migraciones; JUnit 5 prueba comportamientos. La evidencia I1/I2 muestra este stack en uso; no es una lista aspiracional.
- **Qué no sobreafirmar:** no describirlo como el único stack viable ni como arquitectura institucional definitiva. No añadir Spring, ORM, nube o base servidor. PptxGenJS produce la presentación, no forma parte del prototipo. SQLite local no demuestra idoneidad física ni ratifica por sí solo despliegue piloto.
- **Transición:** «Sobre esta base técnica, la regla de diseño más importante es separar lecturas y movimientos».
- **Posible pregunta:** «¿La elección ya estaba aprobada? ¿Por qué no Spring o una base servidor?».
- **Respuesta sugerida:** «DEC-022 fija Java 21/Maven/JDBC/SQLite/Flyway/JUnit 5. La implementación actual es un módulo local con SQL explícito y migraciones. No hay una necesidad trazable aprobada que exija añadir esas capas; su incorporación no puede deducirse de recomendaciones históricas».
- **Fuente:** E25; R p.2 DEC-022; `pom.xml`; I1 pp.3–4; adenda DEC-022 histórica solo como detalle subordinado de la decisión.

## N08 — Varias lecturas del mismo EPC no prueban varios movimientos

- **Qué explicar:** un mismo EPC puede entregarse varias veces; I2 conserva cada observación. La regla de evento, contexto y deduplicación se aplicarán después. Verificación compara esperados/detectados; sustitución registra préstamo/reemplazo temporal y cierre; contingencia conserva excepción manual.
- **Qué no sobreafirmar:** tres lecturas no producen hoy un evento implementado. No inventar ventana temporal ni sugerir que toda omisión inicial es falla definitiva. Una antena no determina ingreso/salida.
- **Transición:** «El primer bloque ya implementado resuelve la identidad que permitirá interpretar esas lecturas».
- **Posible pregunta:** «¿Por qué conservar duplicados si se exigen cero eventos duplicados?».
- **Respuesta sugerida:** «Son objetos distintos. Las lecturas repetidas son evidencia cruda; el criterio exige cero eventos duplicados almacenados. El motor que consolidará eventos sigue pendiente. Fuera de ruta habrá gestión manual explícita sin inventar lectura RFID».
- **Fuente:** E09/E11/E21; P p.4 y p.7; M §§4–4.1; Q VAL-003/RF-006; I2 §§6–9.

## N09 — I1 corrige asociaciones y conserva el historial incluso ante fallos

- **Qué explicar:** crear equipo, crear etiqueta, asociar, consultar vigente, buscar por EPC, corregir y consultar historial son los siete flujos. Corregir cierra la relación anterior e inserta otra en la misma transacción y con un único instante UTC. La prueba de rollback provoca un fallo real de inserción después del cierre y comprueba recuperación íntegra.
- **Qué no sobreafirmar:** no presentar historial de movimientos. Las pruebas de concurrencia de asociación no demuestran caudal de lector real ni transacciones de eventos futuros. No convertir INACTIVA en prohibición no aprobada.
- **Transición:** «Una vez resuelta la asociación, el segundo incremento permite entregar lecturas de prueba».
- **Posible pregunta:** «¿La corrección puede dejar al equipo sin asociación si falla a mitad?».
- **Respuesta sugerida:** «El comportamiento documentado se protege con transacción y rollback completo; la prueba falla después del cierre y verifica que se restaura el estado anterior. Esa evidencia forma parte de las 32 pruebas I1».
- **Fuente:** E06/E07/E10; I1 pp.2–5; Q RF-001; `docs/desarrollo/casos_de_uso_i1.md`, evidencia técnica subordinada, no sus estados antiguos.

## N10 — I2 genera lecturas simuladas mediante un contrato independiente del fabricante

- **Qué explicar:** FuenteLecturasRFID es el contrato; FuenteSimulada lo implementa y entrega LecturaEntradaRFID con EPC, tiempo de aplicación, origen y metadata opcional. La futura fuente UHF deberá cumplir el mismo contrato. I2 conserva orden y repeticiones y no importa tipos del proveedor.
- **Qué no sobreafirmar:** no se ha demostrado procesamiento desde una segunda fuente. No hay persistencia de LecturaRFID ni integración U300. La metadata del simulador es vacía y no hay RSSI físico; timestamp de aplicación no equivale a instante físico de captura.
- **Transición:** «La fuente real exige resolver la compatibilidad que sigue abierta».
- **Posible pregunta:** «Si I2 está cerrado, ¿por qué TEC-002 sigue en desarrollo?».
- **Respuesta sugerida:** «I2 es un incremento acotado: contrato y simulador. TEC-002 requiere evidencia adicional de una segunda fuente compatible. Las 25 pruebas nuevas completan una suite de 57, pero no prueban el lector».
- **Fuente:** E08/E09/E10/E25; M §4; Q pp.2,4; I2 §§4–9,10–11.

## N11 — El SDK inspeccionado aún debe probarse con hardware UHF autorizado

- **Qué explicar:** P1 reúne hardware e integración UHF. SDK son las librerías del fabricante. La inspección A4 identificó rutas de conexión/inventario y límites, pero faltan prueba funcional Java 21, unidad/firmware/configuración y EPC RF_REAL reproducible. U300 es candidato provisional; R3S alternativa.
- **Qué no sobreafirmar:** disponibilidad por catálogo, compatibilidad funcional por bytecode, parada RF por stop/free o autorización por tener SDK. RC522 es HF y no permite leer las etiquetas UHF del proyecto.
- **Transición:** «Además de conseguir y probar el lector, hay que definir dónde y bajo qué operación leerá».
- **Posible pregunta:** «¿Puede hacerse la validación con el RC522 mientras llega el U300?».
- **Respuesta sugerida:** «No para RFID UHF del proyecto. DEC-019 lo excluye como lector UHF y evidencia RF_REAL. P1 cierra con un EPC UHF reproducible desde hardware autorizado y configuración/SDK funcional documentados».
- **Fuente:** E17/E18/E19; M §§5,7; R DEC-019/021/022; S §§1,10–12,17.

## N12 — El punto de control debe delimitarse antes de congelar la configuración

- **Qué explicar:** P2 es la definición física y operativa del único punto. Se reservó la composición para la imagen mejorada y explicación que enviará el usuario. Banda y puerta con dos antenas siguen siendo hipótesis; requieren misma operación/control, límites, roles y solapamientos documentados.
- **Qué no sobreafirmar:** buena resolución no acredita escala, infraestructura disponible o desempeño. No asignar dirección por antena. Si la nueva imagen es una recreación didáctica, rotularla como tal y conservar las fuentes originales. En H p.7 se lee «Túnel con lectores RFID» mientras el informe usa «torre»; no resolverlo por suposición.
- **Transición:** «Estas condiciones delimitan qué está demostrado hoy y qué queda por medir».
- **Posible pregunta:** «¿Dos antenas no significan dos puntos de control?».
- **Respuesta sugerida:** «No basta compartir lector. P2 debe demostrar y obtener autorización para un único punto operativo con límites y contexto comunes. Si eso no se demuestra, la configuración no puede presentarse como piloto autorizado de dos puntos».
- **Fuente:** E02/E13/E14/E15/E16/E22; M §§6.1–6.3,9.1; Q §7; R §8. H p.7 como antecedente; nuevo asset/explicación pendientes.

## N13 — La evidencia demuestra software; el desempeño físico sigue pendiente

- **Qué explicar:** recorrer primero lo demostrado y después cada grupo pendiente. La suite documentada reúne 32 pruebas I1 + 25 nuevas I2 = 57, sin fallos, errores u omitidas. Lo pendiente incluye EPC RF_REAL, desempeño físico, omisiones, externas, interferencia, materiales, arquitectura física, P3, pruebas físicas y piloto. P3 es la definición de criterios de aceptación tras prepruebas.
- **Qué no sobreafirmar:** no decir 89 pruebas ni 57 pruebas físicas; no declarar 100% de lectura o cobertura de código. Tampoco cero eventos duplicados logrado, ya que el motor es futuro. Estos resultados son evidencia previa, no una ejecución nueva durante la preparación editorial.
- **Transición:** «La ruta siguiente busca producir precisamente la evidencia del lado pendiente».
- **Posible pregunta:** «¿Qué conclusión de viabilidad puede defender ahora?».
- **Respuesta sugerida:** «La evidencia respalda funcionamiento de los incrementos de asociación y entrada simulada. No permite concluir desempeño RFID físico ni viabilidad operativa del piloto; eso exige ensayos y condiciones todavía pendientes».
- **Fuente:** E06/E07/E08/E09/E10/E17/E19/E22/E23; M pp.2–5; Q pp.3–5; I2 §9.

## N14 — El piloto requiere completar el MVP y pasar por pruebas controladas

- **Qué explicar:** diseñar LecturaRFID–SesionOperacion antes de I3. Después: persistencia de lectura → sesión → eventos/deduplicación → historial operativo → verificación → sustitución → contingencia; adaptador cuando P1 lo habilite. P1/P2 avanzan coordinadamente; prepruebas permiten fijar P3; Pruebas precede al Piloto y luego viene Validación.
- **Qué no sobreafirmar:** no inventar porcentajes/meta RF o fechas. ≥10 repeticiones y cero eventos duplicados son criterios, no resultados actuales. Prepruebas no sustituyen pruebas formales y hardware pendiente no obliga a detener desarrollo lógico.
- **Transición:** «Para avanzar en esa ruta, propongo concentrar el acompañamiento en tres productos concretos».
- **Posible pregunta:** «¿Cómo evitar conclusiones basadas en una muestra insuficiente?».
- **Respuesta sugerida:** «La propuesta prevé muestra intencional por material/geometría/condición, ≥10 repeticiones por escenario prioritario y verdad de terreno. El piloto busca ≥2 perfiles, ≥20 eventos y ≥3 sesiones, con ajustes justificados antes de iniciar si existen restricciones. La conclusión se limita a la configuración ensayada; no hay inferencia institucional».
- **Fuente:** E03/E19/E20/E22/E23/E24/E26; P pp.5–9; M §9; Q VAL-003 a VAL-006 y §5.

## N15 — El siguiente paso es completar el diseño y concretar condiciones de validación

- **Qué explicar:** proponer revisión de diseño I3, acompañamiento para confirmar responsables/condiciones P1/P2 y revisión del protocolo cuando existan prepruebas. La validación final será técnica, operativa y económica y puede ser favorable, condicionada o no favorable.
- **Qué no sobreafirmar:** no solicitar una nueva aprobación general de la propuesta ni atribuir permisos de EMI al asesor universitario. No comprometer compras personales, fechas o un resultado favorable. Contingencia sigue diseño pendiente y conserva datos de equipo, actor, tiempo, contexto, motivo y origen manual explícito.
- **Transición/cierre:** «Hay una base de software comprobada y un camino definido para producir la evidencia física y operativa que falta». Abrir discusión sobre esos productos.
- **Posible pregunta:** «¿Qué necesita concretamente de mí?».
- **Respuesta sugerida:** «Revisar la coherencia del diseño LecturaRFID–SesionOperacion, orientar la coordinación de condiciones y responsables para P1/P2, y revisar el protocolo/criterios con las prepruebas disponibles. Son propuestas para esta reunión; las autorizaciones institucionales siguen su propio proceso».
- **Fuente:** E15/E19/E20/E26; M §9; P pp.6,9; E21 para contingencia.


Guion oral actualizado a 30 secciones en ../output/Guion_Explicacion_Diapositivas.md. Red PERT/CPM general en 18; cierre en 19. Notas incorporadas completas en ../output/speaker_notes_final.md.
