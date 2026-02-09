package com.paeldav.backend.exception;

/**
 * Excepción lanzada cuando no se encuentra un documento técnico.
 */
public class DocumentoTecnicoNoEncontradoException extends RuntimeException {

    public DocumentoTecnicoNoEncontradoException(String message) {
        super(message);
    }

    public DocumentoTecnicoNoEncontradoException(String message, Throwable cause) {
        super(message, cause);
    }
}
