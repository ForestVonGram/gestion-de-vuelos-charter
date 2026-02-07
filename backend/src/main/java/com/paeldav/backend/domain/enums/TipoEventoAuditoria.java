package com.paeldav.backend.domain.enums;

/**
 * Enum que representa los tipos de eventos de auditoría de acceso al sistema.
 */
public enum TipoEventoAuditoria {
    LOGIN("Inicio de sesión"),
    LOGOUT("Cierre de sesión"),
    ACCESO_DENEGADO("Acceso denegado"),
    TOKEN_EXPIRADO("Token expirado"),
    CREDENCIALES_INVALIDAS("Credenciales inválidas"),
    PERMISO_INSUFICIENTE("Permiso insuficiente"),
    ACCION_COMPLETADA("Acción completada");

    private final String descripcion;

    TipoEventoAuditoria(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
