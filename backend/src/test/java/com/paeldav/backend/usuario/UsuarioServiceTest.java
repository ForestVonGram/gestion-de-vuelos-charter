package com.paeldav.backend.usuario;

import com.paeldav.backend.application.dto.usuario.UsuarioCreateDTO;
import com.paeldav.backend.application.dto.usuario.UsuarioDTO;
import com.paeldav.backend.application.dto.usuario.UsuarioUpdateDTO;
import com.paeldav.backend.application.mapper.UsuarioMapper;
import com.paeldav.backend.application.service.impl.UsuarioServiceImpl;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.enums.RolUsuario;
import com.paeldav.backend.exception.UsuarioNoEncontradoException;
import com.paeldav.backend.exception.UsuarioYaExisteException;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UsuarioService Tests")
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioServiceImpl usuarioService;

    private UsuarioCreateDTO usuarioCreateDTO;
    private Usuario usuario;
    private UsuarioDTO usuarioDTO;

    @BeforeEach
    void setUp() {
        usuarioCreateDTO = UsuarioCreateDTO.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@example.com")
                .password("password123")
                .telefono("123456789")
                .rol(RolUsuario.USUARIO)
                .build();

        usuario = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@example.com")
                .password("encodedPassword")
                .telefono("123456789")
                .rol(RolUsuario.USUARIO)
                .activo(true)
                .fechaRegistro(LocalDateTime.now())
                .build();

        usuarioDTO = UsuarioDTO.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@example.com")
                .telefono("123456789")
                .rol(RolUsuario.USUARIO)
                .activo(true)
                .fechaRegistro(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Crear Usuario Tests")
    class CrearUsuarioTests {

        @Test
        @DisplayName("Crear usuario exitosamente")
        void crearUsuario_ConDatosValidos_CreaNuevoUsuario() {
            when(usuarioRepository.existsByEmail("juan@example.com")).thenReturn(false);
            when(usuarioMapper.toEntity(usuarioCreateDTO)).thenReturn(usuario);
            when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);
            when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioDTO);

            UsuarioDTO resultado = usuarioService.crearUsuario(usuarioCreateDTO);

            assertNotNull(resultado);
            assertEquals("juan@example.com", resultado.getEmail());
            assertTrue(resultado.getActivo());
            verify(usuarioRepository).existsByEmail("juan@example.com");
            verify(usuarioRepository).save(any(Usuario.class));
            verify(passwordEncoder).encode("password123");
        }

        @Test
        @DisplayName("No permitir crear usuario con email duplicado")
        void crearUsuario_ConEmailDuplicado_LanzaExcepcion() {
            when(usuarioRepository.existsByEmail("juan@example.com")).thenReturn(true);

            UsuarioYaExisteException exception = assertThrows(UsuarioYaExisteException.class, () -> {
                usuarioService.crearUsuario(usuarioCreateDTO);
            });

            assertNotNull(exception.getMessage());
            assertTrue(exception.getMessage().contains("juan@example.com"));
            verify(usuarioRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Editar Usuario Tests")
    class EditarUsuarioTests {

        @Test
        @DisplayName("Editar usuario exitosamente")
        void editarUsuario_ConDatosValidos_ActualizaUsuario() {
            UsuarioUpdateDTO updateDTO = UsuarioUpdateDTO.builder()
                    .nombre("Juan Actualizado")
                    .email("juan.nuevo@example.com")
                    .build();

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(usuarioRepository.existsByEmail("juan.nuevo@example.com")).thenReturn(false);
            doNothing().when(usuarioMapper).updateEntityFromDTO(updateDTO, usuario);
            when(usuarioRepository.save(usuario)).thenReturn(usuario);
            when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioDTO);

            UsuarioDTO resultado = usuarioService.editarUsuario(1L, updateDTO);

            assertNotNull(resultado);
            verify(usuarioRepository).findById(1L);
            verify(usuarioRepository).save(usuario);
        }
    }

    @Nested
    @DisplayName("Desactivar Usuario Tests")
    class DesactivarUsuarioTests {

        @Test
        @DisplayName("Desactivar usuario exitosamente")
        void desactivarUsuario_UsuarioExiste_DesactivaUsuario() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(usuarioRepository.save(usuario)).thenReturn(usuario);

            usuarioService.desactivarUsuario(1L);

            assertFalse(usuario.getActivo());
            verify(usuarioRepository).save(usuario);
        }

        @Test
        @DisplayName("Activar usuario exitosamente")
        void activarUsuario_UsuarioExiste_ActivaUsuario() {
            usuario.setActivo(false);
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(usuarioRepository.save(usuario)).thenReturn(usuario);

            usuarioService.activarUsuario(1L);

            assertTrue(usuario.getActivo());
            verify(usuarioRepository).save(usuario);
        }
    }
}
