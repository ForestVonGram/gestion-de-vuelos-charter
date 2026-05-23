package com.paeldav.backend.service;

import com.paeldav.backend.application.dto.aeronave.AeronaveCreateDTO;
import com.paeldav.backend.application.dto.aeronave.AeronaveDTO;
import com.paeldav.backend.application.dto.aeronave.AeronaveUpdateDTO;
import com.paeldav.backend.application.mapper.AeronaveMapper;
import com.paeldav.backend.application.service.impl.AeronaveServiceImpl;
import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import com.paeldav.backend.exception.AeronaveNoEncontradaException;
import com.paeldav.backend.exception.AeronaveYaExisteException;
import com.paeldav.backend.infraestructure.repository.AeronaveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
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
    private AeronaveDTO aeronaveDTO;

    @BeforeEach
    void setUp() {
        aeronaveTest = Aeronave.builder()
                .id(1L)
                .matricula("HK-1234")
                .modelo("Cessna 172")
                .estado(EstadoAeronave.DISPONIBLE)
                .horasVueloTotales(100.0)
                .build();

        aeronaveDTO = AeronaveDTO.builder()
                .id(1L)
                .matricula("HK-1234")
                .modelo("Cessna 172")
                .estado(EstadoAeronave.DISPONIBLE)
                .build();
    }

    @Nested
    @DisplayName("Registro de Aeronave")
    class RegistroAeronaveTests {

        @Test
        @DisplayName("Debe registrar una aeronave exitosamente")
        void registrarAeronave_Exito() {
            AeronaveCreateDTO createDTO = AeronaveCreateDTO.builder()
                    .matricula("HK-1234")
                    .modelo("Cessna 172")
                    .build();

            when(aeronaveRepository.existsByMatricula(createDTO.getMatricula())).thenReturn(false);
            when(aeronaveMapper.toEntity(any(AeronaveCreateDTO.class))).thenReturn(aeronaveTest);
            when(aeronaveRepository.save(any(Aeronave.class))).thenReturn(aeronaveTest);
            when(aeronaveMapper.toDTO(any(Aeronave.class))).thenReturn(aeronaveDTO);

            AeronaveDTO resultado = aeronaveService.registrarAeronave(createDTO);

            assertNotNull(resultado);
            assertEquals("HK-1234", resultado.getMatricula());
            verify(aeronaveRepository).save(any(Aeronave.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción si la matrícula ya existe")
        void registrarAeronave_MatriculaDuplicada() {
            AeronaveCreateDTO createDTO = AeronaveCreateDTO.builder()
                    .matricula("HK-1234")
                    .build();

            when(aeronaveRepository.existsByMatricula(createDTO.getMatricula())).thenReturn(true);

            assertThrows(AeronaveYaExisteException.class, () -> 
                aeronaveService.registrarAeronave(createDTO)
            );
            verify(aeronaveRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Búsqueda de Aeronave")
    class BusquedaAeronaveTests {

        @Test
        @DisplayName("Debe obtener aeronave por ID")
        void obtenerAeronavePorId_Exito() {
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(aeronaveMapper.toDTO(aeronaveTest)).thenReturn(aeronaveDTO);

            AeronaveDTO resultado = aeronaveService.obtenerAeronavePorId(1L);

            assertNotNull(resultado);
            assertEquals(1L, resultado.getId());
        }

        @Test
        @DisplayName("Debe lanzar excepción si ID no existe")
        void obtenerAeronavePorId_NoEncontrada() {
            when(aeronaveRepository.findById(99L)).thenReturn(Optional.empty());

            assertThrows(AeronaveNoEncontradaException.class, () -> 
                aeronaveService.obtenerAeronavePorId(99L)
            );
        }
    }

    @Nested
    @DisplayName("Actualización y Estado")
    class ActualizacionAeronaveTests {

        @Test
        @DisplayName("Debe actualizar aeronave exitosamente")
        void actualizarAeronave_Exito() {
            AeronaveUpdateDTO updateDTO = AeronaveUpdateDTO.builder()
                    .fabricante("Cessna New")
                    .build();

            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            doNothing().when(aeronaveMapper).updateEntityFromUpdateDTO(any(), any());
            when(aeronaveRepository.save(any())).thenReturn(aeronaveTest);
            when(aeronaveMapper.toDTO(any())).thenReturn(aeronaveDTO);

            AeronaveDTO resultado = aeronaveService.actualizarAeronave(1L, updateDTO);

            assertNotNull(resultado);
            verify(aeronaveRepository).save(aeronaveTest);
        }

        @Test
        @DisplayName("Debe cambiar estado de aeronave")
        void cambiarEstadoAeronave_Exito() {
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(aeronaveRepository.save(any())).thenReturn(aeronaveTest);
            when(aeronaveMapper.toDTO(any())).thenReturn(aeronaveDTO);

            AeronaveDTO resultado = aeronaveService.cambiarEstadoAeronave(1L, EstadoAeronave.EN_MANTENIMIENTO);

            assertNotNull(resultado);
            assertEquals(EstadoAeronave.EN_MANTENIMIENTO, aeronaveTest.getEstado());
            verify(aeronaveRepository).save(aeronaveTest);
        }

        @Test
        @DisplayName("Debe incrementar horas de vuelo")
        void incrementarHorasVuelo_Exito() {
            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            
            aeronaveService.incrementarHorasVuelo(1L, 5.5);

            assertEquals(105.5, aeronaveTest.getHorasVueloTotales());
            verify(aeronaveRepository).save(aeronaveTest);
        }
    }
}
