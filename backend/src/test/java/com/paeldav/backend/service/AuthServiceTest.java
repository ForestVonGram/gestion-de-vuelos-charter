package com.paeldav.backend.service;

import com.paeldav.backend.application.dto.auth.AuthResponse;
import com.paeldav.backend.application.dto.auth.LoginRequest;
import com.paeldav.backend.application.dto.auth.RegisterRequest;
import com.paeldav.backend.application.service.base.DosFactoresService;
import com.paeldav.backend.application.service.base.EmailService;
import com.paeldav.backend.application.service.base.RecaptchaService;
import com.paeldav.backend.application.service.base.SesionService;
import com.paeldav.backend.application.service.impl.AuthServiceImpl;
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
import org.springframework.security.core.userdetails.UserDetails;
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

    @Mock
    private DosFactoresService dosFactoresService;

    @Mock
    private RecaptchaService recaptchaService;

    @Mock
    private EmailService emailService;

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

        loginRequest = new LoginRequest("juan@test.com", "password123", "recaptcha-token");
        registerRequest = RegisterRequest.builder()
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@test.com")
                .password("password123")
                .rol(RolUsuario.USUARIO)
                .recaptchaToken("recaptcha-token")
                .build();
    }

    @Nested
    @DisplayName("Login Tests")
    class LoginTests {

        @Test
        @DisplayName("Login exitoso")
        void login_Exitoso() {
            // Arrange
            when(recaptchaService.estaHabilitado()).thenReturn(false);
            when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuarioTest));
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
            when(dosFactoresService.esActivo(any(Usuario.class))).thenReturn(false);
            when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

            // Act
            AuthResponse response = authService.login(loginRequest, "PC", "127.0.0.1", "Mozilla");

            // Assert
            assertNotNull(response);
            assertEquals("jwt-token", response.getToken());
            assertEquals("juan@test.com", response.getEmail());
            verify(sesionService).crearSesion(any(Usuario.class), anyString(), anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Login fallido por credenciales inválidas")
        void login_CredencialesInvalidas_LanzaExcepcion() {
            // Arrange
            when(recaptchaService.estaHabilitado()).thenReturn(false);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenThrow(new BadCredentialsException("Credenciales inválidas"));

            // Act & Assert
            assertThrows(BadCredentialsException.class, () -> {
                authService.login(loginRequest, "PC", "127.0.0.1", "Mozilla");
            });
        }
    }

    @Nested
    @DisplayName("Register Tests")
    class RegisterTests {

        @Test
        @DisplayName("Registro exitoso")
        void register_Exitoso() {
            // Arrange
            when(recaptchaService.estaHabilitado()).thenReturn(false);
            when(usuarioRepository.existsByEmail(anyString())).thenReturn(false);
            when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
            when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioTest);
            when(jwtService.generateToken(any(UserDetails.class))).thenReturn("jwt-token");

            // Act
            AuthResponse response = authService.register(registerRequest, "PC", "127.0.0.1", "Mozilla");

            // Assert
            assertNotNull(response);
            assertEquals("jwt-token", response.getToken());
            verify(usuarioRepository).save(any(Usuario.class));
            verify(emailService).sendEmailLogin(anyString(), anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Registro fallido por email ya existe")
        void register_EmailYaExiste_LanzaExcepcion() {
            // Arrange
            when(recaptchaService.estaHabilitado()).thenReturn(false);
            when(usuarioRepository.existsByEmail(anyString())).thenReturn(true);

            // Act & Assert
            assertThrows(RuntimeException.class, () -> {
                authService.register(registerRequest, "PC", "127.0.0.1", "Mozilla");
            });
        }
    }
}
