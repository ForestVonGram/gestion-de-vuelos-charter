package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.metricas.MetricasDTO;
import com.paeldav.backend.application.dto.metricas.MetricasFlotaDTO;
import com.paeldav.backend.application.dto.metricas.MetricasPersonalDTO;
import com.paeldav.backend.application.dto.metricas.MetricasVuelosDTO;
import com.paeldav.backend.application.service.base.MetricasService;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import com.paeldav.backend.domain.enums.EstadoTripulante;
import com.paeldav.backend.domain.enums.EstadoVuelo;
import com.paeldav.backend.infraestructure.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;

/**
 * Implementación del servicio de Métricas.
 * Proporciona la lógica para calcular estadísticas generales del sistema.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MetricasServiceImpl implements MetricasService {

    // Repositorios necesarios para obtener datos del sistema
    private final VueloRepository vueloRepository;
    private final AeronaveRepository aeronaveRepository;
    private final TripulanteRepository tripulanteRepository;
    private final PersonalRepository personalRepository;
    private final PagoRepository pagoRepository;
    private final RegistroHorasVueloRepository registroHorasVueloRepository;

    /**
     * Obtiene todas las métricas generales del sistema.
     */
    @Override
    public MetricasDTO obtenerMetricasGenerales() {

        // Obtener métricas específicas por módulo
        MetricasVuelosDTO metricasVuelos = obtenerMetricasVuelos();
        MetricasFlotaDTO metricasFlota = obtenerMetricasFlota();
        MetricasPersonalDTO metricasPersonal = obtenerMetricasPersonal();

        // Construir objeto de métricas generales
        return MetricasDTO.builder()
                .fechaActualizacion(LocalDateTime.now())
                .metricasVuelos(metricasVuelos)
                .metricasFlota(metricasFlota)
                .metricasPersonal(metricasPersonal)
                .rentabilidadPromedio(calcularRentabilidadPromedio())
                .ocupacionPromedio(calcularOcupacionPromedio())
                .build();
    }

    /**
     * Calcula métricas relacionadas con los vuelos.
     */
    @Override
    public MetricasVuelosDTO obtenerMetricasVuelos() {

        // Contar vuelos según su estado
        long vuelosTotales = vueloRepository.count();
        long vuelosCompletados = vueloRepository.findByEstado(EstadoVuelo.COMPLETADO).size();
        long vuelosEnProceso = vueloRepository.findByEstado(EstadoVuelo.EN_CURSO).size();
        long vuelosCancelados = vueloRepository.findByEstado(EstadoVuelo.CANCELADO).size();
        long vuelosProgramados = vueloRepository.findByEstado(EstadoVuelo.CONFIRMADO).size();

        // Calcular porcentaje de vuelos completados
        double porcentajeComplecion = vuelosTotales > 0 ?
                (double) vuelosCompletados / vuelosTotales * 100 : 0.0;

        // Calcular ingresos totales generados por vuelos
        double ingresoTotalVuelos = calcularTotalIngresos();

        return MetricasVuelosDTO.builder()
                .vuelosTotales(vuelosTotales)
                .vuelosCompletados(vuelosCompletados)
                .vuelosEnProceso(vuelosEnProceso)
                .vuelosCancelados(vuelosCancelados)
                .vuelosProgramados(vuelosProgramados)
                .porcentajeComplecion(porcentajeComplecion)
                .ingresoTotalVuelos(ingresoTotalVuelos)
                .build();
    }

    /**
     * Calcula métricas relacionadas con la flota de aeronaves.
     */
    @Override
    public MetricasFlotaDTO obtenerMetricasFlota() {

        // Contar aeronaves según estado
        long aeronavesTotales = aeronaveRepository.count();
        long aeronavesActivas = aeronaveRepository.findByEstado(EstadoAeronave.DISPONIBLE).size();
        long aeronavesEnMantenimiento = aeronaveRepository.findByEstado(EstadoAeronave.EN_MANTENIMIENTO).size();
        long aeronavesDisponibles = aeronavesTotales - aeronavesEnMantenimiento;

        // Calcular porcentaje de disponibilidad de la flota
        double porcentajeDisponibilidad = aeronavesTotales > 0 ?
                (double) aeronavesDisponibles / aeronavesTotales * 100 : 0.0;

        // Calcular horas totales y promedio de vuelo
        double horasTotalVuelo = calcularHorasTotalVuelo();
        double horasPromedioPorAeronave = aeronavesTotales > 0 ?
                horasTotalVuelo / aeronavesTotales : 0.0;

        return MetricasFlotaDTO.builder()
                .aeronavesTotales(aeronavesTotales)
                .aeronavesActivas(aeronavesActivas)
                .aeronavesEnMantenimiento(aeronavesEnMantenimiento)
                .aeronavesDisponibles(aeronavesDisponibles)
                .porcentajeDisponibilidad(porcentajeDisponibilidad)
                .horasTotalVuelo(horasTotalVuelo)
                .horasPromedioPorAeronave(horasPromedioPorAeronave)
                .build();
    }

    /**
     * Calcula métricas relacionadas con el personal del sistema.
     */
    @Override
    public MetricasPersonalDTO obtenerMetricasPersonal() {

        // Personal general
        long personalTotal = personalRepository.count();
        long personalActivo = personalRepository.findAll().stream()
                .filter(p -> p.getUsuario() != null && p.getUsuario().getActivo())
                .count();

        // Personal que no está activo (licencias u otros estados)
        long personalEnLicencia = personalTotal - personalActivo;

        // Información específica de tripulación
        long tripulantesTotal = tripulanteRepository.count();
        long tripulantesDisponibles = tripulanteRepository.findByEstado(EstadoTripulante.DISPONIBLE).size();
        long tripulantesEnVuelo = tripulanteRepository.findByEstado(EstadoTripulante.EN_VUELO).size();

        // Horas trabajadas
        double horasTotalPersonal = calcularHorasTotalPersonal();
        double horasPromedioPersonal = personalTotal > 0 ?
                horasTotalPersonal / personalTotal : 0.0;

        return MetricasPersonalDTO.builder()
                .personalTotal(personalTotal)
                .personalActivo(personalActivo)
                .personalEnLicencia(personalEnLicencia)
                .tripulantesTotal(tripulantesTotal)
                .tripulantesDisponibles(tripulantesDisponibles)
                .tripulantesEnVuelo(tripulantesEnVuelo)
                .horasTotalPersonal(horasTotalPersonal)
                .horasPromedioPersonal(horasPromedioPersonal)
                .build();
    }

    /**
     * Calcula una estimación del porcentaje promedio de ocupación de vuelos.
     */
    @Override
    public Double calcularOcupacionPromedio() {

        long vuelosTotales = vueloRepository.count();

        if (vuelosTotales == 0) {
            return 0.0;
        }

        // Obtener número de vuelos completados
        long vuelosCompletados = vueloRepository.findByEstado(EstadoVuelo.COMPLETADO).size();

        if (vuelosCompletados == 0) {
            return 0.0;
        }

        // Estimación simple de ocupación
        return Math.min(100.0, (double) vuelosCompletados / vuelosTotales * 100);
    }

    /**
     * Calcula una estimación de rentabilidad basada en ingresos.
     */
    @Override
    public Double calcularRentabilidadPromedio() {

        double ingresos = calcularTotalIngresos();

        if (ingresos == 0) {
            return 0.0;
        }

        // Normalización básica para mostrar porcentaje
        return Math.min(100.0, ingresos / 100000.0 * 100);
    }

    /**
     * Calcula el porcentaje de disponibilidad de la flota.
     */
    @Override
    public Double calcularDisponibilidadFlota() {

        long aeronavesTotales = aeronaveRepository.count();

        if (aeronavesTotales == 0) {
            return 0.0;
        }

        long aeronavesEnMantenimiento = aeronaveRepository.findByEstado(EstadoAeronave.EN_MANTENIMIENTO).size();

        return (double) (aeronavesTotales - aeronavesEnMantenimiento) / aeronavesTotales * 100;
    }

    /**
     * Calcula el promedio de horas de vuelo por aeronave.
     */
    @Override
    public Double calcularPromedioHorasVueloAeronave() {

        long aeronavesTotales = aeronaveRepository.count();

        if (aeronavesTotales == 0) {
            return 0.0;
        }

        double horasTotales = calcularHorasTotalVuelo();

        return horasTotales / aeronavesTotales;
    }

    /**
     * Calcula el total de ingresos registrados en pagos.
     */
    @Override
    public Double calcularTotalIngresos() {

        return pagoRepository.findAll().stream()
                .mapToDouble(p -> p.getMonto() != null ? p.getMonto() : 0.0)
                .sum();
    }

    /**
     * Obtiene la cantidad de tripulantes disponibles actualmente.
     */
    @Override
    public Long obtenerTripulantesDisponibles() {

        return (long) tripulanteRepository.findByEstado(EstadoTripulante.DISPONIBLE).size();
    }

    /**
     * Calcula el total de horas de vuelo registradas en el sistema.
     */
    private Double calcularHorasTotalVuelo() {

        return registroHorasVueloRepository.findAll().stream()
                .mapToDouble(r -> r.getHorasVoladas() != null ? r.getHorasVoladas() : 0.0)
                .sum();
    }

    /**
     * Calcula el total de horas trabajadas por el personal.
     */
    private Double calcularHorasTotalPersonal() {

        return registroHorasVueloRepository.findAll().stream()
                .mapToDouble(r -> r.getHorasVoladas() != null ? r.getHorasVoladas() : 0.0)
                .sum();
    }
}