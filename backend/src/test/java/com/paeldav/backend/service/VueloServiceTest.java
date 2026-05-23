package com.paeldav.backend.service;

import com.paeldav.backend.application.dto.vuelo.VueloCreateDTO;
import com.paeldav.backend.application.dto.vuelo.VueloDTO;
import com.paeldav.backend.application.mapper.HistorialVueloMapper;
import com.paeldav.backend.application.mapper.VueloMapper;
import com.paeldav.backend.application.service.base.PagoService;
import com.paeldav.backend.application.service.impl.VueloServiceImpl;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.entity.Vuelo;
import com.paeldav.backend.domain.enums.EstadoVuelo;
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

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private PagoService pagoService;

    @Mock
    private VueloMapper vueloMapper;

    @Mock
    private HistorialVueloMapper historialVueloMapper;

    @InjectMocks
    private VueloServiceImpl vueloService;

    private Usuario usuarioTest;
    private Vuelo vueloTest;
    private VueloDTO vueloDTOTest;
    private VueloCreateDTO vueloCreateDTOTest;

    @BeforeEach
    void setUp() {
        usuarioTest = Usuario.builder()
                .id(1L)
                .nombre("Juan")
                .apellido("Pérez")
                .email("juan@test.com")
                .build();

        vueloTest = Vuelo.builder()
                .id(1L)
                .usuario(usuarioTest)
                .origen("Bogotá")
                .destino("Medellín")
                .fechaSalidaProgramada(LocalDateTime.now().plusDays(1))
                .fechaLlegadaProgramada(LocalDateTime.now().plusDays(1).plusHours(1))
                .estado(EstadoVuelo.SOLICITADO)
                .build();

        vueloDTOTest = VueloDTO.builder()
                .id(1L)
                .usuarioId(1L)
                .origen("Bogotá")
                .destino("Medellín")
                .estado(EstadoVuelo.SOLICITADO)
                .build();

        vueloCreateDTOTest = VueloCreateDTO.builder()
                .usuarioId(1L)
                .origen("Bogotá")
                .destino("Medellín")
                .fechaSalidaProgramada(LocalDateTime.now().plusDays(1))
                .fechaLlegadaProgramada(LocalDateTime.now().plusDays(1).plusHours(1))
                .numeroPasajeros(10)
                .build();
    }

    @Nested
    @DisplayName("Crear Vuelo Tests")
    class CrearVueloTests {

        @Test
        @DisplayName("Crear vuelo exitosamente")
        void crearVuelo_Exitoso() {
            // Arrange
            when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTest));
            when(vueloMapper.toEntity(any(VueloCreateDTO.class))).thenReturn(vueloTest);
            when(vueloRepository.save(any(Vuelo.class))).thenReturn(vueloTest);
            when(vueloMapper.toDTO(any(Vuelo.class))).thenReturn(vueloDTOTest);

            // Act
            VueloDTO resultado = vueloService.crearVuelo(vueloCreateDTOTest);

            // Assert
            assertNotNull(resultado);
            assertEquals(EstadoVuelo.SOLICITADO, resultado.getEstado());
            verify(vueloRepository).save(any(Vuelo.class));
        }
    }

    @Nested
    @DisplayName("Cambiar Estado Vuelo Tests")
    class CambiarEstadoTests {

        @Test
        @DisplayName("Cambiar estado de SOLICITADO a CANCELADO")
        void cambiarEstado_Exitoso() {
            // Arrange
            when(vueloRepository.findById(1L)).thenReturn(Optional.of(vueloTest));
            when(vueloRepository.save(any(Vuelo.class))).thenReturn(vueloTest);
            when(vueloMapper.toDTO(any(Vuelo.class))).thenReturn(vueloDTOTest);

            // Act
            VueloDTO resultado = vueloService.cambiarEstadoVuelo(1L, EstadoVuelo.CANCELADO);

            // Assert
            assertNotNull(resultado);
            verify(vueloRepository).save(any(Vuelo.class));
        }

        @Test
        @DisplayName("Lanzar excepción si el vuelo no existe al cambiar estado")
        void cambiarEstado_NoExiste_LanzaExcepcion() {
            // Arrange
            when(vueloRepository.findById(anyLong())).thenReturn(Optional.empty());

            // Act & Assert
            assertThrows(VueloNoEncontradoException.class, () -> {
                vueloService.cambiarEstadoVuelo(999L, EstadoVuelo.CONFIRMADO);
            });
        }
    }
}
