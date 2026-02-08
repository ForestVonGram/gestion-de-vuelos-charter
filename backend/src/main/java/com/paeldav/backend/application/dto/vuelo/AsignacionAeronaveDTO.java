package com.paeldav.backend.application.dto.vuelo;

import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO para asignar una aeronave a un vuelo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsignacionAeronaveDTO {

    /**
     * ID de la aeronave a asignar.
     */
    @NotNull(message = "El ID de la aeronave es obligatorio")
    private Long aeronaveId;

    /**
     * Observaciones sobre la asignación (opcional).
     */
    private String observaciones;
}
