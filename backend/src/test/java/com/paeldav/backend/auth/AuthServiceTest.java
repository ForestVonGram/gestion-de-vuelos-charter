package com.paeldav.backend.auth;

import com.paeldav.backend.application.dto.auth.*;
import com.paeldav.backend.application.service.base.DosFactoresService;
import com.paeldav.backend.application.service.base.RecaptchaService;
import com.paeldav.backend.application.service.base.SesionService;
import com.paeldav.backend.application.service.impl.AuthServiceImpl;
import com.paeldav.backend.domain.entity.SesionActiva;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.entity.VerificacionDosFactores;
import com.paeldav.backend.domain.enums.MetodoDosFactores;
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
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
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
        loginRequest.setRecaptchaToken("valid-recaptcha");

        registerRequest = new RegisterRequest();
        registerRequest.setNombre("Juan");
        registerRequest.setApellido("Pérez");
        registerRequest.setEmail("juan@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setRecaptchaToken("valid-recaptcha");
        
        // Mock recaptcha con lenient para permitir override en tests específicos
        lenient().when(recaptchaService.validarToken("valid-recaptcha")).thenReturn(true);
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

    @Nested
    @DisplayName("Login con 2FA Tests")
    class LoginDosFactoresTests {

        @Test
        @DisplayName("Login falla si reCAPTCHA no se valida")
        void login_RecaptchaFalla_LanzaExcepcion() {
            // Arrange
            loginRequest.setRecaptchaToken("invalid-recaptcha");
            when(recaptchaService.estaHabilitado()).thenReturn(true);
            when(recaptchaService.validarToken("invalid-recaptcha")).thenReturn(false);

            // Act & Assert
            assertThrows(BadCredentialsException.class, () -> {
                authService.login(loginRequest, "Desktop", "127.0.0.1", "Mozilla");
            });

            verify(authenticationManager, never()).authenticate(any(UsernamePasswordAuthenticationToken.class));
        }

        @Test
        @DisplayName("Login exitoso con 2FA habilitado requiere verificación")
        void login_Con2FAHabilitado_RequiereVerificacion() {
            // Arrange
            usuarioTest.setDosFactoresHabilitado(true);
            usuarioTest.setMetodoDosFactores(MetodoDosFactores.EMAIL);
            loginRequest.setRecaptchaToken("valid-recaptcha");

            lenient().when(recaptchaService.validarToken("valid-recaptcha")).thenReturn(true);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(new UsernamePasswordAuthenticationToken(usuarioTest.getEmail(), null));
            when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuarioTest));
            when(dosFactoresService.esActivo(usuarioTest)).thenReturn(true);
            lenient().when(jwtService.generateToken(any(), any(), anyLong())).thenReturn("session-token-temporal");
            when(dosFactoresService.generarCodigoVerificacion(eq(usuarioTest), eq(MetodoDosFactores.EMAIL), anyString())).thenReturn(new VerificacionDosFactores());

            // Act
            AuthResponse response = authService.login(loginRequest, "Desktop", "127.0.0.1", "Mozilla");

            // Assert
            assertNotNull(response);
            assertTrue(response.getRequires2FA());
            assertNotNull(response.getSessionToken());
            assertNull(response.getToken()); // No token JWT completo hasta verificar 2FA
            verify(dosFactoresService).generarCodigoVerificacion(eq(usuarioTest), eq(MetodoDosFactores.EMAIL), anyString());
        }

        @Test
        @DisplayName("Login exitoso sin 2FA genera token JWT inmediato")
        void login_Sin2FA_GeneraTokenInmediato() {
            // Arrange
            usuarioTest.setDosFactoresHabilitado(false);
            loginRequest.setRecaptchaToken("valid-recaptcha");

            lenient().when(recaptchaService.validarToken("valid-recaptcha")).thenReturn(true);
            when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .thenReturn(new UsernamePasswordAuthenticationToken(usuarioTest.getEmail(), null));
            when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuarioTest));
            when(dosFactoresService.esActivo(usuarioTest)).thenReturn(false);
            when(jwtService.generateToken(any())).thenReturn("jwt-token-completo");
            when(sesionService.crearSesion(any(), anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(new SesionActiva());

            // Act
            AuthResponse response = authService.login(loginRequest, "Desktop", "127.0.0.1", "Mozilla");

            // Assert
            assertNotNull(response);
            assertFalse(response.getRequires2FA());
            assertEquals("jwt-token-completo", response.getToken());
            verify(sesionService).crearSesion(any(), anyString(), anyString(), anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("Verificación 2FA Tests")
    class Verificacion2FATests {

        private VerificarCodigoRequest verificacionRequest;
        private VerificacionDosFactores verificacionTest;

        @BeforeEach
        void setUp2FA() {
            verificacionRequest = new VerificarCodigoRequest();
            verificacionRequest.setCodigo("123456");

            verificacionTest = VerificacionDosFactores.builder()
                    .id(1L)
                    .usuario(usuarioTest)
                    .codigo("123456")
                    .verificado(false)
                    .build();
            
            // Reset mocks para evitar stubbings innecesarios
            reset(recaptchaService, authenticationManager, sesionService, dosFactoresService);
        }

        @Test
        @DisplayName("Verificar código 2FA exitosamente")
        void verificarCodigo_Exitoso_RetornaAuthResponse() {
            // Arrange
            when(dosFactoresService.verificarCodigo("123456")).thenReturn(verificacionTest);
            when(jwtService.generateToken(any())).thenReturn("jwt-token-completo");
            when(sesionService.crearSesion(any(), anyString(), anyString(), anyString(), anyString()))
                    .thenReturn(new SesionActiva());

            // Act
            AuthResponse response = authService.verificarDosFactores(verificacionRequest, "Desktop", "127.0.0.1", "Mozilla");

            // Assert
            assertNotNull(response);
            assertEquals("jwt-token-completo", response.getToken());
            verify(dosFactoresService).marcarComoVerificado(verificacionTest);
            verify(sesionService).crearSesion(any(), anyString(), anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Verificar código inválido lanza excepción")
        void verificarCodigo_Invalido_LanzaExcepcion() {
            // Arrange
            when(dosFactoresService.verificarCodigo("000000"))
                    .thenThrow(new NoSuchElementException("Código inválido"));
            verificacionRequest.setCodigo("000000");

            // Act & Assert
            assertThrows(BadCredentialsException.class, () -> {
                authService.verificarDosFactores(verificacionRequest, "Desktop", "127.0.0.1", "Mozilla");
            });
        }
    }
}
