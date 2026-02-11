package com.paeldav.backend.application.dto.metricas;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO que agrupa todas las métricas del sistema.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricasDTO {
    private LocalDateTime fechaActualizacion;
    private MetricasVuelosDTO metricasVuelos;
    private MetricasFlotaDTO metricasFlota;
    private MetricasPersonalDTO metricasPersonal;
    private Double rentabilidadPromedio;
    private Double ocupacionPromedio;
}
