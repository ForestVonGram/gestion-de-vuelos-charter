package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.reporte.ReporteDTO;
import com.paeldav.backend.domain.entity.Aeronave;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Interfaz de servicio para reportes de uso de flota.
 * Define métodos para generar reportes sobre utilización y estado de aeronaves.
 */
public interface ReporteFlotaService {

    /**
     * Genera un reporte completo de uso de la flota en un rango de fechas.
     *
     * @param fechaInicio Fecha de inicio del período
     * @param fechaFin Fecha de fin del período
     * @param usuarioIdAutenticado ID del usuario que genera el reporte
     * @return ReporteDTO con estadísticas de flota
     */
    ReporteDTO generarReporteUsoFlota(LocalDateTime fechaInicio, LocalDateTime fechaFin, Long usuarioIdAutenticado);

    /**
     * Calcula estadísticas de utilización por aeronave.
     *
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Mapa de aeronaves con sus estadísticas
     */
    Map<String, Object> calcularEstadisticasPorAeronave(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Obtiene información de mantenimientos realizados en un período.
     *
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista con información de mantenimientos
     */
    List<Map<String, Object>> obtenerMantenimientosPorFlota(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Obtiene estadísticas de repostajes realizados en un período.
     *
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Mapa con estadísticas de combustible
     */
    Map<String, Object> obtenerEstadisticasCombustible(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Calcula el estado de disponibilidad de la flota.
     *
     * @return Mapa con información de disponibilidad por estado
     */
    Map<String, Object> calcularDisponibilidadFlota();

    /**
     * Obtiene horas de vuelo totales de una aeronave en un período.
     *
     * @param aeronaveId ID de la aeronave
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Total de horas de vuelo
     */
    Double calcularHorasVueloAeronave(Long aeronaveId, LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
