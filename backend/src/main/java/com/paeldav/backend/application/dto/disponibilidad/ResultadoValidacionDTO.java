package com.paeldav.backend.application.dto.disponibilidad;

import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO que representa el resultado de una validación de disponibilidad.
 * Indica si todos los recursos están disponibles y lista los conflictos encontrados.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadoValidacionDTO {

    private boolean disponible;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    @Builder.Default
    private List<ConflictoAgendaDTO> conflictosAeronave = new ArrayList<>();

    @Builder.Default
    private List<ConflictoAgendaDTO> conflictosTripulacion = new ArrayList<>();

    private String resumen;

    /**
     * Verifica si hay algún conflicto.
     */
    public boolean tieneConflictos() {
        return !conflictosAeronave.isEmpty() || !conflictosTripulacion.isEmpty();
    }

    /**
     * Obtiene el total de conflictos detectados.
     */
    public int getTotalConflictos() {
        return conflictosAeronave.size() + conflictosTripulacion.size();
    }
}
