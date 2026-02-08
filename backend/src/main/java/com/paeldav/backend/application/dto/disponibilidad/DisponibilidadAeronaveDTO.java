package com.paeldav.backend.application.dto.disponibilidad;

import com.paeldav.backend.domain.enums.EstadoAeronave;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO que representa el estado de disponibilidad de una aeronave
 * para un rango de tiempo específico.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DisponibilidadAeronaveDTO {

    private Long aeronaveId;
    private String matricula;
    private String modelo;
    private EstadoAeronave estadoActual;
    private boolean disponible;
    private LocalDateTime fechaConsultaInicio;
    private LocalDateTime fechaConsultaFin;
    private List<ConflictoAgendaDTO> conflictos;
    private String motivoNoDisponible;
}
