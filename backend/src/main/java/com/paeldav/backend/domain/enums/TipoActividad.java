package com.paeldav.backend.domain.enums;

/**
 * Enum que representa los tipos de actividades que pueden ser registradas en la auditoría.
 */
public enum TipoActividad {
    CREAR_USUARIO("Crear usuario"),
    EDITAR_USUARIO("Editar usuario"),
    DESACTIVAR_USUARIO("Desactivar usuario"),
    ACTIVAR_USUARIO("Activar usuario"),
    CAMBIAR_PASSWORD("Cambiar contraseña"),
    CREAR_VUELO("Crear vuelo"),
    EDITAR_VUELO("Editar vuelo"),
    CANCELAR_VUELO("Cancelar vuelo"),
    CREAR_MANTENIMIENTO("Crear mantenimiento"),
    COMPLETAR_MANTENIMIENTO("Completar mantenimiento"),
    CREAR_INCIDENCIA("Crear incidencia"),
    ASIGNAR_ROL("Asignar rol"),
    EXPORTAR_DATOS("Exportar datos"),
    OTRA("Otra actividad");

    private final String descripcion;

    TipoActividad(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
