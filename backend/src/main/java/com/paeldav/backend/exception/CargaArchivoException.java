package com.paeldav.backend.exception;

/**
 * Excepción lanzada cuando falla la carga de un archivo.
 */
public class CargaArchivoException extends RuntimeException {

    public CargaArchivoException(String message) {
        super(message);
    }

    public CargaArchivoException(String message, Throwable cause) {
        super(message, cause);
    }
}
