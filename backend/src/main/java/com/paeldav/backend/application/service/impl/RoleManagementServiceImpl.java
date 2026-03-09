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

    // Dependencias inyectadas para la persistencia de usuarios y mapeo de DTOs
    private final UsuarioRepository usuarioRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional
    public UserRoleInfoDTO assignRoleToUser(RoleAssignmentDTO assignmentDTO) {
        // Verificar que el usuario que ejecuta la acción tenga privilegios de ADMINISTRADOR
        if (!hasRole(RolUsuario.ADMINISTRADOR)) {
            throw new AuthorizationException("Solo administradores pueden asignar roles");
        }

        // Buscar al usuario destino en la base de datos
        Usuario usuario = usuarioRepository.findById(assignmentDTO.getUsuarioId())
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario no encontrado con ID: " + assignmentDTO.getUsuarioId()
                ));

        // Regla de negocio: Evitar que un administrador sea degradado a un rol inferior
        if (usuario.getRol() == RolUsuario.ADMINISTRADOR &&
                assignmentDTO.getNuevoRol() != RolUsuario.ADMINISTRADOR) {
            throw new AuthorizationException(
                    "No se puede cambiar el rol de un administrador a otro rol"
            );
        }

        // Aplicar el cambio de rol, guardar en la base de datos y retornar la información actualizada
        usuario.setRol(assignmentDTO.getNuevoRol());
        usuario = usuarioRepository.save(usuario);

        return roleMapper.toUserRoleInfoDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UserRoleInfoDTO getUserRoleInfo(Long usuarioId) {
        // Consultar y retornar la información actual del rol de un usuario específico
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario no encontrado con ID: " + usuarioId
                ));

        return roleMapper.toUserRoleInfoDTO(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserRoleInfoDTO> getUsersByRole(RolUsuario rol) {
        // Obtener una lista de todos los usuarios que poseen un rol determinado en el sistema
        List<Usuario> usuarios = usuarioRepository.findByRol(rol);
        return roleMapper.toUserRoleInfoDTOList(usuarios);
    }

    @Override
    public RolUsuario[] getAvailableRoles() {
        // Listar todos los roles configurados y disponibles en el sistema (Valores del Enum)
        return RolUsuario.values();
    }

    @Override
    @Transactional(readOnly = true)
    public RolUsuario getCurrentUserRole() {
        // Extraer el email de la sesión activa y buscar el rol asociado a ese usuario en la BD
        String email = getAuthenticatedUserEmail();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsuarioNoEncontradoException(
                        "Usuario autenticado no encontrado: " + email
                ));

        return usuario.getRol();
    }

    @Override
    public boolean hasRole(RolUsuario rol) {
        // Validar si el rol del usuario actualmente autenticado coincide con el rol requerido
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
        // Extraer el objeto de autenticación del contexto global de Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Validar que exista una sesión activa y que el usuario esté correctamente autenticado
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthorizationException("Usuario no autenticado");
        }

        // Retornar el identificador principal del usuario (en este sistema, corresponde al email)
        return authentication.getName();
    }
}