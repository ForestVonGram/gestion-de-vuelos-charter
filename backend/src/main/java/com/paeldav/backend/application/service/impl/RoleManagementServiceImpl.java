package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.rol.RoleAssignmentDTO;
import com.paeldav.backend.application.dto.rol.UserRoleInfoDTO;
import com.paeldav.backend.application.mapper.RoleMapper;
import com.paeldav.backend.application.service.base.RoleManagementService;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.enums.RolUsuario;
import com.paeldav.backend.exception.AuthorizationException;
import com.paeldav.backend.exception.UsuarioNoEncontradoException;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio de gestión de roles.
 */
@Service
@RequiredArgsConstructor
public class RoleManagementServiceImpl implements RoleManagementService {

    private final UsuarioRepository usuarioRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional
    public UserRoleInfoDTO assignRoleToUser(RoleAssignmentDTO assignmentDTO) {
        // Verificar que el usuario autenticado tiene permiso (solo ADMINISTRADOR)
        if (!hasRole(RolUsuario.ADMINISTRADOR)) {
            throw new AuthorizationException("Solo administradores pueden asignar roles");
        }

        // Obtener usuario a quien se le asignará el rol
        Usuario usuario = usuarioRepository.findById(assignmentDTO.getUsuarioId())
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario no encontrado con ID: " + assignmentDTO.getUsuarioId()
                ));

        // Validar que no sea intento de cambiar rol a otro administrador
        if (usuario.getRol() == RolUsuario.ADMINISTRADOR && 
            assignmentDTO.getNuevoRol() != RolUsuario.ADMINISTRADOR) {
            throw new AuthorizationException(
                    "No se puede cambiar el rol de un administrador a otro rol"
            );
        }

        // Asignar nuevo rol
        usuario.setRol(assignmentDTO.getNuevoRol());
        usuario = usuarioRepository.save(usuario);

        return roleMapper.toUserRoleInfoDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UserRoleInfoDTO getUserRoleInfo(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario no encontrado con ID: " + usuarioId
                ));

        return roleMapper.toUserRoleInfoDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRoleInfoDTO> getUsersByRole(RolUsuario rol) {
        List<Usuario> usuarios = usuarioRepository.findByRol(rol);
        return roleMapper.toUserRoleInfoDTOList(usuarios);
    }

    @Override
    public RolUsuario[] getAvailableRoles() {
        return RolUsuario.values();
    }

    @Override
    @Transactional(readOnly = true)
    public RolUsuario getCurrentUserRole() {
        String email = getAuthenticatedUserEmail();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario autenticado no encontrado: " + email
                ));

        return usuario.getRol();
    }

    @Override
    public boolean hasRole(RolUsuario rol) {
        try {
            RolUsuario currentRole = getCurrentUserRole();
            return currentRole == rol;
        } catch (UsuarioNoEncontradoException e) {
            return false;
        }
    }

    /**
     * Obtiene el email del usuario autenticado desde el contexto de seguridad.
     *
     * @return email del usuario autenticado
     * @throws AuthorizationException si no hay usuario autenticado
     */
    private String getAuthenticatedUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthorizationException("Usuario no autenticado");
        }

        return authentication.getName();
    }
}
