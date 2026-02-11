package com.paeldav.backend.incidencia;

import com.paeldav.backend.application.dto.incidencia.IncidenciaCreateDTO;
import com.paeldav.backend.application.dto.incidencia.IncidenciaDTO;
import com.paeldav.backend.application.mapper.IncidenciaMapper;
import com.paeldav.backend.application.service.impl.IncidenciaServiceImpl;
import com.paeldav.backend.domain.entity.*;
import com.paeldav.backend.domain.enums.EstadoTripulante;
import com.paeldav.backend.domain.enums.EstadoVuelo;
import com.paeldav.backend.domain.enums.RolUsuario;
import com.paeldav.backend.exception.IncidenciaNoEncontradaException;
import com.paeldav.backend.exception.TripulanteNoEncontradoException;
import com.paeldav.backend.exception.VueloNoEncontradoException;
import com.paeldav.backend.infraestructure.repository.IncidenciaRepository;
import com.paeldav.backend.infraestructure.repository.TripulanteRepository;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("IncidenciaService Tests")
class IncidenciaServiceTest {

    @Mock
    private IncidenciaRepository incidenciaRepository;

    @Mock
    private VueloRepository vueloRepository;

    @Mock
    private TripulanteRepository tripulanteRepository;

    @Mock
    private IncidenciaMapper incidenciaMapper;

    @InjectMocks
    private IncidenciaServiceImpl incidenciaService;

    private Vuelo vueloTest;
    private Usuario usuarioTest;
    private Tripulante tripulanteTest;
    private Incidencia incidenciaTest;
    private IncidenciaCreateDTO incidenciaCreateDTOTest;
    private IncidenciaDTO incidenciaDTOTest;

    @BeforeEach
    void setUp() {
        // Crear usuario de prueba
        usuarioTest = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("García")
                .email("juan.garcia@charter.com")
                .password("encrypted_password")
                .rol(RolUsuario.TRIPULACION)
                .activo(true)
                .build();

        // Crear tripulante de prueba
        tripulanteTest = Tripulante.builder()
                .id(1L)
                .usuario(usuarioTest)
                .numeroLicencia("LIC-001")
                .estado(EstadoTripulante.DISPONIBLE)
                .build();

        // Crear vuelo de prueba
        vueloTest = Vuelo.builder()
                .id(1L)
                .origen("Bogotá")
                .destino("Cartagena")
                .estado(EstadoVuelo.EN_CURSO)
                .fechaSalidaReal(LocalDateTime.now().minusHours(2))
                .build();

        // Crear incidencia de prueba
        incidenciaTest = Incidencia.builder()
                .id(1L)
                .vuelo(vueloTest)
                .reportadoPor(tripulanteTest)
                .titulo("Problema en motor izquierdo")
                .descripcion("El motor izquierdo está haciendo ruido anómalo")
                .gravedad("ALTA")
                .fechaReporte(LocalDateTime.now().minusHours(1))
                .resuelta(false)
                .build();

        // DTO de creación
        incidenciaCreateDTOTest = IncidenciaCreateDTO.builder()
                .vueloId(1L)
                .reportadoPorId(1L)
                .titulo("Problema en motor izquierdo")
                .descripcion("El motor izquierdo está haciendo ruido anómalo")
                .gravedad("ALTA")
                .build();

        // DTO de respuesta
        incidenciaDTOTest = IncidenciaDTO.builder()
                .id(1L)
                .vueloId(1L)
                .vueloOrigen("Bogotá")
                .vueloDestino("Cartagena")
                .reportadoPorId(1L)
                .reportadoPorNombre("Juan García")
                .titulo("Problema en motor izquierdo")
                .descripcion("El motor izquierdo está haciendo ruido anómalo")
                .gravedad("ALTA")
                .fechaReporte(LocalDateTime.now().minusHours(1))
                .resuelta(false)
                .build();
    }

    // ==================== REPORTE DE INCIDENCIAS TESTS ====================

    @Nested
    @DisplayName("Reporte de Incidencias Tests")
    class ReporteIncidenciasTests {

        @Test
        @DisplayName("Reportar incidencia con datos válidos")
        void reportarIncidencia_ConDatosValidos_Exitoso() {
            // Arrange
            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(tripulanteRepository.findById(1L)).thenReturn(Optional.of(tripulanteTest));
            when(incidenciaMapper.toEntity(incidenciaCreateDTOTest)).thenReturn(incidenciaTest);
            when(incidenciaRepository.save(any(Incidencia.class))).thenReturn(incidenciaTest);
            when(incidenciaMapper.toDTO(incidenciaTest)).thenReturn(incidenciaDTOTest);

            // Act
            IncidenciaDTO resultado = incidenciaService.reportarIncidencia(incidenciaCreateDTOTest);

            // Assert
            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            assertEquals("Problema en motor izquierdo", resultado.getTitulo());
            assertEquals("ALTA", resultado.getGravedad());
            assertFalse(resultado.getResuelta());
            verify(vueloRepository).findById(1L);
            verify(tripulanteRepository).findById(1L);
            verify(incidenciaRepository).save(any(Incidencia.class));
        }

        @Test
        @DisplayName("Reportar incidencia con vuelo inexistente lanza excepción")
        void reportarIncidencia_VueloInexistente_LanzaExcepcion() {
            // Arrange
            when(vueloRepository.findById(999L)).thenReturn(Optional.empty());

            IncidenciaCreateDTO dtoConVueloInvalido = IncidenciaCreateDTO.builder()
                    .vueloId(999L)
                    .reportadoPorId(1L)
                    .titulo("Test")
                    .descripcion("Test")
                    .build();

            // Act & Assert
            VueloNoEncontradoException exception = assertThrows(
                    VueloNoEncontradoException.class,
                    () -> incidenciaService.reportarIncidencia(dtoConVueloInvalido)
            );
            assertEquals("Vuelo no encontrado con ID: 999", exception.getMessage());
            verify(incidenciaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Reportar incidencia con tripulante inexistente lanza excepción")
        void reportarIncidencia_TripulanteInexistente_LanzaExcepcion() {
            // Arrange
            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(tripulanteRepository.findById(999L)).thenReturn(Optional.empty());

            IncidenciaCreateDTO dtoConTripulanteInvalido = IncidenciaCreateDTO.builder()
                    .vueloId(1L)
                    .reportadoPorId(999L)
                    .titulo("Test")
                    .descripcion("Test")
                    .build();

            // Act & Assert
            TripulanteNoEncontradoException exception = assertThrows(
                    TripulanteNoEncontradoException.class,
                    () -> incidenciaService.reportarIncidencia(dtoConTripulanteInvalido)
            );
            assertEquals("Tripulante no encontrado con ID: 999", exception.getMessage());
            verify(incidenciaRepository, never()).save(any());
        }
    }

    // ==================== OBTENCIÓN DE INCIDENCIAS TESTS ====================

    @Nested
    @DisplayName("Obtención de Incidencias Tests")
    class ObtenerIncidenciasTests {

        @Test
        @DisplayName("Obtener incidencia por ID exitosamente")
        void obtenerIncidenciaPorId_ConIdValido_Exitoso() {
            // Arrange
            when(incidenciaRepository.findById(1L)).thenReturn(Optional.of(incidenciaTest));
            when(incidenciaMapper.toDTO(incidenciaTest)).thenReturn(incidenciaDTOTest);

            // Act
            IncidenciaDTO resultado = incidenciaService.obtenerIncidenciaPorId(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            assertEquals("Problema en motor izquierdo", resultado.getTitulo());
            verify(incidenciaRepository).findById(1L);
        }

        @Test
        @DisplayName("Obtener incidencia con ID inexistente lanza excepción")
        void obtenerIncidenciaPorId_ConIdInexistente_LanzaExcepcion() {
            // Arrange
            when(incidenciaRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            IncidenciaNoEncontradaException exception = assertThrows(
                    IncidenciaNoEncontradaException.class,
                    () -> incidenciaService.obtenerIncidenciaPorId(999L)
            );
            assertEquals("Incidencia no encontrada con ID: 999", exception.getMessage());
        }

        @Test
        @DisplayName("Obtener todas las incidencias")
        void obtenerTodasIncidencias_Exitoso() {
            // Arrange
            Incidencia incidencia2 = Incidencia.builder()
                    .id(2L)
                    .vuelo(vueloTest)
                    .reportadoPor(tripulanteTest)
                    .titulo("Falla en sistema hidráulico")
                    .descripcion("Sistema hidráulico respondiendo lentamente")
                    .gravedad("MEDIA")
                    .fechaReporte(LocalDateTime.now())
                    .resuelta(false)
                    .build();

            List<Incidencia> incidencias = Arrays.asList(incidenciaTest, incidencia2);
            IncidenciaDTO incidenciaDTO2 = IncidenciaDTO.builder()
                    .id(2L)
                    .vueloId(1L)
                    .titulo("Falla en sistema hidráulico")
                    .gravedad("MEDIA")
                    .build();

            List<IncidenciaDTO> incidenciasDTO = Arrays.asList(incidenciaDTOTest, incidenciaDTO2);

            when(incidenciaRepository.findAll()).thenReturn(incidencias);
            when(incidenciaMapper.toDTOList(incidencias)).thenReturn(incidenciasDTO);

            // Act
            List<IncidenciaDTO> resultado = incidenciaService.obtenerTodasIncidencias();

            // Assert
            assertNotNull(resultado);
            assertEquals(2, resultado.size());
            verify(incidenciaRepository).findAll();
        }

        @Test
        @DisplayName("Obtener incidencias por vuelo")
        void obtenerIncidenciasPorVuelo_ConVueloValido_Exitoso() {
            // Arrange
            List<Incidencia> incidencias = Collections.singletonList(incidenciaTest);
            List<IncidenciaDTO> incidenciasDTO = Collections.singletonList(incidenciaDTOTest);

            when(vueloRepository.existsById(1L)).thenReturn(true);
            when(incidenciaRepository.findByVueloId(1L)).thenReturn(incidencias);
            when(incidenciaMapper.toDTOList(incidencias)).thenReturn(incidenciasDTO);

            // Act
            List<IncidenciaDTO> resultado = incidenciaService.obtenerIncidenciasPorVuelo(1L);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(vueloRepository).existsById(1L);
            verify(incidenciaRepository).findByVueloId(1L);
        }

        @Test
        @DisplayName("Obtener incidencias por vuelo inexistente lanza excepción")
        void obtenerIncidenciasPorVuelo_VueloInexistente_LanzaExcepcion() {
            // Arrange
            when(vueloRepository.existsById(999L)).thenReturn(false);

            // Act & Assert
            VueloNoEncontradoException exception = assertThrows(
                    VueloNoEncontradoException.class,
                    () -> incidenciaService.obtenerIncidenciasPorVuelo(999L)
            );
            assertEquals("Vuelo no encontrado con ID: 999", exception.getMessage());
            verify(incidenciaRepository, never()).findByVueloId(anyLong());
        }

        @Test
        @DisplayName("Obtener incidencias pendientes")
        void obtenerIncidenciasPendientes_Exitoso() {
            // Arrange
            List<Incidencia> incidencias = Collections.singletonList(incidenciaTest);
            List<IncidenciaDTO> incidenciasDTO = Collections.singletonList(incidenciaDTOTest);

            when(incidenciaRepository.findPendientes()).thenReturn(incidencias);
            when(incidenciaMapper.toDTOList(incidencias)).thenReturn(incidenciasDTO);

            // Act
            List<IncidenciaDTO> resultado = incidenciaService.obtenerIncidenciasPendientes();

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertFalse(resultado.get(0).getResuelta());
            verify(incidenciaRepository).findPendientes();
        }

        @Test
        @DisplayName("Obtener incidencias no resueltas")
        void obtenerIncidenciasNoResueltas_Exitoso() {
            // Arrange
            List<Incidencia> incidencias = Collections.singletonList(incidenciaTest);
            List<IncidenciaDTO> incidenciasDTO = Collections.singletonList(incidenciaDTOTest);

            when(incidenciaRepository.findByResuelta(false)).thenReturn(incidencias);
            when(incidenciaMapper.toDTOList(incidencias)).thenReturn(incidenciasDTO);

            // Act
            List<IncidenciaDTO> resultado = incidenciaService.obtenerIncidenciasNoResueltas();

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertFalse(resultado.get(0).getResuelta());
            verify(incidenciaRepository).findByResuelta(false);
        }

        @Test
        @DisplayName("Obtener incidencias por gravedad")
        void obtenerIncidenciasPorGravedad_ConGravedadValida_Exitoso() {
            // Arrange
            List<Incidencia> incidencias = Collections.singletonList(incidenciaTest);
            List<IncidenciaDTO> incidenciasDTO = Collections.singletonList(incidenciaDTOTest);

            when(incidenciaRepository.findAll()).thenReturn(incidencias);
            when(incidenciaMapper.toDTO(incidenciaTest)).thenReturn(incidenciaDTOTest);

            // Act
            List<IncidenciaDTO> resultado = incidenciaService.obtenerIncidenciasPorGravedad("ALTA");

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals("ALTA", resultado.get(0).getGravedad());
        }

        @Test
        @DisplayName("Obtener incidencias por rango de fechas")
        void obtenerIncidenciasPorFecha_ConRangoValido_Exitoso() {
            // Arrange
            LocalDateTime inicio = LocalDateTime.now().minusHours(5);
            LocalDateTime fin = LocalDateTime.now().plusHours(1);

            List<Incidencia> incidencias = Collections.singletonList(incidenciaTest);
            List<IncidenciaDTO> incidenciasDTO = Collections.singletonList(incidenciaDTOTest);

            when(incidenciaRepository.findByFechaReporteBetween(inicio, fin))
                    .thenReturn(incidencias);
            when(incidenciaMapper.toDTOList(incidencias)).thenReturn(incidenciasDTO);

            // Act
            List<IncidenciaDTO> resultado = incidenciaService.obtenerIncidenciasPorFecha(inicio, fin);

            // Assert
            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(incidenciaRepository).findByFechaReporteBetween(inicio, fin);
        }
    }

    // ==================== RESOLUCIÓN DE INCIDENCIAS TESTS ====================

    @Nested
    @DisplayName("Resolución de Incidencias Tests")
    class ResolucionIncidenciasTests {

        @Test
        @DisplayName("Resolver incidencia con datos válidos")
        void resolverIncidencia_ConDatosValidos_Exitoso() {
            // Arrange
            String accionesTomadas = "Se realizó mantenimiento correctivo del motor";
            Incidencia incidenciaResuelta = Incidencia.builder()
                    .id(1L)
                    .vuelo(vueloTest)
                    .reportadoPor(tripulanteTest)
                    .titulo("Problema en motor izquierdo")
                    .descripcion("El motor izquierdo está haciendo ruido anómalo")
                    .gravedad("ALTA")
                    .fechaReporte(LocalDateTime.now().minusHours(1))
                    .resuelta(true)
                    .fechaResolucion(LocalDateTime.now())
                    .accionesTomadas(accionesTomadas)
                    .build();

            IncidenciaDTO incidenciaDTOResuelta = IncidenciaDTO.builder()
                    .id(1L)
                    .vueloId(1L)
                    .vueloOrigen("Bogotá")
                    .vueloDestino("Cartagena")
                    .reportadoPorId(1L)
                    .reportadoPorNombre("Juan García")
                    .titulo("Problema en motor izquierdo")
                    .descripcion("El motor izquierdo está haciendo ruido anómalo")
                    .gravedad("ALTA")
                    .fechaReporte(LocalDateTime.now().minusHours(1))
                    .resuelta(true)
                    .fechaResolucion(LocalDateTime.now())
                    .accionesTomadas(accionesTomadas)
                    .build();

            when(incidenciaRepository.findById(1L)).thenReturn(Optional.of(incidenciaTest));
            when(incidenciaRepository.save(any(Incidencia.class))).thenReturn(incidenciaResuelta);
            when(incidenciaMapper.toDTO(incidenciaResuelta)).thenReturn(incidenciaDTOResuelta);

            // Act
            IncidenciaDTO resultado = incidenciaService.resolverIncidencia(1L, accionesTomadas);

            // Assert
            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
            assertTrue(resultado.getResuelta());
            assertEquals(accionesTomadas, resultado.getAccionesTomadas());
            assertNotNull(resultado.getFechaResolucion());
            verify(incidenciaRepository).findById(1L);
            verify(incidenciaRepository).save(any(Incidencia.class));
        }

        @Test
        @DisplayName("Resolver incidencia inexistente lanza excepción")
        void resolverIncidencia_IncidenciaInexistente_LanzaExcepcion() {
            // Arrange
            when(incidenciaRepository.findById(999L)).thenReturn(Optional.empty());

            // Act & Assert
            IncidenciaNoEncontradaException exception = assertThrows(
                    IncidenciaNoEncontradaException.class,
                    () -> incidenciaService.resolverIncidencia(999L, "Acciones")
            );
            assertEquals("Incidencia no encontrada con ID: 999", exception.getMessage());
            verify(incidenciaRepository, never()).save(any());
        }

        @Test
        @DisplayName("Resolver incidencia sin acciones tomadas")
        void resolverIncidencia_SinAcciones_Exitoso() {
            // Arrange
            Incidencia incidenciaResuelta = Incidencia.builder()
                    .id(1L)
                    .vuelo(vueloTest)
                    .reportadoPor(tripulanteTest)
                    .titulo("Problema en motor izquierdo")
                    .descripcion("El motor izquierdo está haciendo ruido anómalo")
                    .gravedad("ALTA")
                    .fechaReporte(LocalDateTime.now().minusHours(1))
                    .resuelta(true)
                    .fechaResolucion(LocalDateTime.now())
                    .accionesTomadas(null)
                    .build();

            IncidenciaDTO incidenciaDTOResuelta = IncidenciaDTO.builder()
                    .id(1L)
                    .vueloId(1L)
                    .resuelta(true)
                    .build();

            when(incidenciaRepository.findById(1L)).thenReturn(Optional.of(incidenciaTest));
            when(incidenciaRepository.save(any(Incidencia.class))).thenReturn(incidenciaResuelta);
            when(incidenciaMapper.toDTO(incidenciaResuelta)).thenReturn(incidenciaDTOResuelta);

            // Act
            IncidenciaDTO resultado = incidenciaService.resolverIncidencia(1L, null);

            // Assert
            assertNotNull(resultado);
            assertTrue(resultado.getResuelta());
            verify(incidenciaRepository).findById(1L);
            verify(incidenciaRepository).save(any(Incidencia.class));
        }
    }
}
