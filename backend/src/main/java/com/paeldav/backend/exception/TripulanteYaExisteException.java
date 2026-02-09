package com.paeldav.backend.exception;

/**
 * Excepción lanzada cuando se intenta crear un tripulante con una licencia duplicada.
 */
public class TripulanteYaExisteException extends RuntimeException {
    public TripulanteYaExisteException(String message) {
        super(message);
    }

    public TripulanteYaExisteException(String message, Throwable cause) {
        super(message, cause);
    }
}
