package com.paeldav.backend.application.dto.metricas;

import lombok.*;

/**
 * DTO que contiene métricas relacionadas con la flota de aeronaves.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricasFlotaDTO {
    private Long aeronavesTotales;
    private Long aeronavesActivas;
    private Long aeronavesEnMantenimiento;
    private Long aeronavesDisponibles;
    private Double porcentajeDisponibilidad;
    private Double horasTotalVuelo;
    private Double horasPromedioPorAeronave;
}
