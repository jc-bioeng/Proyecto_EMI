# Casos de uso I1

Implementacion sobre V1/V2 existentes, conforme al modelo congelado, RF-001 y DEC-022. La implementación no modificó documentos ACTUAL. AGENTS.md y FUENTES_DE_VERDAD.md se sincronizaron posteriormente mediante autorización documental explícita del usuario. Esta evidencia es de software/persistencia, no SIMULACION RFID ni RF_REAL. No cierra P1 ni autoriza incrementos posteriores.

## Responsabilidades

- ServicioI1 implementa los siete casos de uso, valida datos y conflictos, y obtiene los instantes con Clock inyectable.
- RepositorioI1 agrupa once operaciones concretas de datos del pequeno modelo. No hay repositorios genericos ni una interfaz por entidad.
- UnidadDeTrabajo ejecuta cada caso de uso con un repositorio ligado a una sola conexion. JdbcUnidadDeTrabajo abre la transaccion, confirma solo al terminar y revierte ante excepciones SQL, de aplicacion o errores; conserva errores de rollback como excepciones suprimidas.
- JdbcRepositorioI1 contiene SQL parametrizado y mapeo, sin politica de asociacion. No ofrece DELETE, UPDATE de identidades ni edicion arbitraria de asociaciones cerradas.
- ConsolaEmi adapta argumentos/resultados. EmiApplication ensambla los componentes y migra antes de aceptar operaciones.

Para este MVP local, cada operacion, incluidas las consultas, usa una transaccion IMMEDIATE corta con espera maxima de bloqueo de 5 segundos, ya configurada en BaseDatos. Esto serializa las transacciones y permite que validacion/escritura sean consistentes frente a otro escritor. Es una decision tecnica de simplicidad, no un diseno para lectores concurrentes de alto caudal. No se reintenta automaticamente una escritura cuyo resultado pudiera ser incierto.

## Contratos y convenciones

| Caso | Metodo de ServicioI1 | Resultado |
| --- | --- | --- |
| Crear equipo | crearEquipo | Equipo con ID generado |
| Crear etiqueta | crearEtiqueta | EtiquetaRFID con ID generado |
| Asociar | asociar | Nueva AsignacionEtiqueta |
| Consultar vigente | consultarVigente | Optional de asociacion |
| Buscar equipo por EPC | buscarEquipoPorEpc | Optional de Equipo |
| Corregir | corregir | Nueva asociacion, anterior cerrada |
| Consultar historial | historial | Lista inmutable, orden ascendente por fecha_inicio e ID |

Equipo inexistente genera NO_ENCONTRADO; equipo existente sin asociaciones devuelve ausencia/lista vacia. EPC desconocido o no asociado devuelve ausencia. Datos obligatorios invalidos generan DATO_INVALIDO, duplicados/asociaciones ocupadas CONFLICTO y fallos de acceso PERSISTENCIA. Los mensajes de consola no muestran SQL ni trazas internas.

La correccion consulta la vigente, verifica nueva etiqueta y conflictos, lee Clock una sola vez y convierte a precision de milisegundos UTC. Usa ese mismo instante como fin anterior e inicio nuevo; el motivo opcional queda en la asociacion cerrada. Si el reloj retrocede antes del inicio, falla sin modificar datos para respetar la integridad temporal existente. Un cambio a la misma etiqueta se rechaza porque ya tiene una asociacion vigente; no crea historial artificial. No se exige motivo obligatorio ni se normaliza EPC/codigo; se conserva el comportamiento de unicidad existente, incluidos ceros iniciales y distincion de mayusculas.

## Propuestas funcionales pendientes

No se encontró una prohibición aprobada para asociar etiquetas INACTIVA. La revisión inicial consultó Maestro/Registro v1.8.1 y Matriz v1.8 con su complemento; la matriz completa v1.8.1 ACTUAL, vigente ahora, tampoco incorpora esa prohibición. Por tanto, el servicio permite asociarlas y usarlas en una correccion, y una prueba fija explicitamente este comportamiento. Propuesta pendiente: prohibir nuevas asociaciones con etiquetas INACTIVA si el responsable la aprueba. No se implementa esa prohibicion.

La normalizacion de EPC/codigo y la eventual obligatoriedad del motivo tampoco se introducen. Las convenciones de respuestas vacias y de conflicto para la misma etiqueta se explicitan arriba para revision.

## Referencias vigentes y antecedente documental

Matriz vigente: `docs/control_local/Matriz_Requisitos_Proyecto_EMI_v1.8.1_ACTUAL.pdf`, completa. Maestro y Registro vigentes: v1.8.1 ACTUAL. Los tres históricos se encuentran en `docs/control_local/historico/` con nombres terminados en `_v1.8.pdf`, sin ACTUAL. La adenda DEC-022 se conserva como antecedente de la consolidación.

Incidencia anterior resuelta: durante la implementación la matriz v1.8 se consultó en la carpeta `1.8/` junto al complemento v1.8.1. El usuario reorganizó las fuentes y renombró la matriz completa v1.8.1; se verificaron las rutas actuales. La carpeta anterior y el complemento reemplazado no se usan como referencias vigentes.

Los documentos ACTUAL registran la evidencia anterior de ocho pruebas; el repositorio dispone ahora de 32. Esta sincronización conserva ese antecedente. Los estados actuales del repositorio se registran abajo por solicitud explícita del usuario; los documentos ACTUAL permanecen intactos y el cierre formal de I1 sigue pendiente.

## Evidencia reproducible

Comando: .\mvnw.cmd clean verify con JAVA_HOME apuntando a JDK 21.

Resultado: 32 pruebas, 0 fallos, 0 errores, 0 omitidas. Se conservan las 8 pruebas previas; se agregan 22 pruebas de ServicioI1 y 2 de consola con SQLite real temporal, usando las migraciones de produccion.

La prueba falloRealAlInsertarDespuesDelCierreRevierteTodo instala un trigger unicamente en la base temporal de prueba: falla al insertar si la asociacion anterior ya fue cerrada dentro de esa transaccion. Comprueba la causa y luego verifica la asociacion anterior completa, motivo/fin originales, historial y busquedas por ambos EPC. No altera V1/V2 ni usa un mock para demostrar rollback.

La prueba de concurrencia inicia dos solicitudes con conexiones independientes para una etiqueta: una confirma y la otra informa conflicto. Se verifica que solo queda una asociacion. El reloj contado demuestra una sola lectura temporal para corregir. Tambien se prueban reapertura, orden estable con empates, referencias inexistentes, comillas como datos y codigo/EPC obligatorios y unicos.

La entrada por JAR conserva el modo de inicializacion anterior sin comandos. Codigos de salida: 0 exito, 1 error del caso de uso, 2 argumentos invalidos.

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
