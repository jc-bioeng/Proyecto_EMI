> Actualización de producción autorizada: 29 diapositivas (18 principales + 11 anexos), plano ampliado con recorte editable y definiciones de códigos. Véase [production_revision.md](production_revision.md) para la correspondencia con el storyboard aprobado. Las referencias anteriores a producción pendiente son antecedentes.

# Especificación editorial y visual

Versión 0.1 · 10/09/2026. Propuesta de diseño para futura producción; no plantilla institucional aprobada. No se han generado diapositivas. Los títulos conclusivos, diagramas editables y PptxGenJS responden al encargo explícito.

## Lienzo y retícula

- Formato 16:9: 13,333333 × 7,5 pulgadas. Origen superior izquierdo; cajas en pulgadas, fuentes en puntos.
- Márgenes laterales 0,60; superior/inferior 0,40. Área útil horizontal 12,133333. Retícula de 12 columnas con gutter de 0,20; columna ≈0,827778. Alinear a guías, no a posiciones estimadas visualmente.
- Título: x=0,60, y=0,40, w=12,133333, h=1,05. Contenido principal y=1,70 a 6,65. Fuente/corte/página: y=6,85, h=0,25; sin multiplicar fragmentos de pie.
- Unidad vertical base 0,10; separación entre grupos ≥0,25. Los componentes reciben cajas explícitas. No llenar todo el espacio libre.

## Tipografía y densidad

Arial (disponible en este equipo), regular/negrita; una familia. No depender de fuentes remotas. Títulos de slide 32–36 pt, apertura breve 36–42; el título formal largo puede ir como texto secundario de 22–24 pt en slide 01. Cuerpo 22–24 pt; etiquetas ≥18; notas de fuente visibles ≥12; estados/límites esenciales ≥18. Notas del presentador no se proyectan.

Máximo recomendado: título de dos líneas, 35–55 palabras de cuerpo visible y un visual dominante. El título formal de la apertura requiere tratamiento propio, no reducción de todo el deck. Hasta tres bullets breves si explican elementos paralelos; tablas de hasta cinco filas proyectadas. El cuadro de validación completo se explica en notas.

Título expresa conclusión acotada, sin punto final si es breve. Evitar «Arquitectura», «Resultados», «Avances» como títulos solos. Si el texto no cabe: simplificar/redactar → mover detalle a notas → reorganizar composición; no encoger por debajo de mínimos ni ocultar el límite de evidencia.

## Color, jerarquía y estados

Fondo `F7F9FB`; texto/títulos `172B4D`; secundarios `425466`; énfasis `007F86`. Estados: cerrado/implementado `166534`, en curso/desarrollo `92400E`, pendiente `475569`, límite crítico `9F1239`. Son colores editoriales, no identidad de EMI/UdeA. Combinar texto, símbolo y color; evitar significado basado solo en rojo/verde. Verificar contraste de cada combinación al render.

| Dimensión | Lenguaje visible | Regla |
|---|---|---|
| Proyecto/gate | DISEÑO, CERRADO, EN CURSO, PENDIENTE | Copiar estado del objeto, no del documento que lo describe |
| Requisito | IMPLEMENTADO, EN DESARROLLO, APROBADO | Prioridad Must/Should aparte; aprobado no implica implementado |
| Madurez | CONFIRMADA, CORROBORADA, PRELIMINAR, PENDIENTE, CALCULADA, PENDIENTE_RF_REAL | No usar confirmado para entorno adyacente no verificado |
| Ámbito | SOFTWARE, SIMULACION, RF_REAL; análisis estático SDK | RF_REAL no se muestra como logrado en este corte |

Tarjeta de estado: nombre del objeto, estado y una condición breve, sin porcentajes. Máximo tres tarjetas cuando sea útil comparar gates; evitar paneles repetidos en todas las slides. Para ejemplos hipotéticos usar «Ejemplo didáctico». Para arquitectura futura: línea discontinua + «pendiente»; materializada: continua + nombre concreto.

## Componentes reutilizables previstos

Título y límite; pie de fuente; bloque de evidencia; fila de gate; intervalo histórico; nodo/conector de proceso; tabla editable. `src/presentation/theme.mjs` centraliza medidas y estilos; `geometry.mjs` controla límites rectangulares. El generador futuro compondrá estos elementos en PptxGenJS, sin imagen de slide completa. No se crean componentes de aplicación Java.

Diagramas principales editables con nodos nativos, conectores rectos/ortogonales y leyenda. Etiquetas cercanas a nodos, sin cruces con texto. Flecha significa una relación/proceso definido, nunca causalidad inferida. No dibujar sesión/evento como implementado; no unir AM mediante integración activa. Relación I1 conserva historia; EPC repetido no implica movimiento repetido.

SVG admite logotipos/iconos auxiliares si hay archivo autorizado; viewBox y proporción estables. SVG no vuelve editables sus elementos internos, por lo que no sustituye el diagrama principal. Evitar decoraciones sin función didáctica.

## Fotografías y evidencia

Fuentes candidatas: H pp.3–4 capturas AM; pp.5–7 fotografías del plano. Todavía no seleccionadas ni verificadas visualmente para el deck. Priorizar diagramas conceptuales cuando falte legibilidad; no fingir una captura original.

Antes de usar: comprobar página y figura, resolución al tamaño proyectado, procedencia y datos personales/institucionales innecesarios. Mantener proporciones; no reconstruir con IA; no recortar información necesaria para interpretar la evidencia. Cualquier ocultación de datos o recorte debe registrarse como tratamiento del asset y conservar original fuera del deck. No usar foto de plano como foto de instalación, escala métrica o cobertura RF.

Fuente breve visible, por ejemplo «Maestro v1.9.2, §4 · SOFTWARE/SIMULACION». Fuente completa con ruta, versión, página y E en notas. Mostrar condiciones críticas junto al resultado: «evidencia previa», «hipótesis P2», «RF_REAL pendiente». No relegarlas exclusivamente a notas.

## Entrega posterior y control

PptxGenJS editable, inicialmente 15 slides, con notas. Salida solo tras solicitud de producción. Render de todas las slides, revisión factual/ortográfica/visual y editabilidad. Revisar overflow, alineación, tablas, proporciones y densidad. Medir cajas no reemplaza render ni apertura en PowerPoint. Mantener el informe de QA fuera de las notas académicas.


Actualización de planificación v0.3: 15 slides; stack dedicado en 07. Las fotografías del plano de H pp.5–7 se inspeccionaron como antecedente; por indicación posterior del usuario, 04/12 reservan espacio para una imagen mejorada y su explicación todavía pendientes. No se generan imágenes ahora. Prevalece el estado de recursos y cajas de `visual_assets_plan.md` sobre la selección inicial de fotografías; no se modifica la gramática visual general.


## Estilo final autorizado
Inspiración UdeA: fondo FAFCF8, texto 203C2B, secundarios 4D5E52, acento 357A3E; tarjetas claras E8F1E2 y franja superior verde. Esta revisión sustituye la paleta azul/teal inicial. Tipografía Arial y mínimos conservados. Adaptación editorial, sin escudo ni declaración de plantilla oficial.
