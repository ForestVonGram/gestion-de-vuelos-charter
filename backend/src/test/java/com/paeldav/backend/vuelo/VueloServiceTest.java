package com.paeldav.backend.vuelo;

import com.paeldav.backend.application.dto.vuelo.*;
import com.paeldav.backend.application.mapper.HistorialVueloMapper;
import com.paeldav.backend.application.mapper.VueloMapper;
import com.paeldav.backend.application.service.impl.VueloServiceImpl;
import com.paeldav.backend.domain.entity.*;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import com.paeldav.backend.domain.enums.EstadoTripulante;
import com.paeldav.backend.domain.enums.EstadoVuelo;
import com.paeldav.backend.exception.*;
import com.paeldav.backend.infraestructure.repository.*;
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
@DisplayName("VueloService Tests")
class VueloServiceTest {

    @Mock
    private VueloRepository vueloRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private AeronaveRepository aeronaveRepository;

    @Mock
    private TripulanteRepository tripulanteRepository;

    @Mock
    private HistorialVueloRepository historialVueloRepository;

    @Mock
    private VueloMapper vueloMapper;

    @Mock
    private HistorialVueloMapper historialVueloMapper;

    @InjectMocks
    private VueloServiceImpl vueloService;

    private Usuario usuarioTest;
    private Vuelo vueloTest;
    private Aeronave aeronaveTest;
    private Tripulante tripulantePilotoTest;
    private Tripulante tripulanteAuxiliarTest;
    private VueloCreateDTO vueloCreateDTOTest;
    private VueloDTO vueLoDTOTest;

    @BeforeEach
    void setUp() {
        usuarioTest = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@test.com")
                .password("encodedPassword")
                .activo(true)
                .build();

        vueloTest = Vuelo.builder()
                .id(1L)
                .usuario(usuarioTest)
                .origen("Cartagena")
                .destino("Bogotá")
                .fechaSalidaProgramada(LocalDateTime.now().plusHours(2))
                .fechaLlegadaProgramada(LocalDateTime.now().plusHours(4))
                .numeroPasajeros(10)
                .estado(EstadoVuelo.SOLICITADO)
                .proposito("Viaje de negocios")
                .observaciones("Sin observaciones")
                .fechaSolicitud(LocalDateTime.now())
                .build();

        vueloCreateDTOTest = VueloCreateDTO.builder()
                .usuarioId(1L)
                .origen("Cartagena")
                .destino("Bogotá")
                .fechaSalidaProgramada(LocalDateTime.now().plusHours(2))
                .fechaLlegadaProgramada(LocalDateTime.now().plusHours(4))
                .numeroPasajeros(10)
                .proposito("Viaje de negocios")
                .observaciones("Sin observaciones")
                .build();

        vueLoDTOTest = VueloDTO.builder()
                .id(1L)
                .usuarioId(1L)
                .usuarioNombre("Juan Pérez")
                .origen("Cartagena")
                .destino("Bogotá")
                .fechaSalidaProgramada(LocalDateTime.now().plusHours(2))
                .fechaLlegadaProgramada(LocalDateTime.now().plusHours(4))
                .numeroPasajeros(10)
                .estado(EstadoVuelo.SOLICITADO)
                .proposito("Viaje de negocios")
                .observaciones("Sin observaciones")
                .build();

        aeronaveTest = Aeronave.builder()
                .id(1L)
                .matricula("HK-1234")
                .modelo("Cessna 208")
                .capacidadPasajeros(14)
                .capacidadTripulacion(2)
                .estado(EstadoAeronave.DISPONIBLE)
                .build();

        tripulantePilotoTest = Tripulante.builder()
                .id(1L)
                .usuario(usuarioTest)
                .numeroLicencia("PIL-001")
                .tipoLicencia("ATP")
                .esPiloto(true)
                .estado(EstadoTripulante.DISPONIBLE)
                .fechaVencimientoLicencia(LocalDate.now().plusYears(1))
                .build();

        tripulanteAuxiliarTest = Tripulante.builder()
                .id(2L)
                .usuario(usuarioTest)
                .numeroLicencia("AUX-001")
                .tipoLicencia("Auxiliar de vuelo")
                .esPiloto(false)
                .estado(EstadoTripulante.DISPONIBLE)
                .fechaVencimientoLicencia(LocalDate.now().plusYears(1))
                .build();
    }

    @Nested
    @DisplayName("Crear Vuelo Tests")
    class CrearVueloTests {

        @Test
        @DisplayName("Crear vuelo con datos válidos")
        void crearVuelo_ConDatosValidos_GuardaVuelo() {
            // Arrange
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTest));
            when(vueloMapper.toEntity(vueloCreateDTOTest)).thenReturn(vueloTest);
            when(vueloRepository.save(any(Vuelo.class))).thenReturn(vueloTest);
            when(vueloMapper.toDTO(vueloTest)).thenReturn(vueLoDTOTest);

            // Act
            VueloDTO resultado = vueloService.crearVuelo(vueloCreateDTOTest);

            // Assert
            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            assertEquals("Cartagena", resultado.getOrigen());
            assertEquals("Bogotá", resultado.getDestino());
            assertEquals(EstadoVuelo.SOLICITADO, resultado.getEstado());
            verify(usuarioRepository).findById(1L);
            verify(vueloRepository).save(any(Vuelo.class));
        }

        @Test
        @DisplayName("Crear vuelo con usuario inexistente lanza excepción")
        void crearVuelo_ConUsuarioInexistente_LanzaExcepcion() {
            // Arrange
            when(usuarioRepository.findById(999L)).thenReturn(Optional.empty());
            VueloCreateDTO dtoConUsuarioInvalido = VueloCreateDTO.builder()
                    .usuarioId(999L)
                    .origen("Cartagena")
                    .destino("Bogotá")
                    .fechaSalidaProgramada(LocalDateTime.now().plusHours(2))
                    .fechaLlegadaProgramada(LocalDateTime.now().plusHours(4))
                    .numeroPasajeros(10)
                    .build();

            // Act & Assert
            UsuarioNoEncontradoException exception = assertThrows(
                    UsuarioNoEncontradoException.class,
                    () -> vueloService.crearVuelo(dtoConUsuarioInvalido)
            );
            assertEquals("Usuario no encontrado con ID: 999", exception.getMessage());
        }

        @Test
        @DisplayName("Crear vuelo inicia en estado SOLICITADO")
        void crearVuelo_IniciEnEstadoSolicitado() {
            // Arrange
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTest));
            when(vueloMapper.toEntity(vueloCreateDTOTest)).thenReturn(vueloTest);
            when(vueloRepository.save(any(Vuelo.class))).thenAnswer(invocation -> {
                Vuelo vuelo = invocation.getArgument(0);
                assertEquals(EstadoVuelo.SOLICITADO, vuelo.getEstado());
                return vuelo;
            });
            when(vueloMapper.toDTO(vueloTest)).thenReturn(vueLoDTOTest);

            // Act
            vueloService.crearVuelo(vueloCreateDTOTest);

            // Assert
            verify(vueloRepository).save(any(Vuelo.class));
        }
    }

    @Nested
    @DisplayName("Obtener Vuelo Tests")
    class ObtenerVueloTests {

        @Test
        @DisplayName("Obtener vuelo por ID existente")
        void obtenerVueloPorId_ConIdExistente_RetornaVuelo() {
            // Arrange
            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(vueloMapper.toDTO(vueloTest)).thenReturn(vueLoDTOTest);

            // Act
            VueloDTO resultado = vueloService.obtenerVueloPorId(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            assertEquals("Cartagena", resultado.getOrigen());
        }

        @Test
        @DisplayName("Obtener vuelo por ID inexistente lanza excepción")
        void obtenerVueloPorId_ConIdInexistente_LanzaExcepcion() {
            // Arrange
            when(vueloRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            VueloNoEncontradoException exception = assertThrows(
                    VueloNoEncontradoException.class,
                    () -> vueloService.obtenerVueloPorId(999L)
            );
            assertEquals("Vuelo no encontrado con ID: 999", exception.getMessage());
        }

        @Test
        @DisplayName("Obtener todos los vuelos")
        void obtenerTodosVuelos_RetornaListaCompleta() {
            // Arrange
            Vuelo vuelo2 = Vuelo.builder()
                    .id(2L)
                    .usuario(usuarioTest)
                    .origen("Bogotá")
                    .destino("Miami")
                    .estado(EstadoVuelo.CONFIRMADO)
                    .build();

            List<Vuelo> vuelos = Arrays.asList(vueloTest, vuelo2);
            List<VueloDTO> vuelosDTO = Arrays.asList(vueLoDTOTest);

            when(vueloRepository.findAll()).thenReturn(vuelos);
            when(vueloMapper.toDTOList(vuelos)).thenReturn(vuelosDTO);

            // Act
            List<VueloDTO> resultado = vueloService.obtenerTodosVuelos();

            // Assert
            assertNotNull(resultado);
            verify(vueloRepository).findAll();
            verify(vueloMapper).toDTOList(vuelos);
        }
    }

    @Nested
    @DisplayName("Cancelar Vuelo Tests")
    class CancelarVueloTests {

        @Test
        @DisplayName("Cancelar vuelo en estado SOLICITADO")
        void cancelarVuelo_ConEstadoSolicitado_Exitoso() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.SOLICITADO);
            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));

            // Act
            vueloService.cancelarVuelo(1L);

            // Assert
            assertEquals(EstadoVuelo.CANCELADO, vueloTest.getEstado());
            verify(vueloRepository).save(vueloTest);
        }

        @Test
        @DisplayName("Cancelar vuelo en estado CONFIRMADO")
        void cancelarVuelo_ConEstadoConfirmado_Exitoso() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.CONFIRMADO);
            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));

            // Act
            vueloService.cancelarVuelo(1L);

            // Assert
            assertEquals(EstadoVuelo.CANCELADO, vueloTest.getEstado());
            verify(vueloRepository).save(vueloTest);
        }

        @Test
        @DisplayName("Cancelar vuelo en estado EN_CURSO")
        void cancelarVuelo_ConEstadoEnCurso_Exitoso() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.EN_CURSO);
            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));

            // Act
            vueloService.cancelarVuelo(1L);

            // Assert
            assertEquals(EstadoVuelo.CANCELADO, vueloTest.getEstado());
            verify(vueloRepository).save(vueloTest);
        }

        @Test
        @DisplayName("Cancelar vuelo COMPLETADO lanza excepción")
        void cancelarVuelo_ConEstadoCompletado_LanzaExcepcion() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.COMPLETADO);
            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));

            // Act & Assert
            VueloEstadoInvalidoException exception = assertThrows(
                    VueloEstadoInvalidoException.class,
                    () -> vueloService.cancelarVuelo(1L)
            );
            assertEquals("No se puede cancelar un vuelo que ya ha sido completado", exception.getMessage());
        }

        @Test
        @DisplayName("Cancelar vuelo ya CANCELADO lanza excepción")
        void cancelarVuelo_ConEstadoCancelado_LanzaExcepcion() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.CANCELADO);
            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));

            // Act & Assert
            VueloEstadoInvalidoException exception = assertThrows(
                    VueloEstadoInvalidoException.class,
                    () -> vueloService.cancelarVuelo(1L)
            );
            assertEquals("El vuelo ya ha sido cancelado anteriormente", exception.getMessage());
        }

        @Test
        @DisplayName("Cancelar vuelo inexistente lanza excepción")
        void cancelarVuelo_ConVueloInexistente_LanzaExcepcion() {
            // Arrange
            when(vueloRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            VueloNoEncontradoException exception = assertThrows(
                    VueloNoEncontradoException.class,
                    () -> vueloService.cancelarVuelo(999L)
            );
            assertEquals("Vuelo no encontrado con ID: 999", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Cambiar Estado Vuelo Tests")
    class CambiarEstadoVueloTests {

        @Test
        @DisplayName("Cambiar de SOLICITADO a CONFIRMADO")
        void cambiarEstado_DeSolicitadoAConfirmado_Exitoso() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.SOLICITADO);
            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(vueloRepository.save(any(Vuelo.class))).thenReturn(vueloTest);
            when(vueloMapper.toDTO(any(Vuelo.class))).thenReturn(vueLoDTOTest);

            // Act
            VueloDTO resultado = vueloService.cambiarEstadoVuelo(1L, EstadoVuelo.CONFIRMADO);

            // Assert
            assertEquals(EstadoVuelo.CONFIRMADO, vueloTest.getEstado());
            verify(vueloRepository).save(vueloTest);
        }

        @Test
        @DisplayName("Cambiar de CONFIRMADO a EN_CURSO")
        void cambiarEstado_DeConfirmadoAEnCurso_Exitoso() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.CONFIRMADO);
            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(vueloRepository.save(any(Vuelo.class))).thenReturn(vueloTest);
            when(vueloMapper.toDTO(any(Vuelo.class))).thenReturn(vueLoDTOTest);

            // Act
            VueloDTO resultado = vueloService.cambiarEstadoVuelo(1L, EstadoVuelo.EN_CURSO);

            // Assert
            assertEquals(EstadoVuelo.EN_CURSO, vueloTest.getEstado());
            verify(vueloRepository).save(vueloTest);
        }

        @Test
        @DisplayName("Cambiar de EN_CURSO a COMPLETADO")
        void cambiarEstado_DeEnCursoACompletado_Exitoso() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.EN_CURSO);
            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(vueloRepository.save(any(Vuelo.class))).thenReturn(vueloTest);
            when(vueloMapper.toDTO(any(Vuelo.class))).thenReturn(vueLoDTOTest);

            // Act
            VueloDTO resultado = vueloService.cambiarEstadoVuelo(1L, EstadoVuelo.COMPLETADO);

            // Assert
            assertEquals(EstadoVuelo.COMPLETADO, vueloTest.getEstado());
            verify(vueloRepository).save(vueloTest);
        }

        @Test
        @DisplayName("Cambiar de SOLICITADO a CANCELADO")
        void cambiarEstado_DeSolicitadoACancelado_Exitoso() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.SOLICITADO);
            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(vueloRepository.save(any(Vuelo.class))).thenReturn(vueloTest);
            when(vueloMapper.toDTO(any(Vuelo.class))).thenReturn(vueLoDTOTest);

            // Act
            VueloDTO resultado = vueloService.cambiarEstadoVuelo(1L, EstadoVuelo.CANCELADO);

            // Assert
            assertEquals(EstadoVuelo.CANCELADO, vueloTest.getEstado());
            verify(vueloRepository).save(vueloTest);
        }

        @Test
        @DisplayName("Cambiar estado inválido de SOLICITADO a EN_CURSO lanza excepción")
        void cambiarEstado_TransicionInvalida_LanzaExcepcion() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.SOLICITADO);
            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));

            // Act & Assert
            VueloEstadoInvalidoException exception = assertThrows(
                    VueloEstadoInvalidoException.class,
                    () -> vueloService.cambiarEstadoVuelo(1L, EstadoVuelo.EN_CURSO)
            );
            assertTrue(exception.getMessage().contains("No se puede cambiar de SOLICITADO a EN_CURSO"));
        }

        @Test
        @DisplayName("Cambiar estado de vuelo COMPLETADO lanza excepción")
        void cambiarEstado_DesdeCompletado_LanzaExcepcion() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.COMPLETADO);
            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));

            // Act & Assert
            VueloEstadoInvalidoException exception = assertThrows(
                    VueloEstadoInvalidoException.class,
                    () -> vueloService.cambiarEstadoVuelo(1L, EstadoVuelo.CANCELADO)
            );
            assertTrue(exception.getMessage().contains("No se puede cambiar el estado de un vuelo COMPLETADO"));
        }

        @Test
        @DisplayName("Cambiar a mismo estado lanza excepción")
        void cambiarEstado_AMismoEstado_LanzaExcepcion() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.SOLICITADO);
            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));

            // Act & Assert
            VueloEstadoInvalidoException exception = assertThrows(
                    VueloEstadoInvalidoException.class,
                    () -> vueloService.cambiarEstadoVuelo(1L, EstadoVuelo.SOLICITADO)
            );
            assertEquals("El vuelo ya está en estado SOLICITADO", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Obtener Vuelos Por Estado Tests")
    class ObtenerVuelosPorEstadoTests {

        @Test
        @DisplayName("Obtener vuelos en estado SOLICITADO")
        void obtenerVuelosPorEstado_ConEstadoSolicitado_RetornaVuelos() {
            // Arrange
            List<Vuelo> vuelos = Arrays.asList(vueloTest);
            List<VueloDTO> vuelosDTO = Arrays.asList(vueLoDTOTest);
            when(vueloRepository.findByEstado(EstadoVuelo.SOLICITADO)).thenReturn(vuelos);
            when(vueloMapper.toDTOList(vuelos)).thenReturn(vuelosDTO);

            // Act
            List<VueloDTO> resultado = vueloService.obtenerVuelosPorEstado(EstadoVuelo.SOLICITADO);

            // Assert
            assertNotNull(resultado);
            verify(vueloRepository).findByEstado(EstadoVuelo.SOLICITADO);
        }

        @Test
        @DisplayName("Obtener vuelos con estado sin registros retorna lista vacía")
        void obtenerVuelosPorEstado_SinRegistros_RetornaListaVacia() {
            // Arrange
            when(vueloRepository.findByEstado(EstadoVuelo.COMPLETADO)).thenReturn(Arrays.asList());
            when(vueloMapper.toDTOList(Arrays.asList())).thenReturn(Arrays.asList());

            // Act
            List<VueloDTO> resultado = vueloService.obtenerVuelosPorEstado(EstadoVuelo.COMPLETADO);

            // Assert
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }

    @Nested
    @DisplayName("Actualizar Vuelo Tests")
    class ActualizarVueloTests {

        @Test
        @DisplayName("Actualizar vuelo con datos válidos")
        void actualizarVuelo_ConDatosValidos_Exitoso() {
            // Arrange
            VueloUpdateDTO updateDTO = VueloUpdateDTO.builder()
                    .origen("Medellín")
                    .destino("Cali")
                    .numeroPasajeros(15)
                    .build();

            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(vueloRepository.save(any(Vuelo.class))).thenReturn(vueloTest);
            when(vueloMapper.toDTO(vueloTest)).thenReturn(vueLoDTOTest);

            // Act
            VueloDTO resultado = vueloService.actualizarVuelo(1L, updateDTO);

            // Assert
            assertNotNull(resultado);
            verify(vueloMapper).updateEntityFromDTO(updateDTO, vueloTest);
            verify(vueloRepository).save(vueloTest);
        }

        @Test
        @DisplayName("Actualizar vuelo inexistente lanza excepción")
        void actualizarVuelo_ConVueloInexistente_LanzaExcepcion() {
            // Arrange
            VueloUpdateDTO updateDTO = VueloUpdateDTO.builder()
                    .origen("Medellín")
                    .build();
            when(vueloRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            VueloNoEncontradoException exception = assertThrows(
                    VueloNoEncontradoException.class,
                    () -> vueloService.actualizarVuelo(999L, updateDTO)
            );
            assertEquals("Vuelo no encontrado con ID: 999", exception.getMessage());
        }
    }

    // ==================== TESTS DE APROBACIÓN Y RECHAZO ====================

    @Nested
    @DisplayName("Aprobar Solicitud Tests")
    class AprobarSolicitudTests {

        @Test
        @DisplayName("Aprobar solicitud con estado SOLICITADO cambia a CONFIRMADO")
        void aprobarSolicitud_ConEstadoSolicitado_CambiaAConfirmado() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.SOLICITADO);
            SolicitudAprobacionDTO dto = SolicitudAprobacionDTO.builder()
                    .motivo("Vuelo aprobado por disponibilidad")
                    .costoEstimado(5000.0)
                    .build();

            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(vueloRepository.save(any(Vuelo.class))).thenReturn(vueloTest);
            when(vueloMapper.toDTO(any(Vuelo.class))).thenReturn(vueLoDTOTest);

            // Act
            VueloDTO resultado = vueloService.aprobarSolicitud(1L, dto);

            // Assert
            assertNotNull(resultado);
            assertEquals(EstadoVuelo.CONFIRMADO, vueloTest.getEstado());
            assertEquals(5000.0, vueloTest.getCostoEstimado());
            verify(historialVueloRepository).save(any(HistorialVuelo.class));
        }

        @Test
        @DisplayName("Aprobar solicitud con estado no SOLICITADO lanza excepción")
        void aprobarSolicitud_ConEstadoNoSolicitado_LanzaExcepcion() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.CONFIRMADO);
            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));

            // Act & Assert
            VueloEstadoInvalidoException exception = assertThrows(
                    VueloEstadoInvalidoException.class,
                    () -> vueloService.aprobarSolicitud(1L, null)
            );
            assertTrue(exception.getMessage().contains("SOLICITADO"));
        }

        @Test
        @DisplayName("Aprobar solicitud registra en historial")
        void aprobarSolicitud_RegistraHistorial() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.SOLICITADO);
            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(vueloRepository.save(any(Vuelo.class))).thenReturn(vueloTest);
            when(vueloMapper.toDTO(any(Vuelo.class))).thenReturn(vueLoDTOTest);

            // Act
            vueloService.aprobarSolicitud(1L, null);

            // Assert
            verify(historialVueloRepository).save(argThat(historial ->
                    historial.getTipoAccion().equals("APROBACION") &&
                    historial.getEstadoAnterior() == EstadoVuelo.SOLICITADO &&
                    historial.getEstadoNuevo() == EstadoVuelo.CONFIRMADO
            ));
        }
    }

    @Nested
    @DisplayName("Rechazar Solicitud Tests")
    class RechazarSolicitudTests {

        @Test
        @DisplayName("Rechazar solicitud con estado SOLICITADO cambia a CANCELADO")
        void rechazarSolicitud_ConEstadoSolicitado_CambiaACancelado() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.SOLICITADO);
            SolicitudRechazoDTO dto = SolicitudRechazoDTO.builder()
                    .motivo("No hay disponibilidad de aeronaves")
                    .build();

            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(vueloRepository.save(any(Vuelo.class))).thenReturn(vueloTest);
            when(vueloMapper.toDTO(any(Vuelo.class))).thenReturn(vueLoDTOTest);

            // Act
            VueloDTO resultado = vueloService.rechazarSolicitud(1L, dto);

            // Assert
            assertNotNull(resultado);
            assertEquals(EstadoVuelo.CANCELADO, vueloTest.getEstado());
            verify(historialVueloRepository).save(any(HistorialVuelo.class));
        }

        @Test
        @DisplayName("Rechazar solicitud con estado no SOLICITADO lanza excepción")
        void rechazarSolicitud_ConEstadoNoSolicitado_LanzaExcepcion() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.CONFIRMADO);
            SolicitudRechazoDTO dto = SolicitudRechazoDTO.builder()
                    .motivo("Motivo de rechazo")
                    .build();
            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));

            // Act & Assert
            VueloEstadoInvalidoException exception = assertThrows(
                    VueloEstadoInvalidoException.class,
                    () -> vueloService.rechazarSolicitud(1L, dto)
            );
            assertTrue(exception.getMessage().contains("SOLICITADO"));
        }

        @Test
        @DisplayName("Rechazar solicitud registra historial con motivo")
        void rechazarSolicitud_RegistraHistorialConMotivo() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.SOLICITADO);
            String motivoRechazo = "Aeronave no disponible para la fecha solicitada";
            SolicitudRechazoDTO dto = SolicitudRechazoDTO.builder()
                    .motivo(motivoRechazo)
                    .build();

            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(vueloRepository.save(any(Vuelo.class))).thenReturn(vueloTest);
            when(vueloMapper.toDTO(any(Vuelo.class))).thenReturn(vueLoDTOTest);

            // Act
            vueloService.rechazarSolicitud(1L, dto);

            // Assert
            verify(historialVueloRepository).save(argThat(historial ->
                    historial.getTipoAccion().equals("RECHAZO") &&
                    historial.getMotivo().equals(motivoRechazo) &&
                    historial.getEstadoNuevo() == EstadoVuelo.CANCELADO
            ));
        }
    }

    // ==================== TESTS DE ASIGNACIÓN DE RECURSOS ====================

    @Nested
    @DisplayName("Asignar Aeronave Tests")
    class AsignarAeronaveTests {

        @Test
        @DisplayName("Asignar aeronave disponible exitosamente")
        void asignarAeronave_ConAeronaveDisponible_AsignaExitosamente() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.CONFIRMADO);
            AsignacionAeronaveDTO dto = AsignacionAeronaveDTO.builder()
                    .aeronaveId(1L)
                    .build();

            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(vueloRepository.findVuelosEnRangoPorAeronave(anyLong(), any(), any(), any()))
                    .thenReturn(Collections.emptyList());
            when(vueloRepository.save(any(Vuelo.class))).thenReturn(vueloTest);
            when(vueloMapper.toDTO(any(Vuelo.class))).thenReturn(vueLoDTOTest);

            // Act
            VueloDTO resultado = vueloService.asignarAeronave(1L, dto);

            // Assert
            assertNotNull(resultado);
            assertEquals(aeronaveTest, vueloTest.getAeronave());
            verify(historialVueloRepository).save(any(HistorialVuelo.class));
        }

        @Test
        @DisplayName("Asignar aeronave en mantenimiento lanza excepción")
        void asignarAeronave_ConAeronaveEnMantenimiento_LanzaExcepcion() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.CONFIRMADO);
            aeronaveTest.setEstado(EstadoAeronave.EN_MANTENIMIENTO);
            AsignacionAeronaveDTO dto = AsignacionAeronaveDTO.builder()
                    .aeronaveId(1L)
                    .build();

            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));

            // Act & Assert
            AsignacionInvalidaException exception = assertThrows(
                    AsignacionInvalidaException.class,
                    () -> vueloService.asignarAeronave(1L, dto)
            );
            assertTrue(exception.getMessage().contains("no está disponible"));
        }

        @Test
        @DisplayName("Asignar aeronave con capacidad insuficiente lanza excepción")
        void asignarAeronave_ConCapacidadInsuficiente_LanzaExcepcion() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.CONFIRMADO);
            vueloTest.setNumeroPasajeros(20); // Más que la capacidad de la aeronave (14)
            AsignacionAeronaveDTO dto = AsignacionAeronaveDTO.builder()
                    .aeronaveId(1L)
                    .build();

            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));

            // Act & Assert
            AsignacionInvalidaException exception = assertThrows(
                    AsignacionInvalidaException.class,
                    () -> vueloService.asignarAeronave(1L, dto)
            );
            assertTrue(exception.getMessage().contains("capacidad"));
        }

        @Test
        @DisplayName("Asignar aeronave con conflicto de horario lanza excepción")
        void asignarAeronave_ConConflictoHorario_LanzaExcepcion() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.CONFIRMADO);
            vueloTest.setId(1L);
            
            Vuelo vueloConflicto = Vuelo.builder().id(2L).build();
            AsignacionAeronaveDTO dto = AsignacionAeronaveDTO.builder()
                    .aeronaveId(1L)
                    .build();

            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(vueloRepository.findVuelosEnRangoPorAeronave(anyLong(), any(), any(), any()))
                    .thenReturn(List.of(vueloConflicto));

            // Act & Assert
            ConflictoDisponibilidadException exception = assertThrows(
                    ConflictoDisponibilidadException.class,
                    () -> vueloService.asignarAeronave(1L, dto)
            );
            assertTrue(exception.getMessage().contains("conflictos de horario"));
        }

        @Test
        @DisplayName("Asignar aeronave inexistente lanza excepción")
        void asignarAeronave_ConAeronaveInexistente_LanzaExcepcion() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.CONFIRMADO);
            AsignacionAeronaveDTO dto = AsignacionAeronaveDTO.builder()
                    .aeronaveId(999L)
                    .build();

            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(aeronaveRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            AeronaveNoEncontradaException exception = assertThrows(
                    AeronaveNoEncontradaException.class,
                    () -> vueloService.asignarAeronave(1L, dto)
            );
            assertTrue(exception.getMessage().contains("999"));
        }
    }

    @Nested
    @DisplayName("Asignar Tripulación Tests")
    class AsignarTripulacionTests {

        @Test
        @DisplayName("Asignar tripulación con piloto exitosamente")
        void asignarTripulacion_ConTripulantesDisponibles_AsignaExitosamente() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.CONFIRMADO);
            AsignacionTripulacionDTO dto = AsignacionTripulacionDTO.builder()
                    .tripulanteIds(List.of(1L, 2L))
                    .build();

            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(tripulanteRepository.findById(1L)).thenReturn(Optional.of(tripulantePilotoTest));
            when(tripulanteRepository.findById(2L)).thenReturn(Optional.of(tripulanteAuxiliarTest));
            when(vueloRepository.findVuelosEnRangoPorTripulante(anyLong(), any(), any(), any()))
                    .thenReturn(Collections.emptyList());
            when(vueloRepository.save(any(Vuelo.class))).thenReturn(vueloTest);
            when(vueloMapper.toDTO(any(Vuelo.class))).thenReturn(vueLoDTOTest);

            // Act
            VueloDTO resultado = vueloService.asignarTripulacion(1L, dto);

            // Assert
            assertNotNull(resultado);
            assertEquals(2, vueloTest.getTripulacion().size());
            verify(historialVueloRepository).save(any(HistorialVuelo.class));
        }

        @Test
        @DisplayName("Asignar tripulación sin piloto lanza excepción")
        void asignarTripulacion_SinPiloto_LanzaExcepcion() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.CONFIRMADO);
            AsignacionTripulacionDTO dto = AsignacionTripulacionDTO.builder()
                    .tripulanteIds(List.of(2L)) // Solo auxiliar
                    .build();

            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(tripulanteRepository.findById(2L)).thenReturn(Optional.of(tripulanteAuxiliarTest));
            when(vueloRepository.findVuelosEnRangoPorTripulante(anyLong(), any(), any(), any()))
                    .thenReturn(Collections.emptyList());

            // Act & Assert
            AsignacionInvalidaException exception = assertThrows(
                    AsignacionInvalidaException.class,
                    () -> vueloService.asignarTripulacion(1L, dto)
            );
            assertTrue(exception.getMessage().contains("piloto"));
        }

        @Test
        @DisplayName("Asignar tripulante con licencia vencida lanza excepción")
        void asignarTripulacion_ConLicenciaVencida_LanzaExcepcion() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.CONFIRMADO);
            tripulantePilotoTest.setFechaVencimientoLicencia(LocalDate.now().minusDays(1));
            AsignacionTripulacionDTO dto = AsignacionTripulacionDTO.builder()
                    .tripulanteIds(List.of(1L))
                    .build();

            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(tripulanteRepository.findById(1L)).thenReturn(Optional.of(tripulantePilotoTest));

            // Act & Assert
            AsignacionInvalidaException exception = assertThrows(
                    AsignacionInvalidaException.class,
                    () -> vueloService.asignarTripulacion(1L, dto)
            );
            assertTrue(exception.getMessage().contains("licencia vencida"));
        }

        @Test
        @DisplayName("Asignar tripulante con conflicto de horario lanza excepción")
        void asignarTripulacion_ConConflictoHorario_LanzaExcepcion() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.CONFIRMADO);
            vueloTest.setId(1L);
            Vuelo vueloConflicto = Vuelo.builder().id(2L).build();
            AsignacionTripulacionDTO dto = AsignacionTripulacionDTO.builder()
                    .tripulanteIds(List.of(1L))
                    .build();

            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(tripulanteRepository.findById(1L)).thenReturn(Optional.of(tripulantePilotoTest));
            when(vueloRepository.findVuelosEnRangoPorTripulante(anyLong(), any(), any(), any()))
                    .thenReturn(List.of(vueloConflicto));

            // Act & Assert
            ConflictoDisponibilidadException exception = assertThrows(
                    ConflictoDisponibilidadException.class,
                    () -> vueloService.asignarTripulacion(1L, dto)
            );
            assertTrue(exception.getMessage().contains("conflictos de horario"));
        }

        @Test
        @DisplayName("Asignar tripulante no disponible lanza excepción")
        void asignarTripulacion_ConTripulanteNoDisponible_LanzaExcepcion() {
            // Arrange
            vueloTest.setEstado(EstadoVuelo.CONFIRMADO);
            tripulantePilotoTest.setEstado(EstadoTripulante.EN_VUELO);
            AsignacionTripulacionDTO dto = AsignacionTripulacionDTO.builder()
                    .tripulanteIds(List.of(1L))
                    .build();

            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(tripulanteRepository.findById(1L)).thenReturn(Optional.of(tripulantePilotoTest));

            // Act & Assert
            AsignacionInvalidaException exception = assertThrows(
                    AsignacionInvalidaException.class,
                    () -> vueloService.asignarTripulacion(1L, dto)
            );
            assertTrue(exception.getMessage().contains("no está disponible"));
        }
    }

    // ==================== TESTS DE HISTORIAL ====================

    @Nested
    @DisplayName("Historial Vuelo Tests")
    class HistorialVueloTests {

        @Test
        @DisplayName("Obtener historial de vuelo existente")
        void obtenerHistorialVuelo_RetornaHistorialOrdenado() {
            // Arrange
            HistorialVuelo historial1 = HistorialVuelo.builder()
                    .id(1L)
                    .vuelo(vueloTest)
                    .estadoAnterior(EstadoVuelo.SOLICITADO)
                    .estadoNuevo(EstadoVuelo.CONFIRMADO)
                    .tipoAccion("APROBACION")
                    .fechaCambio(LocalDateTime.now().minusHours(2))
                    .build();

            HistorialVuelo historial2 = HistorialVuelo.builder()
                    .id(2L)
                    .vuelo(vueloTest)
                    .estadoAnterior(EstadoVuelo.CONFIRMADO)
                    .estadoNuevo(EstadoVuelo.CONFIRMADO)
                    .tipoAccion("ASIGNACION_AERONAVE")
                    .fechaCambio(LocalDateTime.now().minusHours(1))
                    .build();

            List<HistorialVuelo> historialList = List.of(historial2, historial1);
            List<HistorialVueloDTO> historialDTOList = List.of(
                    HistorialVueloDTO.builder().id(2L).build(),
                    HistorialVueloDTO.builder().id(1L).build()
            );

            when(vueloRepository.existsById(1L)).thenReturn(true);
            when(historialVueloRepository.findByVueloIdOrderByFechaCambioDesc(1L))
                    .thenReturn(historialList);
            when(historialVueloMapper.toDTOList(historialList)).thenReturn(historialDTOList);

            // Act
            List<HistorialVueloDTO> resultado = vueloService.obtenerHistorialVuelo(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            verify(historialVueloRepository).findByVueloIdOrderByFechaCambioDesc(1L);
        }

        @Test
        @DisplayName("Obtener historial de vuelo inexistente lanza excepción")
        void obtenerHistorialVuelo_VueloInexistente_LanzaExcepcion() {
            // Arrange
            when(vueloRepository.existsById(999L)).thenReturn(false);

            // Act & Assert
            VueloNoEncontradoException exception = assertThrows(
                    VueloNoEncontradoException.class,
                    () -> vueloService.obtenerHistorialVuelo(999L)
            );
            assertTrue(exception.getMessage().contains("999"));
        }
    }

    @Nested
    @DisplayName("Obtener Vuelos Por Usuario Tests")
    class ObtenerVuelosPorUsuarioTests {

        @Test
        @DisplayName("Obtener vuelos de un usuario")
        void obtenerVuelosPorUsuario_RetornaVuelosDelUsuario() {
            // Arrange
            List<Vuelo> vuelos = List.of(vueloTest);
            List<VueloDTO> vuelosDTO = List.of(vueLoDTOTest);

            when(vueloRepository.findByUsuarioId(1L)).thenReturn(vuelos);
            when(vueloMapper.toDTOList(vuelos)).thenReturn(vuelosDTO);

            // Act
            List<VueloDTO> resultado = vueloService.obtenerVuelosPorUsuario(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(vueloRepository).findByUsuarioId(1L);
        }

        @Test
        @DisplayName("Obtener vuelos de usuario sin vuelos retorna lista vacía")
        void obtenerVuelosPorUsuario_SinVuelos_RetornaListaVacia() {
            // Arrange
            when(vueloRepository.findByUsuarioId(999L)).thenReturn(Collections.emptyList());
            when(vueloMapper.toDTOList(Collections.emptyList())).thenReturn(Collections.emptyList());

            // Act
            List<VueloDTO> resultado = vueloService.obtenerVuelosPorUsuario(999L);

            // Assert
            assertNotNull(resultado);
            assertTrue(resultado.isEmpty());
        }
    }
}
