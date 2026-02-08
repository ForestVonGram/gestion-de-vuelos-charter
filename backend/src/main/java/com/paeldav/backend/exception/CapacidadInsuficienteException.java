package com.paeldav.backend.exception;

/**
 * Excepción lanzada cuando una aeronave no tiene capacidad suficiente para pasajeros o tripulación.
 */
public class CapacidadInsuficienteException extends RuntimeException {

    public CapacidadInsuficienteException(String message) {
        super(message);
    }

    public CapacidadInsuficienteException(String message, Throwable cause) {
        super(message, cause);
    }
}
