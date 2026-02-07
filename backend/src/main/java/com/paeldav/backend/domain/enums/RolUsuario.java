package com.paeldav.backend.domain.enums;

/**
 * Roles de usuario en el sistema de gestión de vuelos chárter.
 */
public enum RolUsuario {
    USUARIO,              // Usuario final que solicita vuelos
    ADMINISTRADOR,        // Supervisor global del sistema
    OPERADOR_LOGISTICA,   // Gestiona información técnica de aeronaves
    AYUDANTE_MANTENIMIENTO, // Registra mantenimientos y repostajes
    TRIPULACION           // Pilotos y tripulación
}
