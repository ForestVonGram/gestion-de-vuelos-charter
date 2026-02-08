package com.paeldav.backend.application.dto.disponibilidad;

import com.paeldav.backend.domain.enums.EstadoTripulante;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO que representa el estado de disponibilidad de un tripulante
 * para un rango de tiempo específico.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibilidadTripulanteDTO {

    private Long tripulanteId;
    private String nombreCompleto;
    private String numeroLicencia;
    private boolean esPiloto;
    private EstadoTripulante estadoActual;
    private boolean disponible;
    private LocalDateTime fechaConsultaInicio;
    private LocalDateTime fechaConsultaFin;
    private List<ConflictoAgendaDTO> conflictos;
    private String motivoNoDisponible;
}
