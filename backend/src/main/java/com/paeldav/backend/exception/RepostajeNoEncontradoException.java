package com.paeldav.backend.exception;

/**
 * Excepción lanzada cuando no se encuentra un repostaje.
 */
public class RepostajeNoEncontradoException extends RuntimeException {

    public RepostajeNoEncontradoException(String message) {
        super(message);
    }

    public RepostajeNoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
