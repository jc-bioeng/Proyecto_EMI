> Actualización de producción autorizada: 29 diapositivas (18 principales + 11 anexos), plano ampliado con recorte editable y definiciones de códigos. Véase [production_revision.md](production_revision.md) para la correspondencia con el storyboard aprobado. Las referencias anteriores a producción pendiente son antecedentes.

# Plan de recursos visuales y composición

Versión editorial 0.3 · 15 slides · corte 10/09/2026. Plan de producción futura: no se generan diapositivas, PPTX ni imágenes mejoradas. El usuario enviará imagen de mejor calidad y explicación de los planos; su recepción está pendiente. El storyboard puede revisarse sin ese material, pero no se cierra aún la interpretación espacial ni el arte de las slides 04/12.

## Convenciones de producción

Aplicar `deck_spec.md`: 16:9, 13,333 × 7,5 pulgadas; título x=0,60/y=0,40/w=12,133/h=1,05; área principal y=1,70–6,65; pie y=6,85. Las cajas de abajo son reservas editoriales, no mediciones del plano. Arial 32–36 pt títulos, 22–24 cuerpo, ≥18 etiquetas/limitaciones y ≥12 referencias. Diagramas/tablas/textos principales nativos editables en PptxGenJS; las fotografías son imágenes y no se anuncian como editables por elementos internos.

Un visual por relación explicada, sin decoración. Contornos continuos para componentes implementados, discontinuos para previstos. Etiquetas distinguen estado, madurez y ámbito. Las flechas entre clases (implementa) no se confunden con las de flujo (entrega); cronología no equivale a dependencia de ejecución.

## Inventario y reservas por slide

| Slide / asset | Composición y cajas previstas (x, y, w, h; pulgadas) | Elementos y función didáctica | Fuente / procedencia | Estado de preparación |
|---|---|---|---|---|
| 01 / V01 | Texto principal (0,60;1,80;11,80;2,50); estado (0,60;5,65;12,00;0,60) | Título formal + autor + límite RF_REAL, sin collage | P p.2; M p.2 | Especificado; sin producción |
| 02 / V02 | Hitos (0,60;1,80;12,13;2,40); fases (0,60;4,75;12,13;1,25) | Cuatro hitos y banda metodológica; llave Diseño–incrementos | M p.2; I1 p.1; I2 §9 | Especificado; fechas con significado explícito |
| 03 / V03 | Problema (0,60;1,80;12,13;1,40); perímetro (1,30;3,60;10,70;1,30); límites (0,60;5,55;12,13;0,85) | Movimiento físico/registro y perímetro del prototipo; AM sin conexión activa | P pp.3–4; R DEC-005/008 | Especificado |
| 04 / P-01 + V04 | Imagen reservada (0,60;1,80;4,30;3,65); interpretación (5,20;1,80;7,53;4,45) | Imagen general + flujo recepción/preparación/dotación; madurez junto al hallazgo | Nuevo material del usuario PENDIENTE; H p.5 fig.4 como antecedente | Esperar imagen y explicación; no crear sustituto |
| 05 / V05 | Tres carriles (0,60;2,00;12,13;3,50), separados verticalmente | Necesidad → requisito → diseño → evidencia; códigos explicados | P pp.3–6; Q pp.2,5 | Especificado; no genealogía temporal supuesta |
| 06 / V06 | Módulos existentes (0,60;1,80;4,65;4,40); módulos previstos (5,80;1,80;6,93;4,40) | Identidad y entrada separadas; persistencia LecturaRFID/contexto/operación discontinuos | M §4; Q pp.2,4; I2 §3 | Especificado; conexiones integrales pendientes |
| 07 / V07 | Ejecución (1,10;2,95;11,10;1,10); construcción (1,10;1,90;11,10;0,60); migración/pruebas (1,10;4,65;11,10;1,25) | Java 21 LTS → JDBC → SQLite; Maven/Flyway/JUnit 5 con verbos, sin logos | R DEC-022; pom.xml; I1 pp.3–4 | Especificado; stack visible completo |
| 08 / V08 | Observaciones (0,60;2,00;3,00;2,00); regla (4,00;2,00;4,20;2,00); resultado (8,60;2,00;4,13;2,00); conceptos (0,60;4,80;12,13;1,30) | Tres EPC iguales → procesamiento futuro → evento hipotético | P p.4; M §4; I2 §§6–9 | Ejemplo didáctico, no ejecución nueva |
| 09 / V09 | Intervalos (0,60;2,00;8,50;2,20); prueba (9,60;2,00;3,13;2,20); transacción (0,60;4,70;12,13;1,20) | Cierre e inicio en t, reversión ante fallo; 32 SOFTWARE | I1 pp.2–5 | Especificado; historial de asociaciones |
| 10 / V10 | Contrato (3,30;1,80;6,73;0,70); implementaciones (0,60;2,90;12,13;1,00); dato/salida futura (2,00;4,40;9,33;1,70) | Fuente común; simulador materializado y fuente UHF discontinua; LecturaEntradaRFID | M §4; I2 §§4–9 | Especificado; metadata física no inventada |
| 11 / V11 | Tres pasos (0,60;2,00;12,13;2,40); RC522/candidatos (0,60;5,00;12,13;1,00) | Inspección estática → funcional/configuración → EPC reproducible; exclusión HF | M §§5,7; S; R DEC-019/021 | Especificado; no catálogo como prueba |
| 12 / P-02 + V12 | Imagen reservada (0,60;1,80;5,85;4,30); esquema (6,85;1,80;5,88;4,30) | Detalle del plano + lectura guiada; esquema operativo separado, sin escala | Nuevo material/explicación PENDIENTES; H p.7 fig.6 antecedente | No fijar llamadas/recorridos hasta recibir material |
| 13 / V13 | Demostrado (0,60;1,80;4,20;4,20); pendiente (5,20;1,80;7,53;4,20); límite (0,60;6,10;12,13;0,55) | Comparación 38/62; cifra 57 y tres/cuatro grupos de pendientes | M pp.2–5; Q pp.3–5; I2 §9 | Especificado; ver control crítico inferior |
| 14 / V14 | Carriles de preparación (0,60;1,85;12,13;2,00); secuencia final (0,60;4,15;12,13;1,00); criterios (0,60;5,50;12,13;0,80) | Software/físico convergen en Pruebas → Piloto → Validación | P pp.5–9; M §9; Q §5 | Especificado; sin plazos inventados |
| 15 / V15 | Productos/peticiones (0,60;2,00;12,13;2,90); mensaje (0,60;5,50;12,13;0,80) | Revisión I3, coordinación P1/P2 y protocolo; peticiones, no acuerdos | M §9; P pp.6,9 | Propuestas para reunión |

## Planos: indicación pendiente conforme al último mensaje

**P-01: vista general. P-02: detalle operativo.** Pueden proceder de una sola imagen nueva o de dos vistas; se resolverá con el material aportado. No pedir una segunda entrega ni inventar una reconstrucción para completar estas reservas. La imagen mejorada y la explicación del usuario aún no se han recibido.

Antecedente disponible: `docs/soporte/Informe_Hallazgos_Operativos_AM_RFID_EMI_2026-09-09.pdf`, páginas 5–7, figuras 4–6. Se inspeccionaron las tres imágenes originales extraídas del PDF, sin modificar contenido, para valorar su utilidad. El uso final queda pendiente del nuevo aporte. No se generó una imagen nueva ni se produjeron anotaciones finales.

- P.5 / figura 4 / XObject `Im175`: vista general fotografiada, con perspectiva y texto pequeño. Útil como referencia de conjunto, insuficiente para pedir al público leer cotas.
- P.6 / figura 5 / `Im182`: detalle parcial; el encuadre deja fuera o corta algunos rótulos que menciona su pie. No usar ese pie como sustituto de lo efectivamente visible.
- P.7 / figura 6 / `Im229`: se distinguen ventanilla, picking y «Túnel con lectores RFID»; el texto del informe/control usa «torre». Discrepancia registrada para no fijar denominación por suposición. La foto no acredita hardware instalado.

Los archivos de inspección están en `presentation/build/source_review/`, ignorados por Git y no son entregables finales. La extracción devuelve recursos compartidos entre páginas; se comprobó el operador `Do` de cada página para identificar qué imagen se dibuja realmente. Las otras imágenes extraídas no se deben asociar a una página solo por el nombre del archivo temporal.

Al recibir el material:

1. Registrar archivo, procedencia, explicación y fecha de recepción. Distinguir foto del plano original, mejora visual y recreación interpretativa. Si fuera recreación generada, rotularla como ilustración didáctica basada en fuentes y conservar el plano original como evidencia.
2. Vincular cada llamada a lo realmente legible o a la explicación recibida, indicando madurez. Aclarar «Túnel»/«torre», orientación y recorrido solo hasta donde haya evidencia; no convertir calidad visual en confirmación institucional.
3. Ajustar encuadre y llamadas de 04/12. Separar siempre «lo que muestra el plano» de «interpretación del flujo» y «configuración RFID candidata». Conectores de hipótesis discontinuos; texto ≥18 pt. No trazar una geometría precisa si no hay escala/dimensiones verificadas.
4. Mantener P2 y RF_REAL pendientes salvo nuevas fuentes/decisiones suficientes. No crear cobertura, potencia, altura, muros, ubicación de antenas o responsables no sustentados.

Esta lista prepara la incorporación; no ejecuta modificaciones de imagen ni deja el storyboard dependiente de una supuesta autorización tácita.

## Control de la comparación crítica — slide 13

Los diez pendientes solicitados permanecen visibles, aunque agrupados:

| Exigencia | Grupo visible |
|---|---|
| Lectura EPC UHF RF_REAL reproducible | Captura |
| Desempeño físico | Desempeño físico |
| Lecturas omitidas | Omisiones |
| Lecturas externas | Lecturas externas |
| Interferencia | Interferencia |
| Superficies/materiales relevantes | Superficies/materiales relevantes |
| Arquitectura física congelada | Preparación |
| Criterios de aceptación P3 | P3 = criterios de aceptación |
| Pruebas controladas físicas | Ejecución |
| Piloto | Ejecución |

No trasladar estas exigencias exclusivamente a notas. No usar diez tarjetas: cuatro grupos de texto a la derecha, con ≥18 pt y palabras clave destacadas. A la izquierda, conservar 32 + 25 = 57 y cero fallos/errores/omitidas como evidencia previa SOFTWARE/SIMULACIÓN. El render posterior debe verificar que el lado derecho no obliga a letra pequeña; se puede aumentar su ancho o redistribuir saltos sin quitar pendientes.

## Criterio de cierre visual posterior

La planificación actual no acredita legibilidad renderizada. Al producir: render de las 15 slides, inspección de texto y conexiones, proporciones y contraste; confirmar que los diagramas y el stack son editables y que las notas contienen fuentes. No entregar logos ficticios, capturas simuladas de resultados ni fotografías reconstruidas como evidencia. Fuente breve visible; ruta/versión/página completa en notas. Sin necesidad de buscar imágenes externas para completar este storyboard.


## Logos del stack — revisión final

Se incorporaron seis colocaciones SVG en la diapositiva 10: Java, símbolo Java para JDBC (identificado como API), SQLite, Maven, JUnit 5 y Flyway. Los nombres y explicaciones siguen siendo texto editable. Los logos conservan su proporción y se mantienen separados del contenido factual; no representan certificación o patrocinio. Se restringió el recorte documental a las diapositivas 07/15 para que no afecte a los nuevos logos.

Fuentes de los assets:
- Java: https://cdn.jsdelivr.net/gh/devicons/devicon@master/icons/java/java-original.svg
- SQLite: https://raw.githubusercontent.com/devicons/devicon/master/icons/sqlite/sqlite-original.svg
- JUnit: https://raw.githubusercontent.com/devicons/devicon/master/icons/junit/junit-original.svg
- Maven: https://maven.apache.org/images/logos/MavenLogoLeaf.svg
- Flyway: https://raw.githubusercontent.com/simple-icons/simple-icons/develop/icons/flyway.svg

Archivos locales en `presentation/build/assets/logos/`. Se reexportaron PPTX, PDF y las 29 imágenes; inspección visual ampliada de la diapositiva modificada y comprobación estructural/factual del conjunto. Resultado: 29 diapositivas/notas/páginas, 657 formas nativas, ocho colocaciones de imagen (seis logos y dos planos), cero desbordamientos, fuentes originales sin cambios y cero coincidencias de las seis afirmaciones incorrectas buscadas. Se mantienen los resultados previos para el contenido no modificado.
