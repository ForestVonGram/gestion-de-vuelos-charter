package com.paeldav.backend.application.dto.metricas;

import lombok.*;

/**
 * DTO que contiene métricas relacionadas con el personal.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MetricasPersonalDTO {
    private Long personalTotal;
    private Long personalActivo;
    private Long personalEnLicencia;
    private Long tripulantesTotal;
    private Long tripulantesDisponibles;
    private Long tripulantesEnVuelo;
    private Double horasTotalPersonal;
    private Double horasPromedioPersonal;
}
