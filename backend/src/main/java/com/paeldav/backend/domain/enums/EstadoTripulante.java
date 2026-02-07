package com.paeldav.backend.domain.enums;

/**
 * Estados operativos de un tripulante.
 */
public enum EstadoTripulante {
    DISPONIBLE,   // Disponible para asignación
    EN_VUELO,     // Actualmente en operación
    DE_DESCANSO,  // En periodo de descanso obligatorio
    INACTIVO      // No disponible (licencia, incapacidad, etc.)
}
