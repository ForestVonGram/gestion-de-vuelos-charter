package com.paeldav.backend.exception;

/**
 * Excepción lanzada cuando se intenta realizar una operación en una aeronave que no está disponible.
 */
public class AeronaveNoDisponibleException extends RuntimeException {

    public AeronaveNoDisponibleException(String message) {
        super(message);
    }

    public AeronaveNoDisponibleException(String message, Throwable cause) {
        super(message, cause);
    }
}
