package com.paeldav.backend.exception;

/**
 * Excepción lanzada cuando no se encuentra un vuelo en el sistema.
 */
public class VueloNoEncontradoException extends RuntimeException {

    public VueloNoEncontradoException(String message) {
        super(message);
    }

    public VueloNoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
