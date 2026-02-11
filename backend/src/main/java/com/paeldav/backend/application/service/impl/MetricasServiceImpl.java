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
 * Proporciona la lógica para calcular estadísticas del sistema.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MetricasServiceImpl implements MetricasService {

    private final VueloRepository vueloRepository;
    private final AeronaveRepository aeronaveRepository;
    private final TripulanteRepository tripulanteRepository;
    private final PersonalRepository personalRepository;
    private final PagoRepository pagoRepository;
    private final RegistroHorasVueloRepository registroHorasVueloRepository;

    @Override
    public MetricasDTO obtenerMetricasGenerales() {
        MetricasVuelosDTO metricasVuelos = obtenerMetricasVuelos();
        MetricasFlotaDTO metricasFlota = obtenerMetricasFlota();
        MetricasPersonalDTO metricasPersonal = obtenerMetricasPersonal();

        return MetricasDTO.builder()
                .fechaActualizacion(LocalDateTime.now())
                .metricasVuelos(metricasVuelos)
                .metricasFlota(metricasFlota)
                .metricasPersonal(metricasPersonal)
                .rentabilidadPromedio(calcularRentabilidadPromedio())
                .ocupacionPromedio(calcularOcupacionPromedio())
                .build();
    }

    @Override
    public MetricasVuelosDTO obtenerMetricasVuelos() {
        long vuelosTotales = vueloRepository.count();
        long vuelosCompletados = vueloRepository.findByEstado(EstadoVuelo.COMPLETADO).size();
        long vuelosEnProceso = vueloRepository.findByEstado(EstadoVuelo.EN_CURSO).size();
        long vuelosCancelados = vueloRepository.findByEstado(EstadoVuelo.CANCELADO).size();
        long vuelosProgramados = vueloRepository.findByEstado(EstadoVuelo.CONFIRMADO).size();

        double porcentajeComplecion = vuelosTotales > 0 ? 
                (double) vuelosCompletados / vuelosTotales * 100 : 0.0;
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

    @Override
    public MetricasFlotaDTO obtenerMetricasFlota() {
        long aeronavesTotales = aeronaveRepository.count();
        long aeronavesActivas = aeronaveRepository.findByEstado(EstadoAeronave.DISPONIBLE).size();
        long aeronavesEnMantenimiento = aeronaveRepository.findByEstado(EstadoAeronave.EN_MANTENIMIENTO).size();
        long aeronavesDisponibles = aeronavesTotales - aeronavesEnMantenimiento;

        double porcentajeDisponibilidad = aeronavesTotales > 0 ? 
                (double) aeronavesDisponibles / aeronavesTotales * 100 : 0.0;
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

    @Override
    public MetricasPersonalDTO obtenerMetricasPersonal() {
        long personalTotal = personalRepository.count();
        long personalActivo = personalRepository.findAll().stream()
                .filter(p -> p.getUsuario() != null && p.getUsuario().getActivo())
                .count();
        long personalEnLicencia = personalTotal - personalActivo;

        long tripulantesTotal = tripulanteRepository.count();
        long tripulantesDisponibles = tripulanteRepository.findByEstado(EstadoTripulante.DISPONIBLE).size();
        long tripulantesEnVuelo = tripulanteRepository.findByEstado(EstadoTripulante.EN_VUELO).size();

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

    @Override
    public Double calcularOcupacionPromedio() {
        long vuelosTotales = vueloRepository.count();
        if (vuelosTotales == 0) {
            return 0.0;
        }
        // Obtener suma de pasajeros en vuelos completados
        long vuelosCompletados = vueloRepository.findByEstado(EstadoVuelo.COMPLETADO).size();
        if (vuelosCompletados == 0) {
            return 0.0;
        }
        // Para una estimación simple, asumimos un promedio de capacidad
        return Math.min(100.0, (double) vuelosCompletados / vuelosTotales * 100);
    }

    @Override
    public Double calcularRentabilidadPromedio() {
        double ingresos = calcularTotalIngresos();
        // Para esta implementación básica, asumimos rentabilidad basada en ingresos
        // En una versión más compleja, se restaría los costos operativos
        if (ingresos == 0) {
            return 0.0;
        }
        return Math.min(100.0, ingresos / 100000.0 * 100); // Normalizar a un valor de referencia
    }

    @Override
    public Double calcularDisponibilidadFlota() {
        long aeronavesTotales = aeronaveRepository.count();
        if (aeronavesTotales == 0) {
            return 0.0;
        }
        long aeronavesEnMantenimiento = aeronaveRepository.findByEstado(EstadoAeronave.EN_MANTENIMIENTO).size();
        return (double) (aeronavesTotales - aeronavesEnMantenimiento) / aeronavesTotales * 100;
    }

    @Override
    public Double calcularPromedioHorasVueloAeronave() {
        long aeronavesTotales = aeronaveRepository.count();
        if (aeronavesTotales == 0) {
            return 0.0;
        }
        double horasTotales = calcularHorasTotalVuelo();
        return horasTotales / aeronavesTotales;
    }

    @Override
    public Double calcularTotalIngresos() {
        return pagoRepository.findAll().stream()
                .mapToDouble(p -> p.getMonto() != null ? p.getMonto() : 0.0)
                .sum();
    }

    @Override
    public Long obtenerTripulantesDisponibles() {
        return (long) tripulanteRepository.findByEstado(EstadoTripulante.DISPONIBLE).size();
    }

    /**
     * Calcula el total de horas de vuelo del sistema.
     */
    private Double calcularHorasTotalVuelo() {
        return registroHorasVueloRepository.findAll().stream()
                .mapToDouble(r -> r.getHorasVoladas() != null ? r.getHorasVoladas() : 0.0)
                .sum();
    }

    /**
     * Calcula el total de horas de personal registradas.
     */
    private Double calcularHorasTotalPersonal() {
        return registroHorasVueloRepository.findAll().stream()
                .mapToDouble(r -> r.getHorasVoladas() != null ? r.getHorasVoladas() : 0.0)
                .sum();
    }
}
