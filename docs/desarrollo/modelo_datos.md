# Entidades I1

Implementadas como records Java inmutables sin JDBC, Flyway ni SDK en el dominio. Representan identidades ya creadas (ID positivo); los futuros casos de uso recibiran los datos de alta antes de asignar un ID.

| Entidad | Tabla | Identidad |
| --- | --- | --- |
| Equipo | equipo | equipo_id, codigo_institucional unico |
| EtiquetaRFID | etiqueta_rfid | etiqueta_id, epc unico |
| AsignacionEtiqueta | asignacion_etiqueta | asignacion_id y referencias a ambas identidades |

V2 crea el esquema en tablas STRICT. BOOL se representa con INTEGER 0/1; VARCHAR con TEXT. Las fechas se representan como Instant en Java e INTEGER de milisegundos UTC en SQLite. Al persistir se usara Instant.toEpochMilli; al recuperar, Instant.ofEpochMilli. No almacenar horas locales ni formatos temporales mixtos.

La asociacion vigente tiene fecha_fin nula. Dos indices unicos parciales limitan a una vigente por equipo y etiqueta. Los indices historicos permiten consultas ordenadas por fecha_inicio e ID. No se crea una tabla Historial. Las claves foraneas restringen eliminaciones y un trigger impide eliminar asociaciones, incluso cerradas.

La correccion futura debe obtener un mismo instante de cambio, cerrar la anterior, insertar la nueva y confirmar todo en una misma conexion/transaccion; cualquier fallo exige rollback. Las pruebas actuales demuestran estas capacidades directamente con JDBC, no constituyen todavia el caso de uso de correccion.

No se fija longitud EPC ni se normalizan mayusculas o ceros iniciales sin una regla acordada. La unicidad SQL actual distingue mayusculas. Tampoco se agrega una prohibicion de asociar equipos fuera del piloto o etiquetas INACTIVA: esas reglas no estaban aprobadas en el modelo inicial.

## Alcance comprobado

RF-001 / DAT-003: entidades y esquema, unicidad, claves foraneas, dominios, historial y rollback comprobados con SQLite real. Faltan repositorios de aplicacion, servicios y comandos de alta/asociacion/consulta/correccion para completar I1. No marcar RF-001 completo por disponer del esquema. Los documentos ACTUAL permanecen intactos.
