---
name: didactic-slide-design
description: Define y aplica la gramática visual didáctica de EMI con una idea por diapositiva, diagramas editables y distinción visible entre resultados, hipótesis y pendientes.
---

# Diseño didáctico EMI

Aplicar `presentation/deck_spec.md`; sus medidas son decisiones editoriales, no arquitectura del MVP. Mantener título conclusivo sustentado, un visual dominante y texto de apoyo breve. Preferir diagramas → tablas → bullets → párrafos según la relación que se explica.

Usar diagramas nativos editables para asociación histórica, interfaz común y gates. Un conector representa una relación identificada, no una implementación implícita: línea continua para lo materializado; discontinua y rótulo «previsto» para lo pendiente. No dibujar lector→evento directamente, antena→ingreso/salida ni AM como integración activa.

Mostrar la corrección de etiqueta con dos intervalos contiguos, no borrando la asociación previa. Mostrar repeticiones de EPC como observaciones distintas y la consolidación futura en otra capa. Rotular ejemplos ficticios «Ejemplo didáctico» y datos simulados «SIMULACION». No presentar un mockup como captura de aplicación existente.

No usar círculos de cobertura sobre fotos para sugerir RF validada. En un eventual esquema físico, rotular «Esquema conceptual, sin escala» si no hay mediciones, y separar GEOMETRIA / MODELO_RF_TEORICO / RF_REAL.

Usar texto e icono además de color para estados. Las tarjetas de estado se reservan para comparaciones de gates; el resto del deck debe evitar una repetición de paneles. No usar porcentajes de avance derivados de conteo de requisitos.

Fotografías reales solo cuando expliquen el proceso y su procedencia esté identificada. Las fotos del informe son de un plano fotografiado, no de instalación RFID probada. No recortar evidencia esencial ni revelar datos innecesarios de AM. No añadir adornos o imágenes generadas como supuesta evidencia institucional.

Resolver exceso de densidad reduciendo texto o trasladando detalle a notas; no reducir por debajo de los mínimos del spec. Si se necesita dividir una slide, preservar el rango total acordado reorganizando la narrativa, sin eliminar límites esenciales.
