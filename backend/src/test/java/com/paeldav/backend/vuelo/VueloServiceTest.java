package com.paeldav.backend.vuelo;

import com.paeldav.backend.application.dto.vuelo.VueloCreateDTO;
import com.paeldav.backend.application.dto.vuelo.VueloDTO;
import com.paeldav.backend.application.dto.vuelo.VueloUpdateDTO;
import com.paeldav.backend.application.mapper.VueloMapper;
import com.paeldav.backend.application.service.impl.VueloServiceImpl;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.entity.Vuelo;
import com.paeldav.backend.domain.enums.EstadoVuelo;
import com.paeldav.backend.exception.UsuarioNoEncontradoException;
import com.paeldav.backend.exception.VueloEstadoInvalidoException;
import com.paeldav.backend.exception.VueloNoEncontradoException;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import com.paeldav.backend.infraestructure.repository.VueloRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VueloService Tests")
class VueloServiceTest {

    @Mock
    private VueloRepository vueloRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private VueloMapper vueloMapper;

    @InjectMocks
    private VueloServiceImpl vueloService;

    private Usuario usuarioTest;
    private Vuelo vueloTest;
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
}
