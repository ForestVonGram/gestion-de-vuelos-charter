package com.paeldav.backend.mantenimiento;

import com.paeldav.backend.application.service.base.ValidadorOperativoService;
import com.paeldav.backend.application.service.impl.ValidadorOperativoServiceImpl;
import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.entity.Mantenimiento;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import com.paeldav.backend.domain.enums.TipoMantenimiento;
import com.paeldav.backend.exception.AeronaveNoEncontradaException;
import com.paeldav.backend.infraestructure.repository.AeronaveRepository;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("ValidadorOperativoService Tests")
class ValidadorOperativoServiceTest {

    @Mock
    private AeronaveRepository aeronaveRepository;

    @Mock
    private MantenimientoRepository mantenimientoRepository;

    @InjectMocks
    private ValidadorOperativoServiceImpl validadorOperativoService;

    private Aeronave aeronaveTest;
    private Mantenimiento mantenimientoVencido;
    private Mantenimiento mantenimientoPendiente;

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

        // Mantenimiento PREVENTIVO vencido
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

        // Mantenimiento CORRECTIVO pendiente (no vencido aún)
        mantenimientoPendiente = Mantenimiento.builder()
                .id(2L)
                .aeronave(aeronaveTest)
                .tipo(TipoMantenimiento.CORRECTIVO)
                .descripcion("Reparación de sistema hidráulico")
                .fechaInicio(LocalDateTime.now().plusDays(3))
                .responsable(null)
                .costo(8000.0)
                .completado(false)
                .build();
    }

    // ==================== VALIDAR OPERATIVIDAD TESTS ====================

    @Nested
    @DisplayName("Validar Operatividad Tests")
    class ValidarOperatividadTests {

        @Test
        @DisplayName("Aeronave es operativa sin mantenimientos vencidos")
        void esAeronaveOperativa_SinMantenimientosVencidos_RetornaTrue() {
            // Arrange
            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L)).thenReturn(Collections.emptyList());

            // Act
            boolean resultado = validadorOperativoService.esAeronaveOperativa(1L);

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("Aeronave NO es operativa con mantenimiento PREVENTIVO vencido")
        void esAeronaveOperativa_ConMantenimientoPreventivVencido_RetornaFalse() {
            // Arrange
            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L))
                    .thenReturn(Collections.singletonList(mantenimientoVencido));

            // Act
            boolean resultado = validadorOperativoService.esAeronaveOperativa(1L);

            // Assert
            assertFalse(resultado);
        }

        @Test
        @DisplayName("Aeronave NO es operativa con mantenimiento CORRECTIVO vencido")
        void esAeronaveOperativa_ConMantenimientoCorrectivVencido_RetornaFalse() {
            // Arrange
            Mantenimiento mantenimientoCorrectivVencido = Mantenimiento.builder()
                    .id(3L)
                    .aeronave(aeronaveTest)
                    .tipo(TipoMantenimiento.CORRECTIVO)
                    .descripcion("Reparación de motor")
                    .fechaInicio(LocalDateTime.now().minusDays(1))
                    .completado(false)
                    .build();

            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L))
                    .thenReturn(Collections.singletonList(mantenimientoCorrectivVencido));

            // Act
            boolean resultado = validadorOperativoService.esAeronaveOperativa(1L);

            // Assert
            assertFalse(resultado);
        }

        @Test
        @DisplayName("Aeronave es operativa con mantenimiento pendiente (no vencido)")
        void esAeronaveOperativa_ConMantenimientoPendienteNoVencido_RetornaTrue() {
            // Arrange
            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L))
                    .thenReturn(Collections.singletonList(mantenimientoPendiente));

            // Act
            boolean resultado = validadorOperativoService.esAeronaveOperativa(1L);

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("Validar aeronave inexistente lanza excepción")
        void esAeronaveOperativa_AeronaveInexistente_LanzaExcepcion() {
            // Arrange
            when(aeronaveRepository.existsById(999L)).thenReturn(false);

            // Act & Assert
            AeronaveNoEncontradaException exception = assertThrows(
                    AeronaveNoEncontradaException.class,
                    () -> validadorOperativoService.esAeronaveOperativa(999L)
            );
            assertEquals("Aeronave no encontrada con ID: 999", exception.getMessage());
        }
    }

    // ==================== OBTENER RAZÓN NO OPERATIVA TESTS ====================

    @Nested
    @DisplayName("Obtener Razón No Operativa Tests")
    class ObtenerRazonNoOperativaTests {

        @Test
        @DisplayName("Obtener razón para aeronave operativa retorna null")
        void obtenerRazonNoOperativa_AeronaveOperativa_RetornaNul() {
            // Arrange
            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L)).thenReturn(Collections.emptyList());

            // Act
            String razon = validadorOperativoService.obtenerRazonNoOperativa(1L);

            // Assert
            assertNull(razon);
        }

        @Test
        @DisplayName("Obtener razón para aeronave con mantenimiento vencido")
        void obtenerRazonNoOperativa_ConMantenimientoVencido_RetornaRazon() {
            // Arrange
            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L))
                    .thenReturn(Collections.singletonList(mantenimientoVencido));

            // Act
            String razon = validadorOperativoService.obtenerRazonNoOperativa(1L);

            // Assert
            assertNotNull(razon);
            assertTrue(razon.contains("Aeronave bloqueada"));
            assertTrue(razon.contains("pendiente"));
        }

        @Test
        @DisplayName("Prioriza CORRECTIVO sobre PREVENTIVO en razón")
        void obtenerRazonNoOperativa_PriorizaCorrectivo_Exitoso() {
            // Arrange
            Mantenimiento correctivoVencido = Mantenimiento.builder()
                    .id(4L)
                    .aeronave(aeronaveTest)
                    .tipo(TipoMantenimiento.CORRECTIVO)
                    .descripcion("Reparación crítica")
                    .fechaInicio(LocalDateTime.now().minusDays(1))
                    .completado(false)
                    .build();

            List<Mantenimiento> mantenimientos = Arrays.asList(mantenimientoVencido, correctivoVencido);

            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L)).thenReturn(mantenimientos);

            // Act
            String razon = validadorOperativoService.obtenerRazonNoOperativa(1L);

            // Assert
            assertNotNull(razon);
            assertTrue(razon.contains("CORRECTIVO"));
        }
    }

    // ==================== VERIFICAR MANTENIMIENTO VENCIDO TESTS ====================

    @Nested
    @DisplayName("Verificar Mantenimiento Vencido Tests")
    class VerificarMantenimientoVencidoTests {

        @Test
        @DisplayName("Verificar mantenimiento vencido retorna true")
        void tieneMantenimientoVencido_ConMantenimientoVencido_RetornaTrue() {
            // Arrange
            when(mantenimientoRepository.findByAeronaveId(1L))
                    .thenReturn(Collections.singletonList(mantenimientoVencido));

            // Act
            boolean resultado = validadorOperativoService.tieneMantenimientoVencido(1L);

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("Verificar sin mantenimiento vencido retorna false")
        void tieneMantenimientoVencido_SinMantenimientoVencido_RetornaFalse() {
            // Arrange
            when(mantenimientoRepository.findByAeronaveId(1L)).thenReturn(Collections.emptyList());

            // Act
            boolean resultado = validadorOperativoService.tieneMantenimientoVencido(1L);

            // Assert
            assertFalse(resultado);
        }
    }

    // ==================== VERIFICAR MANTENIMIENTO PENDIENTE TESTS ====================

    @Nested
    @DisplayName("Verificar Mantenimiento Pendiente Tests")
    class VerificarMantenimientoPendienteTests {

        @Test
        @DisplayName("Verificar mantenimiento pendiente retorna true")
        void tieneMantenimientoPendiente_ConMantenimientoPendiente_RetornaTrue() {
            // Arrange
            when(mantenimientoRepository.findByAeronaveId(1L))
                    .thenReturn(Collections.singletonList(mantenimientoPendiente));

            // Act
            boolean resultado = validadorOperativoService.tieneMantenimientoPendiente(1L);

            // Assert
            assertTrue(resultado);
        }

        @Test
        @DisplayName("Verificar sin mantenimiento pendiente retorna false")
        void tieneMantenimientoPendiente_SinMantenimientoPendiente_RetornaFalse() {
            // Arrange
            when(mantenimientoRepository.findByAeronaveId(1L)).thenReturn(Collections.emptyList());

            // Act
            boolean resultado = validadorOperativoService.tieneMantenimientoPendiente(1L);

            // Assert
            assertFalse(resultado);
        }
    }

    // ==================== OBTENER RESUMEN OPERATIVIDAD TESTS ====================

    @Nested
    @DisplayName("Obtener Resumen Operatividad Tests")
    class ObtenerResumenOperatividadTests {

        @Test
        @DisplayName("Obtener resumen de aeronave operativa")
        void obtenerResumenOperatividad_AeronaveOperativa_Exitoso() {
            // Arrange
            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L)).thenReturn(Collections.emptyList());

            // Act
            ValidadorOperativoService.ResumenOperatividad resumen = validadorOperativoService.obtenerResumenOperatividad(1L);

            // Assert
            assertNotNull(resumen);
            assertTrue(resumen.esOperativa);
            assertNull(resumen.razon);
            assertFalse(resumen.tieneMantenimientoVencido);
            assertFalse(resumen.tieneMantenimientoPendiente);
            assertEquals(0, resumen.cantidadMantenimientosVencidos);
            assertEquals(0, resumen.cantidadMantenimientosPendientes);
        }

        @Test
        @DisplayName("Obtener resumen de aeronave con mantenimiento vencido")
        void obtenerResumenOperatividad_ConMantenimientoVencido_Exitoso() {
            // Arrange
            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L))
                    .thenReturn(Collections.singletonList(mantenimientoVencido));

            // Act
            ValidadorOperativoService.ResumenOperatividad resumen = validadorOperativoService.obtenerResumenOperatividad(1L);

            // Assert
            assertNotNull(resumen);
            // Con mantenimiento vencido, la aeronave NO es operativa
            assertFalse(resumen.esOperativa, "Aeronave debería no ser operativa con mantenimiento vencido");
            assertNotNull(resumen.razon);
            assertTrue(resumen.tieneMantenimientoVencido);
            // Si hay vencido, también hay pendiente (los vencidos son pendientes)
            assertTrue(resumen.tieneMantenimientoPendiente);
            assertEquals(1, resumen.cantidadMantenimientosVencidos);
            assertEquals(1, resumen.cantidadMantenimientosPendientes);
        }

        @Test
        @DisplayName("Obtener resumen con múltiples mantenimientos")
        void obtenerResumenOperatividad_ConMultiplesMantenimientos_Exitoso() {
            // Arrange
            List<Mantenimiento> mantenimientos = Arrays.asList(
                    mantenimientoVencido,
                    mantenimientoPendiente
            );

            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L)).thenReturn(mantenimientos);

            // Act
            ValidadorOperativoService.ResumenOperatividad resumen = validadorOperativoService.obtenerResumenOperatividad(1L);

            // Assert
            assertNotNull(resumen);
            assertFalse(resumen.esOperativa);
            assertTrue(resumen.tieneMantenimientoVencido);
            assertTrue(resumen.tieneMantenimientoPendiente);
            assertEquals(1, resumen.cantidadMantenimientosVencidos);
            assertEquals(2, resumen.cantidadMantenimientosPendientes);
        }
    }
}
