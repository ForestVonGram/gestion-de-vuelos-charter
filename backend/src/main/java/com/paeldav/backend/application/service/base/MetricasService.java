package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.metricas.MetricasDTO;
import com.paeldav.backend.application.dto.metricas.MetricasFlotaDTO;
import com.paeldav.backend.application.dto.metricas.MetricasPersonalDTO;
import com.paeldav.backend.application.dto.metricas.MetricasVuelosDTO;

/**
 * Interfaz de servicio para calcular y obtener métricas del sistema.
 * Proporciona métodos para obtener estadísticas sobre vuelos, flota y personal.
 */
public interface MetricasService {

    /**
     * Obtiene todas las métricas del sistema de forma agregada.
     *
     * @return MetricasDTO con todas las métricas
     */
    MetricasDTO obtenerMetricasGenerales();

    /**
     * Obtiene solo las métricas relacionadas con vuelos.
     *
     * @return MetricasVuelosDTO con estadísticas de vuelos
     */
    MetricasVuelosDTO obtenerMetricasVuelos();

    /**
     * Obtiene solo las métricas relacionadas con la flota de aeronaves.
     *
     * @return MetricasFlotaDTO con estadísticas de la flota
     */
    MetricasFlotaDTO obtenerMetricasFlota();

    /**
     * Obtiene solo las métricas relacionadas con el personal y tripulación.
     *
     * @return MetricasPersonalDTO con estadísticas de personal
     */
    MetricasPersonalDTO obtenerMetricasPersonal();

    /**
     * Calcula el porcentaje de ocupación promedio de los vuelos.
     *
     * @return porcentaje de ocupación (0-100)
     */
    Double calcularOcupacionPromedio();

    /**
     * Calcula la rentabilidad promedio del sistema.
     * Se basa en los ingresos vs gastos operativos.
     *
     * @return porcentaje de rentabilidad
     */
    Double calcularRentabilidadPromedio();

    /**
     * Calcula el porcentaje de disponibilidad de la flota.
     *
     * @return porcentaje de disponibilidad (0-100)
     */
    Double calcularDisponibilidadFlota();

    /**
     * Calcula el promedio de horas de vuelo por aeronave.
     *
     * @return promedio de horas
     */
    Double calcularPromedioHorasVueloAeronave();

    /**
     * Calcula el total de ingresos generados por vuelos completados.
     *
     * @return total de ingresos
     */
    Double calcularTotalIngresos();

    /**
     * Obtiene el total de tripulantes disponibles.
     *
     * @return cantidad de tripulantes disponibles
     */
    Long obtenerTripulantesDisponibles();
}
