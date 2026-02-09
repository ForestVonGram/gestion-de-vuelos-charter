package com.paeldav.backend.domain.enums;

/**
 * Tipos de alertas que pueden generarse en el sistema.
 */
public enum TipoAlerta {
    MANTENIMIENTO_PENDIENTE,  // Mantenimiento registrado pero no completado
    MANTENIMIENTO_VENCIDO,    // Mantenimiento cuya fecha de inicio ha pasado sin completarse
    MANTENIMIENTO_PROXIMO     // Alerta anticipada de mantenimiento próximo a vencer
}
