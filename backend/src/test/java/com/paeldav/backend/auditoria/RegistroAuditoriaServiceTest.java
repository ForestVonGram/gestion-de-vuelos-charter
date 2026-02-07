package com.paeldav.backend.auditoria;

import com.paeldav.backend.application.dto.registroauditoria.RegistroAuditoriaDTO;
import com.paeldav.backend.application.mapper.RegistroAuditoriaMapper;
import com.paeldav.backend.application.service.impl.RegistroAuditoriaServiceImpl;
import com.paeldav.backend.domain.entity.RegistroAuditoria;
import com.paeldav.backend.domain.enums.TipoEventoAuditoria;
import com.paeldav.backend.infraestructure.repository.RegistroAuditoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegistroAuditoriaService Tests")
class RegistroAuditoriaServiceTest {

    @Mock
    private RegistroAuditoriaRepository registroAuditoriaRepository;

    @Mock
    private RegistroAuditoriaMapper registroAuditoriaMapper;

    @InjectMocks
    private RegistroAuditoriaServiceImpl registroAuditoriaService;

    private RegistroAuditoria registroAuditoria;
    private RegistroAuditoriaDTO registroAuditoriaDTO;

    @BeforeEach
    void setUp() {
        registroAuditoria = RegistroAuditoria.builder()
                .id(1L)
                .usuarioId(1L)
                .tipoEvento(TipoEventoAuditoria.LOGIN)
                .directorIP("192.168.1.100")
                .navegador("Mozilla/5.0")
                .resultado(true)
                .timestamp(LocalDateTime.now())
                .build();

        registroAuditoriaDTO = RegistroAuditoriaDTO.builder()
                .id(1L)
                .usuarioId(1L)
                .tipoEvento(TipoEventoAuditoria.LOGIN)
                .directorIP("192.168.1.100")
                .navegador("Mozilla/5.0")
                .resultado(true)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Registrar Evento Tests")
    class RegistrarEventoTests {

        @Test
        @DisplayName("Registrar evento exitosamente")
        void registrarEvento_ConDatosValidos_RegistraEvento() {
            when(registroAuditoriaRepository.save(any(RegistroAuditoria.class))).thenReturn(registroAuditoria);
            when(registroAuditoriaMapper.toDTO(registroAuditoria)).thenReturn(registroAuditoriaDTO);

            RegistroAuditoriaDTO resultado = registroAuditoriaService.registrarEvento(
                    1L, TipoEventoAuditoria.LOGIN, "192.168.1.100", "Mozilla/5.0", true, null
            );

            assertNotNull(resultado);
            assertEquals(TipoEventoAuditoria.LOGIN, resultado.getTipoEvento());
            assertTrue(resultado.getResultado());
            verify(registroAuditoriaRepository).save(any(RegistroAuditoria.class));
        }

        @Test
        @DisplayName("Registrar evento fallido")
        void registrarEvento_EventoFallido_RegistraEventoConResultadoFalso() {
            RegistroAuditoria registroFallido = RegistroAuditoria.builder()
                    .id(2L)
                    .usuarioId(null)
                    .tipoEvento(TipoEventoAuditoria.CREDENCIALES_INVALIDAS)
                    .directorIP("192.168.1.101")
                    .navegador("Chrome")
                    .resultado(false)
                    .detallesError("Email o contraseña incorrectos")
                    .timestamp(LocalDateTime.now())
                    .build();

            RegistroAuditoriaDTO registroFallidoDTO = RegistroAuditoriaDTO.builder()
                    .id(2L)
                    .tipoEvento(TipoEventoAuditoria.CREDENCIALES_INVALIDAS)
                    .directorIP("192.168.1.101")
                    .resultado(false)
                    .detallesError("Email o contraseña incorrectos")
                    .build();

            when(registroAuditoriaRepository.save(any(RegistroAuditoria.class))).thenReturn(registroFallido);
            when(registroAuditoriaMapper.toDTO(registroFallido)).thenReturn(registroFallidoDTO);

            RegistroAuditoriaDTO resultado = registroAuditoriaService.registrarEvento(
                    null, TipoEventoAuditoria.CREDENCIALES_INVALIDAS, "192.168.1.101", "Chrome", false,
                    "Email o contraseña incorrectos"
            );

            assertNotNull(resultado);
            assertFalse(resultado.getResultado());
            verify(registroAuditoriaRepository).save(any(RegistroAuditoria.class));
        }
    }

    @Nested
    @DisplayName("Obtener Eventos Tests")
    class ObtenerEventosTests {

        @Test
        @DisplayName("Obtener eventos por usuario")
        void obtenerAuditoriaPorUsuario_UsuarioExiste_RetornaEventos() {
            List<RegistroAuditoria> eventos = List.of(registroAuditoria);
            List<RegistroAuditoriaDTO> eventosDTO = List.of(registroAuditoriaDTO);

            when(registroAuditoriaRepository.findByUsuarioId(1L)).thenReturn(eventos);
            when(registroAuditoriaMapper.toDTOList(eventos)).thenReturn(eventosDTO);

            List<RegistroAuditoriaDTO> resultado = registroAuditoriaService.obtenerAuditoriaPorUsuario(1L);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(registroAuditoriaRepository).findByUsuarioId(1L);
        }

        @Test
        @DisplayName("Obtener intentos de acceso denegados")
        void obtenerIntentosAccesoDenegados_ConEventosFallidos_RetornaEventosFallidos() {
            RegistroAuditoria registroFallido = RegistroAuditoria.builder()
                    .id(2L)
                    .tipoEvento(TipoEventoAuditoria.CREDENCIALES_INVALIDAS)
                    .resultado(false)
                    .build();

            RegistroAuditoriaDTO registroFallidoDTO = RegistroAuditoriaDTO.builder()
                    .id(2L)
                    .tipoEvento(TipoEventoAuditoria.CREDENCIALES_INVALIDAS)
                    .resultado(false)
                    .build();

            List<RegistroAuditoria> eventos = List.of(registroFallido);
            List<RegistroAuditoriaDTO> eventosDTO = List.of(registroFallidoDTO);

            when(registroAuditoriaRepository.findByResultadoFalse()).thenReturn(eventos);
            when(registroAuditoriaMapper.toDTOList(eventos)).thenReturn(eventosDTO);

            List<RegistroAuditoriaDTO> resultado = registroAuditoriaService.obtenerIntentosAccesoDenegados();

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertFalse(resultado.get(0).getResultado());
            verify(registroAuditoriaRepository).findByResultadoFalse();
        }

        @Test
        @DisplayName("Obtener todos los eventos")
        void obtenerTodosLosEventos_RetornaTodosLosEventos() {
            List<RegistroAuditoria> eventos = List.of(registroAuditoria);
            List<RegistroAuditoriaDTO> eventosDTO = List.of(registroAuditoriaDTO);

            when(registroAuditoriaRepository.findAll()).thenReturn(eventos);
            when(registroAuditoriaMapper.toDTOList(eventos)).thenReturn(eventosDTO);

            List<RegistroAuditoriaDTO> resultado = registroAuditoriaService.obtenerTodosLosEventos();

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(registroAuditoriaRepository).findAll();
        }
    }
}
