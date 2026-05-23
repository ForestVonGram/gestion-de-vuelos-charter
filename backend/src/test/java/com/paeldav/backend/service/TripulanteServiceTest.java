package com.paeldav.backend.service;

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

import java.util.Collections;
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

    private Usuario usuarioTest;
    private Tripulante tripulanteTest;
    private TripulanteDTO tripulanteDTO;

    @BeforeEach
    void setUp() {
        usuarioTest = Usuario.builder()
                .id(1L)
                .nombre("Pedro")
                .apellido("Dávila")
                .email("pedro@test.com")
                .rol(RolUsuario.USUARIO)
                .build();

        tripulanteTest = Tripulante.builder()
                .id(1L)
                .usuario(usuarioTest)
                .numeroLicencia("LIC-12345")
                .esPiloto(true)
                .estado(EstadoTripulante.DISPONIBLE)
                .build();

        tripulanteDTO = TripulanteDTO.builder()
                .id(1L)
                .numeroLicencia("LIC-12345")
                .usuarioNombre("Pedro Dávila")
                .build();
    }

    @Nested
    @DisplayName("Registro de Tripulante")
    class RegistroTripulanteTests {

        @Test
        @DisplayName("Debe registrar un tripulante exitosamente")
        void registrarTripulante_Exito() {
            TripulanteCreateDTO createDTO = TripulanteCreateDTO.builder()
                    .usuarioId(1L)
                    .numeroLicencia("LIC-12345")
                    .build();

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTest));
            when(tripulanteRepository.existsByNumeroLicencia("LIC-12345")).thenReturn(false);
            when(tripulanteMapper.toEntity(any(TripulanteCreateDTO.class))).thenReturn(tripulanteTest);
            when(tripulanteRepository.save(any(Tripulante.class))).thenReturn(tripulanteTest);
            when(tripulanteMapper.toDTO(any(Tripulante.class))).thenReturn(tripulanteDTO);

            TripulanteDTO resultado = tripulanteService.registrarTripulante(createDTO);

            assertNotNull(resultado);
            assertEquals(RolUsuario.TRIPULACION, usuarioTest.getRol());
            verify(tripulanteRepository).save(any(Tripulante.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción si la licencia ya existe")
        void registrarTripulante_LicenciaDuplicada() {
            TripulanteCreateDTO createDTO = TripulanteCreateDTO.builder()
                    .usuarioId(1L)
                    .numeroLicencia("LIC-12345")
                    .build();

            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTest));
            when(tripulanteRepository.existsByNumeroLicencia("LIC-12345")).thenReturn(true);

            assertThrows(TripulanteYaExisteException.class, () -> 
                tripulanteService.registrarTripulante(createDTO)
            );
        }
    }

    @Nested
    @DisplayName("Búsqueda y Disponibilidad")
    class BusquedaTripulanteTests {

        @Test
        @DisplayName("Debe obtener tripulante por ID de usuario")
        void obtenerTripulantePorId_Exito() {
            when(tripulanteRepository.findByUsuarioId(1L)).thenReturn(Optional.of(tripulanteTest));
            when(tripulanteMapper.toDTO(tripulanteTest)).thenReturn(tripulanteDTO);

            TripulanteDTO resultado = tripulanteService.obtenerTripulantePorId(1L);

            assertNotNull(resultado);
            verify(tripulanteRepository).findByUsuarioId(1L);
        }

        @Test
        @DisplayName("Debe obtener tripulantes disponibles")
        void obtenerTripulantesDisponibles_Exito() {
            when(tripulanteRepository.findByEstado(EstadoTripulante.DISPONIBLE))
                    .thenReturn(Collections.singletonList(tripulanteTest));
            when(tripulanteMapper.toDTOList(any())).thenReturn(Collections.singletonList(tripulanteDTO));

            List<TripulanteDTO> resultado = tripulanteService.obtenerTripulantesDisponibles();

            assertFalse(resultado.isEmpty());
            assertEquals(1, resultado.size());
        }
    }

    @Nested
    @DisplayName("Edición y Validación")
    class EdicionTripulanteTests {

        @Test
        @DisplayName("Debe editar tripulante exitosamente")
        void editarTripulante_Exito() {
            TripulanteUpdateDTO updateDTO = TripulanteUpdateDTO.builder()
                    .observaciones("Nueva observación")
                    .build();

            when(tripulanteRepository.findById(1L)).thenReturn(Optional.of(tripulanteTest));
            doNothing().when(tripulanteMapper).updateEntityFromUpdateDTO(any(), any());
            when(tripulanteRepository.save(any())).thenReturn(tripulanteTest);
            when(tripulanteMapper.toDTO(any())).thenReturn(tripulanteDTO);

            TripulanteDTO resultado = tripulanteService.editarTripulante(1L, updateDTO);

            assertNotNull(resultado);
            verify(tripulanteRepository).save(tripulanteTest);
        }

        @Test
        @DisplayName("Debe validar certificaciones del tripulante")
        void validarTripulante_Exito() {
            when(tripulanteRepository.findById(1L)).thenReturn(Optional.of(tripulanteTest));
            doNothing().when(validadorCertificaciones).validarTripulanteCompleto(any());

            assertDoesNotThrow(() -> tripulanteService.validarTripulante(1L));
            verify(validadorCertificaciones).validarTripulanteCompleto(tripulanteTest);
        }
    }
}
