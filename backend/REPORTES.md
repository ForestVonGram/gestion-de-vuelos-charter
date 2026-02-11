# Sistema de Reportes Operativos - Charter Flight Management System

## Descripción General

Se ha implementado un sistema completo de generación de reportes operativos para el sistema de gestión de vuelos chárter. El sistema incluye tres módulos principales:

1. **Reportes Operativos Generales**
2. **Reportes de Uso de Flota**
3. **Reportes de Horas Trabajadas**

## Estructura de Implementación

### Entidades

- **Reporte** (`domain/entity/Reporte.java`): Entidad principal que almacena información de todos los reportes generados
- **TipoReporte** (`domain/enums/TipoReporte.java`): Enum con los tipos: OPERATIVO, FLOTA, HORAS

### DTOs

- **ReporteDTO**: Para lectura de reportes
- **ReporteCreateDTO**: Para generación de nuevos reportes
- **ReporteFiltroDTO**: Para filtrado en consultas

### Servicios

#### 1. ReporteGeneralService
Interfaz y implementación en:
- `application/service/base/ReporteGeneralService.java`
- `application/service/impl/ReporteGeneralServiceImpl.java`

**Métodos principales:**
- `generarReporteOperativo()`: Genera nuevos reportes
- `obtenerReportePorId()`: Obtiene un reporte específico
- `listarReportes()`: Lista reportes con filtros
- `eliminarReporte()`: Elimina un reporte
- `validarRangoFechas()`: Valida rangos de fechas

#### 2. ReporteFlotaService
Interfaz y implementación en:
- `application/service/base/ReporteFlotaService.java`
- `application/service/impl/ReporteFlotaServiceImpl.java`

**Métodos principales:**
- `generarReporteUsoFlota()`: Genera reporte de uso de flota
- `calcularEstadisticasPorAeronave()`: Estadísticas de utilización por aeronave
- `obtenerMantenimientosPorFlota()`: Información de mantenimientos
- `obtenerEstadisticasCombustible()`: Estadísticas de combustible y costos
- `calcularDisponibilidadFlota()`: Estado de disponibilidad actual
- `calcularHorasVueloAeronave()`: Horas de vuelo por aeronave

#### 3. ReporteHorasService
Interfaz y implementación en:
- `application/service/base/ReporteHorasService.java`
- `application/service/impl/ReporteHorasServiceImpl.java`

**Métodos principales:**
- `generarReporteHorasTrabajadas()`: Genera reporte de horas trabajadas
- `calcularHorasPorTripulante()`: Horas agrupadas por tripulante
- `calcularHorasPorFuncion()`: Horas agrupadas por función desempeñada
- `validarConsistenciaDatos()`: **Valida inconsistencias en registros de horas**
- `validarConsistenciaVuelo()`: **Valida consistencia de datos en vuelos**
- `obtenerRegistrosPendientesAprobacion()`: Registros sin aprobar
- `calcularEstadisticasTiposVuelo()`: Estadísticas por tipo de vuelo

### Controlador REST

**ReporteController** en `presentation/controller/ReporteController.java`

**Endpoints principales:**

```
POST   /api/reportes/generar                    - Generar reporte operativo
GET    /api/reportes                            - Listar reportes
GET    /api/reportes/{id}                       - Obtener reporte específico
DELETE /api/reportes/{id}                       - Eliminar reporte
GET    /api/reportes/todos                      - Obtener todos los reportes

POST   /api/reportes/flota/generar              - Generar reporte de flota
GET    /api/reportes/flota/resumen              - Resumen de uso de flota
GET    /api/reportes/flota/combustible          - Estadísticas de combustible
GET    /api/reportes/flota/disponibilidad       - Disponibilidad actual

POST   /api/reportes/horas/generar              - Generar reporte de horas
GET    /api/reportes/horas/resumen              - Resumen de horas trabajadas
GET    /api/reportes/horas/por-funcion          - Horas por función
GET    /api/reportes/horas/validar              - Validar consistencia
GET    /api/reportes/horas/pendientes           - Registros pendientes
GET    /api/reportes/horas/tipos-vuelo          - Estadísticas de tipos de vuelo
```

## Validación de Consistencia de Datos Administrativos

### Detección de Anomalías

El sistema valida automáticamente:

1. **Horas negativas o cero**: Detecta registros con horas inválidas
2. **Registros sin aprobación**: Identifica registros pendientes de validación
3. **Inconsistencia con vuelos**: Detecta discrepancias entre datos de vuelos y registros de horas
4. **Fechas invertidas**: Verifica que las fechas de salida sean anteriores a las de llegada
5. **Fechas reales inconsistentes**: Valida que fechas reales sean posteriores a programadas

### Métodos de Validación

```java
// Validación completa en un rango de fechas
Map<String, Object> validarConsistenciaDatos(LocalDateTime inicio, LocalDateTime fin)

// Validación individual de un vuelo
boolean validarConsistenciaVuelo(Vuelo vuelo)
```

## Pruebas

Todas las pruebas unitarias se encuentran en `src/test/java/com/paeldav/backend/reportes/`

### Test Classes

1. **ReporteGeneralServiceTest**
   - Generación exitosa de reportes
   - Validación de rangos de fechas
   - Manejo de usuarios no encontrados
   - Filtrado y eliminación de reportes

2. **ReporteFlotaServiceTest**
   - Generación de reportes de flota
   - Cálculo de estadísticas por aeronave
   - Estadísticas de combustible
   - Cálculo de disponibilidad

3. **ReporteHorasServiceTest** ⭐ (Énfasis en validación)
   - **Validación de consistencia con datos válidos**
   - **Detección de horas negativas**
   - **Detección de registros pendientes**
   - **Validación de consistencia de vuelos**
   - **Validación de fechas invertidas**
   - **Validación de fechas reales inconsistentes**
   - Cálculo de horas por tripulante
   - Cálculo de horas por función

4. **ReporteControllerTest**
   - Endpoints REST
   - Códigos de respuesta HTTP
   - Datos retornados correctos

### Ejecución de Tests

```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar tests específicos del módulo de reportes
./gradlew test --tests "*Reporte*"

# Ejecutar tests con salida detallada
./gradlew test --tests "*ReporteHorasService*" -i
```

## Ejemplos de Uso

### Generar Reporte Operativo

```bash
POST /api/reportes/generar
Content-Type: application/json

{
  "tipo": "OPERATIVO",
  "fechaInicioRango": "2026-01-01T00:00:00",
  "fechaFinRango": "2026-02-01T00:00:00",
  "descripcion": "Reporte operativo enero",
  "observaciones": "Análisis de operaciones del mes"
}
```

### Generar Reporte de Flota

```bash
POST /api/reportes/flota/generar?fechaInicio=2026-01-01T00:00:00&fechaFin=2026-02-01T00:00:00
```

### Validar Consistencia de Datos

```bash
GET /api/reportes/horas/validar?fechaInicio=2026-01-01T00:00:00&fechaFin=2026-02-01T00:00:00
```

Respuesta:
```json
{
  "esValido": true,
  "anomalias": [],
  "totalAnomalias": 0,
  "registrosValidados": 45
}
```

## Repositorios Actualizados

Se han agregado métodos a los siguientes repositorios:

- **RegistroHorasVueloRepository**: `findByFechaRegistroBetween()`, `findByAprobadoFalse()`
- **VueloRepository**: `findEstadisticasPorAeronave()`, `findByAeronaveIdAndFechaSalidaRealBetween()`
- **RepostajeRepository**: `findByFechaRepostajeBetween()`
- **AeronaveRepository**: `countByEstado()`

## Notas Técnicas

- Los reportes utilizan **MapStruct** para mapping de entidades a DTOs
- Implementación de **transacciones** para garantizar consistencia
- Uso de **logging** para auditoría de operaciones
- Soporte para **paginación** en listados de reportes
- Validación de **seguridad** basada en autenticación de usuarios

## Estado de Compilación

✅ **BUILD SUCCESSFUL**
- 380 tests completados
- 0 errores de compilación
- Todas las advertencias corregidas

## Pendientes para Producción

1. Implementar exportación de reportes a PDF/Excel
2. Agregar esquemas de base de datos en migraciones Flyway
3. Configurar almacenamiento de archivos en Cloudinary
4. Implementar caché para reportes frecuentes
5. Añadir notificaciones por email cuando se complete un reporte
