package com.paeldav.backend.application.service.base;

import com.paeldav.backend.application.dto.rol.RoleAssignmentDTO;
import com.paeldav.backend.application.dto.rol.UserRoleInfoDTO;
import com.paeldav.backend.domain.enums.RolUsuario;

import java.util.List;

/**
 * Interfaz para gestión de roles de usuarios.
 */
public interface RoleManagementService {

    /**
     * Asigna un nuevo rol a un usuario.
     *
     * @param assignmentDTO DTO con información del usuario e id del nuevo rol
     * @return DTO con información actualizada del usuario
     * @throws com.paeldav.backend.exception.UsuarioNoEncontradoException si no existe el usuario
     * @throws com.paeldav.backend.exception.AuthorizationException si el usuario actual no tiene permiso
     */
    UserRoleInfoDTO assignRoleToUser(RoleAssignmentDTO assignmentDTO);

    /**
     * Obtiene información de un usuario incluyendo su rol.
     *
     * @param usuarioId ID del usuario
     * @return DTO con información del usuario
     * @throws com.paeldav.backend.exception.UsuarioNoEncontradoException si no existe el usuario
     */
    UserRoleInfoDTO getUserRoleInfo(Long usuarioId);

    /**
     * Lista todos los usuarios con un rol específico.
     *
     * @param rol el rol a filtrar
     * @return lista de DTOs de usuarios con ese rol
     */
    List<UserRoleInfoDTO> getUsersByRole(RolUsuario rol);

    /**
     * Obtiene todos los roles disponibles en el sistema.
     *
     * @return array con todos los roles disponibles
     */
    RolUsuario[] getAvailableRoles();

    /**
     * Obtiene el rol actual del usuario autenticado.
     *
     * @return el rol del usuario autenticado
     * @throws com.paeldav.backend.exception.UsuarioNoEncontradoException si no existe el usuario
     */
    RolUsuario getCurrentUserRole();

    /**
     * Verifica si el usuario autenticado tiene un rol específico.
     *
     * @param rol el rol a verificar
     * @return true si el usuario tiene el rol, false en caso contrario
     */
    boolean hasRole(RolUsuario rol);
}
