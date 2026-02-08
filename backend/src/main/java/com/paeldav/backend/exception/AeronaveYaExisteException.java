package com.paeldav.backend.exception;

/**
 * Excepción lanzada cuando se intenta registrar una aeronave con una matrícula que ya existe.
 */
public class AeronaveYaExisteException extends RuntimeException {

    public AeronaveYaExisteException(String message) {
        super(message);
    }

    public AeronaveYaExisteException(String message, Throwable cause) {
        super(message, cause);
    }
}
