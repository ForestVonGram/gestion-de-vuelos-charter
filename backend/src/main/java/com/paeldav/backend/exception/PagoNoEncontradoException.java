package com.paeldav.backend.exception;

/**
 * Excepción lanzada cuando se intenta buscar un pago que no existe en la base de datos.
 */
public class PagoNoEncontradoException extends RuntimeException {

    public PagoNoEncontradoException(String message) {
        super(message);
    }

    public PagoNoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
