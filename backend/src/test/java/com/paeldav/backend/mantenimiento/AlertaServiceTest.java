package com.paeldav.backend.mantenimiento;

import com.paeldav.backend.application.dto.alerta.AlertaCreateDTO;
import com.paeldav.backend.application.dto.alerta.AlertaDTO;
import com.paeldav.backend.application.mapper.AlertaMapper;
import com.paeldav.backend.application.service.base.MantenimientoService;
import com.paeldav.backend.application.service.impl.AlertaServiceImpl;
import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.entity.Alerta;
import com.paeldav.backend.domain.entity.Mantenimiento;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import com.paeldav.backend.domain.enums.TipoAlerta;
import com.paeldav.backend.domain.enums.TipoMantenimiento;
import com.paeldav.backend.exception.AeronaveNoEncontradaException;
import com.paeldav.backend.exception.AlertaNoEncontradaException;
import com.paeldav.backend.infraestructure.repository.AeronaveRepository;
import com.paeldav.backend.infraestructure.repository.AlertaRepository;
import com.paeldav.backend.infraestructure.repository.MantenimientoRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AlertaService Tests")
class AlertaServiceTest {

    @Mock
    private AlertaRepository alertaRepository;

    @Mock
    private AeronaveRepository aeronaveRepository;

    @Mock
    private MantenimientoRepository mantenimientoRepository;

    @Mock
    private AlertaMapper alertaMapper;

    @Mock
    private MantenimientoService mantenimientoService;

    @InjectMocks
    private AlertaServiceImpl alertaService;

    private Aeronave aeronaveTest;
    private Alerta alertaTest;
    private AlertaDTO alertaDTOTest;
    private Mantenimiento mantenimientoVencido;

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

        // Mantenimiento vencido
        mantenimientoVencido = Mantenimiento.builder()
                .id(1L)
                .aeronave(aeronaveTest)
                .tipo(TipoMantenimiento.PREVENTIVO)
                .descripcion("Revisión de motores")
                .fechaInicio(LocalDateTime.now().minusDays(2))
                .responsable(null)
                .costo(5000.0)
                .completado(false)
                .build();

        // Alerta de prueba
        alertaTest = Alerta.builder()
                .id(1L)
                .aeronave(aeronaveTest)
                .tipo(TipoAlerta.MANTENIMIENTO_VENCIDO)
                .descripcion("Mantenimiento PREVENTIVO vencido desde hace 2 días")
                .fechaCreacion(LocalDateTime.now())
                .activa(true)
                .mantenimientoRelacionado(mantenimientoVencido)
                .build();

        // DTO de alerta
        alertaDTOTest = AlertaDTO.builder()
                .id(1L)
                .aeronaveId(1L)
                .aeronaveMatricula("HK-5000")
                .tipo(TipoAlerta.MANTENIMIENTO_VENCIDO)
                .descripcion("Mantenimiento PREVENTIVO vencido desde hace 2 días")
                .fechaCreacion(LocalDateTime.now())
                .activa(true)
                .mantenimientoRelacionadoId(1L)
                .build();
    }

    // ==================== CREAR ALERTA TESTS ====================

    @Nested
    @DisplayName("Crear Alerta Tests")
    class CrearAlertaTests {

        @Test
        @DisplayName("Crear alerta de mantenimiento vencido exitosamente")
        void crearAlerta_MantenimientoVencido_Exitoso() {
            // Arrange
            AlertaCreateDTO alertaCreateDTO = AlertaCreateDTO.builder()
                    .aeronaveId(1L)
                    .tipo(TipoAlerta.MANTENIMIENTO_VENCIDO)
                    .descripcion("Mantenimiento PREVENTIVO vencido desde hace 2 días")
                    .mantenimientoRelacionadoId(1L)
                    .build();

            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(mantenimientoRepository.findById(1L)).thenReturn(Optional.of(mantenimientoVencido));
            when(alertaMapper.toEntity(alertaCreateDTO)).thenReturn(alertaTest);
            when(alertaRepository.save(any(Alerta.class))).thenReturn(alertaTest);
            when(alertaMapper.toDTO(alertaTest)).thenReturn(alertaDTOTest);

            // Act
            AlertaDTO resultado = alertaService.crearAlerta(alertaCreateDTO);

            // Assert
            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            assertEquals(TipoAlerta.MANTENIMIENTO_VENCIDO, resultado.getTipo());
            assertTrue(resultado.getActiva());
            verify(aeronaveRepository).findById(1L);
            verify(alertaRepository).save(any(Alerta.class));
        }

        @Test
        @DisplayName("Crear alerta con aeronave inexistente lanza excepción")
        void crearAlerta_AeronaveInexistente_LanzaExcepcion() {
            // Arrange
            AlertaCreateDTO alertaCreateDTO = AlertaCreateDTO.builder()
                    .aeronaveId(999L)
                    .tipo(TipoAlerta.MANTENIMIENTO_VENCIDO)
                    .descripcion("Test")
                    .build();

            when(aeronaveRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            AeronaveNoEncontradaException exception = assertThrows(
                    AeronaveNoEncontradaException.class,
                    () -> alertaService.crearAlerta(alertaCreateDTO)
            );
            assertEquals("Aeronave no encontrada con ID: 999", exception.getMessage());
            verify(alertaRepository, never()).save(any());
        }
    }

    // ==================== OBTENER ALERTAS TESTS ====================

    @Nested
    @DisplayName("Obtener Alertas Tests")
    class ObtenerAlertasTests {

        @Test
        @DisplayName("Obtener alerta por ID exitosamente")
        void obtenerAlertaPorId_ConIdValido_Exitoso() {
            // Arrange
            when(alertaRepository.findById(1L)).thenReturn(Optional.of(alertaTest));
            when(alertaMapper.toDTO(alertaTest)).thenReturn(alertaDTOTest);

            // Act
            AlertaDTO resultado = alertaService.obtenerAlertaPorId(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            assertEquals(TipoAlerta.MANTENIMIENTO_VENCIDO, resultado.getTipo());
            verify(alertaRepository).findById(1L);
        }

        @Test
        @DisplayName("Obtener alerta con ID inexistente lanza excepción")
        void obtenerAlertaPorId_ConIdInexistente_LanzaExcepcion() {
            // Arrange
            when(alertaRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            AlertaNoEncontradaException exception = assertThrows(
                    AlertaNoEncontradaException.class,
                    () -> alertaService.obtenerAlertaPorId(999L)
            );
            assertEquals("Alerta no encontrada con ID: 999", exception.getMessage());
        }

        @Test
        @DisplayName("Obtener todas las alertas")
        void obtenerTodasAlertas_Exitoso() {
            // Arrange
            List<Alerta> alertas = Collections.singletonList(alertaTest);
            List<AlertaDTO> alertasDTO = Collections.singletonList(alertaDTOTest);

            when(alertaRepository.findAll()).thenReturn(alertas);
            when(alertaMapper.toDTOList(alertas)).thenReturn(alertasDTO);

            // Act
            List<AlertaDTO> resultado = alertaService.obtenerTodasAlertas();

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals(TipoAlerta.MANTENIMIENTO_VENCIDO, resultado.get(0).getTipo());
            verify(alertaRepository).findAll();
        }

        @Test
        @DisplayName("Obtener alertas por aeronave")
        void obtenerAlertasPorAeronave_Exitoso() {
            // Arrange
            List<Alerta> alertas = Collections.singletonList(alertaTest);
            List<AlertaDTO> alertasDTO = Collections.singletonList(alertaDTOTest);

            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(alertaRepository.findByAeronaveId(1L)).thenReturn(alertas);
            when(alertaMapper.toDTOList(alertas)).thenReturn(alertasDTO);

            // Act
            List<AlertaDTO> resultado = alertaService.obtenerAlertasPorAeronave(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(aeronaveRepository).existsById(1L);
        }

        @Test
        @DisplayName("Obtener alertas activas por aeronave")
        void obtenerAlertasActivasPorAeronave_Exitoso() {
            // Arrange
            List<Alerta> alertasActivas = Collections.singletonList(alertaTest);
            List<AlertaDTO> alertasDTO = Collections.singletonList(alertaDTOTest);

            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(alertaRepository.findAlertasActivasPorAeronave(1L)).thenReturn(alertasActivas);
            when(alertaMapper.toDTOList(alertasActivas)).thenReturn(alertasDTO);

            // Act
            List<AlertaDTO> resultado = alertaService.obtenerAlertasActivasPorAeronave(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertTrue(resultado.get(0).getActiva());
        }

        @Test
        @DisplayName("Obtener alertas activas")
        void obtenerAlertasActivas_Exitoso() {
            // Arrange
            List<Alerta> alertasActivas = Collections.singletonList(alertaTest);
            List<AlertaDTO> alertasDTO = Collections.singletonList(alertaDTOTest);

            when(alertaRepository.findByActiva(true)).thenReturn(alertasActivas);
            when(alertaMapper.toDTOList(alertasActivas)).thenReturn(alertasDTO);

            // Act
            List<AlertaDTO> resultado = alertaService.obtenerAlertasActivas();

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertTrue(resultado.stream().allMatch(AlertaDTO::getActiva));
        }
    }

    // ==================== GENERAR ALERTAS AUTOMÁTICAMENTE TESTS ====================

    @Nested
    @DisplayName("Generar Alertas Automáticamente Tests")
    class GenerarAlertasAutomaticamenteTests {

        @Test
        @DisplayName("Generar alerta de mantenimiento vencido")
        void generarAlertaMantenimientoVencido_Exitoso() {
            // Arrange
            List<Mantenimiento> mantenimientosVencidos = Collections.singletonList(mantenimientoVencido);

            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L)).thenReturn(mantenimientosVencidos);
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(alertaRepository.save(any(Alerta.class))).thenReturn(alertaTest);
            when(alertaMapper.toDTO(alertaTest)).thenReturn(alertaDTOTest);

            // Act
            AlertaDTO resultado = alertaService.generarAlertaMantenimientoVencido(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(TipoAlerta.MANTENIMIENTO_VENCIDO, resultado.getTipo());
            verify(alertaRepository).save(any(Alerta.class));
        }

        @Test
        @DisplayName("Generar alerta de mantenimiento próximo")
        void generarAlertaMantenimientoProximo_Exitoso() {
            // Arrange
            Mantenimiento mantenimientoProximo = Mantenimiento.builder()
                    .id(2L)
                    .aeronave(aeronaveTest)
                    .tipo(TipoMantenimiento.PREVENTIVO)
                    .descripcion("Revisión programada")
                    .fechaInicio(LocalDateTime.now().plusDays(3))
                    .completado(false)
                    .build();

            List<Mantenimiento> mantenimientos = Collections.singletonList(mantenimientoProximo);

            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L)).thenReturn(mantenimientos);
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(alertaRepository.save(any(Alerta.class))).thenReturn(alertaTest);
            when(alertaMapper.toDTO(alertaTest)).thenReturn(alertaDTOTest);

            // Act
            AlertaDTO resultado = alertaService.generarAlertaMantenimientoProximo(1L, 7);

            // Assert
            assertNotNull(resultado);
            verify(alertaRepository).save(any(Alerta.class));
        }
    }

    // ==================== RESOLVER ALERTA TESTS ====================

    @Nested
    @DisplayName("Resolver Alerta Tests")
    class ResolverAlertaTests {

        @Test
        @DisplayName("Resolver alerta exitosamente")
        void resolverAlerta_Exitoso() {
            // Arrange
            Alerta alertaResuelta = Alerta.builder()
                    .id(1L)
                    .aeronave(aeronaveTest)
                    .tipo(TipoAlerta.MANTENIMIENTO_VENCIDO)
                    .descripcion("Mantenimiento PREVENTIVO vencido")
                    .fechaCreacion(LocalDateTime.now().minusHours(2))
                    .fechaResolucion(LocalDateTime.now())
                    .activa(false)
                    .observaciones("Mantenimiento completado")
                    .build();

            AlertaDTO alertaDTOResuelta = AlertaDTO.builder()
                    .id(1L)
                    .aeronaveId(1L)
                    .aeronaveMatricula("HK-5000")
                    .tipo(TipoAlerta.MANTENIMIENTO_VENCIDO)
                    .descripcion("Mantenimiento PREVENTIVO vencido")
                    .fechaCreacion(LocalDateTime.now().minusHours(2))
                    .fechaResolucion(LocalDateTime.now())
                    .activa(false)
                    .observaciones("Mantenimiento completado")
                    .build();

            when(alertaRepository.findById(1L)).thenReturn(Optional.of(alertaTest));
            when(alertaRepository.save(any(Alerta.class))).thenReturn(alertaResuelta);
            when(alertaMapper.toDTO(alertaResuelta)).thenReturn(alertaDTOResuelta);

            // Act
            AlertaDTO resultado = alertaService.resolverAlerta(1L, "Mantenimiento completado");

            // Assert
            assertNotNull(resultado);
            assertFalse(resultado.getActiva());
            assertNotNull(resultado.getFechaResolucion());
            verify(alertaRepository).findById(1L);
            verify(alertaRepository).save(any(Alerta.class));
        }
    }

    // ==================== OBTENER ALERTAS POR MANTENIMIENTO TESTS ====================

    @Nested
    @DisplayName("Obtener Alertas por Mantenimiento Tests")
    class ObtenerAlertasPorMantenimientoTests {

        @Test
        @DisplayName("Obtener alertas relacionadas a un mantenimiento")
        void obtenerAlertasPorMantenimiento_Exitoso() {
            // Arrange
            List<Alerta> alertas = Collections.singletonList(alertaTest);
            List<AlertaDTO> alertasDTO = Collections.singletonList(alertaDTOTest);

            when(alertaRepository.findByMantenimientoRelacionadoId(1L)).thenReturn(alertas);
            when(alertaMapper.toDTOList(alertas)).thenReturn(alertasDTO);

            // Act
            List<AlertaDTO> resultado = alertaService.obtenerAlertasPorMantenimiento(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(alertaRepository).findByMantenimientoRelacionadoId(1L);
        }
    }
}
