package com.paeldav.backend.registrohorasvuelo;

import com.paeldav.backend.application.dto.registrohorasvuelo.RegistroHorasVueloCreateDTO;
import com.paeldav.backend.application.dto.registrohorasvuelo.RegistroHorasVueloDTO;
import com.paeldav.backend.application.mapper.RegistroHorasVueloMapper;
import com.paeldav.backend.application.service.impl.RegistroHorasVueloServiceImpl;
import com.paeldav.backend.domain.entity.*;
import com.paeldav.backend.domain.enums.EstadoTripulante;
import com.paeldav.backend.domain.enums.EstadoVuelo;
import com.paeldav.backend.exception.AsignacionInvalidaException;
import com.paeldav.backend.exception.TripulanteNoEncontradoException;
import com.paeldav.backend.exception.VueloNoEncontradoException;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegistroHorasVueloService Tests")
class RegistroHorasVueloServiceTest {

    @Mock
    private RegistroHorasVueloRepository registroRepository;

    @Mock
    private TripulanteRepository tripulanteRepository;

    @Mock
    private VueloRepository vueloRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private RegistroHorasVueloMapper registroMapper;

    @InjectMocks
    private RegistroHorasVueloServiceImpl registroService;

    private Usuario usuarioTest;
    private Vuelo vueloTest;
    private Tripulante tripulanteTest;
    private RegistroHorasVuelo registroTest;
    private RegistroHorasVueloCreateDTO registroCreateDTOTest;
    private RegistroHorasVueloDTO registroDTOTest;

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

        tripulanteTest = Tripulante.builder()
                .id(1L)
                .usuario(usuarioTest)
                .numeroLicencia("PIL-001")
                .tipoLicencia("ATP")
                .esPiloto(true)
                .estado(EstadoTripulante.DISPONIBLE)
                .fechaVencimientoLicencia(LocalDate.now().plusYears(1))
                .horasVueloTotales(100.0)
                .horasVueloMes(15.0)
                .build();

        vueloTest = Vuelo.builder()
                .id(1L)
                .usuario(usuarioTest)
                .origen("Cartagena")
                .destino("Bogotá")
                .fechaSalidaProgramada(LocalDateTime.now().plusHours(2))
                .fechaLlegadaProgramada(LocalDateTime.now().plusHours(4))
                .estado(EstadoVuelo.CONFIRMADO)
                .tripulacion(List.of(tripulanteTest))
                .build();

        registroTest = RegistroHorasVuelo.builder()
                .id(1L)
                .tripulante(tripulanteTest)
                .vuelo(vueloTest)
                .horasVoladas(2.5)
                .funcionDesempenada("PILOTO_COMANDANTE")
                .horaDespegue(LocalDateTime.now())
                .horaAterrizaje(LocalDateTime.now().plusHours(2).plusMinutes(30))
                .tipoVuelo("DIURNO")
                .fechaRegistro(LocalDateTime.now())
                .aprobado(false)
                .build();

        registroCreateDTOTest = RegistroHorasVueloCreateDTO.builder()
                .tripulanteId(1L)
                .vueloId(1L)
                .horasVoladas(2.5)
                .funcionDesempenada("PILOTO_COMANDANTE")
                .tipoVuelo("DIURNO")
                .build();

        registroDTOTest = RegistroHorasVueloDTO.builder()
                .id(1L)
                .tripulanteId(1L)
                .tripulanteNombre("Juan Pérez")
                .vueloId(1L)
                .vueloRuta("Cartagena - Bogotá")
                .horasVoladas(2.5)
                .funcionDesempenada("PILOTO_COMANDANTE")
                .aprobado(false)
                .build();
    }

    @Nested
    @DisplayName("Crear Registro Tests")
    class CrearRegistroTests {

        @Test
        @DisplayName("Crear registro con datos válidos")
        void crearRegistro_ConDatosValidos_GuardaRegistro() {
            // Arrange
            when(tripulanteRepository.findById(1L)).thenReturn(Optional.of(tripulanteTest));
            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(registroMapper.toEntity(registroCreateDTOTest)).thenReturn(registroTest);
            when(registroRepository.save(any(RegistroHorasVuelo.class))).thenReturn(registroTest);
            when(registroMapper.toDTO(registroTest)).thenReturn(registroDTOTest);

            // Act
            RegistroHorasVueloDTO resultado = registroService.crearRegistro(registroCreateDTOTest);

            // Assert
            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            assertEquals(2.5, resultado.getHorasVoladas());
            verify(tripulanteRepository).findById(1L);
            verify(vueloRepository).findById(1L);
            verify(registroRepository).save(any(RegistroHorasVuelo.class));
        }

        @Test
        @DisplayName("Crear registro con tripulante inexistente lanza excepción")
        void crearRegistro_ConTripulanteInexistente_LanzaExcepcion() {
            // Arrange
            when(tripulanteRepository.findById(999L)).thenReturn(Optional.empty());
            registroCreateDTOTest.setTripulanteId(999L);

            // Act & Assert
            TripulanteNoEncontradoException exception = assertThrows(
                    TripulanteNoEncontradoException.class,
                    () -> registroService.crearRegistro(registroCreateDTOTest)
            );
            assertEquals("Tripulante no encontrado con ID: 999", exception.getMessage());
        }

        @Test
        @DisplayName("Crear registro con vuelo inexistente lanza excepción")
        void crearRegistro_ConVueloInexistente_LanzaExcepcion() {
            // Arrange
            when(tripulanteRepository.findById(1L)).thenReturn(Optional.of(tripulanteTest));
            when(vueloRepository.findById(999L)).thenReturn(Optional.empty());
            registroCreateDTOTest.setVueloId(999L);

            // Act & Assert
            VueloNoEncontradoException exception = assertThrows(
                    VueloNoEncontradoException.class,
                    () -> registroService.crearRegistro(registroCreateDTOTest)
            );
            assertEquals("Vuelo no encontrado con ID: 999", exception.getMessage());
        }

        @Test
        @DisplayName("Crear registro con tripulante no asignado lanza excepción")
        void crearRegistro_ConTripulanteNoAsignado_LanzaExcepcion() {
            // Arrange - Vuelo sin tripulante asignado
            Vuelo vueloSinTripulacion = Vuelo.builder()
                    .id(1L)
                    .usuario(usuarioTest)
                    .origen("Cartagena")
                    .destino("Bogotá")
                    .estado(EstadoVuelo.CONFIRMADO)
                    .tripulacion(Collections.emptyList())
                    .build();

            when(tripulanteRepository.findById(1L)).thenReturn(Optional.of(tripulanteTest));
            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloSinTripulacion));

            // Act & Assert
            AsignacionInvalidaException exception = assertThrows(
                    AsignacionInvalidaException.class,
                    () -> registroService.crearRegistro(registroCreateDTOTest)
            );
            assertTrue(exception.getMessage().contains("no está asignado al vuelo"));
        }
    }

    @Nested
    @DisplayName("Obtener Registro Tests")
    class ObtenerRegistroTests {

        @Test
        @DisplayName("Obtener registro por ID")
        void obtenerRegistroPorId_ConIdValido_RetornaRegistro() {
            // Arrange
            when(registroRepository.findById(1L)).thenReturn(Optional.of(registroTest));
            when(registroMapper.toDTO(registroTest)).thenReturn(registroDTOTest);

            // Act
            RegistroHorasVueloDTO resultado = registroService.obtenerRegistroPorId(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            verify(registroRepository).findById(1L);
        }

        @Test
        @DisplayName("Obtener registros por tripulante")
        void obtenerRegistrosPorTripulante_ConIdValido_RetornaLista() {
            // Arrange
            when(tripulanteRepository.existsById(1L)).thenReturn(true);
            when(registroRepository.findByTripulanteId(1L)).thenReturn(List.of(registroTest));
            when(registroMapper.toDTOList(List.of(registroTest))).thenReturn(List.of(registroDTOTest));

            // Act
            List<RegistroHorasVueloDTO> resultado = registroService.obtenerRegistrosPorTripulante(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(registroRepository).findByTripulanteId(1L);
        }

        @Test
        @DisplayName("Obtener registros por vuelo")
        void obtenerRegistrosPorVuelo_ConIdValido_RetornaLista() {
            // Arrange
            when(vueloRepository.existsById(1L)).thenReturn(true);
            when(registroRepository.findByVueloId(1L)).thenReturn(List.of(registroTest));
            when(registroMapper.toDTOList(List.of(registroTest))).thenReturn(List.of(registroDTOTest));

            // Act
            List<RegistroHorasVueloDTO> resultado = registroService.obtenerRegistrosPorVuelo(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(registroRepository).findByVueloId(1L);
        }

        @Test
        @DisplayName("Obtener registros pendientes de aprobación")
        void obtenerRegistrosPendientes_RetornaRegistrosNoAprobados() {
            // Arrange
            when(registroRepository.findByAprobado(false)).thenReturn(List.of(registroTest));
            when(registroMapper.toDTOList(List.of(registroTest))).thenReturn(List.of(registroDTOTest));

            // Act
            List<RegistroHorasVueloDTO> resultado = registroService.obtenerRegistrosPendientes();

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(registroRepository).findByAprobado(false);
        }
    }

    @Nested
    @DisplayName("Cálculo de Horas Tests")
    class CalculoHorasTests {

        @Test
        @DisplayName("Calcular horas totales de un tripulante")
        void calcularHorasTotales_ConTripulanteValido_RetornaTotal() {
            // Arrange
            when(tripulanteRepository.existsById(1L)).thenReturn(true);
            when(registroRepository.sumHorasByTripulanteId(1L)).thenReturn(50.0);

            // Act
            Double resultado = registroService.calcularHorasTotales(1L);

            // Assert
            assertEquals(50.0, resultado);
            verify(registroRepository).sumHorasByTripulanteId(1L);
        }

        @Test
        @DisplayName("Calcular horas totales retorna 0 si no hay registros")
        void calcularHorasTotales_SinRegistros_RetornaCero() {
            // Arrange
            when(tripulanteRepository.existsById(1L)).thenReturn(true);
            when(registroRepository.sumHorasByTripulanteId(1L)).thenReturn(null);

            // Act
            Double resultado = registroService.calcularHorasTotales(1L);

            // Assert
            assertEquals(0.0, resultado);
        }

        @Test
        @DisplayName("Calcular horas mensuales de un tripulante")
        void calcularHorasMensuales_ConFechaValida_RetornaTotal() {
            // Arrange
            when(tripulanteRepository.existsById(1L)).thenReturn(true);
            LocalDate fecha = LocalDate.now();
            when(registroRepository.sumHorasByTripulanteIdAndFechaBetween(
                    anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(15.0);

            // Act
            Double resultado = registroService.calcularHorasMensuales(1L, fecha);

            // Assert
            assertEquals(15.0, resultado);
            verify(registroRepository).sumHorasByTripulanteIdAndFechaBetween(
                    anyLong(), any(LocalDateTime.class), any(LocalDateTime.class));
        }

        @Test
        @DisplayName("Obtener horas en vuelo específico")
        void obtenerHorasEnVuelo_ConVueloValido_RetornaTotal() {
            // Arrange
            when(tripulanteRepository.existsById(1L)).thenReturn(true);
            when(vueloRepository.existsById(1L)).thenReturn(true);
            when(registroRepository.findByTripulanteIdAndAprobado(1L, true))
                    .thenReturn(List.of(registroTest));

            // Act
            Double resultado = registroService.obtenerHorasEnVuelo(1L, 1L);

            // Assert
            assertEquals(2.5, resultado);
        }
    }

    @Nested
    @DisplayName("Aprobación de Registro Tests")
    class AprobacionTests {

        @Test
        @DisplayName("Aprobar registro actualiza estado y horas")
        void aprobarRegistro_ConRegistroValido_ActualizaEstadoYHoras() {
            // Arrange
            registroTest.setAprobado(false);
            when(registroRepository.findById(1L)).thenReturn(Optional.of(registroTest));
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTest));
            when(tripulanteRepository.existsById(1L)).thenReturn(true);
            when(registroRepository.sumHorasByTripulanteIdAndFechaBetween(
                    anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                    .thenReturn(0.0);
            when(tripulanteRepository.save(any(Tripulante.class))).thenReturn(tripulanteTest);
            when(registroRepository.save(any(RegistroHorasVuelo.class))).thenReturn(registroTest);
            when(registroMapper.toDTO(registroTest)).thenReturn(registroDTOTest);

            // Act
            RegistroHorasVueloDTO resultado = registroService.aprobarRegistro(1L, 1L);

            // Assert
            assertNotNull(resultado);
            verify(registroRepository).findById(1L);
            verify(usuarioRepository).findById(1L);
            verify(tripulanteRepository).save(any(Tripulante.class));
            verify(registroRepository).save(any(RegistroHorasVuelo.class));
        }

        @Test
        @DisplayName("Aprobar registro ya aprobado lanza excepción")
        void aprobarRegistro_ConRegistroYaAprobado_LanzaExcepcion() {
            // Arrange
            registroTest.setAprobado(true);
            when(registroRepository.findById(1L)).thenReturn(Optional.of(registroTest));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> registroService.aprobarRegistro(1L, 1L)
            );
            assertEquals("El registro ya ha sido aprobado anteriormente", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("Eliminación de Registro Tests")
    class EliminacionTests {

        @Test
        @DisplayName("Eliminar registro no aprobado")
        void eliminarRegistro_ConRegistroNoAprobado_Elimina() {
            // Arrange
            registroTest.setAprobado(false);
            when(registroRepository.findById(1L)).thenReturn(Optional.of(registroTest));

            // Act
            registroService.eliminarRegistro(1L);

            // Assert
            verify(registroRepository).deleteById(1L);
        }

        @Test
        @DisplayName("Eliminar registro aprobado lanza excepción")
        void eliminarRegistro_ConRegistroAprobado_LanzaExcepcion() {
            // Arrange
            registroTest.setAprobado(true);
            when(registroRepository.findById(1L)).thenReturn(Optional.of(registroTest));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> registroService.eliminarRegistro(1L)
            );
            assertEquals("No se pueden eliminar registros aprobados", exception.getMessage());
        }
    }
}
