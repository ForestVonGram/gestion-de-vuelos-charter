package com.paeldav.backend.aeronave;

import com.paeldav.backend.application.dto.aeronave.AeronaveCreateDTO;
import com.paeldav.backend.application.dto.aeronave.AeronaveDTO;
import com.paeldav.backend.application.dto.aeronave.AeronaveUpdateDTO;
import com.paeldav.backend.application.mapper.AeronaveMapper;
import com.paeldav.backend.application.service.impl.AeronaveServiceImpl;
import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import com.paeldav.backend.exception.*;
import com.paeldav.backend.infraestructure.repository.AeronaveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AeronaveService Tests")
class AeronaveServiceTest {

    @Mock
    private AeronaveRepository aeronaveRepository;

    @Mock
    private AeronaveMapper aeronaveMapper;

    @InjectMocks
    private AeronaveServiceImpl aeronaveService;

    private Aeronave aeronaveTest;
    private AeronaveCreateDTO aeronaveCreateDTOTest;
    private AeronaveDTO aeronaveDTOTest;

    @BeforeEach
    void setUp() {
        aeronaveTest = Aeronave.builder()
                .id(1L)
                .matricula("HK-1234")
                .modelo("Cessna 208")
                .fabricante("Cessna Aircraft Company")
                .capacidadPasajeros(14)
                .capacidadTripulacion(2)
                .autonomiaKm(1600.0)
                .velocidadCruceroKmh(200.0)
                .fechaFabricacion(LocalDate.of(2015, 5, 10))
                .fechaUltimaRevision(LocalDate.now().minusMonths(3))
                .horasVueloTotales(2500.0)
                .estado(EstadoAeronave.DISPONIBLE)
                .especificacionesTecnicas("Avión de transporte regional")
                .build();

        aeronaveCreateDTOTest = AeronaveCreateDTO.builder()
                .matricula("HK-1234")
                .modelo("Cessna 208")
                .fabricante("Cessna Aircraft Company")
                .capacidadPasajeros(14)
                .capacidadTripulacion(2)
                .autonomiaKm(1600.0)
                .velocidadCruceroKmh(200.0)
                .fechaFabricacion(LocalDate.of(2015, 5, 10))
                .fechaUltimaRevision(LocalDate.now().minusMonths(3))
                .estado(EstadoAeronave.DISPONIBLE)
                .especificacionesTecnicas("Avión de transporte regional")
                .build();

        aeronaveDTOTest = AeronaveDTO.builder()
                .id(1L)
                .matricula("HK-1234")
                .modelo("Cessna 208")
                .fabricante("Cessna Aircraft Company")
                .capacidadPasajeros(14)
                .capacidadTripulacion(2)
                .autonomiaKm(1600.0)
                .velocidadCruceroKmh(200.0)
                .fechaFabricacion(LocalDate.of(2015, 5, 10))
                .fechaUltimaRevision(LocalDate.now().minusMonths(3))
                .horasVueloTotales(2500.0)
                .estado(EstadoAeronave.DISPONIBLE)
                .especificacionesTecnicas("Avión de transporte regional")
                .build();
    }

    // ==================== REGISTRO DE AERONAVE TESTS ====================

    @Nested
    @DisplayName("Registro de Aeronave Tests")
    class RegistroAeronaveTests {

        @Test
        @DisplayName("Registrar aeronave con datos válidos")
        void registrarAeronave_ConDatosValidos_GuardaAeronave() {
            // Arrange
            when(aeronaveRepository.existsByMatricula("HK-1234")).thenReturn(false);
            when(aeronaveMapper.toEntity(aeronaveCreateDTOTest)).thenReturn(aeronaveTest);
            when(aeronaveRepository.save(any(Aeronave.class))).thenReturn(aeronaveTest);
            when(aeronaveMapper.toDTO(aeronaveTest)).thenReturn(aeronaveDTOTest);

            // Act
            AeronaveDTO resultado = aeronaveService.registrarAeronave(aeronaveCreateDTOTest);

            // Assert
            assertNotNull(resultado);
            assertEquals("HK-1234", resultado.getMatricula());
            assertEquals("Cessna 208", resultado.getModelo());
            assertEquals(EstadoAeronave.DISPONIBLE, resultado.getEstado());
            assertEquals(14, resultado.getCapacidadPasajeros());
            verify(aeronaveRepository).existsByMatricula("HK-1234");
            verify(aeronaveRepository).save(any(Aeronave.class));
        }

        @Test
        @DisplayName("Registrar aeronave con matrícula duplicada lanza excepción")
        void registrarAeronave_ConMatriculaDuplicada_LanzaExcepcion() {
            // Arrange
            when(aeronaveRepository.existsByMatricula("HK-1234")).thenReturn(true);

            // Act & Assert
            AeronaveYaExisteException exception = assertThrows(
                    AeronaveYaExisteException.class,
                    () -> aeronaveService.registrarAeronave(aeronaveCreateDTOTest)
            );
            assertEquals("Una aeronave con la matrícula HK-1234 ya existe en el sistema", exception.getMessage());
            verify(aeronaveRepository, never()).save(any());
        }

        @Test
        @DisplayName("Registrar aeronave inicializa en estado DISPONIBLE por defecto")
        void registrarAeronave_InicializaEstadoDisponible() {
            // Arrange
            Aeronave aeronaveConEstadoNulo = Aeronave.builder()
                    .matricula("HK-1234")
                    .modelo("Cessna 208")
                    .capacidadPasajeros(14)
                    .capacidadTripulacion(2)
                    .build();

            when(aeronaveRepository.existsByMatricula("HK-1234")).thenReturn(false);
            when(aeronaveMapper.toEntity(aeronaveCreateDTOTest)).thenReturn(aeronaveConEstadoNulo);
            when(aeronaveRepository.save(any(Aeronave.class))).thenAnswer(invocation -> {
                Aeronave aeronave = invocation.getArgument(0);
                assertEquals(EstadoAeronave.DISPONIBLE, aeronave.getEstado());
                return aeronave;
            });
            when(aeronaveMapper.toDTO(any())).thenReturn(aeronaveDTOTest);

            // Act
            aeronaveService.registrarAeronave(aeronaveCreateDTOTest);

            // Assert
            verify(aeronaveRepository).save(any(Aeronave.class));
        }
    }

    // ==================== OBTENER AERONAVE TESTS ====================

    @Nested
    @DisplayName("Obtener Aeronave Tests")
    class ObtenerAeronaveTests {

        @Test
        @DisplayName("Obtener aeronave por ID existente")
        void obtenerAeronavePorId_ConIdExistente_RetornaAeronave() {
            // Arrange
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(aeronaveMapper.toDTO(aeronaveTest)).thenReturn(aeronaveDTOTest);

            // Act
            AeronaveDTO resultado = aeronaveService.obtenerAeronavePorId(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            assertEquals("HK-1234", resultado.getMatricula());
            verify(aeronaveRepository).findById(1L);
        }

        @Test
        @DisplayName("Obtener aeronave por ID inexistente lanza excepción")
        void obtenerAeronavePorId_ConIdInexistente_LanzaExcepcion() {
            // Arrange
            when(aeronaveRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            AeronaveNoEncontradaException exception = assertThrows(
                    AeronaveNoEncontradaException.class,
                    () -> aeronaveService.obtenerAeronavePorId(999L)
            );
            assertEquals("Aeronave no encontrada con ID: 999", exception.getMessage());
        }

        @Test
        @DisplayName("Obtener aeronave por matrícula")
        void obtenerAeronavePorMatricula_ConMatriculaExistente_RetornaAeronave() {
            // Arrange
            when(aeronaveRepository.findByMatricula("HK-1234")).thenReturn(Optional.of(aeronaveTest));
            when(aeronaveMapper.toDTO(aeronaveTest)).thenReturn(aeronaveDTOTest);

            // Act
            AeronaveDTO resultado = aeronaveService.obtenerAeronavePorMatricula("HK-1234");

            // Assert
            assertNotNull(resultado);
            assertEquals("HK-1234", resultado.getMatricula());
            verify(aeronaveRepository).findByMatricula("HK-1234");
        }

        @Test
        @DisplayName("Obtener todas las aeronaves")
        void obtenerTodasAeronaves_RetornaListaCompleta() {
            // Arrange
            Aeronave aeronave2 = Aeronave.builder()
                    .id(2L)
                    .matricula("HK-5678")
                    .modelo("Beechcraft King Air")
                    .capacidadPasajeros(8)
                    .capacidadTripulacion(2)
                    .estado(EstadoAeronave.DISPONIBLE)
                    .build();

            List<Aeronave> aeronaves = Arrays.asList(aeronaveTest, aeronave2);
            List<AeronaveDTO> aeronavesDTO = Arrays.asList(aeronaveDTOTest);

            when(aeronaveRepository.findAll()).thenReturn(aeronaves);
            when(aeronaveMapper.toDTOList(aeronaves)).thenReturn(aeronavesDTO);

            // Act
            List<AeronaveDTO> resultado = aeronaveService.obtenerTodasAeronaves();

            // Assert
            assertNotNull(resultado);
            verify(aeronaveRepository).findAll();
            verify(aeronaveMapper).toDTOList(aeronaves);
        }
    }

    // ==================== ACTUALIZACIÓN DE DATOS TÉCNICOS TESTS ====================

    @Nested
    @DisplayName("Actualizar Aeronave Tests")
    class ActualizarAeronaveTests {

        @Test
        @DisplayName("Actualizar datos técnicos de aeronave")
        void actualizarAeronave_ConDatosValidos_ActualizaExitosamente() {
            // Arrange
            AeronaveUpdateDTO updateDTO = AeronaveUpdateDTO.builder()
                    .capacidadPasajeros(16)
                    .fechaUltimaRevision(LocalDate.now())
                    .autonomiaKm(1800.0)
                    .build();

            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(aeronaveRepository.save(any(Aeronave.class))).thenReturn(aeronaveTest);
            when(aeronaveMapper.toDTO(aeronaveTest)).thenReturn(aeronaveDTOTest);

            // Act
            AeronaveDTO resultado = aeronaveService.actualizarAeronave(1L, updateDTO);

            // Assert
            assertNotNull(resultado);
            verify(aeronaveMapper).updateEntityFromUpdateDTO(updateDTO, aeronaveTest);
            verify(aeronaveRepository).save(aeronaveTest);
        }

        @Test
        @DisplayName("Actualizar aeronave inexistente lanza excepción")
        void actualizarAeronave_ConIdInexistente_LanzaExcepcion() {
            // Arrange
            AeronaveUpdateDTO updateDTO = AeronaveUpdateDTO.builder()
                    .capacidadPasajeros(16)
                    .build();
            when(aeronaveRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            AeronaveNoEncontradaException exception = assertThrows(
                    AeronaveNoEncontradaException.class,
                    () -> aeronaveService.actualizarAeronave(999L, updateDTO)
            );
            assertEquals("Aeronave no encontrada con ID: 999", exception.getMessage());
        }

        @Test
        @DisplayName("Actualizar aeronave no modifica matrícula ni modelo")
        void actualizarAeronave_NoModificaMatriculaNiModelo() {
            // Arrange
            AeronaveUpdateDTO updateDTO = AeronaveUpdateDTO.builder()
                    .capacidadPasajeros(16)
                    .build();

            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(aeronaveRepository.save(any(Aeronave.class))).thenReturn(aeronaveTest);
            when(aeronaveMapper.toDTO(any())).thenReturn(aeronaveDTOTest);

            // Act
            aeronaveService.actualizarAeronave(1L, updateDTO);

            // Assert
            verify(aeronaveMapper).updateEntityFromUpdateDTO(updateDTO, aeronaveTest);
        }
    }

    // ==================== CAMBIAR ESTADO TESTS ====================

    @Nested
    @DisplayName("Cambiar Estado Aeronave Tests")
    class CambiarEstadoAeronaveTests {

        @Test
        @DisplayName("Cambiar estado de DISPONIBLE a EN_MANTENIMIENTO")
        void cambiarEstadoAeronave_DeDisponibleAEnMantenimiento_Exitoso() {
            // Arrange
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(aeronaveRepository.save(any(Aeronave.class))).thenReturn(aeronaveTest);
            when(aeronaveMapper.toDTO(any())).thenReturn(aeronaveDTOTest);

            // Act
            AeronaveDTO resultado = aeronaveService.cambiarEstadoAeronave(1L, EstadoAeronave.EN_MANTENIMIENTO);

            // Assert
            assertNotNull(resultado);
            assertEquals(EstadoAeronave.EN_MANTENIMIENTO, aeronaveTest.getEstado());
            verify(aeronaveRepository).save(aeronaveTest);
        }

        @Test
        @DisplayName("Cambiar a mismo estado lanza excepción")
        void cambiarEstadoAeronave_AMismoEstado_LanzaExcepcion() {
            // Arrange
            aeronaveTest.setEstado(EstadoAeronave.DISPONIBLE);
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));

            // Act & Assert
            AeronaveNoDisponibleException exception = assertThrows(
                    AeronaveNoDisponibleException.class,
                    () -> aeronaveService.cambiarEstadoAeronave(1L, EstadoAeronave.DISPONIBLE)
            );
            assertTrue(exception.getMessage().contains("ya está en estado"));
        }
    }

    // ==================== VALIDACIÓN DE CAPACIDAD TESTS ====================

    @Nested
    @DisplayName("Validar Capacidad Operativa Tests")
    class ValidarCapacidadOperativaTests {

        @Test
        @DisplayName("Validar capacidad con datos dentro del límite")
        void validarCapacidadOperativa_DentroDelLimite_Exitoso() {
            // Arrange
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));

            // Act & Assert
            assertDoesNotThrow(() ->
                    aeronaveService.validarCapacidadOperativa(1L, 10, 2)
            );
            verify(aeronaveRepository).findById(1L);
        }

        @Test
        @DisplayName("Validar capacidad máxima de pasajeros")
        void validarCapacidadOperativa_CapacidadMaximaPasajeros_Exitoso() {
            // Arrange
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));

            // Act & Assert
            assertDoesNotThrow(() ->
                    aeronaveService.validarCapacidadOperativa(1L, 14, 2)
            );
        }

        @Test
        @DisplayName("Validar capacidad de pasajeros insuficiente lanza excepción")
        void validarCapacidadOperativa_CapacidadPasajerosInsuficiente_LanzaExcepcion() {
            // Arrange
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));

            // Act & Assert
            CapacidadInsuficienteException exception = assertThrows(
                    CapacidadInsuficienteException.class,
                    () -> aeronaveService.validarCapacidadOperativa(1L, 20, 2)
            );
            assertTrue(exception.getMessage().contains("Capacidad de pasajeros insuficiente"));
            assertTrue(exception.getMessage().contains("máximo 14"));
        }

        @Test
        @DisplayName("Validar capacidad de tripulación insuficiente lanza excepción")
        void validarCapacidadOperativa_CapacidadTripulacionInsuficiente_LanzaExcepcion() {
            // Arrange
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));

            // Act & Assert
            CapacidadInsuficienteException exception = assertThrows(
                    CapacidadInsuficienteException.class,
                    () -> aeronaveService.validarCapacidadOperativa(1L, 10, 5)
            );
            assertTrue(exception.getMessage().contains("Capacidad de tripulación insuficiente"));
            assertTrue(exception.getMessage().contains("máximo 2"));
        }

        @Test
        @DisplayName("Validar capacidad con aeronave inexistente lanza excepción")
        void validarCapacidadOperativa_AeronaveInexistente_LanzaExcepcion() {
            // Arrange
            when(aeronaveRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            AeronaveNoEncontradaException exception = assertThrows(
                    AeronaveNoEncontradaException.class,
                    () -> aeronaveService.validarCapacidadOperativa(999L, 10, 2)
            );
            assertEquals("Aeronave no encontrada con ID: 999", exception.getMessage());
        }
    }

    // ==================== FILTROS POR ESTADO, MODELO Y CAPACIDAD TESTS ====================

    @Nested
    @DisplayName("Filtros de Búsqueda Tests")
    class FiltrosBusquedaTests {

        @Test
        @DisplayName("Obtener aeronaves por estado DISPONIBLE")
        void obtenerAerronavesPorEstado_ConEstadoDisponible_RetornaAeronaves() {
            // Arrange
            List<Aeronave> aeronaves = Arrays.asList(aeronaveTest);
            List<AeronaveDTO> aeronavesDTO = Arrays.asList(aeronaveDTOTest);

            when(aeronaveRepository.findByEstado(EstadoAeronave.DISPONIBLE)).thenReturn(aeronaves);
            when(aeronaveMapper.toDTOList(aeronaves)).thenReturn(aeronavesDTO);

            // Act
            List<AeronaveDTO> resultado = aeronaveService.obtenerAerronavesPorEstado(EstadoAeronave.DISPONIBLE);

            // Assert
            assertNotNull(resultado);
            verify(aeronaveRepository).findByEstado(EstadoAeronave.DISPONIBLE);
        }

        @Test
        @DisplayName("Obtener aeronaves por modelo")
        void obtenerAerronavesPorModelo_ConModeloExistente_RetornaAeronaves() {
            // Arrange
            List<Aeronave> aeronaves = Arrays.asList(aeronaveTest);
            List<AeronaveDTO> aeronavesDTO = Arrays.asList(aeronaveDTOTest);

            when(aeronaveRepository.findByModelo("Cessna 208")).thenReturn(aeronaves);
            when(aeronaveMapper.toDTOList(aeronaves)).thenReturn(aeronavesDTO);

            // Act
            List<AeronaveDTO> resultado = aeronaveService.obtenerAerronavesPorModelo("Cessna 208");

            // Assert
            assertNotNull(resultado);
            verify(aeronaveRepository).findByModelo("Cessna 208");
        }

        @Test
        @DisplayName("Obtener aeronaves por capacidad mínima")
        void obtenerAerronavesPorCapacidad_ConCapacidad10_RetornaAeronaves() {
            // Arrange
            List<Aeronave> aeronaves = Arrays.asList(aeronaveTest);
            List<AeronaveDTO> aeronavesDTO = Arrays.asList(aeronaveDTOTest);

            when(aeronaveRepository.findByCapacidadPasajerosGreaterThanEqual(10))
                    .thenReturn(aeronaves);
            when(aeronaveMapper.toDTOList(aeronaves)).thenReturn(aeronavesDTO);

            // Act
            List<AeronaveDTO> resultado = aeronaveService.obtenerAerronavesPorCapacidad(10);

            // Assert
            assertNotNull(resultado);
            verify(aeronaveRepository).findByCapacidadPasajerosGreaterThanEqual(10);
        }

        @Test
        @DisplayName("Obtener aeronaves sin resultados retorna lista vacía")
        void obtenerAerronavesPorEstado_SinResultados_RetornaListaVacia() {
            // Arrange
            when(aeronaveRepository.findByEstado(EstadoAeronave.FUERA_DE_SERVICIO))
                    .thenReturn(Collections.emptyList());
            when(aeronaveMapper.toDTOList(Collections.emptyList()))
                    .thenReturn(Collections.emptyList());

            // Act
            List<AeronaveDTO> resultado = aeronaveService.obtenerAerronavesPorEstado(EstadoAeronave.FUERA_DE_SERVICIO);

            // Assert
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    // ==================== ELIMINAR AERONAVE TESTS ====================

    @Nested
    @DisplayName("Eliminar Aeronave Tests")
    class EliminarAeronaveTests {

        @Test
        @DisplayName("Eliminar aeronave disponible exitosamente")
        void eliminarAeronave_ConEstadoDisponible_Exitoso() {
            // Arrange
            aeronaveTest.setEstado(EstadoAeronave.DISPONIBLE);
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));

            // Act
            aeronaveService.eliminarAeronave(1L);

            // Assert
            assertEquals(EstadoAeronave.FUERA_DE_SERVICIO, aeronaveTest.getEstado());
            verify(aeronaveRepository).save(aeronaveTest);
        }

        @Test
        @DisplayName("Eliminar aeronave en mantenimiento lanza excepción")
        void eliminarAeronave_ConEstadoEnMantenimiento_LanzaExcepcion() {
            // Arrange
            aeronaveTest.setEstado(EstadoAeronave.EN_MANTENIMIENTO);
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));

            // Act & Assert
            AeronaveNoDisponibleException exception = assertThrows(
                    AeronaveNoDisponibleException.class,
                    () -> aeronaveService.eliminarAeronave(1L)
            );
            assertTrue(exception.getMessage().contains("no está en estado DISPONIBLE"));
        }

        @Test
        @DisplayName("Eliminar aeronave inexistente lanza excepción")
        void eliminarAeronave_ConIdInexistente_LanzaExcepcion() {
            // Arrange
            when(aeronaveRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            AeronaveNoEncontradaException exception = assertThrows(
                    AeronaveNoEncontradaException.class,
                    () -> aeronaveService.eliminarAeronave(999L)
            );
            assertEquals("Aeronave no encontrada con ID: 999", exception.getMessage());
        }
    }

    // ==================== INCREMENTAR HORAS DE VUELO TESTS ====================

    @Nested
    @DisplayName("Incrementar Horas de Vuelo Tests")
    class IncrementarHorasVueloTests {

        @Test
        @DisplayName("Incrementar horas de vuelo exitosamente")
        void incrementarHorasVuelo_ConHorasValidas_Exitoso() {
            // Arrange
            Double horasActuales = aeronaveTest.getHorasVueloTotales();
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));

            // Act
            aeronaveService.incrementarHorasVuelo(1L, 10.0);

            // Assert
            assertEquals(horasActuales + 10.0, aeronaveTest.getHorasVueloTotales());
            verify(aeronaveRepository).save(aeronaveTest);
        }

        @Test
        @DisplayName("Incrementar horas inicia desde cero si es null")
        void incrementarHorasVuelo_SiHorasVueloEsNull_IniciaDesdeCero() {
            // Arrange
            aeronaveTest.setHorasVueloTotales(null);
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));

            // Act
            aeronaveService.incrementarHorasVuelo(1L, 5.0);

            // Assert
            assertEquals(5.0, aeronaveTest.getHorasVueloTotales());
            verify(aeronaveRepository).save(aeronaveTest);
        }

        @Test
        @DisplayName("Incrementar horas con valor negativo lanza excepción")
        void incrementarHorasVuelo_ConValorNegativo_LanzaExcepcion() {
            // Arrange
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> aeronaveService.incrementarHorasVuelo(1L, -5.0)
            );
            assertEquals("Las horas de vuelo a agregar deben ser positivas", exception.getMessage());
        }

        @Test
        @DisplayName("Incrementar horas con cero lanza excepción")
        void incrementarHorasVuelo_ConCero_LanzaExcepcion() {
            // Arrange
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> aeronaveService.incrementarHorasVuelo(1L, 0.0)
            );
            assertEquals("Las horas de vuelo a agregar deben ser positivas", exception.getMessage());
        }

        @Test
        @DisplayName("Incrementar horas con aeronave inexistente lanza excepción")
        void incrementarHorasVuelo_AeronaveInexistente_LanzaExcepcion() {
            // Arrange
            when(aeronaveRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            AeronaveNoEncontradaException exception = assertThrows(
                    AeronaveNoEncontradaException.class,
                    () -> aeronaveService.incrementarHorasVuelo(999L, 5.0)
            );
            assertEquals("Aeronave no encontrada con ID: 999", exception.getMessage());
        }
    }
}
