package com.paeldav.backend.application.dto.metricas;

import lombok.*;

/**
 * DTO que contiene métricas relacionadas con los vuelos.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricasVuelosDTO {
    private Long vuelosTotales;
    private Long vuelosCompletados;
    private Long vuelosEnProceso;
    private Long vuelosCancelados;
    private Long vuelosProgramados;
    private Double porcentajeComplecion;
    private Double ingresoTotalVuelos;
}
