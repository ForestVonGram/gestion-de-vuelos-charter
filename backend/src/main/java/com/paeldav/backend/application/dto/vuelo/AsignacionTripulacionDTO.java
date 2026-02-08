package com.paeldav.backend.application.dto.vuelo;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

/**
 * DTO para asignar tripulación a un vuelo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignacionTripulacionDTO {

    /**
     * Lista de IDs de tripulantes a asignar.
     */
    @NotEmpty(message = "Debe asignar al menos un tripulante")
    private List<Long> tripulanteIds;

    /**
     * Observaciones sobre la asignación (opcional).
     */
    private String observaciones;
}
