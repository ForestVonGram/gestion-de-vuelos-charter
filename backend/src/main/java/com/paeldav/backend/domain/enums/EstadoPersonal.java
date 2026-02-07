package com.paeldav.backend.domain.enums;

/**
 * Estados del personal de mantenimiento y logística.
 */
public enum EstadoPersonal {
    ACTIVO,        // Disponible para asignaciones
    EN_PERMISO,    // Ausencia temporal autorizada
    INCAPACITADO,  // Incapacidad médica
    INACTIVO       // Ya no trabaja en la empresa
}
