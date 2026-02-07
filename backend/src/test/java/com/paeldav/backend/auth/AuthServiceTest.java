package com.paeldav.backend.auth;

import com.paeldav.backend.application.dto.auth.AuthResponse;
import com.paeldav.backend.application.dto.auth.LoginRequest;
import com.paeldav.backend.application.dto.auth.RegisterRequest;
import com.paeldav.backend.application.service.base.SesionService;
import com.paeldav.backend.application.service.impl.AuthServiceImpl;
import com.paeldav.backend.domain.entity.SesionActiva;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.enums.RolUsuario;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import com.paeldav.backend.infraestructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService Tests")
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private SesionService sesionService;

    @InjectMocks
    private AuthServiceImpl authService;

    private Usuario usuarioTest;
    private LoginRequest loginRequest;
    private RegisterRequest registerRequest;

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

        loginRequest = new LoginRequest();
        loginRequest.setEmail("juan@test.com");
        loginRequest.setPassword("password123");

        registerRequest = new RegisterRequest();
        registerRequest.setNombre("Juan");
        registerRequest.setApellido("Pérez");
        registerRequest.setEmail("juan@test.com");
        registerRequest.setPassword("password123");
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Login exitoso con credenciales válidas")
        void login_ConCredencialesValidas_RetornaAuthResponse() {
            // Arrange
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(new UsernamePasswordAuthenticationToken(usuarioTest.getEmail(), null));
            when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuarioTest));
            when(jwtService.generateToken(any())).thenReturn("jwt-token-generado");
            when(sesionService.crearSesion(any(), anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(new SesionActiva());

            // Act
            AuthResponse response = authService.login(loginRequest, "Desktop", "127.0.0.1", "Mozilla");

            // Assert
            assertNotNull(response);
            assertEquals("jwt-token-generado", response.getToken());
            assertEquals("Bearer", response.getTokenType());
            assertEquals(usuarioTest.getEmail(), response.getEmail());
            assertEquals(usuarioTest.getId(), response.getUserId());
            assertEquals("Juan Pérez", response.getNombreCompleto());
            assertEquals(RolUsuario.USUARIO, response.getRol());

            verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
            verify(sesionService).crearSesion(any(), anyString(), anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Login fallido con credenciales inválidas")
        void login_ConCredencialesInvalidas_LanzaExcepcion() {
            // Arrange
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Credenciales inválidas"));

            // Act & Assert
            assertThrows(BadCredentialsException.class, () -> {
                authService.login(loginRequest, "Desktop", "127.0.0.1", "Mozilla");
            });

            verify(usuarioRepository, never()).findByEmail(anyString());
            verify(jwtService, never()).generateToken(any());
        }

        @Test
        @DisplayName("Login fallido con usuario inactivo")
        void login_ConUsuarioInactivo_LanzaExcepcion() {
            // Arrange
            usuarioTest.setActivo(false);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(new UsernamePasswordAuthenticationToken(usuarioTest.getEmail(), null));
            when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuarioTest));

            // Act & Assert
            assertThrows(BadCredentialsException.class, () -> {
                authService.login(loginRequest, "Desktop", "127.0.0.1", "Mozilla");
            });

            verify(jwtService, never()).generateToken(any());
        }

        @Test
        @DisplayName("Login fallido con email no registrado")
        void login_ConEmailNoRegistrado_LanzaExcepcion() {
            // Arrange
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), null));
            when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(BadCredentialsException.class, () -> {
                authService.login(loginRequest, "Desktop", "127.0.0.1", "Mozilla");
            });
        }
    }

    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {

        @Test
        @DisplayName("Registro exitoso de nuevo usuario")
        void register_ConDatosValidos_RetornaAuthResponse() {
            // Arrange
            when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTest);
            when(jwtService.generateToken(any())).thenReturn("jwt-token-generado");
            when(sesionService.crearSesion(any(), anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(new SesionActiva());

            // Act
            AuthResponse response = authService.register(registerRequest, "Desktop", "127.0.0.1", "Mozilla");

            // Assert
            assertNotNull(response);
            assertEquals("jwt-token-generado", response.getToken());
            assertEquals("Bearer", response.getTokenType());
            assertNotNull(response.getUserId());

            verify(usuarioRepository).existsByEmail(registerRequest.getEmail());
            verify(passwordEncoder).encode(registerRequest.getPassword());
            verify(usuarioRepository).save(any(Usuario.class));
            verify(sesionService).crearSesion(any(), anyString(), anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Registro fallido con email ya existente")
        void register_ConEmailExistente_LanzaExcepcion() {
            // Arrange
            when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                authService.register(registerRequest, "Desktop", "127.0.0.1", "Mozilla");
            });

            assertEquals("El email ya está registrado", exception.getMessage());
            verify(usuarioRepository, never()).save(any(Usuario.class));
        }
    }
}
