package com.paeldav.backend.mantenimiento;

import com.paeldav.backend.application.dto.mantenimiento.MantenimientoDTO;
import com.paeldav.backend.application.mapper.MantenimientoMapper;
import com.paeldav.backend.application.service.impl.MantenimientoServiceImpl;
import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.entity.Mantenimiento;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import com.paeldav.backend.domain.enums.TipoMantenimiento;
import com.paeldav.backend.exception.AeronaveNoEncontradaException;
import com.paeldav.backend.infraestructure.repository.AeronaveRepository;
import com.paeldav.backend.infraestructure.repository.MantenimientoRepository;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("MantenimientoService Alertas Tests")
class MantenimientoServiceAlertasTest {

    @Mock
    private MantenimientoRepository mantenimientoRepository;

    @Mock
    private AeronaveRepository aeronaveRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MantenimientoMapper mantenimientoMapper;

    @InjectMocks
    private MantenimientoServiceImpl mantenimientoService;

    private Aeronave aeronaveTest;
    private Mantenimiento mantenimientoVencido;
    private Mantenimiento mantenimientoPendienteNoVencido;
    private MantenimientoDTO mantenimientoDTOVencido;

    @BeforeEach
    void setUp() {
        // Crear aeronave de prueba
        aeronaveTest = Aeronave.builder()
                .id(1L)
                .matricula("HK-5000")
                .modelo("Boeing 737")
                .fabricante("Boeing")
                .capacidadPasajeros(180)
                .capacidadTripulacion(6)
                .autonomiaKm(5000.0)
                .velocidadCruceroKmh(490.0)
                .fechaFabricacion(LocalDate.of(2010, 3, 15))
                .fechaUltimaRevision(LocalDate.now().minusMonths(6))
                .horasVueloTotales(15000.0)
                .estado(EstadoAeronave.DISPONIBLE)
                .build();

        // Mantenimiento PREVENTIVO vencido (fecha en el pasado, no completado)
        mantenimientoVencido = Mantenimiento.builder()
                .id(1L)
                .aeronave(aeronaveTest)
                .tipo(TipoMantenimiento.PREVENTIVO)
                .descripcion("Revisión de motores")
                .fechaInicio(LocalDateTime.now().minusDays(3))
                .responsable(null)
                .costo(5000.0)
                .completado(false)
                .build();

        // Mantenimiento CORRECTIVO pendiente pero NO vencido (fecha en el futuro)
        mantenimientoPendienteNoVencido = Mantenimiento.builder()
                .id(2L)
                .aeronave(aeronaveTest)
                .tipo(TipoMantenimiento.CORRECTIVO)
                .descripcion("Reparación de sistema hidráulico")
                .fechaInicio(LocalDateTime.now().plusDays(5))
                .responsable(null)
                .costo(8000.0)
                .completado(false)
                .build();

        // DTO de mantenimiento vencido
        mantenimientoDTOVencido = MantenimientoDTO.builder()
                .id(1L)
                .aeronaveId(1L)
                .aeronaveMatricula("HK-5000")
                .tipo(TipoMantenimiento.PREVENTIVO)
                .descripcion("Revisión de motores")
                .fechaInicio(LocalDateTime.now().minusDays(3))
                .responsableId(null)
                .responsableNombre(null)
                .costo(5000.0)
                .completado(false)
                .build();
    }

    // ==================== VERIFICAR MANTENIMIENTO VENCIDO TESTS ====================

    @Nested
    @DisplayName("Verificar Mantenimiento Vencido Tests")
    class VerificarMantenimientoVencidoTests {

        @Test
        @DisplayName("Verificar mantenimiento vencido retorna true cuando existe")
        void verificarMantenimientoVencido_ConMantenimientoVencido_RetornaTrue() {
            // Arrange
            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L))
                    .thenReturn(Collections.singletonList(mantenimientoVencido));

            // Act
            boolean resultado = mantenimientoService.verificarMantenimientoVencido(1L);

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("Verificar mantenimiento vencido retorna false cuando no existe")
        void verificarMantenimientoVencido_SinMantenimientoVencido_RetornaFalse() {
            // Arrange
            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L))
                    .thenReturn(Collections.singletonList(mantenimientoPendienteNoVencido));

            // Act
            boolean resultado = mantenimientoService.verificarMantenimientoVencido(1L);

            // Assert
            assertFalse(resultado);
        }

        @Test
        @DisplayName("Verificar mantenimiento vencido con aeronave inexistente lanza excepción")
        void verificarMantenimientoVencido_AeronaveInexistente_LanzaExcepcion() {
            // Arrange
            when(aeronaveRepository.existsById(999L)).thenReturn(false);

            // Act & Assert
            AeronaveNoEncontradaException exception = assertThrows(
                    AeronaveNoEncontradaException.class,
                    () -> mantenimientoService.verificarMantenimientoVencido(999L)
            );
            assertEquals("Aeronave no encontrada con ID: 999", exception.getMessage());
        }

        @Test
        @DisplayName("Verificar mantenimiento vencido retorna false sin mantenimientos")
        void verificarMantenimientoVencido_SinMantenimientos_RetornaFalse() {
            // Arrange
            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L)).thenReturn(Collections.emptyList());

            // Act
            boolean resultado = mantenimientoService.verificarMantenimientoVencido(1L);

            // Assert
            assertFalse(resultado);
        }

        @Test
        @DisplayName("Verificar mantenimiento vencido ignora completados")
        void verificarMantenimientoVencido_IgnoraCompletados_RetornaFalse() {
            // Arrange
            Mantenimiento mantenimientoCompletado = Mantenimiento.builder()
                    .id(3L)
                    .aeronave(aeronaveTest)
                    .tipo(TipoMantenimiento.PREVENTIVO)
                    .descripcion("Revisión completada")
                    .fechaInicio(LocalDateTime.now().minusDays(5))
                    .completado(true)  // Completado
                    .build();

            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L))
                    .thenReturn(Collections.singletonList(mantenimientoCompletado));

            // Act
            boolean resultado = mantenimientoService.verificarMantenimientoVencido(1L);

            // Assert
            assertFalse(resultado);
        }
    }

    // ==================== OBTENER MANTENIMIENTOS VENCIDOS TESTS ====================

    @Nested
    @DisplayName("Obtener Mantenimientos Vencidos Tests")
    class ObtenerMantenimientosVencidosTests {

        @Test
        @DisplayName("Obtener mantenimientos vencidos de aeronave")
        void obtenerMantenimientosVencidos_Exitoso() {
            // Arrange
            List<Mantenimiento> mantenimientosVencidos = Collections.singletonList(mantenimientoVencido);
            List<MantenimientoDTO> mantenimientosDTO = Collections.singletonList(mantenimientoDTOVencido);

            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L))
                    .thenReturn(Collections.singletonList(mantenimientoVencido));
            when(mantenimientoMapper.toDTOList(mantenimientosVencidos)).thenReturn(mantenimientosDTO);

            // Act
            List<MantenimientoDTO> resultado = mantenimientoService.obtenerMantenimientosVencidos(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals("Revisión de motores", resultado.get(0).getDescripcion());
        }

        @Test
        @DisplayName("Obtener mantenimientos vencidos lista vacía cuando no existen")
        void obtenerMantenimientosVencidos_SinVencidos_RetornaListaVacia() {
            // Arrange
            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L))
                    .thenReturn(Collections.singletonList(mantenimientoPendienteNoVencido));
            when(mantenimientoMapper.toDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

            // Act
            List<MantenimientoDTO> resultado = mantenimientoService.obtenerMantenimientosVencidos(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(0, resultado.size());
        }

        @Test
        @DisplayName("Obtener mantenimientos vencidos con múltiples vencidos")
        void obtenerMantenimientosVencidos_ConMultiplesVencidos_Exitoso() {
            // Arrange
            Mantenimiento mantenimientoVencido2 = Mantenimiento.builder()
                    .id(3L)
                    .aeronave(aeronaveTest)
                    .tipo(TipoMantenimiento.CORRECTIVO)
                    .descripcion("Reparación urgente")
                    .fechaInicio(LocalDateTime.now().minusDays(1))
                    .completado(false)
                    .build();

            List<Mantenimiento> mantenimientosVencidos = Arrays.asList(mantenimientoVencido, mantenimientoVencido2);
            MantenimientoDTO dto1 = mantenimientoDTOVencido;
            MantenimientoDTO dto2 = MantenimientoDTO.builder()
                    .id(3L)
                    .aeronaveId(1L)
                    .aeronaveMatricula("HK-5000")
                    .tipo(TipoMantenimiento.CORRECTIVO)
                    .descripcion("Reparación urgente")
                    .completado(false)
                    .build();

            List<MantenimientoDTO> mantenimientosDTO = Arrays.asList(dto1, dto2);

            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L)).thenReturn(mantenimientosVencidos);
            when(mantenimientoMapper.toDTOList(mantenimientosVencidos)).thenReturn(mantenimientosDTO);

            // Act
            List<MantenimientoDTO> resultado = mantenimientoService.obtenerMantenimientosVencidos(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
        }

        @Test
        @DisplayName("Obtener mantenimientos vencidos con aeronave inexistente lanza excepción")
        void obtenerMantenimientosVencidos_AeronaveInexistente_LanzaExcepcion() {
            // Arrange
            when(aeronaveRepository.existsById(999L)).thenReturn(false);

            // Act & Assert
            AeronaveNoEncontradaException exception = assertThrows(
                    AeronaveNoEncontradaException.class,
                    () -> mantenimientoService.obtenerMantenimientosVencidos(999L)
            );
            assertEquals("Aeronave no encontrada con ID: 999", exception.getMessage());
        }
    }

    // ==================== VERIFICAR MANTENIMIENTO PENDIENTE TESTS ====================

    @Nested
    @DisplayName("Verificar Mantenimiento Pendiente Tests")
    class VerificarMantenimientoPendienteTests {

        @Test
        @DisplayName("Verificar mantenimiento pendiente retorna true cuando existe")
        void verificarMantenimientoPendiente_ConMantenimientoPendiente_RetornaTrue() {
            // Arrange
            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L))
                    .thenReturn(Collections.singletonList(mantenimientoPendienteNoVencido));

            // Act
            boolean resultado = mantenimientoService.verificarMantenimientoPendiente(1L);

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("Verificar mantenimiento pendiente retorna true para vencidos")
        void verificarMantenimientoPendiente_ConMantenimientoVencido_RetornaTrue() {
            // Arrange
            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L))
                    .thenReturn(Collections.singletonList(mantenimientoVencido));

            // Act
            boolean resultado = mantenimientoService.verificarMantenimientoPendiente(1L);

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("Verificar mantenimiento pendiente retorna false sin pendientes")
        void verificarMantenimientoPendiente_SinMantenimientoPendiente_RetornaFalse() {
            // Arrange
            Mantenimiento mantenimientoCompletado = Mantenimiento.builder()
                    .id(4L)
                    .aeronave(aeronaveTest)
                    .tipo(TipoMantenimiento.PREVENTIVO)
                    .descripcion("Completado")
                    .fechaInicio(LocalDateTime.now().minusDays(10))
                    .completado(true)  // Completado
                    .build();

            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L))
                    .thenReturn(Collections.singletonList(mantenimientoCompletado));

            // Act
            boolean resultado = mantenimientoService.verificarMantenimientoPendiente(1L);

            // Assert
            assertFalse(resultado);
        }

        @Test
        @DisplayName("Verificar mantenimiento pendiente con aeronave inexistente lanza excepción")
        void verificarMantenimientoPendiente_AeronaveInexistente_LanzaExcepcion() {
            // Arrange
            when(aeronaveRepository.existsById(999L)).thenReturn(false);

            // Act & Assert
            AeronaveNoEncontradaException exception = assertThrows(
                    AeronaveNoEncontradaException.class,
                    () -> mantenimientoService.verificarMantenimientoPendiente(999L)
            );
            assertEquals("Aeronave no encontrada con ID: 999", exception.getMessage());
        }

        @Test
        @DisplayName("Verificar mantenimiento pendiente retorna false sin mantenimientos")
        void verificarMantenimientoPendiente_SinMantenimientos_RetornaFalse() {
            // Arrange
            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L)).thenReturn(Collections.emptyList());

            // Act
            boolean resultado = mantenimientoService.verificarMantenimientoPendiente(1L);

            // Assert
            assertFalse(resultado);
        }
    }

    // ==================== INTEGRACIÓN Y TRAZABILIDAD TESTS ====================

    @Nested
    @DisplayName("Integración y Trazabilidad Tests")
    class IntegracionYTrazabilidadTests {

        @Test
        @DisplayName("Verificar trazabilidad de historial de mantenimiento")
        void verificarTrazabilidadHistorial_Exitoso() {
            // Arrange
            Mantenimiento m1 = Mantenimiento.builder()
                    .id(10L)
                    .aeronave(aeronaveTest)
                    .tipo(TipoMantenimiento.PREVENTIVO)
                    .fechaInicio(LocalDateTime.now().minusDays(10))
                    .completado(true)  // Completado, así que no cuenta como vencido
                    .build();

            Mantenimiento m2 = Mantenimiento.builder()
                    .id(11L)
                    .aeronave(aeronaveTest)
                    .tipo(TipoMantenimiento.PREVENTIVO)
                    .fechaInicio(LocalDateTime.now().plusDays(10))
                    .completado(false)  // Pendiente pero no vencido (fecha futura)
                    .build();

            List<Mantenimiento> historial = Arrays.asList(m1, m2);

            MantenimientoDTO dto1 = MantenimientoDTO.builder()
                    .id(10L)
                    .aeronaveId(1L)
                    .aeronaveMatricula("HK-5000")
                    .tipo(TipoMantenimiento.PREVENTIVO)
                    .completado(true)
                    .build();

            MantenimientoDTO dto2 = MantenimientoDTO.builder()
                    .id(11L)
                    .aeronaveId(1L)
                    .aeronaveMatricula("HK-5000")
                    .tipo(TipoMantenimiento.PREVENTIVO)
                    .completado(false)
                    .build();

            List<MantenimientoDTO> historialDTO = Arrays.asList(dto1, dto2);

            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L)).thenReturn(historial);
            when(mantenimientoMapper.toDTOList(historial)).thenReturn(historialDTO);

            // Act
            List<MantenimientoDTO> todosMantenimientos = mantenimientoService.obtenerMantenimientosPorAeronave(1L);
            boolean tieneVencidos = mantenimientoService.verificarMantenimientoVencido(1L);

            // Assert
            assertNotNull(todosMantenimientos);
            assertEquals(2, todosMantenimientos.size());
            // m1 está completado, así que no cuenta como vencido
            // m2 tiene fecha futura, así que tampoco está vencido
            assertFalse(tieneVencidos);
            assertEquals(true, todosMantenimientos.get(0).getCompletado());
            assertEquals(false, todosMantenimientos.get(1).getCompletado());
        }

        @Test
        @DisplayName("Verificar cambio de estado de operatividad después de completar mantenimiento")
        void verificarCambioOperatividadAlCompletar_Exitoso() {
            // Arrange - Antes: con mantenimiento vencido
            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L))
                    .thenReturn(Collections.singletonList(mantenimientoVencido));

            boolean operativaBefore = !mantenimientoService.verificarMantenimientoVencido(1L);
            assertTrue(!operativaBefore); // NO es operativa

            // Act - Simular completar el mantenimiento
            Mantenimiento mantenimientoCompletado = mantenimientoVencido;
            mantenimientoCompletado.setCompletado(true);

            when(mantenimientoRepository.findByAeronaveId(1L))
                    .thenReturn(Collections.singletonList(mantenimientoCompletado));

            boolean operativaAfter = !mantenimientoService.verificarMantenimientoVencido(1L);

            // Assert - Después: sin mantenimiento vencido, es operativa
            assertTrue(operativaAfter);
        }
    }
}
