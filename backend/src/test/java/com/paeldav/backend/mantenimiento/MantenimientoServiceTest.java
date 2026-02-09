package com.paeldav.backend.mantenimiento;

import com.paeldav.backend.application.dto.mantenimiento.MantenimientoCreateDTO;
import com.paeldav.backend.application.dto.mantenimiento.MantenimientoDTO;
import com.paeldav.backend.application.mapper.MantenimientoMapper;
import com.paeldav.backend.application.service.impl.MantenimientoServiceImpl;
import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.entity.Mantenimiento;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import com.paeldav.backend.domain.enums.RolUsuario;
import com.paeldav.backend.domain.enums.TipoMantenimiento;
import com.paeldav.backend.exception.AeronaveNoEncontradaException;
import com.paeldav.backend.exception.MantenimientoNoEncontradoException;
import com.paeldav.backend.exception.UsuarioNoEncontradoException;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("MantenimientoService Tests")
class MantenimientoServiceTest {

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
    private Usuario responsableTest;
    private Mantenimiento mantenimientoPreventivo;
    private Mantenimiento mantenimientoCorrectivo;
    private MantenimientoCreateDTO mantenimientoCreateDTOPreventivo;
    private MantenimientoCreateDTO mantenimientoCreateDTOCorrectivo;
    private MantenimientoDTO mantenimientoDTOPreventivo;
    private MantenimientoDTO mantenimientoDTOCorrectivo;

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
                .especificacionesTecnicas("Avión comercial de largo alcance")
                .build();

        // Crear usuario responsable de prueba
        responsableTest = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("García")
                .email("juan.garcia@charter.com")
                .password("encrypted_password")
                .rol(RolUsuario.AYUDANTE_MANTENIMIENTO)
                .activo(true)
                .build();

        // Mantenimiento preventivo
        mantenimientoPreventivo = Mantenimiento.builder()
                .id(1L)
                .aeronave(aeronaveTest)
                .tipo(TipoMantenimiento.PREVENTIVO)
                .descripcion("Revisión de motores y sistemas")
                .fechaInicio(LocalDateTime.now().minusHours(2))
                .responsable(responsableTest)
                .costo(5000.0)
                .horasVueloAeronave(15000.0)
                .observaciones("Todos los sistemas operativos")
                .completado(false)
                .build();

        // Mantenimiento correctivo
        mantenimientoCorrectivo = Mantenimiento.builder()
                .id(2L)
                .aeronave(aeronaveTest)
                .tipo(TipoMantenimiento.CORRECTIVO)
                .descripcion("Reparación del sistema hidráulico")
                .fechaInicio(LocalDateTime.now().minusHours(1))
                .responsable(responsableTest)
                .costo(8000.0)
                .horasVueloAeronave(15000.0)
                .observaciones("Falla en línea secundaria")
                .completado(false)
                .build();

        // DTOs de creación
        mantenimientoCreateDTOPreventivo = MantenimientoCreateDTO.builder()
                .aeronaveId(1L)
                .tipo(TipoMantenimiento.PREVENTIVO)
                .descripcion("Revisión de motores y sistemas")
                .responsableId(1L)
                .costo(5000.0)
                .horasVueloAeronave(15000.0)
                .observaciones("Mantenimiento programado")
                .build();

        mantenimientoCreateDTOCorrectivo = MantenimientoCreateDTO.builder()
                .aeronaveId(1L)
                .tipo(TipoMantenimiento.CORRECTIVO)
                .descripcion("Reparación del sistema hidráulico")
                .responsableId(1L)
                .costo(8000.0)
                .horasVueloAeronave(15000.0)
                .observaciones("Reparación de emergencia")
                .build();

        // DTOs de respuesta
        mantenimientoDTOPreventivo = MantenimientoDTO.builder()
                .id(1L)
                .aeronaveId(1L)
                .aeronaveMatricula("HK-5000")
                .tipo(TipoMantenimiento.PREVENTIVO)
                .descripcion("Revisión de motores y sistemas")
                .fechaInicio(LocalDateTime.now().minusHours(2))
                .responsableId(1L)
                .responsableNombre("Juan García")
                .costo(5000.0)
                .horasVueloAeronave(15000.0)
                .observaciones("Todos los sistemas operativos")
                .completado(false)
                .build();

        mantenimientoDTOCorrectivo = MantenimientoDTO.builder()
                .id(2L)
                .aeronaveId(1L)
                .aeronaveMatricula("HK-5000")
                .tipo(TipoMantenimiento.CORRECTIVO)
                .descripcion("Reparación del sistema hidráulico")
                .fechaInicio(LocalDateTime.now().minusHours(1))
                .responsableId(1L)
                .responsableNombre("Juan García")
                .costo(8000.0)
                .horasVueloAeronave(15000.0)
                .observaciones("Falla en línea secundaria")
                .completado(false)
                .build();
    }

    // ==================== REGISTRO DE MANTENIMIENTO TESTS ====================

    @Nested
    @DisplayName("Registro de Mantenimiento Tests")
    class RegistroMantenimientoTests {

        @Test
        @DisplayName("Registrar mantenimiento preventivo con datos válidos")
        void registrarMantenimiento_Preventivo_ConDatosValidos_Exitoso() {
            // Arrange
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(responsableTest));
            when(mantenimientoMapper.toEntity(mantenimientoCreateDTOPreventivo))
                    .thenReturn(mantenimientoPreventivo);
            when(mantenimientoRepository.save(any(Mantenimiento.class)))
                    .thenReturn(mantenimientoPreventivo);
            when(mantenimientoMapper.toDTO(mantenimientoPreventivo))
                    .thenReturn(mantenimientoDTOPreventivo);

            // Act
            MantenimientoDTO resultado = mantenimientoService
                    .registrarMantenimiento(mantenimientoCreateDTOPreventivo);

            // Assert
            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            assertEquals(TipoMantenimiento.PREVENTIVO, resultado.getTipo());
            assertEquals("HK-5000", resultado.getAeronaveMatricula());
            assertEquals("Revisión de motores y sistemas", resultado.getDescripcion());
            assertFalse(resultado.getCompletado());
            verify(aeronaveRepository).findById(1L);
            verify(usuarioRepository).findById(1L);
            verify(mantenimientoRepository).save(any(Mantenimiento.class));
        }

        @Test
        @DisplayName("Registrar mantenimiento correctivo con datos válidos")
        void registrarMantenimiento_Correctivo_ConDatosValidos_Exitoso() {
            // Arrange
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(responsableTest));
            when(mantenimientoMapper.toEntity(mantenimientoCreateDTOCorrectivo))
                    .thenReturn(mantenimientoCorrectivo);
            when(mantenimientoRepository.save(any(Mantenimiento.class)))
                    .thenReturn(mantenimientoCorrectivo);
            when(mantenimientoMapper.toDTO(mantenimientoCorrectivo))
                    .thenReturn(mantenimientoDTOCorrectivo);

            // Act
            MantenimientoDTO resultado = mantenimientoService
                    .registrarMantenimiento(mantenimientoCreateDTOCorrectivo);

            // Assert
            assertNotNull(resultado);
            assertEquals(2L, resultado.getId());
            assertEquals(TipoMantenimiento.CORRECTIVO, resultado.getTipo());
            assertEquals("HK-5000", resultado.getAeronaveMatricula());
            assertEquals("Reparación del sistema hidráulico", resultado.getDescripcion());
            assertFalse(resultado.getCompletado());
            verify(aeronaveRepository).findById(1L);
            verify(usuarioRepository).findById(1L);
            verify(mantenimientoRepository).save(any(Mantenimiento.class));
        }

        @Test
        @DisplayName("Registrar mantenimiento sin responsable asignado")
        void registrarMantenimiento_SinResponsable_Exitoso() {
            // Arrange
            MantenimientoCreateDTO dtoSinResponsable = MantenimientoCreateDTO.builder()
                    .aeronaveId(1L)
                    .tipo(TipoMantenimiento.PREVENTIVO)
                    .descripcion("Revisión rápida")
                    .costo(1000.0)
                    .build();

            Mantenimiento mantenimientoSinResponsable = Mantenimiento.builder()
                    .id(3L)
                    .aeronave(aeronaveTest)
                    .tipo(TipoMantenimiento.PREVENTIVO)
                    .descripcion("Revisión rápida")
                    .fechaInicio(LocalDateTime.now())
                    .responsable(null)
                    .costo(1000.0)
                    .completado(false)
                    .build();

            MantenimientoDTO dtoSinResponsableResult = MantenimientoDTO.builder()
                    .id(3L)
                    .aeronaveId(1L)
                    .aeronaveMatricula("HK-5000")
                    .tipo(TipoMantenimiento.PREVENTIVO)
                    .descripcion("Revisión rápida")
                    .responsableId(null)
                    .responsableNombre(null)
                    .completado(false)
                    .build();

            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(mantenimientoMapper.toEntity(dtoSinResponsable))
                    .thenReturn(mantenimientoSinResponsable);
            when(mantenimientoRepository.save(any(Mantenimiento.class)))
                    .thenReturn(mantenimientoSinResponsable);
            when(mantenimientoMapper.toDTO(mantenimientoSinResponsable))
                    .thenReturn(dtoSinResponsableResult);

            // Act
            MantenimientoDTO resultado = mantenimientoService.registrarMantenimiento(dtoSinResponsable);

            // Assert
            assertNotNull(resultado);
            assertNull(resultado.getResponsableId());
            assertNull(resultado.getResponsableNombre());
            verify(usuarioRepository, never()).findById(anyLong());
        }

        @Test
        @DisplayName("Registrar mantenimiento con aeronave inexistente lanza excepción")
        void registrarMantenimiento_AeronaveInexistente_LanzaExcepcion() {
            // Arrange
            when(aeronaveRepository.findById(999L)).thenReturn(Optional.empty());

            MantenimientoCreateDTO dtoConAeronaveInvalida = MantenimientoCreateDTO.builder()
                    .aeronaveId(999L)
                    .tipo(TipoMantenimiento.PREVENTIVO)
                    .descripcion("Test")
                    .build();

            // Act & Assert
            AeronaveNoEncontradaException exception = assertThrows(
                    AeronaveNoEncontradaException.class,
                    () -> mantenimientoService.registrarMantenimiento(dtoConAeronaveInvalida)
            );
            assertEquals("Aeronave no encontrada con ID: 999", exception.getMessage());
            verify(mantenimientoRepository, never()).save(any());
        }

        @Test
        @DisplayName("Registrar mantenimiento con responsable inexistente lanza excepción")
        void registrarMantenimiento_ResponsableInexistente_LanzaExcepcion() {
            // Arrange
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());

            MantenimientoCreateDTO dtoConResponsableInvalido = MantenimientoCreateDTO.builder()
                    .aeronaveId(1L)
                    .tipo(TipoMantenimiento.PREVENTIVO)
                    .descripcion("Test")
                    .responsableId(999L)
                    .build();

            // Act & Assert
            UsuarioNoEncontradoException exception = assertThrows(
                    UsuarioNoEncontradoException.class,
                    () -> mantenimientoService.registrarMantenimiento(dtoConResponsableInvalido)
            );
            assertEquals("Usuario no encontrado con ID: 999", exception.getMessage());
            verify(mantenimientoRepository, never()).save(any());
        }
    }

    // ==================== OBTENCIÓN DE MANTENIMIENTOS TESTS ====================

    @Nested
    @DisplayName("Obtención de Mantenimientos Tests")
    class ObtenerMantenimientosTests {

        @Test
        @DisplayName("Obtener mantenimiento por ID exitosamente")
        void obtenerMantenimientoPorId_ConIdValido_Exitoso() {
            // Arrange
            when(mantenimientoRepository.findById(1L))
                    .thenReturn(Optional.of(mantenimientoPreventivo));
            when(mantenimientoMapper.toDTO(mantenimientoPreventivo))
                    .thenReturn(mantenimientoDTOPreventivo);

            // Act
            MantenimientoDTO resultado = mantenimientoService.obtenerMantenimientoPorId(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            assertEquals(TipoMantenimiento.PREVENTIVO, resultado.getTipo());
            verify(mantenimientoRepository).findById(1L);
        }

        @Test
        @DisplayName("Obtener mantenimiento con ID inexistente lanza excepción")
        void obtenerMantenimientoPorId_ConIdInexistente_LanzaExcepcion() {
            // Arrange
            when(mantenimientoRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            MantenimientoNoEncontradoException exception = assertThrows(
                    MantenimientoNoEncontradoException.class,
                    () -> mantenimientoService.obtenerMantenimientoPorId(999L)
            );
            assertEquals("Mantenimiento no encontrado con ID: 999", exception.getMessage());
        }

        @Test
        @DisplayName("Obtener todos los mantenimientos")
        void obtenerTodosMantenimientos_Exitoso() {
            // Arrange
            List<Mantenimiento> mantenimientos = Arrays.asList(
                    mantenimientoPreventivo,
                    mantenimientoCorrectivo
            );
            List<MantenimientoDTO> mantenimientosDTO = Arrays.asList(
                    mantenimientoDTOPreventivo,
                    mantenimientoDTOCorrectivo
            );

            when(mantenimientoRepository.findAll()).thenReturn(mantenimientos);
            when(mantenimientoMapper.toDTOList(mantenimientos))
                    .thenReturn(mantenimientosDTO);

            // Act
            List<MantenimientoDTO> resultado = mantenimientoService.obtenerTodosMantenimientos();

            // Assert
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            assertEquals(TipoMantenimiento.PREVENTIVO, resultado.get(0).getTipo());
            assertEquals(TipoMantenimiento.CORRECTIVO, resultado.get(1).getTipo());
            verify(mantenimientoRepository).findAll();
        }

        @Test
        @DisplayName("Obtener mantenimientos por aeronave")
        void obtenerMantenimientosPorAeronave_Exitoso() {
            // Arrange
            List<Mantenimiento> mantenimientos = Arrays.asList(
                    mantenimientoPreventivo,
                    mantenimientoCorrectivo
            );
            List<MantenimientoDTO> mantenimientosDTO = Arrays.asList(
                    mantenimientoDTOPreventivo,
                    mantenimientoDTOCorrectivo
            );

            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L))
                    .thenReturn(mantenimientos);
            when(mantenimientoMapper.toDTOList(mantenimientos))
                    .thenReturn(mantenimientosDTO);

            // Act
            List<MantenimientoDTO> resultado = mantenimientoService
                    .obtenerMantenimientosPorAeronave(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            verify(aeronaveRepository).existsById(1L);
            verify(mantenimientoRepository).findByAeronaveId(1L);
        }

        @Test
        @DisplayName("Obtener mantenimientos pendientes de una aeronave")
        void obtenerMantenimientosPendientesPorAeronave_Exitoso() {
            // Arrange
            List<Mantenimiento> mantenimientosPendientes = Arrays.asList(
                    mantenimientoPreventivo,
                    mantenimientoCorrectivo
            );
            List<MantenimientoDTO> mantenimientosDTO = Arrays.asList(
                    mantenimientoDTOPreventivo,
                    mantenimientoDTOCorrectivo
            );

            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveId(1L))
                    .thenReturn(mantenimientosPendientes);
            when(mantenimientoMapper.toDTOList(mantenimientosPendientes))
                    .thenReturn(mantenimientosDTO);

            // Act
            List<MantenimientoDTO> resultado = mantenimientoService
                    .obtenerMantenimientosPendientesPorAeronave(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            assertTrue(resultado.stream().allMatch(m -> !m.getCompletado()));
            verify(aeronaveRepository).existsById(1L);
        }

        @Test
        @DisplayName("Obtener mantenimientos por tipo (PREVENTIVO)")
        void obtenerMantenimientosPorTipo_Preventivo_Exitoso() {
            // Arrange
            List<Mantenimiento> preventivos = Collections.singletonList(mantenimientoPreventivo);
            List<MantenimientoDTO> preventivosDTO = Collections.singletonList(mantenimientoDTOPreventivo);

            when(mantenimientoRepository.findByTipo(TipoMantenimiento.PREVENTIVO))
                    .thenReturn(preventivos);
            when(mantenimientoMapper.toDTOList(preventivos))
                    .thenReturn(preventivosDTO);

            // Act
            List<MantenimientoDTO> resultado = mantenimientoService
                    .obtenerMantenimientosPorTipo(TipoMantenimiento.PREVENTIVO);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals(TipoMantenimiento.PREVENTIVO, resultado.get(0).getTipo());
        }

        @Test
        @DisplayName("Obtener mantenimientos por tipo (CORRECTIVO)")
        void obtenerMantenimientosPorTipo_Correctivo_Exitoso() {
            // Arrange
            List<Mantenimiento> correctivos = Collections.singletonList(mantenimientoCorrectivo);
            List<MantenimientoDTO> correctivosDTO = Collections.singletonList(mantenimientoDTOCorrectivo);

            when(mantenimientoRepository.findByTipo(TipoMantenimiento.CORRECTIVO))
                    .thenReturn(correctivos);
            when(mantenimientoMapper.toDTOList(correctivos))
                    .thenReturn(correctivosDTO);

            // Act
            List<MantenimientoDTO> resultado = mantenimientoService
                    .obtenerMantenimientosPorTipo(TipoMantenimiento.CORRECTIVO);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals(TipoMantenimiento.CORRECTIVO, resultado.get(0).getTipo());
        }

        @Test
        @DisplayName("Obtener mantenimientos por aeronave y tipo")
        void obtenerMantenimientosPorAeronaveYTipo_Exitoso() {
            // Arrange
            List<Mantenimiento> preventivos = Collections.singletonList(mantenimientoPreventivo);
            List<MantenimientoDTO> preventivosDTO = Collections.singletonList(mantenimientoDTOPreventivo);

            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByAeronaveIdAndTipo(1L, TipoMantenimiento.PREVENTIVO))
                    .thenReturn(preventivos);
            when(mantenimientoMapper.toDTOList(preventivos))
                    .thenReturn(preventivosDTO);

            // Act
            List<MantenimientoDTO> resultado = mantenimientoService
                    .obtenerMantenimientosPorAeronaveYTipo(1L, TipoMantenimiento.PREVENTIVO);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals(TipoMantenimiento.PREVENTIVO, resultado.get(0).getTipo());
            verify(aeronaveRepository).existsById(1L);
        }
    }

    // ==================== COMPLETAR MANTENIMIENTO TESTS ====================

    @Nested
    @DisplayName("Completar Mantenimiento Tests")
    class CompletarMantenimientoTests {

        @Test
        @DisplayName("Completar mantenimiento exitosamente")
        void completarMantenimiento_Exitoso() {
            // Arrange
            LocalDateTime fechaFin = LocalDateTime.now();
            String observaciones = "Mantenimiento completado exitosamente";

            Mantenimiento mantenimientoCompletado = Mantenimiento.builder()
                    .id(1L)
                    .aeronave(aeronaveTest)
                    .tipo(TipoMantenimiento.PREVENTIVO)
                    .descripcion("Revisión de motores y sistemas")
                    .fechaInicio(LocalDateTime.now().minusHours(4))
                    .fechaFin(fechaFin)
                    .responsable(responsableTest)
                    .costo(5000.0)
                    .observaciones(observaciones)
                    .completado(true)
                    .build();

            MantenimientoDTO dtoCompletado = MantenimientoDTO.builder()
                    .id(1L)
                    .aeronaveId(1L)
                    .aeronaveMatricula("HK-5000")
                    .tipo(TipoMantenimiento.PREVENTIVO)
                    .descripcion("Revisión de motores y sistemas")
                    .fechaInicio(LocalDateTime.now().minusHours(4))
                    .fechaFin(fechaFin)
                    .responsableId(1L)
                    .responsableNombre("Juan García")
                    .costo(5000.0)
                    .observaciones(observaciones)
                    .completado(true)
                    .build();

            when(mantenimientoRepository.findById(1L))
                    .thenReturn(Optional.of(mantenimientoPreventivo));
            when(mantenimientoRepository.save(any(Mantenimiento.class)))
                    .thenReturn(mantenimientoCompletado);
            when(mantenimientoMapper.toDTO(mantenimientoCompletado))
                    .thenReturn(dtoCompletado);

            // Act
            MantenimientoDTO resultado = mantenimientoService
                    .completarMantenimiento(1L, fechaFin, observaciones);

            // Assert
            assertNotNull(resultado);
            assertTrue(resultado.getCompletado());
            assertEquals(observaciones, resultado.getObservaciones());
            assertNotNull(resultado.getFechaFin());
            verify(mantenimientoRepository).findById(1L);
            verify(mantenimientoRepository).save(any(Mantenimiento.class));
        }

        @Test
        @DisplayName("Completar mantenimiento con ID inexistente lanza excepción")
        void completarMantenimiento_IdInexistente_LanzaExcepcion() {
            // Arrange
            when(mantenimientoRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            MantenimientoNoEncontradoException exception = assertThrows(
                    MantenimientoNoEncontradoException.class,
                    () -> mantenimientoService.completarMantenimiento(999L, LocalDateTime.now(), "test")
            );
            assertEquals("Mantenimiento no encontrado con ID: 999", exception.getMessage());
            verify(mantenimientoRepository, never()).save(any());
        }
    }

    // ==================== MANTENIMIENTOS PENDIENTES TESTS ====================

    @Nested
    @DisplayName("Mantenimientos Pendientes Tests")
    class MantenimientosPendientesTests {

        @Test
        @DisplayName("Obtener todos los mantenimientos pendientes")
        void obtenerMantenimientosPendientes_Exitoso() {
            // Arrange
            List<Mantenimiento> pendientes = Arrays.asList(
                    mantenimientoPreventivo,
                    mantenimientoCorrectivo
            );
            List<MantenimientoDTO> pendientesDTO = Arrays.asList(
                    mantenimientoDTOPreventivo,
                    mantenimientoDTOCorrectivo
            );

            when(mantenimientoRepository.findByCompletado(false))
                    .thenReturn(pendientes);
            when(mantenimientoMapper.toDTOList(pendientes))
                    .thenReturn(pendientesDTO);

            // Act
            List<MantenimientoDTO> resultado = mantenimientoService
                    .obtenerMantenimientosPendientes();

            // Assert
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            assertTrue(resultado.stream().allMatch(m -> !m.getCompletado()));
            verify(mantenimientoRepository).findByCompletado(false);
        }

        @Test
        @DisplayName("Obtener mantenimientos por responsable")
        void obtenerMantenimientosPorResponsable_Exitoso() {
            // Arrange
            List<Mantenimiento> asignados = Arrays.asList(
                    mantenimientoPreventivo,
                    mantenimientoCorrectivo
            );
            List<MantenimientoDTO> asignadosDTO = Arrays.asList(
                    mantenimientoDTOPreventivo,
                    mantenimientoDTOCorrectivo
            );

            when(usuarioRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findByResponsableId(1L))
                    .thenReturn(asignados);
            when(mantenimientoMapper.toDTOList(asignados))
                    .thenReturn(asignadosDTO);

            // Act
            List<MantenimientoDTO> resultado = mantenimientoService
                    .obtenerMantenimientosPorResponsable(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            verify(usuarioRepository).existsById(1L);
            verify(mantenimientoRepository).findByResponsableId(1L);
        }

        @Test
        @DisplayName("Obtener mantenimientos de responsable inexistente lanza excepción")
        void obtenerMantenimientosPorResponsable_ResponsableInexistente_LanzaExcepcion() {
            // Arrange
            when(usuarioRepository.existsById(999L)).thenReturn(false);

            // Act & Assert
            UsuarioNoEncontradoException exception = assertThrows(
                    UsuarioNoEncontradoException.class,
                    () -> mantenimientoService.obtenerMantenimientosPorResponsable(999L)
            );
            assertEquals("Usuario no encontrado con ID: 999", exception.getMessage());
        }
    }

    // ==================== OBTENCIÓN POR FECHA TESTS ====================

    @Nested
    @DisplayName("Obtención por Fecha Tests")
    class ObtenerPorFechaTests {

        @Test
        @DisplayName("Obtener mantenimientos en rango de fechas")
        void obtenerMantenimientosPorFecha_Exitoso() {
            // Arrange
            LocalDateTime inicio = LocalDateTime.now().minusHours(24);
            LocalDateTime fin = LocalDateTime.now().plusHours(24);

            List<Mantenimiento> enRango = Arrays.asList(
                    mantenimientoPreventivo,
                    mantenimientoCorrectivo
            );
            List<MantenimientoDTO> enRangoDTO = Arrays.asList(
                    mantenimientoDTOPreventivo,
                    mantenimientoDTOCorrectivo
            );

            when(mantenimientoRepository.findByFechaInicioBetween(inicio, fin))
                    .thenReturn(enRango);
            when(mantenimientoMapper.toDTOList(enRango))
                    .thenReturn(enRangoDTO);

            // Act
            List<MantenimientoDTO> resultado = mantenimientoService
                    .obtenerMantenimientosPorFecha(inicio, fin);

            // Assert
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            verify(mantenimientoRepository).findByFechaInicioBetween(inicio, fin);
        }
    }

    // ==================== ÚLTIMOS MANTENIMIENTOS TESTS ====================

    @Nested
    @DisplayName("Últimos Mantenimientos Tests")
    class UltimosMantenimientosTests {

        @Test
        @DisplayName("Obtener últimos mantenimientos de una aeronave")
        void obtenerUltimosMantenimientos_Exitoso() {
            // Arrange
            List<Mantenimiento> ultimos = Arrays.asList(
                    mantenimientoCorrectivo,
                    mantenimientoPreventivo
            );
            List<MantenimientoDTO> ultimosDTO = Arrays.asList(
                    mantenimientoDTOCorrectivo,
                    mantenimientoDTOPreventivo
            );

            when(aeronaveRepository.existsById(1L)).thenReturn(true);
            when(mantenimientoRepository.findUltimosMantenimientos(1L))
                    .thenReturn(ultimos);
            when(mantenimientoMapper.toDTOList(ultimos))
                    .thenReturn(ultimosDTO);

            // Act
            List<MantenimientoDTO> resultado = mantenimientoService
                    .obtenerUltimosMantenimientos(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            verify(aeronaveRepository).existsById(1L);
            verify(mantenimientoRepository).findUltimosMantenimientos(1L);
        }
    }
}
