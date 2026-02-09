package com.paeldav.backend.exception;

/**
 * Excepción lanzada cuando no se encuentra un personal.
 */
public class PersonalNoEncontradoException extends RuntimeException {

    public PersonalNoEncontradoException(String message) {
        super(message);
    }

    public PersonalNoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
