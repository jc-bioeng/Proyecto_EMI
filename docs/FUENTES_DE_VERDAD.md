# Fuentes de verdad del Proyecto EMI

Este inventario organiza las referencias vigentes y su histórico documental del Proyecto EMI. Su función es indicar qué documento prevalece para cada tipo de decisión y evitar que archivos históricos, anexos o documentos auxiliares se interpreten como control vigente.

Este archivo no modifica por sí mismo la línea base formal, no sustituye los documentos controlados y no autoriza cambios de alcance ni implementación fuera de lo aprobado.

Las rutas indicadas son relativas a `docs/` dentro de `Proyecto_EMI`.

---

## 1. Jerarquía documental vigente

| Prioridad | Archivo local | Versión / estado | Función |
| --- | --- | --- | --- |
| 1 | `control_local/Propuesta_Juan_Cortes_20262.pdf` | 20262 · APROBADA | Línea base formal de objetivos, alcance, metodología, cronograma, presupuesto y estrategia de validación. Prevalece ante contradicciones. |
| 2 | `control_local/Arquitecto_Proyecto_Documento_Maestro_v1.8.1_ACTUAL.pdf` | v1.8.1 ACTUAL · 2026-09-06 | Control interno vigente del proyecto. Desarrolla la propuesta aprobada y registra el estado actual de Diseño/I1 sin alterar la línea base formal. |
| 3 | `control_local/Matriz_Requisitos_Proyecto_EMI_v1.8.1_ACTUAL.pdf` | v1.8.1 ACTUAL · 2026-09-06 | Matriz completa vigente de requisitos, prioridades, estados, evidencia, criterios de validación, dependencias, entregables y gates. |
| 4 | `control_local/Registro_Decisiones_Proyecto_EMI_v1.8.1_ACTUAL.pdf` | v1.8.1 ACTUAL · 2026-09-06 | Registro de decisiones vigente. Conserva el histórico y consolida DEC-001 a DEC-022. |
| 5 | `soporte/Informe_Cierre_P0_Proyecto_EMI_2026-09-03.docx` | v1.0 · 2026-09-03 | Evidencia histórica del cierre P0 y transición Requisitos → Diseño. Documento auxiliar. |
| 5 | `soporte/Informe_Tecnico_Avance_P1_v0.1_2026-09-05.pdf` | v0.1 · 2026-09-05 | Evidencia auxiliar del avance técnico P1. No certifica por sí solo el cierre de P1. |
| 5 | `soporte/Checklist_P1_v0.1_ACTUALIZABLE.xlsx` | v0.1 · ACTUALIZABLE | Seguimiento operativo de hardware, configuración, autorización, pruebas y evidencia de P1. |
| 5 | `soporte/Informe_Tecnico_Avance_I1_Proyecto_EMI_v0.1_2026-09-06.pdf` | v0.1 · 2026-09-06 | Informe técnico auxiliar redundante del avance de I1. No sustituye Maestro, Matriz ni Registro. |
| 6 | `anexos/Transcripcion_Entrevista_Trazabilidad_Biomedica (1).docx` | Anexo contextual | Evidencia preliminar/contextual. No amplía automáticamente el alcance del MVP. |

### Regla de autoridad

La jerarquía vigente es:

**Propuesta aprobada → Documento Maestro ACTUAL → Matriz de Requisitos ACTUAL → Registro de Decisiones ACTUAL → documentos de soporte → anexos.**

Ante contradicciones:

1. identificar los pasajes concretos;
2. aplicar la jerarquía anterior;
3. conservar la condición histórica reemplazada;
4. registrar el impacto;
5. no editar silenciosamente fuentes controladas;
6. no convertir recomendaciones, entrevistas o resultados parciales en decisiones aprobadas.

La propuesta aprobada sigue siendo la línea base formal.

---

## 2. Documentos ACTUAL vigentes

### 2.1 Documento Maestro

**Vigente**

`control_local/Arquitecto_Proyecto_Documento_Maestro_v1.8.1_ACTUAL.pdf`

La versión v1.8.1 registra, sin cambiar la línea base aprobada:

- fase formal de Diseño;
- P0 cerrado;
- P1 abierto/en curso;
- inicio materializado de I1;
- stack técnico adoptado mediante DEC-022;
- persistencia SQLite/JDBC;
- migraciones Flyway V1/V2;
- esquema inicial de `Equipo`, `EtiquetaRFID` y `AsignacionEtiqueta`;
- evidencia de pruebas del bloque de persistencia;
- pendientes para completar I1.

### 2.2 Matriz de Requisitos

**Vigente**

`control_local/Matriz_Requisitos_Proyecto_EMI_v1.8.1_ACTUAL.pdf`

Esta es la **Matriz completa vigente**.

Conserva íntegramente la estructura documental heredada de v1.8:

- propósito y regla de integridad;
- categorías, prioridades y estados;
- estado post-aprobación;
- resumen completo de requisitos;
- fichas individuales completas;
- pendientes priorizados;
- exclusiones del alcance;
- históricos y controles de cambio;
- trazabilidad de cierre P0;
- actualización P1.

Además incorpora el avance de I1 sin eliminar antecedentes.

Estados registrados en la Matriz ACTUAL al emitirla (antecedente documental; el estado técnico posterior se detalla al final de este inventario):

- `RF-001`: pasa de **Aprobado** a **En desarrollo**, conservando el estado anterior como histórico.
- `DAT-003`: permanece **En desarrollo**, ahora con evidencia de materialización parcial del submodelo I1.
- `TEC-002`: permanece **En desarrollo**; la interfaz común aún no está implementada en código.
- No se agrega ningún requisito funcional nuevo.
- No se modifica ninguna prioridad Must/Should/Could/Won't.
- No se declara ningún requisito de RF física como Implementado o Validado.
- P1 permanece abierto.

La antigua:

`Matriz_Requisitos_Proyecto_EMI_v1.8.1_ACTUALIZACION_CONTROLADA.pdf`

queda **REEMPLAZADA / NO VIGENTE** y no debe usarse como Matriz de control, porque fue sustituida por la Matriz v1.8.1 completa.

Si se conserva por trazabilidad, ubicarla únicamente en:

`historico/artefactos_reemplazados/`

y marcarla explícitamente como no vigente. Actualmente no se encuentra entre las copias locales; la ruta indica la ubicación prevista si se conserva, no una referencia disponible.

### 2.3 Registro de Decisiones

**Vigente**

`control_local/Registro_Decisiones_Proyecto_EMI_v1.8.1_ACTUAL.pdf`

La versión v1.8.1:

- conserva DEC-001 a DEC-021;
- incorpora DEC-022;
- no elimina ni renumera decisiones;
- mantiene decisiones reemplazadas como histórico;
- conserva DEC-019 como la exclusión de MFRC522/RC522 como lector UHF del MVP y como evidencia `RF_REAL`;
- consolida DEC-022 como decisión del stack inicial de desarrollo.

---

## 3. Histórico preservado

Las versiones v1.8 anteriores fueron trasladadas a `historico/`. No deben borrarse ni reutilizarse como ACTUAL.

### 3.1 Documento Maestro histórico

`control_local/historico/Arquitecto_Proyecto_Documento_Maestro_v1.8.pdf`

Estado:

**HISTÓRICO · REEMPLAZADO POR v1.8.1 ACTUAL**

### 3.2 Matriz de Requisitos histórica

`control_local/historico/Matriz_Requisitos_Proyecto_EMI_v1.8.pdf`

Estado:

**HISTÓRICO · REEMPLAZADO POR v1.8.1 ACTUAL**

La v1.8 conserva valor histórico porque contiene el estado anterior de los requisitos, incluido `RF-001 = Aprobado` antes de iniciar su implementación.

### 3.3 Registro de Decisiones histórico

`control_local/historico/Registro_Decisiones_Proyecto_EMI_v1.8.pdf`

Estado:

**HISTÓRICO · REEMPLAZADO POR v1.8.1 ACTUAL**

Conserva el registro hasta DEC-021 previo a la consolidación de DEC-022.

---

## 4. DEC-022 y decisiones aplicables

### DEC-022 — Adoptar el stack inicial de desarrollo del MVP

Fuente primaria vigente:

`control_local/Registro_Decisiones_Proyecto_EMI_v1.8.1_ACTUAL.pdf`

Stack aprobado:

- Java 21 LTS;
- Maven;
- JDBC;
- SQLite;
- Flyway;
- JUnit 5.

Condiciones vigentes:

- SQLite está aprobada para desarrollo e I1;
- SQLite no está ratificada todavía como base definitiva del piloto;
- Spring Boot queda pospuesto salvo necesidad concreta y trazable;
- Hibernate/JPA queda pospuesto salvo necesidad concreta y trazable;
- una base de datos servidor queda pospuesta salvo necesidad concreta y trazable;
- elegir Java no demuestra compatibilidad con el U300;
- DEC-022 no cierra P1;
- DEC-022 no constituye evidencia `RF_REAL`.

### Adenda individual preservada

`control_local/decisiones_individuales/DEC-022_Adenda_Registro_Decisiones_Proyecto_EMI_2026-09-05.pdf`

Estado:

**HISTÓRICO DE DECISIÓN / FUENTE DE CONSOLIDACIÓN**

Debe conservarse aunque DEC-022 ya esté incorporada al Registro v1.8.1 ACTUAL.

### Integridad de numeración

- DEC-019 vigente = exclusión de MFRC522/RC522 como lector UHF del MVP y como evidencia `RF_REAL`.
- DEC-020 vigente = adopción del Informe Técnico P1 y checklist como documentos auxiliares redundantes.
- DEC-021 vigente = mantener P1 abierto hasta obtener lectura EPC UHF reproducible con hardware autorizado.
- DEC-022 vigente = adopción del stack inicial del MVP.

La entrega errónea que utilizó DEC-019 para el stack **no forma parte del registro oficial**.

---

## 5. Estado vigente del proyecto

### Fase

**DISEÑO**

La transición Requisitos → Diseño fue autorizada mediante el cierre P0.

### Gates

| Gate | Estado | Condición |
| --- | --- | --- |
| P0 | CERRADO | Línea base suficiente, regla de evento, modelo mínimo, baseline Must e interfaz común definidos a nivel de Diseño. |
| P1 | EN CURSO | Falta lector UHF autorizado, configuración documentada y primera lectura EPC UHF reproducible `RF_REAL`. |
| P2 | ABIERTO | Condiciones operativas del punto de piloto pendientes. |
| P3 | ABIERTO | Umbrales posteriores a prepruebas pendientes. |

El cierre P0 no implica software completo, pruebas cerradas, hardware autorizado ni piloto ejecutable.

---

## 6. Estado técnico de I1 al 06/09/2026

I1 está: **IMPLEMENTADO Y VERIFICADO TÉCNICAMENTE**

### Materializado y verificado en el repositorio

- Java 21 LTS;
- Maven + Maven Wrapper;
- JDBC;
- SQLite;
- Flyway;
- JUnit 5;
- JAR ejecutable;
- migraciones V1 y V2;
- esquema persistente para:
  - `Equipo`;
  - `EtiquetaRFID`;
  - `AsignacionEtiqueta`;
- `codigo_institucional` único y obligatorio;
- EPC único y obligatorio;
- máximo una asociación activa por equipo;
- máximo una asociación activa por etiqueta;
- asociación vigente definida por `fecha_fin IS NULL`;
- preservación de asociaciones históricas;
- estado de etiqueta controlado;
- integridad temporal mínima;
- convención temporal UTC;
- persistencia inicializable desde una base vacía;
- siete casos de uso implementados y verificados: crear equipo, crear etiqueta, asociar, consultar vigente, buscar equipo por EPC, corregir transaccionalmente y consultar historial;
- corrección con un único instante UTC y rollback completo ante fallo;
- 32 pruebas automáticas (8 de infraestructura/esquema, 22 de servicio y 2 de consola):
  - 0 fallos;
  - 0 errores;
  - 0 omitidas;
- corrección del empaquetado incremental Maven Shade sin alterar dependencias funcionales.

Los documentos ACTUAL v1.8.1 registran el avance anterior de ocho pruebas; la evidencia del repositorio se amplió posteriormente como se detalla en la sección 7. No se actualizan aquí sus estados oficiales.

### Evidencia de este bloque

La evidencia corresponde a:

**SOFTWARE / PERSISTENCIA**

No corresponde a:

- `SIMULACION` RFID;
- `RF_REAL`;
- desempeño físico RFID;
- alcance de lectura;
- interferencia;
- metal;
- antenas;
- porcentaje físico de lectura.

---

## 7. Evidencia actual y cierre pendiente de I1

Los siete casos de uso están implementados y verificados sobre V1/V2. La corrección conserva el historial en una sola transacción y un fallo posterior al cierre revierte completamente la operación. El historial queda ordenado e incluye asociaciones cerradas y vigente. La consola permite demostrar los flujos desde el JAR.

Evidencia reproducible: `.\mvnw.cmd clean verify`, **BUILD SUCCESS**, 32 pruebas, 0 fallos, 0 errores y 0 omitidas. Ver `desarrollo/casos_de_uso_i1.md` y las pruebas de servicio, consola e infraestructura del repositorio.

**Antecedente preservado:** el bloque inicial de persistencia/esquema contaba con 8 pruebas y los casos de uso estaban pendientes. La implementación posterior agregó 22 pruebas de servicio y 2 de consola. Esa condición anterior permanece en los documentos ACTUAL v1.8.1 como evidencia registrada de aquel avance; esta actualización del inventario no reescribe esos documentos ni cambia por sí sola los estados oficiales de requisitos.

**I1 está IMPLEMENTADO Y VERIFICADO TÉCNICAMENTE, con cierre formal pendiente de los commits de cierre.** La revisión final de integridad está aprobada; corresponde registrar los cambios en commits coherentes cuando el usuario lo autorice. Los casos de uso ya implementados no son pendientes de programación.

La prohibición de nuevas asociaciones con etiquetas INACTIVA sigue como propuesta funcional pendiente y no se implementó; no se convierte automáticamente en requisito de cierre. DAT-003 global y TEC-002 no se consideran cerrados por completar el submodelo I1. P1 permanece abierto y no existe evidencia RF_REAL nueva.

No avanzar a `FuenteLecturasRFID` ni a componentes posteriores sin autorización del usuario.

---

## 8. Informe Técnico de Avance I1

Archivo:

`soporte/Informe_Tecnico_Avance_I1_Proyecto_EMI_v0.1_2026-09-06.pdf`

Clasificación:

**Documento auxiliar redundante**

Su función es explicar pedagógicamente:

- arquitectura;
- stack;
- persistencia;
- migraciones;
- UML del submodelo;
- pruebas;
- evidencias;
- límites;
- pendientes.

No debe utilizarse como fuente primaria de:

- decisiones;
- requisitos;
- alcance;
- objetivos;
- metodología;
- presupuesto.

Ante cualquier discrepancia, prevalecen los documentos de control según la jerarquía definida en este archivo.

---

## 9. P1 y hardware

P1 permanece:

**EN CURSO**

Estado de evidencia conocido:

- DTB-C42M: disponibilidad reportada parcialmente corroborada;
- MFRC522/RC522: disponible como banco auxiliar, pero incompatible con UHF EPC del proyecto;
- ESP32-S3: disponible reportado como recurso auxiliar;
- lector UHF principal: pendiente de disponibilidad/autorización;
- configuración RF: pendiente;
- SDK operativo real: pendiente;
- primera lectura EPC UHF reproducible: pendiente.

El RC522:

- no cierra TEC-001;
- no produce evidencia `RF_REAL` UHF;
- no sustituye al lector UHF del MVP.

La simulación:

- puede demostrar lógica;
- no demuestra desempeño físico RFID.

---

## 10. Custodia documental y Git

Las siguientes carpetas permanecen excluidas de Git:

- `docs/control_local/`
- `docs/soporte/`
- `docs/anexos/`

Los agentes deben tratarlas como referencias de solo lectura salvo solicitud expresa del usuario.

No utilizar `git add -f` para publicar documentos institucionales.

No copiar información personal, institucional o sensible desde documentos controlados hacia archivos versionables salvo necesidad explícita.

### Estructura documental vigente esperada

```text
docs/
├── FUENTES_DE_VERDAD.md
│
├── control_local/
│   ├── Propuesta_Juan_Cortes_20262.pdf
│   ├── Arquitecto_Proyecto_Documento_Maestro_v1.8.1_ACTUAL.pdf
│   ├── Matriz_Requisitos_Proyecto_EMI_v1.8.1_ACTUAL.pdf
│   ├── Registro_Decisiones_Proyecto_EMI_v1.8.1_ACTUAL.pdf
│   │
│   ├── decisiones_individuales/
│   │   └── DEC-022_Adenda_Registro_Decisiones_Proyecto_EMI_2026-09-05.pdf
│   │
│   └── historico/
│       ├── Arquitecto_Proyecto_Documento_Maestro_v1.8.pdf
│       ├── Matriz_Requisitos_Proyecto_EMI_v1.8.pdf
│       ├── Registro_Decisiones_Proyecto_EMI_v1.8.pdf
│       └── artefactos_reemplazados/
│           └── Matriz_Requisitos_Proyecto_EMI_v1.8.1_ACTUALIZACION_CONTROLADA.pdf
│
├── soporte/
│   ├── Informe_Cierre_P0_Proyecto_EMI_2026-09-03.docx
│   ├── Informe_Tecnico_Avance_P1_v0.1_2026-09-05.pdf
│   ├── Checklist_P1_v0.1_ACTUALIZABLE.xlsx
│   └── Informe_Tecnico_Avance_I1_Proyecto_EMI_v0.1_2026-09-06.pdf
│
├── anexos/
│   └── Transcripcion_Entrevista_Trazabilidad_Biomedica (1).docx
│
└── desarrollo/
```

---

## 11. Reglas para agentes

Las instrucciones operativas permanecen en:

`AGENTS.md`

Todo agente debe:

- leer `FUENTES_DE_VERDAD.md` antes de tomar decisiones de requisitos o arquitectura;
- consultar la versión ACTUAL, no una histórica;
- consultar el Registro ACTUAL para cualquier DEC-###;
- no recrear decisiones a partir de informes auxiliares;
- no inventar estados de requisitos;
- no marcar un requisito Implementado solo porque exista diseño;
- no confundir prueba de software con `RF_REAL`;
- no ampliar el MVP a medicamentos/insumos, localización en tiempo real, múltiples puntos o sustitución de AM;
- conservar versiones históricas;
- reportar contradicciones en lugar de resolverlas silenciosamente.

---

## 12. Control de actualización de este inventario

**Fecha:** 06 de septiembre de 2026

Cambios registrados:

1. Documento Maestro v1.8.1 pasa a ACTUAL.
2. Matriz de Requisitos v1.8.1 completa pasa a ACTUAL.
3. Registro de Decisiones v1.8.1 pasa a ACTUAL.
4. Maestro v1.8, Matriz v1.8 y Registro v1.8 pasan a `historico/`.
5. DEC-022 queda consolidada en el Registro ACTUAL.
6. La adenda individual DEC-022 se conserva como histórico de decisión.
7. La Matriz v1.8.1 de “actualización controlada” queda reemplazada y no vigente.
8. Se incorpora el Informe Técnico I1 v0.1 como documento auxiliar.
9. Se registra el avance técnico verificado de I1 sin declarar su cierre.
10. P1 permanece abierto y no se registra nueva evidencia `RF_REAL`.
11. Se verifican los nombres reales: matriz v1.8.1 ACTUAL e históricos v1.8 sin sufijo ACTUAL.
12. Por autorización del usuario, se actualiza la evidencia del repositorio a siete casos de uso implementados y 32 pruebas aprobadas, preservando el antecedente de ocho pruebas y sin declarar cierre formal de I1.

---

## 13. Regla final de integridad

**Nunca borrar información histórica relevante.**

Cuando una condición cambie:

1. conservar la condición anterior;
2. indicar que fue reemplazada o actualizada;
3. registrar la nueva condición;
4. identificar la decisión o evidencia que produjo el cambio;
5. actualizar únicamente los documentos que corresponda.

Este archivo debe mantenerse sincronizado con la estructura documental real de `Proyecto_EMI`.

## Estado técnico y trazabilidad de I1

**I1 = IMPLEMENTADO Y VERIFICADO TÉCNICAMENTE.** El cierre formal queda pendiente de los commits de cierre; todavía no se declara CERRADO.

Estados documentados en el repositorio por solicitud explícita del usuario:

| Requisito | Estado | Alcance |
| --- | --- | --- |
| RF-001 | IMPLEMENTADO | Los siete casos de uso de I1 están implementados y verificados. |
| DAT-003 | EN DESARROLLO | El submodelo I1 está implementado; el modelo mínimo completo del MVP todavía no. |
| TEC-002 | EN DESARROLLO | FuenteLecturasRFID todavía no está implementada. |

No se cambian otros requisitos ni se reescriben los documentos ACTUAL. Su evidencia anterior se conserva como antecedente.

Evidencia técnica: 7 casos de uso implementados; 32 pruebas, 0 fallos, 0 errores y 0 omitidas; rollback completo probado; corrección en una transacción y con un único instante UTC; concurrencia probada; historial ordenado; búsqueda por EPC vigente; JAR ejecutando los siete flujos.

La prohibición de asociar etiquetas INACTIVA no está aprobada: sigue como propuesta pendiente, no implementada y no constituye un requisito.

Después del cierre formal de I1, el siguiente incremento previsto es **FuenteLecturasRFID → FuenteSimulada**. Todavía no se implementa y requiere autorización operativa.
