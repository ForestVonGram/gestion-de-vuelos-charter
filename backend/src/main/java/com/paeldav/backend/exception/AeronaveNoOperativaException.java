package com.paeldav.backend.exception;

/**
 * Excepción lanzada cuando una aeronave no es operativa debido a restricciones de mantenimiento.
 */
public class AeronaveNoOperativaException extends RuntimeException {

    public AeronaveNoOperativaException(String message) {
        super(message);
    }

    public AeronaveNoOperativaException(String message, Throwable cause) {
        super(message, cause);
    }
}
