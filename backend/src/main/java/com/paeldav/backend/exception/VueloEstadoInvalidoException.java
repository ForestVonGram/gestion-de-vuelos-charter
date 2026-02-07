package com.paeldav.backend.exception;

/**
 * Excepción lanzada cuando se intenta una transición de estado inválida en un vuelo.
 */
public class VueloEstadoInvalidoException extends RuntimeException {

    public VueloEstadoInvalidoException(String message) {
        super(message);
    }

    public VueloEstadoInvalidoException(String message, Throwable cause) {
        super(message, cause);
    }
}
