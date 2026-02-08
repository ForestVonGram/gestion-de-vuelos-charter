package com.paeldav.backend.exception;

/**
 * Excepción lanzada cuando no se encuentra una aeronave.
 */
public class AeronaveNoEncontradaException extends RuntimeException {

    public AeronaveNoEncontradaException(String message) {
        super(message);
    }

    public AeronaveNoEncontradaException(String message, Throwable cause) {
        super(message, cause);
    }
}
