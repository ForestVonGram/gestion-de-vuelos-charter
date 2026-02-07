package com.paeldav.backend.auth;

import com.paeldav.backend.application.service.base.SesionService;
import com.paeldav.backend.application.service.impl.PasswordServiceImpl;
import com.paeldav.backend.application.service.integration.EmailService;
import com.paeldav.backend.domain.entity.TokenRecuperacion;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.enums.RolUsuario;
import com.paeldav.backend.infraestructure.repository.TokenRecuperacionRepository;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PasswordService Tests")
class PasswordServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TokenRecuperacionRepository tokenRecuperacionRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @Mock
    private SesionService sesionService;

    @InjectMocks
    private PasswordServiceImpl passwordService;

    private Usuario usuarioTest;
    private TokenRecuperacion tokenTest;

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

        tokenTest = TokenRecuperacion.builder()
                .id(1L)
                .usuario(usuarioTest)
                .token("valid-token-123")
                .fechaExpiracion(LocalDateTime.now().plusHours(1))
                .usado(false)
                .build();
    }

    @Nested
    @DisplayName("Solicitar Recuperación Tests")
    class SolicitarRecuperacionTests {

        @Test
        @DisplayName("Solicitar recuperación con email válido genera token y envía email")
        void solicitarRecuperacion_ConEmailValido_GeneraTokenYEnviaEmail() {
            // Arrange
            when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuarioTest));
            when(tokenRecuperacionRepository.save(any(TokenRecuperacion.class))).thenReturn(tokenTest);

            // Act
            passwordService.solicitarRecuperacion("juan@test.com");

            // Assert
            verify(tokenRecuperacionRepository).invalidarTokensAnteriores(usuarioTest.getId());
            verify(tokenRecuperacionRepository).save(any(TokenRecuperacion.class));
            verify(emailService).enviarEmailRecuperacion(eq("juan@test.com"), anyString(), eq("Juan Pérez"));
        }

        @Test
        @DisplayName("Solicitar recuperación con email no registrado no genera error (seguridad)")
        void solicitarRecuperacion_ConEmailNoRegistrado_NoGeneraError() {
            // Arrange
            when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.empty());

            // Act - No debe lanzar excepción por seguridad
            assertDoesNotThrow(() -> passwordService.solicitarRecuperacion("noexiste@test.com"));

            // Assert
            verify(tokenRecuperacionRepository, never()).save(any(TokenRecuperacion.class));
            verify(emailService, never()).enviarEmailRecuperacion(anyString(), anyString(), anyString());
        }

        @Test
        @DisplayName("Solicitar recuperación con usuario inactivo no genera token")
        void solicitarRecuperacion_ConUsuarioInactivo_NoGeneraToken() {
            // Arrange
            usuarioTest.setActivo(false);
            when(usuarioRepository.findByEmail(anyString())).thenReturn(Optional.of(usuarioTest));

            // Act
            assertDoesNotThrow(() -> passwordService.solicitarRecuperacion("juan@test.com"));

            // Assert
            verify(tokenRecuperacionRepository, never()).save(any(TokenRecuperacion.class));
            verify(emailService, never()).enviarEmailRecuperacion(anyString(), anyString(), anyString());
        }
    }

    @Nested
    @DisplayName("Resetear Password Tests")
    class ResetearPasswordTests {

        @Test
        @DisplayName("Resetear password con token válido actualiza contraseña")
        void resetearPassword_ConTokenValido_ActualizaPassword() {
            // Arrange
            when(tokenRecuperacionRepository.findValidToken(anyString(), any(LocalDateTime.class)))
                    .thenReturn(Optional.of(tokenTest));
            when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");

            // Act
            passwordService.resetearPassword("valid-token-123", "nuevaPassword123");

            // Assert
            ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
            verify(usuarioRepository).save(usuarioCaptor.capture());
            assertEquals("newEncodedPassword", usuarioCaptor.getValue().getPassword());

            verify(tokenRecuperacionRepository).save(any(TokenRecuperacion.class));
            assertTrue(tokenTest.getUsado());
            verify(sesionService).revocarTodasLasSesiones(usuarioTest.getId());
            verify(emailService).enviarEmailConfirmacionCambio(anyString(), anyString());
        }

        @Test
        @DisplayName("Resetear password con token inválido lanza excepción")
        void resetearPassword_ConTokenInvalido_LanzaExcepcion() {
            // Arrange
            when(tokenRecuperacionRepository.findValidToken(anyString(), any(LocalDateTime.class)))
                    .thenReturn(Optional.empty());

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                passwordService.resetearPassword("invalid-token", "nuevaPassword123");
            });

            assertEquals("Token inválido o expirado", exception.getMessage());
            verify(usuarioRepository, never()).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Resetear password con token expirado lanza excepción")
        void resetearPassword_ConTokenExpirado_LanzaExcepcion() {
            // Arrange - El token está expirado porque findValidToken no lo encuentra
            when(tokenRecuperacionRepository.findValidToken(anyString(), any(LocalDateTime.class)))
                    .thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                passwordService.resetearPassword("expired-token", "nuevaPassword123");
            });
        }
    }

    @Nested
    @DisplayName("Cambiar Password Tests")
    class CambiarPasswordTests {

        @Test
        @DisplayName("Cambiar password con contraseña actual correcta")
        void cambiarPassword_ConPasswordActualCorrecta_ActualizaPassword() {
            // Arrange
            when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuarioTest));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
            when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");

            // Act
            passwordService.cambiarPassword(1L, "passwordActual", "nuevaPassword123");

            // Assert
            ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
            verify(usuarioRepository).save(usuarioCaptor.capture());
            assertEquals("newEncodedPassword", usuarioCaptor.getValue().getPassword());
            verify(emailService).enviarEmailConfirmacionCambio(anyString(), anyString());
        }

        @Test
        @DisplayName("Cambiar password con contraseña actual incorrecta lanza excepción")
        void cambiarPassword_ConPasswordActualIncorrecta_LanzaExcepcion() {
            // Arrange
            when(usuarioRepository.findById(anyLong())).thenReturn(Optional.of(usuarioTest));
            when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

            // Act & Assert
            assertThrows(BadCredentialsException.class, () -> {
                passwordService.cambiarPassword(1L, "passwordIncorrecta", "nuevaPassword123");
            });

            verify(usuarioRepository, never()).save(any(Usuario.class));
        }

        @Test
        @DisplayName("Cambiar password con usuario no encontrado lanza excepción")
        void cambiarPassword_ConUsuarioNoEncontrado_LanzaExcepcion() {
            // Arrange
            when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                passwordService.cambiarPassword(999L, "passwordActual", "nuevaPassword123");
            });
        }
    }

    @Nested
    @DisplayName("Validar Token Tests")
    class ValidarTokenTests {

        @Test
        @DisplayName("Validar token existente y válido retorna true")
        void validarToken_ConTokenValido_RetornaTrue() {
            // Arrange
            when(tokenRecuperacionRepository.findValidToken(anyString(), any(LocalDateTime.class)))
                    .thenReturn(Optional.of(tokenTest));

            // Act
            boolean resultado = passwordService.validarToken("valid-token-123");

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("Validar token inexistente retorna false")
        void validarToken_ConTokenInexistente_RetornaFalse() {
            // Arrange
            when(tokenRecuperacionRepository.findValidToken(anyString(), any(LocalDateTime.class)))
                    .thenReturn(Optional.empty());

            // Act
            boolean resultado = passwordService.validarToken("invalid-token");

            // Assert
            assertFalse(resultado);
        }
    }
}
