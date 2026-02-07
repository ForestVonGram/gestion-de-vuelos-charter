package com.paeldav.backend.auth;

import com.paeldav.backend.application.dto.auth.SesionActivaDTO;
import com.paeldav.backend.application.service.impl.SesionServiceImpl;
import com.paeldav.backend.domain.entity.SesionActiva;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.enums.RolUsuario;
import com.paeldav.backend.infraestructure.repository.SesionActivaRepository;
import com.paeldav.backend.infraestructure.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("SesionService Tests")
class SesionServiceTest {

    @Mock
    private SesionActivaRepository sesionActivaRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private SesionServiceImpl sesionService;

    private Usuario usuarioTest;
    private SesionActiva sesionTest;
    private SesionActiva sesionTest2;

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

        sesionTest = SesionActiva.builder()
                .id(1L)
                .usuario(usuarioTest)
                .tokenHash("hashed-token-1")
                .dispositivo("Desktop")
                .direccionIp("127.0.0.1")
                .userAgent("Mozilla")
                .fechaCreacion(LocalDateTime.now())
                .fechaExpiracion(LocalDateTime.now().plusHours(24))
                .ultimaActividad(LocalDateTime.now())
                .activa(true)
                .build();

        sesionTest2 = SesionActiva.builder()
                .id(2L)
                .usuario(usuarioTest)
                .tokenHash("hashed-token-2")
                .dispositivo("Móvil")
                .direccionIp("192.168.1.1")
                .userAgent("Chrome Mobile")
                .fechaCreacion(LocalDateTime.now().minusHours(2))
                .fechaExpiracion(LocalDateTime.now().plusHours(22))
                .ultimaActividad(LocalDateTime.now().minusHours(1))
                .activa(true)
                .build();
    }

    @Nested
    @DisplayName("Crear Sesión Tests")
    class CrearSesionTests {

        @Test
        @DisplayName("Crear sesión guarda correctamente los datos")
        void crearSesion_ConDatosValidos_GuardaSesion() {
            // Arrange
            when(jwtService.getExpirationTime()).thenReturn(86400000L); // 24 horas
            when(sesionActivaRepository.save(any(SesionActiva.class))).thenReturn(sesionTest);

            // Act
            SesionActiva resultado = sesionService.crearSesion(
                    usuarioTest, "jwt-token", "Desktop", "127.0.0.1", "Mozilla"
            );

            // Assert
            assertNotNull(resultado);
            
            ArgumentCaptor<SesionActiva> sesionCaptor = ArgumentCaptor.forClass(SesionActiva.class);
            verify(sesionActivaRepository).save(sesionCaptor.capture());
            
            SesionActiva sesionGuardada = sesionCaptor.getValue();
            assertEquals(usuarioTest, sesionGuardada.getUsuario());
            assertEquals("Desktop", sesionGuardada.getDispositivo());
            assertEquals("127.0.0.1", sesionGuardada.getDireccionIp());
            assertEquals("Mozilla", sesionGuardada.getUserAgent());
            assertTrue(sesionGuardada.getActiva());
            assertNotNull(sesionGuardada.getTokenHash());
        }
    }

    @Nested
    @DisplayName("Obtener Sesiones Activas Tests")
    class ObtenerSesionesActivasTests {

        @Test
        @DisplayName("Obtener sesiones activas retorna lista correcta")
        void obtenerSesionesActivas_ConSesionesExistentes_RetornaLista() {
            // Arrange
            List<SesionActiva> sesiones = Arrays.asList(sesionTest, sesionTest2);
            when(sesionActivaRepository.findByUsuarioIdAndActivaTrue(anyLong())).thenReturn(sesiones);

            // Act
            List<SesionActivaDTO> resultado = sesionService.obtenerSesionesActivas(1L, "token-actual");

            // Assert
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            verify(sesionActivaRepository).findByUsuarioIdAndActivaTrue(1L);
        }

        @Test
        @DisplayName("Obtener sesiones activas marca la sesión actual")
        void obtenerSesionesActivas_MarcaSesionActual() {
            // Arrange
            // Simulamos que el hash del token actual coincide con sesionTest
            String tokenActual = "token-actual";
            String hashTokenActual = sesionService.hashToken(tokenActual);
            sesionTest.setTokenHash(hashTokenActual);
            
            List<SesionActiva> sesiones = Arrays.asList(sesionTest, sesionTest2);
            when(sesionActivaRepository.findByUsuarioIdAndActivaTrue(anyLong())).thenReturn(sesiones);

            // Act
            List<SesionActivaDTO> resultado = sesionService.obtenerSesionesActivas(1L, tokenActual);

            // Assert
            assertTrue(resultado.stream().anyMatch(SesionActivaDTO::isSesionActual));
        }

        @Test
        @DisplayName("Obtener sesiones activas filtra sesiones expiradas")
        void obtenerSesionesActivas_FiltraSesionesExpiradas() {
            // Arrange
            sesionTest2.setFechaExpiracion(LocalDateTime.now().minusHours(1)); // Expirada
            List<SesionActiva> sesiones = Arrays.asList(sesionTest, sesionTest2);
            when(sesionActivaRepository.findByUsuarioIdAndActivaTrue(anyLong())).thenReturn(sesiones);

            // Act
            List<SesionActivaDTO> resultado = sesionService.obtenerSesionesActivas(1L, "token");

            // Assert
            assertEquals(1, resultado.size()); // Solo la no expirada
        }
    }

    @Nested
    @DisplayName("Revocar Sesión Tests")
    class RevocarSesionTests {

        @Test
        @DisplayName("Revocar sesión propia desactiva la sesión")
        void revocarSesion_ConSesionPropia_DesactivaSesion() {
            // Arrange
            when(sesionActivaRepository.findById(anyLong())).thenReturn(Optional.of(sesionTest));

            // Act
            sesionService.revocarSesion(1L, 1L);

            // Assert
            assertFalse(sesionTest.getActiva());
            verify(sesionActivaRepository).save(sesionTest);
        }

        @Test
        @DisplayName("Revocar sesión de otro usuario lanza excepción")
        void revocarSesion_ConSesionDeOtroUsuario_LanzaExcepcion() {
            // Arrange
            when(sesionActivaRepository.findById(anyLong())).thenReturn(Optional.of(sesionTest));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
                sesionService.revocarSesion(1L, 999L); // Usuario diferente
            });

            assertEquals("No tienes permiso para revocar esta sesión", exception.getMessage());
        }

        @Test
        @DisplayName("Revocar sesión inexistente lanza excepción")
        void revocarSesion_ConSesionInexistente_LanzaExcepcion() {
            // Arrange
            when(sesionActivaRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(IllegalArgumentException.class, () -> {
                sesionService.revocarSesion(999L, 1L);
            });
        }
    }

    @Nested
    @DisplayName("Revocar Todas Las Sesiones Tests")
    class RevocarTodasLasSesionesTests {

        @Test
        @DisplayName("Revocar todas las sesiones llama al repositorio")
        void revocarTodasLasSesiones_LlamaAlRepositorio() {
            // Act
            sesionService.revocarTodasLasSesiones(1L);

            // Assert
            verify(sesionActivaRepository).revocarTodasLasSesiones(1L);
        }
    }

    @Nested
    @DisplayName("Revocar Otras Sesiones Tests")
    class RevocarOtrasSesionesTests {

        @Test
        @DisplayName("Revocar otras sesiones mantiene la sesión actual")
        void revocarOtrasSesiones_MantieneSesionActual() {
            // Arrange
            String tokenActual = "token-actual";
            String hashTokenActual = sesionService.hashToken(tokenActual);
            sesionTest.setTokenHash(hashTokenActual);
            
            List<SesionActiva> sesiones = Arrays.asList(sesionTest, sesionTest2);
            when(sesionActivaRepository.findByUsuarioIdAndActivaTrue(anyLong())).thenReturn(sesiones);

            // Act
            sesionService.revocarOtrasSesiones(1L, tokenActual);

            // Assert
            assertTrue(sesionTest.getActiva()); // La actual sigue activa
            assertFalse(sesionTest2.getActiva()); // Las otras se desactivan
            verify(sesionActivaRepository).save(sesionTest2);
            verify(sesionActivaRepository, never()).save(sesionTest);
        }
    }

    @Nested
    @DisplayName("Validar Sesión Tests")
    class ValidarSesionTests {

        @Test
        @DisplayName("Validar sesión existente y activa retorna true")
        void validarSesion_ConSesionValida_RetornaTrue() {
            // Arrange
            when(sesionActivaRepository.findValidSession(anyString(), any(LocalDateTime.class)))
                    .thenReturn(Optional.of(sesionTest));

            // Act
            boolean resultado = sesionService.validarSesion("jwt-token");

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("Validar sesión inexistente retorna false")
        void validarSesion_ConSesionInexistente_RetornaFalse() {
            // Arrange
            when(sesionActivaRepository.findValidSession(anyString(), any(LocalDateTime.class)))
                    .thenReturn(Optional.empty());

            // Act
            boolean resultado = sesionService.validarSesion("invalid-token");

            // Assert
            assertFalse(resultado);
        }
    }

    @Nested
    @DisplayName("Hash Token Tests")
    class HashTokenTests {

        @Test
        @DisplayName("Hash token genera hash consistente")
        void hashToken_GeneraHashConsistente() {
            // Act
            String hash1 = sesionService.hashToken("mismo-token");
            String hash2 = sesionService.hashToken("mismo-token");

            // Assert
            assertEquals(hash1, hash2);
        }

        @Test
        @DisplayName("Hash token genera diferentes hashes para diferentes tokens")
        void hashToken_GeneraDiferentesHashes() {
            // Act
            String hash1 = sesionService.hashToken("token-1");
            String hash2 = sesionService.hashToken("token-2");

            // Assert
            assertNotEquals(hash1, hash2);
        }
    }
}
