package com.paeldav.backend.exception;

/**
 * Excepción lanzada cuando se intenta crear un usuario con un email que ya existe en el sistema.
 */
public class UsuarioYaExisteException extends RuntimeException {

    public UsuarioYaExisteException(String message) {
        super(message);
    }

    public UsuarioYaExisteException(String message, Throwable cause) {
        super(message, cause);
    }
}
