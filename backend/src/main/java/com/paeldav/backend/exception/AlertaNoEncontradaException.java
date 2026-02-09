package com.paeldav.backend.exception;

/**
 * Excepción lanzada cuando no se encuentra una alerta.
 */
public class AlertaNoEncontradaException extends RuntimeException {

    public AlertaNoEncontradaException(String message) {
        super(message);
    }

    public AlertaNoEncontradaException(String message, Throwable cause) {
        super(message, cause);
    }
}
