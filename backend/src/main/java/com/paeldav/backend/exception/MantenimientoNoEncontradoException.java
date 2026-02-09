package com.paeldav.backend.exception;

/**
 * Excepción lanzada cuando no se encuentra un mantenimiento.
 */
public class MantenimientoNoEncontradoException extends RuntimeException {

    public MantenimientoNoEncontradoException(String message) {
        super(message);
    }

    public MantenimientoNoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
