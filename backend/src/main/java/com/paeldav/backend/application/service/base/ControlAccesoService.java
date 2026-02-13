package com.paeldav.backend.application.service.base;

import com.paeldav.backend.domain.enums.RolUsuario;

/**
 * Servicio para validar y controlar el acceso a recursos basándose en roles de usuario.
 * Define políticas de autorización y validación de permisos.
 */
public interface ControlAccesoService {

    /**
     * Verifica si el usuario actual tiene permiso para realizar una operación sobre una entidad.
     *
     * @param usuarioId ID del usuario autenticado
     * @param rol rol del usuario
     * @param operacion tipo de operación (CREATE, READ, UPDATE, DELETE)
     * @param tipoEntidad tipo de entidad sobre la que se realiza la operación
     * @return true si tiene permiso, false en caso contrario
     */
    boolean tienePermiso(Long usuarioId, RolUsuario rol, String operacion, String tipoEntidad);

    /**
     * Verifica si el usuario tiene un rol específico.
     *
     * @param usuarioId ID del usuario
     * @param rolRequerido rol requerido
     * @return true si el usuario tiene el rol
     */
    boolean tieneRol(Long usuarioId, RolUsuario rolRequerido);

    /**
     * Valida si un usuario puede acceder a un recurso específico.
     *
     * @param usuarioId ID del usuario
     * @param recursoId ID del recurso
     * @param tipoRecurso tipo de recurso
     * @return true si tiene acceso
     */
    boolean tieneAccesoAlRecurso(Long usuarioId, Long recursoId, String tipoRecurso);

    /**
     * Obtiene los permisos totales de un usuario.
     *
     * @param rol rol del usuario
     * @return conjunto de permisos disponibles
     */
    java.util.Set<String> obtenerPermisos(RolUsuario rol);

    /**
     * Verifica si una operación es crítica y requiere auditoría.
     *
     * @param operacion tipo de operación
     * @param tipoEntidad tipo de entidad
     * @return true si requiere auditoría
     */
    boolean esOperacionCritica(String operacion, String tipoEntidad);
}
