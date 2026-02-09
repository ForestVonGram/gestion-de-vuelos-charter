package com.paeldav.backend.tripulante;

import com.paeldav.backend.application.dto.tripulante.TripulanteCreateDTO;
import com.paeldav.backend.application.dto.tripulante.TripulanteDTO;
import com.paeldav.backend.application.dto.tripulante.TripulanteUpdateDTO;
import com.paeldav.backend.application.mapper.TripulanteMapper;
import com.paeldav.backend.application.service.impl.TripulanteServiceImpl;
import com.paeldav.backend.application.service.integration.ValidadorCertificacionesService;
import com.paeldav.backend.domain.entity.Tripulante;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.enums.EstadoTripulante;
import com.paeldav.backend.domain.enums.RolUsuario;
import com.paeldav.backend.exception.CertificacionVencidaException;
import com.paeldav.backend.exception.TripulanteNoEncontradoException;
import com.paeldav.backend.exception.TripulanteYaExisteException;
import com.paeldav.backend.infraestructure.repository.TripulanteRepository;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TripulanteService Tests")
class TripulanteServiceTest {

    @Mock
    private TripulanteRepository tripulanteRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TripulanteMapper tripulanteMapper;

    @Mock
    private ValidadorCertificacionesService validadorCertificaciones;

    @InjectMocks
    private TripulanteServiceImpl tripulanteService;

    private TripulanteCreateDTO tripulanteCreateDTO;
    private Tripulante tripulante;
    private TripulanteDTO tripulanteDTO;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuario = Usuario.builder()
                .id(1L)
                .nombre("Carlos")
                .apellido("Perez")
                .email("carlos@example.com")
                .telefono("123456789")
                .rol(RolUsuario.TRIPULACION)
                .activo(true)
                .fechaRegistro(LocalDateTime.now())
                .build();

        tripulanteCreateDTO = TripulanteCreateDTO.builder()
                .usuarioId(1L)
                .numeroLicencia("PIL-12345")
                .tipoLicencia("CPL")
                .fechaExpedicionLicencia(LocalDate.of(2022, 1, 15))
                .fechaVencimientoLicencia(LocalDate.of(2025, 1, 15))
                .esPiloto(true)
                .certificaciones("ATPL, IFR")
                .observaciones("Piloto experimentado")
                .build();

        tripulante = Tripulante.builder()
                .id(1L)
                .usuario(usuario)
                .numeroLicencia("PIL-12345")
                .tipoLicencia("CPL")
                .fechaExpedicionLicencia(LocalDate.of(2022, 1, 15))
                .fechaVencimientoLicencia(LocalDate.of(2025, 1, 15))
                .horasVueloTotales(1500.0)
                .horasVueloMes(80.0)
                .estado(EstadoTripulante.DISPONIBLE)
                .esPiloto(true)
                .certificaciones("ATPL, IFR")
                .observaciones("Piloto experimentado")
                .build();

        tripulanteDTO = TripulanteDTO.builder()
                .id(1L)
                .usuarioId(1L)
                .usuarioNombre("Carlos Perez")
                .usuarioEmail("carlos@example.com")
                .numeroLicencia("PIL-12345")
                .tipoLicencia("CPL")
                .fechaExpedicionLicencia(LocalDate.of(2022, 1, 15))
                .fechaVencimientoLicencia(LocalDate.of(2025, 1, 15))
                .horasVueloTotales(1500.0)
                .horasVueloMes(80.0)
                .estado(EstadoTripulante.DISPONIBLE)
                .esPiloto(true)
                .certificaciones("ATPL, IFR")
                .observaciones("Piloto experimentado")
                .build();
    }

    @Nested
    @DisplayName("Registrar Tripulante Tests")
    class RegistrarTripulanteTests {

        @Test
        @DisplayName("Registrar tripulante exitosamente")
        void registrarTripulante_ConDatosValidos_CreaNuevoTripulante() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(tripulanteRepository.existsByNumeroLicencia("PIL-12345")).thenReturn(false);
            when(tripulanteMapper.toEntity(tripulanteCreateDTO)).thenReturn(tripulante);
            when(tripulanteRepository.save(any(Tripulante.class))).thenReturn(tripulante);
            when(tripulanteMapper.toDTO(tripulante)).thenReturn(tripulanteDTO);

            TripulanteDTO resultado = tripulanteService.registrarTripulante(tripulanteCreateDTO);

            assertNotNull(resultado);
            assertEquals("PIL-12345", resultado.getNumeroLicencia());
            assertTrue(resultado.getEsPiloto());
            verify(usuarioRepository).findById(1L);
            verify(tripulanteRepository).existsByNumeroLicencia("PIL-12345");
            verify(tripulanteRepository).save(any(Tripulante.class));
        }

        @Test
        @DisplayName("No permitir registrar con licencia duplicada")
        void registrarTripulante_ConLicenciaDuplicada_LanzaExcepcion() {
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
            when(tripulanteRepository.existsByNumeroLicencia("PIL-12345")).thenReturn(true);

            TripulanteYaExisteException exception = assertThrows(TripulanteYaExisteException.class, () -> {
                tripulanteService.registrarTripulante(tripulanteCreateDTO);
            });

            assertNotNull(exception.getMessage());
            assertTrue(exception.getMessage().contains("PIL-12345"));
            verify(tripulanteRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Editar Tripulante Tests")
    class EditarTripulanteTests {

        @Test
        @DisplayName("Editar tripulante exitosamente")
        void editarTripulante_ConDatosValidos_ActualizaTripulante() {
            TripulanteUpdateDTO updateDTO = TripulanteUpdateDTO.builder()
                    .certificaciones("ATPL, IFR, MCC")
                    .estado(EstadoTripulante.DE_DESCANSO)
                    .build();

            when(tripulanteRepository.findById(1L)).thenReturn(Optional.of(tripulante));
            doNothing().when(tripulanteMapper).updateEntityFromUpdateDTO(updateDTO, tripulante);
            when(tripulanteRepository.save(tripulante)).thenReturn(tripulante);
            when(tripulanteMapper.toDTO(tripulante)).thenReturn(tripulanteDTO);

            TripulanteDTO resultado = tripulanteService.editarTripulante(1L, updateDTO);

            assertNotNull(resultado);
            verify(tripulanteRepository).findById(1L);
            verify(tripulanteRepository).save(tripulante);
        }
    }

    @Nested
    @DisplayName("Validar Tripulante Tests")
    class ValidarTripulanteTests {

        @Test
        @DisplayName("Validar tripulante exitosamente")
        void validarTripulante_CumpleRequisitos_NoLanzaExcepcion() {
            when(tripulanteRepository.findById(1L)).thenReturn(Optional.of(tripulante));
            doNothing().when(validadorCertificaciones).validarTripulanteCompleto(tripulante);

            assertDoesNotThrow(() -> tripulanteService.validarTripulante(1L));
            verify(validadorCertificaciones).validarTripulanteCompleto(tripulante);
        }

        @Test
        @DisplayName("Fallar validacion cuando certificacion vencida")
        void validarTripulante_CertificacionVencida_LanzaExcepcion() {
            when(tripulanteRepository.findById(1L)).thenReturn(Optional.of(tripulante));
            doThrow(new CertificacionVencidaException("Licencia vencida"))
                    .when(validadorCertificaciones).validarTripulanteCompleto(tripulante);

            assertThrows(CertificacionVencidaException.class, () -> {
                tripulanteService.validarTripulante(1L);
            });
        }
    }

    @Nested
    @DisplayName("Obtener Tripulantes Tests")
    class ObtenerTripulantesTests {

        @Test
        @DisplayName("Obtener todos los tripulantes")
        void obtenerTodosTripulantes_RetornaLista() {
            List<Tripulante> tripulantes = List.of(tripulante);
            when(tripulanteRepository.findAll()).thenReturn(tripulantes);
            when(tripulanteMapper.toDTOList(tripulantes)).thenReturn(List.of(tripulanteDTO));

            List<TripulanteDTO> resultado = tripulanteService.obtenerTodosTripulantes();

            assertEquals(1, resultado.size());
            verify(tripulanteRepository).findAll();
        }

        @Test
        @DisplayName("Obtener tripulantes disponibles")
        void obtenerTripulantesDisponibles_RetornaDisponibles() {
            List<Tripulante> disponibles = List.of(tripulante);
            when(tripulanteRepository.findByEstado(EstadoTripulante.DISPONIBLE)).thenReturn(disponibles);
            when(tripulanteMapper.toDTOList(disponibles)).thenReturn(List.of(tripulanteDTO));

            List<TripulanteDTO> resultado = tripulanteService.obtenerTripulantesDisponibles();

            assertEquals(1, resultado.size());
            verify(tripulanteRepository).findByEstado(EstadoTripulante.DISPONIBLE);
        }
    }
}
