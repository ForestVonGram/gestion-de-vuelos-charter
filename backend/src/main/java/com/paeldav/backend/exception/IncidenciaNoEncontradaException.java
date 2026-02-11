package com.paeldav.backend.exception;

/**
 * Excepción lanzada cuando no se encuentra una incidencia.
 */
public class IncidenciaNoEncontradaException extends RuntimeException {

    public IncidenciaNoEncontradaException(String message) {
        super(message);
    }

    public IncidenciaNoEncontradaException(String message, Throwable cause) {
        super(message, cause);
    }
}
