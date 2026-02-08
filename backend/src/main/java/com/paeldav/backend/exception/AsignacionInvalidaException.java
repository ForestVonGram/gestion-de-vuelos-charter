package com.paeldav.backend.exception;

/**
 * Excepción lanzada cuando una asignación de recursos es inválida.
 * Por ejemplo: capacidad insuficiente, licencia vencida, recurso no disponible.
 */
public class AsignacionInvalidaException extends RuntimeException {

    public AsignacionInvalidaException(String message) {
        super(message);
    }

    public AsignacionInvalidaException(String message, Throwable cause) {
        super(message, cause);
    }
}
