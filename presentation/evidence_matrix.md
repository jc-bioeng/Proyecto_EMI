> Actualización de producción autorizada: 29 diapositivas (18 principales + 11 anexos), plano ampliado con recorte editable y definiciones de códigos. Véase [production_revision.md](production_revision.md) para la correspondencia con el storyboard aprobado. Las referencias anteriores a producción pendiente son antecedentes.

# Matriz editorial de evidencia

Versión editorial 0.3 · storyboard de 15 diapositivas. Corte 10/09/2026. E son identificadores editoriales, no requisitos/DEC. Páginas físicas PDF desde 1. SOFTWARE y SIMULACION son ámbitos distintos; NO APLICA se usa para control/contexto. «Sí» autoriza la conclusión exactamente acotada, no extrapolaciones. Estado formal de requisito no se infiere de la madurez documental.

## Catálogo de fuentes

| ID | Ruta desde la raíz | Versión / localización |
|---|---|---|
| P | `docs/control_local/Propuesta_Juan_Cortes_20262.pdf` | Propuesta aprobada 20262, 11 pp. |
| M | `docs/control_local/historico/Arquitecto_Proyecto_Documento_Maestro_v1.9.2_ACTUAL.pdf` | v1.9.2, 09/09/2026, 6 pp. |
| Q | `docs/control_local/historico/Matriz_Requisitos_Proyecto_EMI_v1.9.2_ACTUAL.pdf` | v1.9.2, 09/09/2026, 6 pp. |
| R | `docs/control_local/historico/Registro_Decisiones_Proyecto_EMI_v1.9.2_ACTUAL.pdf` | v1.9.2, 09/09/2026, 4 pp. |
| H | `docs/soporte/Informe_Hallazgos_Operativos_AM_RFID_EMI_2026-09-09.pdf` | 09/09/2026, auxiliar, 9 pp. |
| I1 | `docs/soporte/Informe_Tecnico_Cierre_I1_Proyecto_EMI_v0.2_2026-09-06.pdf` | v0.2, 06/09/2026, 6 pp. |
| I2 | `docs/analisis/INFORME_TECNICO_I2_FUENTE_RFID_SIMULADA.md` | 07/09/2026, auxiliar |
| S | `docs/analisis/ANALISIS_SDK_CHAINWAY_U300.md` | Inspección 06–07/09/2026, auxiliar |
| X | `target/surefire-reports/TEST-*.xml` | 7 resultados locales existentes; inspección 10/09/2026, sin ejecución nueva |
| C | `src/main/java/co/emi/trazabilidad/` | Código presente al corte; archivos/hash en manifest |

## Afirmaciones

| ID | Afirmación y límite | Fuente, versión/localizador | Madurez | Ámbito | ¿Conclusión visible? | Slides |
|---|---|---|---|---|---|---|
| E01 | La propuesta aprobada compromete desarrollo y validación del prototipo para equipos biomédicos EMI Medellín. | P pp.2–4; M v1.9.2 p.1 | CONFIRMADA por línea base/control | NO APLICA | Sí; título formal y objetivo, sin fecha de aprobación inventada | 01,02,03 |
| E02 | RFID UHF pasivo y un único punto; AM se conserva; exclusiones institucionales/RTLS/insumos. | P pp.2–3,6–7; R v1.9.2 p.2 DEC-005/007/008/012 | CONFIRMADA | NO APLICA | Sí; no sugerir integración AM aprobada | 03,12 |
| E03 | Secuencia metodológica completa; pruebas físicas/piloto condicionados a hardware autorizado oportunamente. | P pp.6–9; M v1.9.2 pp.2,5 | CONFIRMADA | NO APLICA | Sí; condiciones no equivalen a cancelar piloto | 02,14 |
| E04 | Línea base de 24 semanas y COP 43.724.342; desembolso personal $0. | P pp.7–9, tablas 1–2; Q v1.9.2 pp.2–3 AC-002/003, ECO-002; R p.2 DEC-010 | CONFIRMADA | NO APLICA | Sí como planificación, nunca ejecución/gasto/disponibilidad | 03 |
| E05 | P0 cerrado 03/09/2026; fase formal Diseño; Must congelados. | M v1.9.2 p.2 §2; R p.2 DEC-016/018 | CONFIRMADA | NO APLICA | Sí; no cierra MVP ni piloto | 02,05 |
| E06 | I1 cerrado; RF-001 implementado con siete flujos y tres entidades de asociación. | M v1.9.2 p.2; Q p.2 RF-001; I1 v0.2 pp.2–3; C `aplicacion/ServicioI1.java` | CONFIRMADA | SOFTWARE | Sí; historial de asociación, no de movimientos | 02,05,06,09,13 |
| E07 | La corrección preserva historial y usa una transacción/un instante UTC; rollback y concurrencia documentados. | I1 v0.2 pp.2,4–5; I2 §9; Q v1.9.2 p.2 RF-001 | CONFIRMADA | SOFTWARE | Sí; no se extrapola a caudal de lector real | 09,13 |
| E08 | I2 cerrado técnicamente: fuente común, simulador, LecturaEntradaRFID y OrigenDatos. | M v1.9.2 pp.2,5; Q pp.4–5; I2 §§2,4–8 | CONFIRMADA | SOFTWARE / SIMULACION | Sí; no cierra TEC-002 ni MVP | 02,06,10,13 |
| E09 | Entrada preserva EPC literal, orden, repeticiones, timestamp de aplicación y origen SIMULACION. | M v1.9.2 p.2 §4; I2 §§6–9; C `infraestructura/rfid/FuenteSimulada.java` métodos emitir | CONFIRMADA | SIMULACION | Sí; sin timestamp RF ni metadata física inventada | 08,10,13 |
| E10 | Suite documentada: 57 = 32 I1 + 25 nuevas; 0 fallos/errores/omitidas. | M v1.9.2 pp.2,5–6; Q p.4; I2 §9; X totales 22+5+2+1+7+1+19 | CONFIRMADA documental | SOFTWARE / SIMULACION | Sí, como evidencia previa; no 57 ensayos RF o 100% de cobertura | 09,10,13 |
| E11 | Lectura ≠ evento ≠ verificación ≠ sustitución ≠ contingencia; evento exige contexto/deduplicación. | P p.4; M v1.9.2 pp.2–3; R p.2 DEC-014/015 | CONFIRMADA como diseño | NO APLICA | Sí como regla; motor posterior no implementado | 05,06,08 |
| E12 | «Ubicación Física» existe en AM; significado dinámico/actualización institucional pendientes. | H pp.2–4, figs.1–3; M v1.9.2 pp.3–4; R p.3 DEC-023 | CONFIRMADA existencia / PENDIENTE semántica | NO APLICA | Sí con ambos límites; no afirmar que AM carece de ubicación ni que ya se integra | 03,04 |
| E13 | Ventanilla/picking/torre RFID están corroborados según DEC-023, como insumo P2. | R v1.9.2 p.3; M p.4 §6.3; H pp.5–7 figs.4–6 | CORROBORADA según control | NO APLICA | Sí como contexto del plano; no instalación, medidas o RF validada | 04,12 |
| E14 | Mayoría de activos atravesaría área candidata; rutas de camillas, sillas camilla, raqui, corto espinal y férula espinal por confirmar. | M v1.9.2 pp.3–4 §6.1 | PRELIMINAR / PENDIENTE | NO APLICA | Solo hipótesis rotulada; no cobertura universal ni clasificación definitiva | 04,12 |
| E15 | Farmacia es candidato a operar sesiones; inicio/confirmación/corrección/contingencia y rol Biomédica pendientes. | M v1.9.2 p.4 §6.2; Q p.5 OPE-002 | PRELIMINAR / PENDIENTE | NO APLICA | Solo como pendiente, nunca responsable confirmado | 04,12,15 |
| E16 | Banda + puerta con dos antenas es hipótesis sujeta a un único punto operativo autorizado; antena no determina dirección. | M v1.9.2 p.4 §6.2; Q p.5 §7; R p.4 §8 | PENDIENTE | NO APLICA | Sí como condición; no configuración aprobada ni RF_REAL | 12 |
| E17 | SDK A4 inspeccionado estáticamente; compatibilidad funcional Java 21/unidad/firmware y RF_REAL pendientes. | M v1.9.2 p.3 §5; Q pp.4,6; S §§1,10–12,17 | CONFIRMADA inspección / PENDIENTE funcional | SOFTWARE (análisis estático) | Sí con límite; no repetir auditoría general ni afirmar adaptador operativo | 11,13 |
| E18 | U300 candidato principal provisional; R3S alternativa; RC522 excluido como UHF/RF_REAL. | M v1.9.2 p.4 §7; R p.2 DEC-019/021 | CONFIRMADA decisión de exclusión / PENDIENTE hardware | NO APLICA | Sí como estado de selección, no disponibilidad | 11 |
| E19 | P1/P2 en curso; P3, Pruebas formales, Piloto y RF_REAL pendientes; MVP no cerrado. | M v1.9.2 pp.1–2,5–6; Q p.5 §5; R pp.3–4 | CONFIRMADA estado / PENDIENTE_RF_REAL desempeño | NO APLICA | Sí como límite central | 01,02,11,13,14,15 |
| E20 | Persistencia LecturaRFID y relación con SesionOperacion deben diseñarse antes de I3; componentes posteriores pendientes. | M v1.9.2 p.5 §9; R p.3 §4; I2 §14; AGENTS secuencia de incrementos | CONFIRMADA directriz | NO APLICA | Sí como trabajo futuro, no implementación | 06,14,15 |
| E21 | Reintento y contingencia explícita; fuera de ruta no se exige RFID artificial. RF-006 Should/Aprobado. | M v1.9.2 p.3 §4.1; Q p.5 §7 | CONFIRMADA diseño | NO APLICA | Sí como diseño pendiente; no introducir valor MANUAL en OrigenDatos | 05,06,08,15 notas |
| E22 | Geometría y modelo RF teórico no acreditan desempeño físico; P2 exige plano/fichas y prepruebas. | M v1.9.2 p.5 §9.1; Q pp.5–6 §7 | CONFIRMADA regla / PENDIENTE_RF_REAL desempeño | NO APLICA | Sí; no dibujar cobertura validada | 12,13,14 |
| E23 | ≥10 repeticiones por escenario prioritario, verdad de terreno y cero eventos duplicados; metas RF/tiempo después de prepruebas. | P pp.5–7; Q v1.9.2 p.3 VAL-003/004/005 | CONFIRMADA criterio | NO APLICA | Sí como criterios futuros, no resultados | 13,14 |
| E24 | Piloto busca ≥2 perfiles, ≥20 eventos, ≥3 sesiones; ajustes justificados antes de iniciar por restricciones. | P p.5; Q v1.9.2 p.3 VAL-006 Should/Aprobado | CONFIRMADA meta condicionada | NO APLICA | Sí con condición; no garantía ni ejecución | 14 |
| E25 | Java 21 LTS/Maven/JDBC/SQLite/Flyway/JUnit 5 es stack vigente. | R v1.9.2 p.2 DEC-022; `pom.xml` | CONFIRMADA | SOFTWARE | Sí; no Java17/Spring del checklist | 06,07,10 |
| E26 | Validación final técnica, operativa y económica; conclusión favorable, condicionada o no favorable según evidencia. | P pp.6,9 | CONFIRMADA metodología | NO APLICA | Sí; no anticipar resultado ni beneficio cuantificado | 14,15 |
| E27 | Hallazgos AM/remodelación aceptados como insumo P2 sin reabrir P0 ni crear DEC-024. | R v1.9.2 pp.3–4 DEC-023, §§5,8–9 | CONFIRMADA | NO APLICA | Sí; no elevar recomendaciones auxiliares a decisiones | 04,05 |

| E28 | La propuesta identifica una brecha entre movimientos físicos y registro oportuno; las entrevistas orientan el diagnóstico sin cuantificar su magnitud institucional. | P p.3, Problema a abordar; p.4, Información, lecturas y eventos | CONFIRMADA formulación de la propuesta / PRELIMINAR magnitud operativa | NO APLICA | Sí como problema delimitado; no afirmar tasa de pérdida, ahorro o incumplimiento institucional | 03,04,05 |

## Afirmaciones que no pueden presentarse como logros

«MVP completo», «piloto validado», «100% de lectura», «cero lecturas externas», «U300 compatible funcionalmente», «cobertura de todos los activos», «Farmacia autorizada para confirmar», «dos puntos piloto», «integración automática AM», «deduplicación/eventos implementados» y «RF-006 implementado»: evidencia insuficiente o contraria al control. Las 57 pruebas no sustentan ninguna de estas conclusiones.
