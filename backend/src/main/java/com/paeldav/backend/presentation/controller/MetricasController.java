package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.metricas.MetricasDTO;
import com.paeldav.backend.application.dto.metricas.MetricasFlotaDTO;
import com.paeldav.backend.application.dto.metricas.MetricasPersonalDTO;
import com.paeldav.backend.application.dto.metricas.MetricasVuelosDTO;
import com.paeldav.backend.application.service.base.MetricasService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la visualización de métricas del sistema.
 * Proporciona endpoints para obtener estadísticas generales y específicas.
 * Solo accessible por usuarios con rol ADMINISTRADOR.
 */
@RestController
@RequestMapping("/api/metricas")
@RequiredArgsConstructor
public class MetricasController {

    private final MetricasService metricasService;

    /**
     * Obtiene todas las métricas del sistema de forma agregada.
     *
     * @return ResponseEntity con MetricasDTO que contiene todas las estadísticas
     */
    @GetMapping("/generales")
    public ResponseEntity<MetricasDTO> obtenerMetricasGenerales() {
        MetricasDTO metricas = metricasService.obtenerMetricasGenerales();
        return ResponseEntity.ok(metricas);
    }

    /**
     * Obtiene solo las métricas relacionadas con vuelos.
     *
     * @return ResponseEntity con MetricasVuelosDTO
     */
    @GetMapping("/vuelos")
    public ResponseEntity<MetricasVuelosDTO> obtenerMetricasVuelos() {
        MetricasVuelosDTO metricas = metricasService.obtenerMetricasVuelos();
        return ResponseEntity.ok(metricas);
    }

    /**
     * Obtiene solo las métricas relacionadas con la flota.
     *
     * @return ResponseEntity con MetricasFlotaDTO
     */
    @GetMapping("/flota")
    public ResponseEntity<MetricasFlotaDTO> obtenerMetricasFlota() {
        MetricasFlotaDTO metricas = metricasService.obtenerMetricasFlota();
        return ResponseEntity.ok(metricas);
    }

    /**
     * Obtiene solo las métricas relacionadas con el personal.
     *
     * @return ResponseEntity con MetricasPersonalDTO
     */
    @GetMapping("/personal")
    public ResponseEntity<MetricasPersonalDTO> obtenerMetricasPersonal() {
        MetricasPersonalDTO metricas = metricasService.obtenerMetricasPersonal();
        return ResponseEntity.ok(metricas);
    }

    /**
     * Obtiene el porcentaje de ocupación promedio.
     *
     * @return ResponseEntity con el valor del porcentaje
     */
    @GetMapping("/ocupacion")
    public ResponseEntity<Double> obtenerOcupacionPromedio() {
        Double ocupacion = metricasService.calcularOcupacionPromedio();
        return ResponseEntity.ok(ocupacion);
    }

    /**
     * Obtiene la rentabilidad promedio del sistema.
     *
     * @return ResponseEntity con el valor de rentabilidad
     */
    @GetMapping("/rentabilidad")
    public ResponseEntity<Double> obtenerRentabilidadPromedio() {
        Double rentabilidad = metricasService.calcularRentabilidadPromedio();
        return ResponseEntity.ok(rentabilidad);
    }

    /**
     * Obtiene la disponibilidad de la flota.
     *
     * @return ResponseEntity con el porcentaje de disponibilidad
     */
    @GetMapping("/disponibilidad-flota")
    public ResponseEntity<Double> obtenerDisponibilidadFlota() {
        Double disponibilidad = metricasService.calcularDisponibilidadFlota();
        return ResponseEntity.ok(disponibilidad);
    }

    /**
     * Obtiene el promedio de horas de vuelo por aeronave.
     *
     * @return ResponseEntity con el valor promedio de horas
     */
    @GetMapping("/promedio-horas-vuelo")
    public ResponseEntity<Double> obtenerPromedioHorasVuelo() {
        Double promedio = metricasService.calcularPromedioHorasVueloAeronave();
        return ResponseEntity.ok(promedio);
    }

    /**
     * Obtiene el total de ingresos del sistema.
     *
     * @return ResponseEntity con el valor de ingresos totales
     */
    @GetMapping("/ingresos-totales")
    public ResponseEntity<Double> obtenerIngresosTotales() {
        Double ingresos = metricasService.calcularTotalIngresos();
        return ResponseEntity.ok(ingresos);
    }

    /**
     * Obtiene el total de tripulantes disponibles.
     *
     * @return ResponseEntity con la cantidad de tripulantes disponibles
     */
    @GetMapping("/tripulantes-disponibles")
    public ResponseEntity<Long> obtenerTripulantesDisponibles() {
        Long tripulantes = metricasService.obtenerTripulantesDisponibles();
        return ResponseEntity.ok(tripulantes);
    }
}
