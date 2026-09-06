# Entidades I1

Implementadas como records Java inmutables sin JDBC, Flyway ni SDK en el dominio. Representan identidades ya creadas (ID positivo); los casos de uso reciben los datos de alta antes de asignar un ID.

| Entidad | Tabla | Identidad |
| --- | --- | --- |
| Equipo | equipo | equipo_id, codigo_institucional unico |
| EtiquetaRFID | etiqueta_rfid | etiqueta_id, epc unico |
| AsignacionEtiqueta | asignacion_etiqueta | asignacion_id y referencias a ambas identidades |

V2 crea el esquema en tablas STRICT. BOOL se representa con INTEGER 0/1; VARCHAR con TEXT. Las fechas se representan como Instant en Java e INTEGER de milisegundos UTC en SQLite. Al persistir se usa Instant.toEpochMilli; al recuperar, Instant.ofEpochMilli. No almacenar horas locales ni formatos temporales mixtos.

La asociacion vigente tiene fecha_fin nula. Dos indices unicos parciales limitan a una vigente por equipo y etiqueta. Los indices historicos permiten consultas ordenadas por fecha_inicio e ID. No se crea una tabla Historial. Las claves foraneas restringen eliminaciones y un trigger impide eliminar asociaciones, incluso cerradas.

La correccion de ServicioI1 obtiene un mismo instante de cambio, cierra la anterior, inserta la nueva y confirma todo en una misma conexion/transaccion; cualquier fallo produce rollback. Las pruebas de servicio verifican la correccion completa y un fallo de insercion posterior al cierre.

No se fija longitud EPC ni se normalizan mayusculas o ceros iniciales sin una regla acordada. La unicidad SQL actual distingue mayusculas. Tampoco se agrega una prohibicion de asociar equipos fuera del piloto o etiquetas INACTIVA: esas reglas no estaban aprobadas en el modelo inicial.

## Alcance comprobado

RF-001 / DAT-003: entidades y esquema, unicidad, claves foraneas, dominios, historial y rollback comprobados con SQLite real. Los repositorios, servicios y comandos de alta/asociacion/consulta/correccion estan implementados y probados. Esta evidencia no modifica automaticamente estados de los documentos de control. Los documentos ACTUAL permanecen intactos.

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
