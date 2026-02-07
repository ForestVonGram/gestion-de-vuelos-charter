package com.paeldav.backend.exception;

/**
 * Excepción lanzada cuando no se encuentra un usuario en el sistema.
 */
public class UsuarioNoEncontradoException extends RuntimeException {

    public UsuarioNoEncontradoException(String message) {
        super(message);
    }

    public UsuarioNoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
