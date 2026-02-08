package com.paeldav.backend.aeronave;

import com.paeldav.backend.application.dto.aeronave.AeronaveCreateDTO;
import com.paeldav.backend.application.dto.aeronave.AeronaveDTO;
import com.paeldav.backend.application.dto.aeronave.AeronaveUpdateDTO;
import com.paeldav.backend.application.dto.aeronave.HistorialUsoAeronaveDTO;
import com.paeldav.backend.application.dto.disponibilidad.ResumenDisponibilidadFlotaDTO;
import com.paeldav.backend.application.dto.mantenimiento.MantenimientoDTO;
import com.paeldav.backend.application.dto.repostaje.RepostajeDTO;
import com.paeldav.backend.application.dto.vuelo.VueloDTO;
import com.paeldav.backend.application.mapper.AeronaveMapper;
import com.paeldav.backend.application.mapper.MantenimientoMapper;
import com.paeldav.backend.application.mapper.RepostajeMapper;
import com.paeldav.backend.application.mapper.VueloMapper;
import com.paeldav.backend.application.service.impl.AeronaveServiceImpl;
import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.entity.Mantenimiento;
import com.paeldav.backend.domain.entity.Repostaje;
import com.paeldav.backend.domain.entity.Vuelo;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import com.paeldav.backend.domain.enums.EstadoVuelo;
import com.paeldav.backend.domain.enums.TipoMantenimiento;
import com.paeldav.backend.exception.*;
import com.paeldav.backend.infraestructure.repository.AeronaveRepository;
import com.paeldav.backend.infraestructure.repository.MantenimientoRepository;
import com.paeldav.backend.infraestructure.repository.RepostajeRepository;
import com.paeldav.backend.infraestructure.repository.VueloRepository;
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
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AeronaveService Tests")
class AeronaveServiceTest {

    @Mock
    private AeronaveRepository aeronaveRepository;

    @Mock
    private VueloRepository vueloRepository;

    @Mock
    private MantenimientoRepository mantenimientoRepository;

    @Mock
    private RepostajeRepository repostajeRepository;

    @Mock
    private AeronaveMapper aeronaveMapper;

    @Mock
    private VueloMapper vueloMapper;

    @Mock
    private MantenimientoMapper mantenimientoMapper;

    @Mock
    private RepostajeMapper repostajeMapper;

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

    // ==================== TRANSICIONES DE ESTADO OPERATIVO TESTS ====================

    @Nested
    @DisplayName("Transiciones de Estado Operativo Tests")
    class TransicionesEstadoOperativoTests {

        @Test
        @DisplayName("Transición válida de DISPONIBLE a EN_VUELO")
        void cambiarEstado_DeDisponibleAEnVuelo_Exitoso() {
            // Arrange
            aeronaveTest.setEstado(EstadoAeronave.DISPONIBLE);
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(aeronaveRepository.save(any(Aeronave.class))).thenReturn(aeronaveTest);
            when(aeronaveMapper.toDTO(any())).thenReturn(aeronaveDTOTest);

            // Act
            AeronaveDTO resultado = aeronaveService.cambiarEstadoAeronave(1L, EstadoAeronave.EN_VUELO);

            // Assert
            assertNotNull(resultado);
            assertEquals(EstadoAeronave.EN_VUELO, aeronaveTest.getEstado());
            verify(aeronaveRepository).save(aeronaveTest);
        }

        @Test
        @DisplayName("Transición válida de EN_VUELO a DISPONIBLE (aterriza)")
        void cambiarEstado_DeEnVueloADisponible_Exitoso() {
            // Arrange
            aeronaveTest.setEstado(EstadoAeronave.EN_VUELO);
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(aeronaveRepository.save(any(Aeronave.class))).thenReturn(aeronaveTest);
            when(aeronaveMapper.toDTO(any())).thenReturn(aeronaveDTOTest);

            // Act
            AeronaveDTO resultado = aeronaveService.cambiarEstadoAeronave(1L, EstadoAeronave.DISPONIBLE);

            // Assert
            assertNotNull(resultado);
            assertEquals(EstadoAeronave.DISPONIBLE, aeronaveTest.getEstado());
        }

        @Test
        @DisplayName("Transición inválida de EN_VUELO a EN_MANTENIMIENTO lanza excepción")
        void cambiarEstado_DeEnVueloAEnMantenimiento_LanzaExcepcion() {
            // Arrange
            aeronaveTest.setEstado(EstadoAeronave.EN_VUELO);
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));

            // Act & Assert
            AeronaveNoDisponibleException exception = assertThrows(
                    AeronaveNoDisponibleException.class,
                    () -> aeronaveService.cambiarEstadoAeronave(1L, EstadoAeronave.EN_MANTENIMIENTO)
            );
            assertTrue(exception.getMessage().contains("Transición de estado no permitida"));
            verify(aeronaveRepository, never()).save(any());
        }

        @Test
        @DisplayName("Transición válida de FUERA_DE_SERVICIO a DISPONIBLE")
        void cambiarEstado_DeFueraDeServicioADisponible_Exitoso() {
            // Arrange
            aeronaveTest.setEstado(EstadoAeronave.FUERA_DE_SERVICIO);
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(aeronaveRepository.save(any(Aeronave.class))).thenReturn(aeronaveTest);
            when(aeronaveMapper.toDTO(any())).thenReturn(aeronaveDTOTest);

            // Act
            AeronaveDTO resultado = aeronaveService.cambiarEstadoAeronave(1L, EstadoAeronave.DISPONIBLE);

            // Assert
            assertNotNull(resultado);
            assertEquals(EstadoAeronave.DISPONIBLE, aeronaveTest.getEstado());
        }

        @Test
        @DisplayName("Transición inválida de FUERA_DE_SERVICIO a EN_VUELO lanza excepción")
        void cambiarEstado_DeFueraDeServicioAEnVuelo_LanzaExcepcion() {
            // Arrange
            aeronaveTest.setEstado(EstadoAeronave.FUERA_DE_SERVICIO);
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));

            // Act & Assert
            AeronaveNoDisponibleException exception = assertThrows(
                    AeronaveNoDisponibleException.class,
                    () -> aeronaveService.cambiarEstadoAeronave(1L, EstadoAeronave.EN_VUELO)
            );
            assertTrue(exception.getMessage().contains("Transición de estado no permitida"));
        }

        @Test
        @DisplayName("Verificar transición válida retorna true")
        void esTransicionValida_TransicionPermitida_RetornaTrue() {
            // Act & Assert
            assertTrue(aeronaveService.esTransicionEstadoValida(EstadoAeronave.DISPONIBLE, EstadoAeronave.EN_VUELO));
            assertTrue(aeronaveService.esTransicionEstadoValida(EstadoAeronave.DISPONIBLE, EstadoAeronave.EN_MANTENIMIENTO));
            assertTrue(aeronaveService.esTransicionEstadoValida(EstadoAeronave.EN_VUELO, EstadoAeronave.DISPONIBLE));
            assertTrue(aeronaveService.esTransicionEstadoValida(EstadoAeronave.EN_MANTENIMIENTO, EstadoAeronave.DISPONIBLE));
        }

        @Test
        @DisplayName("Verificar transición inválida retorna false")
        void esTransicionValida_TransicionNoPermitida_RetornaFalse() {
            // Act & Assert
            assertFalse(aeronaveService.esTransicionEstadoValida(EstadoAeronave.EN_VUELO, EstadoAeronave.EN_MANTENIMIENTO));
            assertFalse(aeronaveService.esTransicionEstadoValida(EstadoAeronave.FUERA_DE_SERVICIO, EstadoAeronave.EN_VUELO));
        }
    }

    // ==================== BLOQUEO DE AERONAVES TESTS ====================

    @Nested
    @DisplayName("Bloqueo de Aeronaves Tests")
    class BloqueoAeronavesTests {

        @Test
        @DisplayName("Bloquear aeronave disponible exitosamente")
        void bloquearAeronave_ConEstadoDisponible_Exitoso() {
            // Arrange
            aeronaveTest.setEstado(EstadoAeronave.DISPONIBLE);
            AeronaveDTO dtoBloqueado = AeronaveDTO.builder()
                    .id(1L)
                    .matricula("HK-1234")
                    .estado(EstadoAeronave.FUERA_DE_SERVICIO)
                    .build();

            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(aeronaveRepository.save(any(Aeronave.class))).thenReturn(aeronaveTest);
            when(aeronaveMapper.toDTO(any())).thenReturn(dtoBloqueado);

            // Act
            AeronaveDTO resultado = aeronaveService.bloquearAeronave(1L, "Mantenimiento preventivo");

            // Assert
            assertNotNull(resultado);
            assertEquals(EstadoAeronave.FUERA_DE_SERVICIO, aeronaveTest.getEstado());
            verify(aeronaveRepository).save(aeronaveTest);
        }

        @Test
        @DisplayName("Bloquear aeronave en mantenimiento exitosamente")
        void bloquearAeronave_ConEstadoEnMantenimiento_Exitoso() {
            // Arrange
            aeronaveTest.setEstado(EstadoAeronave.EN_MANTENIMIENTO);
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(aeronaveRepository.save(any(Aeronave.class))).thenReturn(aeronaveTest);
            when(aeronaveMapper.toDTO(any())).thenReturn(aeronaveDTOTest);

            // Act
            AeronaveDTO resultado = aeronaveService.bloquearAeronave(1L, "Falla crítica detectada");

            // Assert
            assertNotNull(resultado);
            assertEquals(EstadoAeronave.FUERA_DE_SERVICIO, aeronaveTest.getEstado());
        }

        @Test
        @DisplayName("Bloquear aeronave en vuelo lanza excepción")
        void bloquearAeronave_ConEstadoEnVuelo_LanzaExcepcion() {
            // Arrange
            aeronaveTest.setEstado(EstadoAeronave.EN_VUELO);
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));

            // Act & Assert
            AeronaveNoDisponibleException exception = assertThrows(
                    AeronaveNoDisponibleException.class,
                    () -> aeronaveService.bloquearAeronave(1L, "Intento de bloqueo")
            );
            assertTrue(exception.getMessage().contains("EN_VUELO"));
            verify(aeronaveRepository, never()).save(any());
        }

        @Test
        @DisplayName("Bloquear aeronave ya bloqueada retorna sin cambios")
        void bloquearAeronave_YaBloqueada_RetornaSinCambios() {
            // Arrange
            aeronaveTest.setEstado(EstadoAeronave.FUERA_DE_SERVICIO);
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(aeronaveMapper.toDTO(aeronaveTest)).thenReturn(aeronaveDTOTest);

            // Act
            AeronaveDTO resultado = aeronaveService.bloquearAeronave(1L, "Motivo");

            // Assert
            assertNotNull(resultado);
            verify(aeronaveRepository, never()).save(any());
        }

        @Test
        @DisplayName("Bloquear aeronave inexistente lanza excepción")
        void bloquearAeronave_AeronaveInexistente_LanzaExcepcion() {
            // Arrange
            when(aeronaveRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            AeronaveNoEncontradaException exception = assertThrows(
                    AeronaveNoEncontradaException.class,
                    () -> aeronaveService.bloquearAeronave(999L, "Motivo")
            );
            assertEquals("Aeronave no encontrada con ID: 999", exception.getMessage());
        }

        @Test
        @DisplayName("Desbloquear aeronave bloqueada exitosamente")
        void desbloquearAeronave_ConEstadoFueraDeServicio_Exitoso() {
            // Arrange
            aeronaveTest.setEstado(EstadoAeronave.FUERA_DE_SERVICIO);
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(aeronaveRepository.save(any(Aeronave.class))).thenReturn(aeronaveTest);
            when(aeronaveMapper.toDTO(any())).thenReturn(aeronaveDTOTest);

            // Act
            AeronaveDTO resultado = aeronaveService.desbloquearAeronave(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(EstadoAeronave.DISPONIBLE, aeronaveTest.getEstado());
            verify(aeronaveRepository).save(aeronaveTest);
        }

        @Test
        @DisplayName("Desbloquear aeronave no bloqueada lanza excepción")
        void desbloquearAeronave_ConEstadoDisponible_LanzaExcepcion() {
            // Arrange
            aeronaveTest.setEstado(EstadoAeronave.DISPONIBLE);
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));

            // Act & Assert
            AeronaveNoDisponibleException exception = assertThrows(
                    AeronaveNoDisponibleException.class,
                    () -> aeronaveService.desbloquearAeronave(1L)
            );
            assertTrue(exception.getMessage().contains("FUERA_DE_SERVICIO"));
        }
    }

    // ==================== BLOQUEO AUTOMÁTICO POR ESTADO NO OPERATIVO TESTS ====================

    @Nested
    @DisplayName("Bloqueo Automático por Estado No Operativo Tests")
    class BloqueoAutomaticoTests {

        @Test
        @DisplayName("Cambio a FUERA_DE_SERVICIO bloquea automáticamente")
        void cambiarEstado_AFueraDeServicio_BloqueaAutomaticamente() {
            // Arrange
            aeronaveTest.setEstado(EstadoAeronave.EN_MANTENIMIENTO);
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(aeronaveRepository.save(any(Aeronave.class))).thenReturn(aeronaveTest);
            when(aeronaveMapper.toDTO(any())).thenReturn(aeronaveDTOTest);

            // Act
            aeronaveService.cambiarEstadoAeronave(1L, EstadoAeronave.FUERA_DE_SERVICIO);

            // Assert - Verifica que el estado se cambió a FUERA_DE_SERVICIO (bloqueado)
            assertEquals(EstadoAeronave.FUERA_DE_SERVICIO, aeronaveTest.getEstado());
            verify(aeronaveRepository).save(aeronaveTest);
        }

        @Test
        @DisplayName("Aeronave no puede pasar de EN_VUELO a EN_MANTENIMIENTO directamente")
        void cambiarEstado_DeEnVueloAEnMantenimiento_RequiereDisponiblePrimero() {
            // Arrange
            aeronaveTest.setEstado(EstadoAeronave.EN_VUELO);
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));

            // Act & Assert - No puede ir directo de EN_VUELO a EN_MANTENIMIENTO
            assertThrows(
                    AeronaveNoDisponibleException.class,
                    () -> aeronaveService.cambiarEstadoAeronave(1L, EstadoAeronave.EN_MANTENIMIENTO)
            );

            // Verifica que el estado no cambió
            assertEquals(EstadoAeronave.EN_VUELO, aeronaveTest.getEstado());
        }

        @Test
        @DisplayName("Aeronave bloqueada no puede iniciar vuelo")
        void cambiarEstado_DeFueraDeServicioAEnVuelo_NoPermitido() {
            // Arrange
            aeronaveTest.setEstado(EstadoAeronave.FUERA_DE_SERVICIO);
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));

            // Act & Assert
            AeronaveNoDisponibleException exception = assertThrows(
                    AeronaveNoDisponibleException.class,
                    () -> aeronaveService.cambiarEstadoAeronave(1L, EstadoAeronave.EN_VUELO)
            );
            assertTrue(exception.getMessage().contains("Transición de estado no permitida"));
        }
    }

    // ==================== CONSULTA RESUMEN DISPONIBILIDAD FLOTA TESTS ====================

    @Nested
    @DisplayName("Consulta Resumen Disponibilidad Flota Tests")
    class ConsultaResumenDisponibilidadFlotaTests {

        @Test
        @DisplayName("Consultar resumen con flota mixta")
        void consultarResumen_ConFlotaMixta_RetornaResumenCorrecto() {
            // Arrange
            Aeronave aeronave2 = Aeronave.builder()
                    .id(2L)
                    .matricula("HK-5678")
                    .modelo("King Air 350")
                    .capacidadPasajeros(8)
                    .capacidadTripulacion(2)
                    .estado(EstadoAeronave.EN_MANTENIMIENTO)
                    .build();

            Aeronave aeronave3 = Aeronave.builder()
                    .id(3L)
                    .matricula("HK-9012")
                    .modelo("Pilatus PC-12")
                    .capacidadPasajeros(9)
                    .capacidadTripulacion(2)
                    .estado(EstadoAeronave.FUERA_DE_SERVICIO)
                    .build();

            List<Aeronave> todasAeronaves = Arrays.asList(aeronaveTest, aeronave2, aeronave3);

            when(aeronaveRepository.findAll()).thenReturn(todasAeronaves);
            when(aeronaveRepository.findByEstado(EstadoAeronave.DISPONIBLE))
                    .thenReturn(Collections.singletonList(aeronaveTest));
            when(aeronaveRepository.findByEstado(EstadoAeronave.FUERA_DE_SERVICIO))
                    .thenReturn(Collections.singletonList(aeronave3));
            when(aeronaveMapper.toDTO(aeronaveTest)).thenReturn(aeronaveDTOTest);
            when(aeronaveMapper.toDTO(aeronave3)).thenReturn(AeronaveDTO.builder()
                    .id(3L).matricula("HK-9012").estado(EstadoAeronave.FUERA_DE_SERVICIO).build());

            // Act
            ResumenDisponibilidadFlotaDTO resumen = aeronaveService.consultarResumenDisponibilidadFlota();

            // Assert
            assertNotNull(resumen);
            assertEquals(3, resumen.getTotalAeronaves());
            assertEquals(1, resumen.getAeronavesDisponibles());
            assertEquals(1, resumen.getAeronavesEnMantenimiento());
            assertEquals(1, resumen.getAeronavesFueraDeServicio());
            assertEquals(0, resumen.getAeronavesEnVuelo());
            assertNotNull(resumen.getFechaConsulta());
            assertNotNull(resumen.getContadorPorEstado());
        }

        @Test
        @DisplayName("Consultar resumen con flota vacía")
        void consultarResumen_ConFlotaVacia_RetornaResumenVacio() {
            // Arrange
            when(aeronaveRepository.findAll()).thenReturn(Collections.emptyList());
            when(aeronaveRepository.findByEstado(any())).thenReturn(Collections.emptyList());

            // Act
            ResumenDisponibilidadFlotaDTO resumen = aeronaveService.consultarResumenDisponibilidadFlota();

            // Assert
            assertNotNull(resumen);
            assertEquals(0, resumen.getTotalAeronaves());
            assertEquals(0, resumen.getAeronavesDisponibles());
            assertEquals(0.0, resumen.getPorcentajeDisponibilidad());
            assertTrue(resumen.getListaAeronavesDisponibles().isEmpty());
        }

        @Test
        @DisplayName("Consultar resumen calcula porcentaje de disponibilidad correcto")
        void consultarResumen_CalculaPorcentajeCorrectamente() {
            // Arrange - 2 de 4 aeronaves disponibles = 50%
            Aeronave disponible1 = Aeronave.builder().id(1L).estado(EstadoAeronave.DISPONIBLE).build();
            Aeronave disponible2 = Aeronave.builder().id(2L).estado(EstadoAeronave.DISPONIBLE).build();
            Aeronave enVuelo = Aeronave.builder().id(3L).estado(EstadoAeronave.EN_VUELO).build();
            Aeronave bloqueada = Aeronave.builder().id(4L).estado(EstadoAeronave.FUERA_DE_SERVICIO).build();

            when(aeronaveRepository.findAll())
                    .thenReturn(Arrays.asList(disponible1, disponible2, enVuelo, bloqueada));
            when(aeronaveRepository.findByEstado(EstadoAeronave.DISPONIBLE))
                    .thenReturn(Arrays.asList(disponible1, disponible2));
            when(aeronaveRepository.findByEstado(EstadoAeronave.FUERA_DE_SERVICIO))
                    .thenReturn(Collections.singletonList(bloqueada));
            when(aeronaveMapper.toDTO(any(Aeronave.class))).thenReturn(aeronaveDTOTest);

            // Act
            ResumenDisponibilidadFlotaDTO resumen = aeronaveService.consultarResumenDisponibilidadFlota();

            // Assert
            assertEquals(50.0, resumen.getPorcentajeDisponibilidad());
        }

        @Test
        @DisplayName("Consultar resumen incluye listas de aeronaves disponibles y bloqueadas")
        void consultarResumen_IncluyeListasAeronaves() {
            // Arrange
            aeronaveTest.setEstado(EstadoAeronave.DISPONIBLE);
            Aeronave bloqueada = Aeronave.builder()
                    .id(2L)
                    .matricula("HK-BLOQ")
                    .estado(EstadoAeronave.FUERA_DE_SERVICIO)
                    .build();

            when(aeronaveRepository.findAll()).thenReturn(Arrays.asList(aeronaveTest, bloqueada));
            when(aeronaveRepository.findByEstado(EstadoAeronave.DISPONIBLE))
                    .thenReturn(Collections.singletonList(aeronaveTest));
            when(aeronaveRepository.findByEstado(EstadoAeronave.FUERA_DE_SERVICIO))
                    .thenReturn(Collections.singletonList(bloqueada));
            when(aeronaveMapper.toDTO(aeronaveTest)).thenReturn(aeronaveDTOTest);
            when(aeronaveMapper.toDTO(bloqueada)).thenReturn(AeronaveDTO.builder()
                    .id(2L).matricula("HK-BLOQ").estado(EstadoAeronave.FUERA_DE_SERVICIO).build());

            // Act
            ResumenDisponibilidadFlotaDTO resumen = aeronaveService.consultarResumenDisponibilidadFlota();

            // Assert
            assertEquals(1, resumen.getListaAeronavesDisponibles().size());
            assertEquals(1, resumen.getListaAerronavesBloqueadas().size());
            assertEquals("HK-1234", resumen.getListaAeronavesDisponibles().get(0).getMatricula());
            assertEquals("HK-BLOQ", resumen.getListaAerronavesBloqueadas().get(0).getMatricula());
        }
    }

    // ==================== HISTORIAL DE USO DE AERONAVE TESTS ====================

    @Nested
    @DisplayName("Historial de Uso de Aeronave Tests")
    class HistorialUsoAeronaveTests {

        @Test
        @DisplayName("Obtener historial de uso completo de aeronave")
        void obtenerHistorialUso_ConDatosCompletos_RetornaHistorialCompleto() {
            // Arrange
            Vuelo vuelo1 = Vuelo.builder()
                    .id(1L)
                    .aeronave(aeronaveTest)
                    .estado(EstadoVuelo.COMPLETADO)
                    .fechaSalidaProgramada(LocalDateTime.now().minusDays(5))
                    .fechaSalidaReal(LocalDateTime.now().minusDays(5))
                    .fechaLlegadaReal(LocalDateTime.now().minusDays(5).plusHours(2))
                    .build();

            Vuelo vuelo2 = Vuelo.builder()
                    .id(2L)
                    .aeronave(aeronaveTest)
                    .estado(EstadoVuelo.CANCELADO)
                    .fechaSalidaProgramada(LocalDateTime.now().minusDays(3))
                    .build();

            Mantenimiento mantenimiento1 = Mantenimiento.builder()
                    .id(1L)
                    .aeronave(aeronaveTest)
                    .tipo(TipoMantenimiento.PREVENTIVO)
                    .fechaInicio(LocalDateTime.now().minusDays(10))
                    .costo(500.0)
                    .build();

            Mantenimiento mantenimiento2 = Mantenimiento.builder()
                    .id(2L)
                    .aeronave(aeronaveTest)
                    .tipo(TipoMantenimiento.CORRECTIVO)
                    .fechaInicio(LocalDateTime.now().minusDays(2))
                    .costo(1500.0)
                    .build();

            Repostaje repostaje1 = Repostaje.builder()
                    .id(1L)
                    .aeronave(aeronaveTest)
                    .cantidadLitros(200.0)
                    .costoTotal(400.0)
                    .fechaRepostaje(LocalDateTime.now().minusDays(5))
                    .build();

            List<Vuelo> vuelos = Arrays.asList(vuelo1, vuelo2);
            List<Mantenimiento> mantenimientos = Arrays.asList(mantenimiento1, mantenimiento2);
            List<Repostaje> repostajes = Collections.singletonList(repostaje1);

            VueloDTO vueloDTO1 = VueloDTO.builder().id(1L).estado(EstadoVuelo.COMPLETADO).build();
            VueloDTO vueloDTO2 = VueloDTO.builder().id(2L).estado(EstadoVuelo.CANCELADO).build();
            MantenimientoDTO mantenimientoDTO1 = MantenimientoDTO.builder().id(1L).tipo(TipoMantenimiento.PREVENTIVO).build();
            MantenimientoDTO mantenimientoDTO2 = MantenimientoDTO.builder().id(2L).tipo(TipoMantenimiento.CORRECTIVO).build();
            RepostajeDTO repostajeDTO1 = RepostajeDTO.builder().id(1L).cantidadLitros(200.0).build();

            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(aeronaveMapper.toDTO(aeronaveTest)).thenReturn(aeronaveDTOTest);
            when(vueloRepository.findByAeronaveId(1L)).thenReturn(vuelos);
            when(mantenimientoRepository.findByAeronaveId(1L)).thenReturn(mantenimientos);
            when(repostajeRepository.findByAeronaveId(1L)).thenReturn(repostajes);
            when(vueloMapper.toDTOList(vuelos)).thenReturn(Arrays.asList(vueloDTO1, vueloDTO2));
            when(mantenimientoMapper.toDTOList(mantenimientos)).thenReturn(Arrays.asList(mantenimientoDTO1, mantenimientoDTO2));
            when(repostajeMapper.toDTOList(repostajes)).thenReturn(Collections.singletonList(repostajeDTO1));

            // Act
            HistorialUsoAeronaveDTO historial = aeronaveService.obtenerHistorialUso(1L);

            // Assert
            assertNotNull(historial);
            assertNotNull(historial.getAeronave());
            assertEquals("HK-1234", historial.getAeronave().getMatricula());

            // Verificar estadísticas de vuelos
            assertEquals(2, historial.getTotalVuelos());
            assertEquals(1, historial.getVuelosCompletados());
            assertEquals(1, historial.getVuelosCancelados());
            assertEquals(2.0, historial.getTotalHorasVuelo());

            // Verificar estadísticas de mantenimientos
            assertEquals(2, historial.getTotalMantenimientos());
            assertEquals(1, historial.getMantenimientosPreventivos());
            assertEquals(1, historial.getMantenimientosCorrectivos());
            assertEquals(2000.0, historial.getCostoTotalMantenimientos());

            // Verificar estadísticas de repostajes
            assertEquals(1, historial.getTotalRepostajes());
            assertEquals(200.0, historial.getTotalLitrosCombustible());
            assertEquals(400.0, historial.getCostoTotalCombustible());

            // Verificar que no hay filtro de fecha
            assertNull(historial.getFechaDesde());
            assertNull(historial.getFechaHasta());
            assertNotNull(historial.getFechaGeneracion());

            verify(aeronaveRepository).findById(1L);
            verify(vueloRepository).findByAeronaveId(1L);
            verify(mantenimientoRepository).findByAeronaveId(1L);
            verify(repostajeRepository).findByAeronaveId(1L);
        }

        @Test
        @DisplayName("Obtener historial de uso con aeronave sin registros")
        void obtenerHistorialUso_SinRegistros_RetornaHistorialVacio() {
            // Arrange
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(aeronaveMapper.toDTO(aeronaveTest)).thenReturn(aeronaveDTOTest);
            when(vueloRepository.findByAeronaveId(1L)).thenReturn(Collections.emptyList());
            when(mantenimientoRepository.findByAeronaveId(1L)).thenReturn(Collections.emptyList());
            when(repostajeRepository.findByAeronaveId(1L)).thenReturn(Collections.emptyList());
            when(vueloMapper.toDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());
            when(mantenimientoMapper.toDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());
            when(repostajeMapper.toDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

            // Act
            HistorialUsoAeronaveDTO historial = aeronaveService.obtenerHistorialUso(1L);

            // Assert
            assertNotNull(historial);
            assertEquals(0, historial.getTotalVuelos());
            assertEquals(0, historial.getVuelosCompletados());
            assertEquals(0, historial.getVuelosCancelados());
            assertEquals(0.0, historial.getTotalHorasVuelo());
            assertEquals(0, historial.getTotalMantenimientos());
            assertEquals(0, historial.getTotalRepostajes());
            assertTrue(historial.getVuelos().isEmpty());
            assertTrue(historial.getMantenimientos().isEmpty());
            assertTrue(historial.getRepostajes().isEmpty());
        }

        @Test
        @DisplayName("Obtener historial de uso con aeronave inexistente lanza excepción")
        void obtenerHistorialUso_AeronaveInexistente_LanzaExcepcion() {
            // Arrange
            when(aeronaveRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            AeronaveNoEncontradaException exception = assertThrows(
                    AeronaveNoEncontradaException.class,
                    () -> aeronaveService.obtenerHistorialUso(999L)
            );
            assertEquals("Aeronave no encontrada con ID: 999", exception.getMessage());
        }

        @Test
        @DisplayName("Obtener historial de uso con rango de fechas")
        void obtenerHistorialUso_ConRangoDeFechas_FiltraCorrectamente() {
            // Arrange
            LocalDateTime fechaDesde = LocalDateTime.now().minusDays(7);
            LocalDateTime fechaHasta = LocalDateTime.now();

            Vuelo vueloDentroRango = Vuelo.builder()
                    .id(1L)
                    .aeronave(aeronaveTest)
                    .estado(EstadoVuelo.COMPLETADO)
                    .fechaSalidaProgramada(LocalDateTime.now().minusDays(3))
                    .fechaSalidaReal(LocalDateTime.now().minusDays(3))
                    .fechaLlegadaReal(LocalDateTime.now().minusDays(3).plusHours(1))
                    .build();

            Vuelo vueloFueraRango = Vuelo.builder()
                    .id(2L)
                    .aeronave(aeronaveTest)
                    .estado(EstadoVuelo.COMPLETADO)
                    .fechaSalidaProgramada(LocalDateTime.now().minusDays(15))
                    .build();

            List<Vuelo> todosVuelos = Arrays.asList(vueloDentroRango, vueloFueraRango);
            List<Vuelo> vuelosFiltrados = Collections.singletonList(vueloDentroRango);

            VueloDTO vueloDTO = VueloDTO.builder().id(1L).estado(EstadoVuelo.COMPLETADO).build();

            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(aeronaveMapper.toDTO(aeronaveTest)).thenReturn(aeronaveDTOTest);
            when(vueloRepository.findByAeronaveId(1L)).thenReturn(todosVuelos);
            when(mantenimientoRepository.findByAeronaveId(1L)).thenReturn(Collections.emptyList());
            when(repostajeRepository.findByAeronaveId(1L)).thenReturn(Collections.emptyList());
            when(vueloMapper.toDTOList(anyList())).thenReturn(Collections.singletonList(vueloDTO));
            when(mantenimientoMapper.toDTOList(anyList())).thenReturn(Collections.emptyList());
            when(repostajeMapper.toDTOList(anyList())).thenReturn(Collections.emptyList());

            // Act
            HistorialUsoAeronaveDTO historial = aeronaveService.obtenerHistorialUso(1L, fechaDesde, fechaHasta);

            // Assert
            assertNotNull(historial);
            assertEquals(1, historial.getTotalVuelos());
            assertEquals(fechaDesde, historial.getFechaDesde());
            assertEquals(fechaHasta, historial.getFechaHasta());
        }

        @Test
        @DisplayName("Obtener historial de uso calcula horas de vuelo correctamente")
        void obtenerHistorialUso_CalculaHorasVueloCorrectamente() {
            // Arrange
            LocalDateTime salidaVuelo1 = LocalDateTime.now().minusDays(2).withHour(10).withMinute(0);
            LocalDateTime llegadaVuelo1 = LocalDateTime.now().minusDays(2).withHour(12).withMinute(30);

            Vuelo vuelo = Vuelo.builder()
                    .id(1L)
                    .aeronave(aeronaveTest)
                    .estado(EstadoVuelo.COMPLETADO)
                    .fechaSalidaProgramada(salidaVuelo1)
                    .fechaSalidaReal(salidaVuelo1)
                    .fechaLlegadaReal(llegadaVuelo1)
                    .build();

            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(aeronaveMapper.toDTO(aeronaveTest)).thenReturn(aeronaveDTOTest);
            when(vueloRepository.findByAeronaveId(1L)).thenReturn(Collections.singletonList(vuelo));
            when(mantenimientoRepository.findByAeronaveId(1L)).thenReturn(Collections.emptyList());
            when(repostajeRepository.findByAeronaveId(1L)).thenReturn(Collections.emptyList());
            when(vueloMapper.toDTOList(anyList())).thenReturn(Collections.singletonList(VueloDTO.builder().id(1L).build()));
            when(mantenimientoMapper.toDTOList(anyList())).thenReturn(Collections.emptyList());
            when(repostajeMapper.toDTOList(anyList())).thenReturn(Collections.emptyList());

            // Act
            HistorialUsoAeronaveDTO historial = aeronaveService.obtenerHistorialUso(1L);

            // Assert
            // 2 horas y 30 minutos = 2.5 horas
            assertEquals(2.5, historial.getTotalHorasVuelo());
        }

        @Test
        @DisplayName("Obtener historial de uso cuenta tipos de mantenimiento correctamente")
        void obtenerHistorialUso_CuentaTiposMantenimientoCorrectamente() {
            // Arrange
            Mantenimiento preventivo1 = Mantenimiento.builder()
                    .id(1L).aeronave(aeronaveTest).tipo(TipoMantenimiento.PREVENTIVO)
                    .fechaInicio(LocalDateTime.now().minusDays(5)).costo(100.0).build();
            Mantenimiento preventivo2 = Mantenimiento.builder()
                    .id(2L).aeronave(aeronaveTest).tipo(TipoMantenimiento.PREVENTIVO)
                    .fechaInicio(LocalDateTime.now().minusDays(3)).costo(150.0).build();
            Mantenimiento correctivo1 = Mantenimiento.builder()
                    .id(3L).aeronave(aeronaveTest).tipo(TipoMantenimiento.CORRECTIVO)
                    .fechaInicio(LocalDateTime.now().minusDays(1)).costo(500.0).build();

            List<Mantenimiento> mantenimientos = Arrays.asList(preventivo1, preventivo2, correctivo1);

            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(aeronaveMapper.toDTO(aeronaveTest)).thenReturn(aeronaveDTOTest);
            when(vueloRepository.findByAeronaveId(1L)).thenReturn(Collections.emptyList());
            when(mantenimientoRepository.findByAeronaveId(1L)).thenReturn(mantenimientos);
            when(repostajeRepository.findByAeronaveId(1L)).thenReturn(Collections.emptyList());
            when(vueloMapper.toDTOList(anyList())).thenReturn(Collections.emptyList());
            when(mantenimientoMapper.toDTOList(anyList())).thenReturn(Collections.emptyList());
            when(repostajeMapper.toDTOList(anyList())).thenReturn(Collections.emptyList());

            // Act
            HistorialUsoAeronaveDTO historial = aeronaveService.obtenerHistorialUso(1L);

            // Assert
            assertEquals(3, historial.getTotalMantenimientos());
            assertEquals(2, historial.getMantenimientosPreventivos());
            assertEquals(1, historial.getMantenimientosCorrectivos());
            assertEquals(750.0, historial.getCostoTotalMantenimientos());
        }

        @Test
        @DisplayName("Obtener historial de uso suma combustible correctamente")
        void obtenerHistorialUso_SumaCombustibleCorrectamente() {
            // Arrange
            Repostaje repostaje1 = Repostaje.builder()
                    .id(1L).aeronave(aeronaveTest).cantidadLitros(150.0).costoTotal(300.0)
                    .fechaRepostaje(LocalDateTime.now().minusDays(3)).build();
            Repostaje repostaje2 = Repostaje.builder()
                    .id(2L).aeronave(aeronaveTest).cantidadLitros(200.0).costoTotal(450.0)
                    .fechaRepostaje(LocalDateTime.now().minusDays(1)).build();

            List<Repostaje> repostajes = Arrays.asList(repostaje1, repostaje2);

            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(aeronaveMapper.toDTO(aeronaveTest)).thenReturn(aeronaveDTOTest);
            when(vueloRepository.findByAeronaveId(1L)).thenReturn(Collections.emptyList());
            when(mantenimientoRepository.findByAeronaveId(1L)).thenReturn(Collections.emptyList());
            when(repostajeRepository.findByAeronaveId(1L)).thenReturn(repostajes);
            when(vueloMapper.toDTOList(anyList())).thenReturn(Collections.emptyList());
            when(mantenimientoMapper.toDTOList(anyList())).thenReturn(Collections.emptyList());
            when(repostajeMapper.toDTOList(anyList())).thenReturn(Collections.emptyList());

            // Act
            HistorialUsoAeronaveDTO historial = aeronaveService.obtenerHistorialUso(1L);

            // Assert
            assertEquals(2, historial.getTotalRepostajes());
            assertEquals(350.0, historial.getTotalLitrosCombustible());
            assertEquals(750.0, historial.getCostoTotalCombustible());
        }
    }
}
