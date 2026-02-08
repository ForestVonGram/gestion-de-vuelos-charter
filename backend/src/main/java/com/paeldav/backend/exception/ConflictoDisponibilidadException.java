package com.paeldav.backend.exception;

import com.paeldav.backend.application.dto.disponibilidad.ResultadoValidacionDTO;
import lombok.Getter;

/**
 * Excepción lanzada cuando se detecta un conflicto de disponibilidad
 * (doble asignación de aeronave o tripulante).
 */
@Getter
public class ConflictoDisponibilidadException extends RuntimeException {

    private final ResultadoValidacionDTO resultadoValidacion;

    public ConflictoDisponibilidadException(String message) {
        super(message);
        this.resultadoValidacion = null;
    }

    public ConflictoDisponibilidadException(String message, ResultadoValidacionDTO resultadoValidacion) {
        super(message);
        this.resultadoValidacion = resultadoValidacion;
    }

    public ConflictoDisponibilidadException(String message, Throwable cause) {
        super(message, cause);
        this.resultadoValidacion = null;
    }
}
