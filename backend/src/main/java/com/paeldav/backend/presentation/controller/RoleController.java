package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.rol.RoleAssignmentDTO;
import com.paeldav.backend.application.dto.rol.UserRoleInfoDTO;
import com.paeldav.backend.application.service.base.RoleManagementService;
import com.paeldav.backend.domain.enums.RolUsuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de roles de usuarios.
 * Todos los endpoints requieren autenticación y roles específicos.
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleManagementService roleManagementService;

    /**
     * Asigna un nuevo rol a un usuario.
     * Solo ADMINISTRADOR puede ejecutar esta acción.
     *
     * @param assignmentDTO DTO con información del usuario y nuevo rol
     * @return ResponseEntity con información actualizada del usuario
     */
    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<UserRoleInfoDTO> assignRoleToUser(
            @Valid @RequestBody RoleAssignmentDTO assignmentDTO) {
        UserRoleInfoDTO result = roleManagementService.assignRoleToUser(assignmentDTO);
        return ResponseEntity.ok(result);
    }

    /**
     * Obtiene información de un usuario incluyendo su rol.
     * Cualquier usuario autenticado puede obtener información de otros usuarios.
     *
     * @param usuarioId ID del usuario
     * @return ResponseEntity con información del usuario
     */
    @GetMapping("/{usuarioId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserRoleInfoDTO> getUserRoleInfo(@PathVariable Long usuarioId) {
        UserRoleInfoDTO result = roleManagementService.getUserRoleInfo(usuarioId);
        return ResponseEntity.ok(result);
    }

    /**
     * Lista todos los usuarios con un rol específico.
     * Solo ADMINISTRADOR puede ejecutar esta acción.
     *
     * @param rol el rol a filtrar
     * @return ResponseEntity con lista de usuarios con ese rol
     */
    @GetMapping("/by-role/{rol}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<List<UserRoleInfoDTO>> getUsersByRole(@PathVariable RolUsuario rol) {
        List<UserRoleInfoDTO> result = roleManagementService.getUsersByRole(rol);
        return ResponseEntity.ok(result);
    }

    /**
     * Obtiene todos los roles disponibles en el sistema.
     *
     * @return ResponseEntity con lista de roles disponibles
     */
    @GetMapping("/available")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getAvailableRoles() {
        RolUsuario[] roles = roleManagementService.getAvailableRoles();
        Map<String, Object> response = new HashMap<>();
        response.put("roles", Arrays.asList(roles));
        response.put("total", roles.length);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene el rol del usuario autenticado.
     *
     * @return ResponseEntity con el rol actual
     */
    @GetMapping("/current")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCurrentUserRole() {
        RolUsuario currentRole = roleManagementService.getCurrentUserRole();
        Map<String, Object> response = new HashMap<>();
        response.put("rol", currentRole);
        response.put("nombreRol", currentRole.name());
        return ResponseEntity.ok(response);
    }

    /**
     * Verifica si el usuario autenticado tiene un rol específico.
     *
     * @param rol el rol a verificar
     * @return ResponseEntity con resultado booleano
     */
    @GetMapping("/check/{rol}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> checkUserRole(@PathVariable RolUsuario rol) {
        boolean hasRole = roleManagementService.hasRole(rol);
        Map<String, Object> response = new HashMap<>();
        response.put("rol", rol.name());
        response.put("tieneRol", hasRole);
        return ResponseEntity.ok(response);
    }

    /**
     * Obtiene información del usuario actual con su rol.
     *
     * @return ResponseEntity con información del usuario autenticado
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getCurrentUserInfo() {
        RolUsuario currentRole = roleManagementService.getCurrentUserRole();
        Map<String, Object> response = new HashMap<>();
        response.put("rol", currentRole);
        response.put("nombreRol", currentRole.name());
        
        // Información sobre permisos
        Map<String, Boolean> permisos = new HashMap<>();
        permisos.put("esAdministrador", roleManagementService.hasRole(RolUsuario.ADMINISTRADOR));
        permisos.put("esOperadorLogistica", roleManagementService.hasRole(RolUsuario.OPERADOR_LOGISTICA));
        permisos.put("esAyudanteMantenimiento", roleManagementService.hasRole(RolUsuario.AYUDANTE_MANTENIMIENTO));
        permisos.put("esTripulacion", roleManagementService.hasRole(RolUsuario.TRIPULACION));
        permisos.put("esUsuarioRegular", roleManagementService.hasRole(RolUsuario.USUARIO));
        
        response.put("permisos", permisos);
        return ResponseEntity.ok(response);
    }
}
