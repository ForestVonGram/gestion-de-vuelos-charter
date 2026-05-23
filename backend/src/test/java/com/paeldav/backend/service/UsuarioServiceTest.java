package com.paeldav.backend.service;

import com.paeldav.backend.application.dto.usuario.UsuarioCreateDTO;
import com.paeldav.backend.application.dto.usuario.UsuarioDTO;
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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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

    private Usuario usuarioTest;
    private UsuarioDTO usuarioDTOTest;
    private UsuarioCreateDTO usuarioCreateDTOTest;

    @BeforeEach
    void setUp() {
        usuarioTest = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@test.com")
                .password("encodedPassword")
                .rol(RolUsuario.USUARIO)
                .activo(true)
                .build();

        usuarioDTOTest = UsuarioDTO.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@test.com")
                .rol(RolUsuario.USUARIO)
                .activo(true)
                .build();

        usuarioCreateDTOTest = UsuarioCreateDTO.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@test.com")
                .password("password123")
                .rol(RolUsuario.USUARIO)
                .build();
    }

    @Nested
    @DisplayName("Crear Usuario Tests")
    class CrearUsuarioTests {

        @Test
        @DisplayName("Crear usuario exitosamente")
        void crearUsuario_Exitoso() {
            // Arrange
            when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
            when(usuarioMapper.toEntity(any(UsuarioCreateDTO.class))).thenReturn(usuarioTest);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTest);
            when(usuarioMapper.toDTO(any(Usuario.class))).thenReturn(usuarioDTOTest);

            // Act
            UsuarioDTO resultado = usuarioService.crearUsuario(usuarioCreateDTOTest);

            // Assert
            assertNotNull(resultado);
            assertEquals(usuarioDTOTest.getEmail(), resultado.getEmail());
            verify(usuarioRepository).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Lanzar excepción si el email ya existe")
        void crearUsuario_EmailYaExiste_LanzaExcepcion() {
            // Arrange
            when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

            // Act & Assert
            assertThrows(UsuarioYaExisteException.class, () -> {
                usuarioService.crearUsuario(usuarioCreateDTOTest);
            });
            verify(usuarioRepository, never()).save(any(Usuario.class));
        }
    }

    @Nested
    @DisplayName("Obtener Usuario Tests")
    class ObtenerUsuarioTests {

        @Test
        @DisplayName("Obtener usuario por ID exitosamente")
        void obtenerUsuarioPorId_Exitoso() {
            // Arrange
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTest));
            when(usuarioMapper.toDTO(usuarioTest)).thenReturn(usuarioDTOTest);

            // Act
            UsuarioDTO resultado = usuarioService.obtenerUsuarioPorId(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
        }

        @Test
        @DisplayName("Lanzar excepción si el usuario no existe")
        void obtenerUsuarioPorId_NoExiste_LanzaExcepcion() {
            // Arrange
            when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(UsuarioNoEncontradoException.class, () -> {
                usuarioService.obtenerUsuarioPorId(999L);
            });
        }
    }
}
