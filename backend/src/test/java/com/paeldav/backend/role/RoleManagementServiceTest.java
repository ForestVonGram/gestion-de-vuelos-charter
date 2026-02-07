package com.paeldav.backend.role;

import com.paeldav.backend.application.dto.rol.RoleAssignmentDTO;
import com.paeldav.backend.application.dto.rol.UserRoleInfoDTO;
import com.paeldav.backend.application.mapper.RoleMapper;
import com.paeldav.backend.application.service.impl.RoleManagementServiceImpl;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.enums.RolUsuario;
import com.paeldav.backend.exception.AuthorizationException;
import com.paeldav.backend.exception.UsuarioNoEncontradoException;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RoleManagementService Tests")
class RoleManagementServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleManagementServiceImpl roleManagementService;

    private Usuario adminUsuario;
    private Usuario usuarioRegular;
    private Usuario usuarioAAsignar;

    @BeforeEach
    void setUp() {
        adminUsuario = Usuario.builder()
                .id(1L)
                .nombre("Admin")
                .apellido("User")
                .email("admin@test.com")
                .password("encodedPassword")
                .rol(RolUsuario.ADMINISTRADOR)
                .activo(true)
                .build();

        usuarioRegular = Usuario.builder()
                .id(2L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@test.com")
                .password("encodedPassword")
                .rol(RolUsuario.USUARIO)
                .activo(true)
                .build();

        usuarioAAsignar = Usuario.builder()
                .id(3L)
                .nombre("Carlos")
                .apellido("López")
                .email("carlos@test.com")
                .password("encodedPassword")
                .rol(RolUsuario.USUARIO)
                .activo(true)
                .build();
    }

    @Nested
    @DisplayName("Assign Role Tests")
    class AssignRoleTests {

        @Test
        @DisplayName("Asignar rol exitosamente por administrador")
        void assignRoleToUser_ConAdministrador_AsignaRolExitosamente() {
            setAuthenticatedUser(adminUsuario);
            RoleAssignmentDTO assignmentDTO = RoleAssignmentDTO.builder()
                    .usuarioId(3L)
                    .nuevoRol(RolUsuario.OPERADOR_LOGISTICA)
                    .motivo("Promoción por desempeño")
                    .build();

            usuarioAAsignar.setRol(RolUsuario.OPERADOR_LOGISTICA);
            UserRoleInfoDTO expectedDTO = UserRoleInfoDTO.builder()
                    .id(3L)
                    .nombre("Carlos")
                    .apellido("López")
                    .email("carlos@test.com")
                    .rol(RolUsuario.OPERADOR_LOGISTICA)
                    .activo(true)
                    .build();

            when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminUsuario));
            when(usuarioRepository.findById(3L)).thenReturn(Optional.of(usuarioAAsignar));
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioAAsignar);
            when(roleMapper.toUserRoleInfoDTO(usuarioAAsignar)).thenReturn(expectedDTO);

            UserRoleInfoDTO result = roleManagementService.assignRoleToUser(assignmentDTO);

            assertNotNull(result);
            assertEquals(RolUsuario.OPERADOR_LOGISTICA, result.getRol());
            verify(usuarioRepository).findById(3L);
            verify(usuarioRepository).save(usuarioAAsignar);
        }

        @Test
        @DisplayName("No permitir asignar rol si no es administrador")
        void assignRoleToUser_ConUsuarioNoAdmin_LanzaExcepcion() {
            setAuthenticatedUser(usuarioRegular);
            RoleAssignmentDTO assignmentDTO = RoleAssignmentDTO.builder()
                    .usuarioId(3L)
                    .nuevoRol(RolUsuario.OPERADOR_LOGISTICA)
                    .build();

            when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(usuarioRegular));

            AuthorizationException exception = assertThrows(AuthorizationException.class, () -> {
                roleManagementService.assignRoleToUser(assignmentDTO);
            });

            assertEquals("Solo administradores pueden asignar roles", exception.getMessage());
            verify(usuarioRepository, never()).findById(3L);
        }

        @Test
        @DisplayName("No permitir cambiar rol de otro administrador")
        void assignRoleToUser_CambiarRolAdministrador_LanzaExcepcion() {
            setAuthenticatedUser(adminUsuario);
            Usuario otroAdmin = Usuario.builder()
                    .id(4L)
                    .nombre("Otro")
                    .apellido("Admin")
                    .email("otroadmin@test.com")
                    .rol(RolUsuario.ADMINISTRADOR)
                    .activo(true)
                    .build();

            RoleAssignmentDTO assignmentDTO = RoleAssignmentDTO.builder()
                    .usuarioId(4L)
                    .nuevoRol(RolUsuario.USUARIO)
                    .build();

            when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminUsuario));
            when(usuarioRepository.findById(4L)).thenReturn(Optional.of(otroAdmin));

            AuthorizationException exception = assertThrows(AuthorizationException.class, () -> {
                roleManagementService.assignRoleToUser(assignmentDTO);
            });

            assertEquals("No se puede cambiar el rol de un administrador a otro rol", exception.getMessage());
            verify(usuarioRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get User Info Tests")
    class GetUserInfoTests {

        @Test
        @DisplayName("Obtener información de rol del usuario")
        void getUserRoleInfo_UsuarioExiste_RetornaUserRoleInfoDTO() {
            UserRoleInfoDTO expectedDTO = UserRoleInfoDTO.builder()
                    .id(2L)
                    .nombre("Juan")
                    .apellido("Pérez")
                    .email("juan@test.com")
                    .rol(RolUsuario.USUARIO)
                    .activo(true)
                    .build();

            when(usuarioRepository.findById(2L)).thenReturn(Optional.of(usuarioRegular));
            when(roleMapper.toUserRoleInfoDTO(usuarioRegular)).thenReturn(expectedDTO);

            UserRoleInfoDTO result = roleManagementService.getUserRoleInfo(2L);

            assertNotNull(result);
            assertEquals(2L, result.getId());
            assertEquals(RolUsuario.USUARIO, result.getRol());
            verify(usuarioRepository).findById(2L);
        }
    }

    @Nested
    @DisplayName("Current User Role Tests")
    class CurrentUserRoleTests {

        @Test
        @DisplayName("Obtener rol del usuario autenticado")
        void getCurrentUserRole_UsuarioAutenticado_RetornaRol() {
            setAuthenticatedUser(usuarioRegular);
            when(usuarioRepository.findByEmail("juan@test.com")).thenReturn(Optional.of(usuarioRegular));

            RolUsuario rol = roleManagementService.getCurrentUserRole();

            assertEquals(RolUsuario.USUARIO, rol);
        }

        @Test
        @DisplayName("Verificar si usuario autenticado tiene un rol específico")
        void hasRole_UsuarioTieneRol_RetornaTrue() {
            setAuthenticatedUser(adminUsuario);
            when(usuarioRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminUsuario));

            boolean hasAdminRole = roleManagementService.hasRole(RolUsuario.ADMINISTRADOR);
            boolean hasUserRole = roleManagementService.hasRole(RolUsuario.USUARIO);

            assertTrue(hasAdminRole);
            assertFalse(hasUserRole);
        }
    }

    private void setAuthenticatedUser(Usuario usuario) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()));
        
        var authentication = new UsernamePasswordAuthenticationToken(
                usuario.getEmail(),
                usuario.getPassword(),
                authorities
        );
        
        SecurityContext context = new org.springframework.security.core.context.SecurityContextImpl();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
    }
}
