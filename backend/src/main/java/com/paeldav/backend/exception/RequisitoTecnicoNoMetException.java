package com.paeldav.backend.exception;

/**
 * Excepción lanzada cuando se detecta que no se cumplen los requisitos técnicos
 * o normativos para un tripulante.
 */
public class RequisitoTecnicoNoMetException extends RuntimeException {
    public RequisitoTecnicoNoMetException(String message) {
        super(message);
    }

    public RequisitoTecnicoNoMetException(String message, Throwable cause) {
        super(message, cause);
    }
}
