package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.reporte.ReporteDTO;
import com.paeldav.backend.domain.entity.Vuelo;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Interfaz de servicio para reportes de horas trabajadas y registros de tripulación.
 * Define métodos para generar reportes sobre actividad laboral y validación de datos administrativos.
 */
public interface ReporteHorasService {

    /**
     * Genera un reporte completo de horas trabajadas en un rango de fechas.
     *
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @param usuarioIdAutenticado ID del usuario que genera el reporte
     * @return ReporteDTO con estadísticas de horas trabajadas
     */
    ReporteDTO generarReporteHorasTrabajadas(LocalDateTime fechaInicio, LocalDateTime fechaFin, Long usuarioIdAutenticado);

    /**
     * Calcula horas de vuelo totales por tripulante en un período.
     *
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Mapa de tripulantes con sus horas totales
     */
    Map<String, Object> calcularHorasPorTripulante(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Calcula horas de vuelo por función desempeñada.
     *
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Mapa con horas por función (piloto, copiloto, auxiliar, etc.)
     */
    Map<String, Object> calcularHorasPorFuncion(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Valida la consistencia de datos en registros de horas.
     * Detecta anomalías como horas pendientes de aprobación, registros inconsistentes, etc.
     *
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Mapa con resultados de validación y anomalías encontradas
     */
    Map<String, Object> validarConsistenciaDatos(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Valida la consistencia de un vuelo específico.
     *
     * @param vuelo Vuelo a validar
     * @return true si los datos son consistentes, false en caso contrario
     */
    boolean validarConsistenciaVuelo(Vuelo vuelo);

    /**
     * Obtiene registros de horas pendientes de aprobación.
     *
     * @return Lista con información de registros no aprobados
     */
    List<Map<String, Object>> obtenerRegistrosPendientesAprobacion();

    /**
     * Calcula estadísticas de tipos de vuelo realizados.
     *
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Mapa con estadísticas por tipo de vuelo (diurno, nocturno, IFR, etc.)
     */
    Map<String, Object> calcularEstadisticasTiposVuelo(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
