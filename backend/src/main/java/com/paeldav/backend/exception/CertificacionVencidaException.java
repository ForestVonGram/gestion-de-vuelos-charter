package com.paeldav.backend.exception;

/**
 * Excepción lanzada cuando se detecta que una certificación de tripulación ha vencido.
 */
public class CertificacionVencidaException extends RuntimeException {
    public CertificacionVencidaException(String message) {
        super(message);
    }

    public CertificacionVencidaException(String message, Throwable cause) {
        super(message, cause);
    }
}
