package com.paeldav.backend.domain.enums;

/**
 * Estados posibles de un vuelo chárter.
 */
public enum EstadoVuelo {
    SOLICITADO,    // Vuelo solicitado por el usuario
    CONFIRMADO,    // Vuelo confirmado con aeronave y tripulación asignada
    EN_CURSO,      // Vuelo en progreso
    COMPLETADO,    // Vuelo finalizado exitosamente
    CANCELADO      // Vuelo cancelado
}
