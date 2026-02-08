package com.paeldav.backend.exception;

/**
 * Excepción lanzada cuando no se encuentra un tripulante.
 */
public class TripulanteNoEncontradoException extends RuntimeException {

    public TripulanteNoEncontradoException(String message) {
        super(message);
    }

    public TripulanteNoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
