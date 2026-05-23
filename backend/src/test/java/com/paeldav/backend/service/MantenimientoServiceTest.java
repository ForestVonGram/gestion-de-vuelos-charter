package com.paeldav.backend.service;

import com.paeldav.backend.application.dto.mantenimiento.MantenimientoCreateDTO;
import com.paeldav.backend.application.dto.mantenimiento.MantenimientoDTO;
import com.paeldav.backend.application.mapper.MantenimientoMapper;
import com.paeldav.backend.application.service.impl.MantenimientoServiceImpl;
import com.paeldav.backend.domain.entity.Aeronave;
import com.paeldav.backend.domain.entity.Mantenimiento;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.enums.EstadoAeronave;
import com.paeldav.backend.domain.enums.TipoMantenimiento;
import com.paeldav.backend.infraestructure.repository.AeronaveRepository;
import com.paeldav.backend.infraestructure.repository.MantenimientoRepository;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
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
@DisplayName("MantenimientoService Tests")
class MantenimientoServiceTest {

    @Mock
    private MantenimientoRepository mantenimientoRepository;

    @Mock
    private AeronaveRepository aeronaveRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MantenimientoMapper mantenimientoMapper;

    @InjectMocks
    private MantenimientoServiceImpl mantenimientoService;

    private Aeronave aeronaveTest;
    private Usuario responsableTest;
    private Mantenimiento mantenimientoTest;
    private MantenimientoDTO mantenimientoDTO;

    @BeforeEach
    void setUp() {
        aeronaveTest = Aeronave.builder()
                .id(1L)
                .matricula("HK-1234")
                .estado(EstadoAeronave.DISPONIBLE)
                .build();

        responsableTest = Usuario.builder()
                .id(2L)
                .nombre("Juan")
                .apellido("Mecánico")
                .build();

        mantenimientoTest = Mantenimiento.builder()
                .id(1L)
                .aeronave(aeronaveTest)
                .responsable(responsableTest)
                .tipo(TipoMantenimiento.PREVENTIVO)
                .completado(false)
                .build();

        mantenimientoDTO = MantenimientoDTO.builder()
                .id(1L)
                .aeronaveMatricula("HK-1234")
                .completado(false)
                .build();
    }

    @Nested
    @DisplayName("Registro de Mantenimiento")
    class RegistroMantenimientoTests {

        @Test
        @DisplayName("Debe registrar un mantenimiento exitosamente y bloquear la aeronave")
        void registrarMantenimiento_Exito() {
            MantenimientoCreateDTO createDTO = MantenimientoCreateDTO.builder()
                    .aeronaveId(1L)
                    .responsableId(2L)
                    .tipo(TipoMantenimiento.PREVENTIVO)
                    .descripcion("Revisión anual")
                    .build();

            when(aeronaveRepository.findById(1L)).thenReturn(Optional.of(aeronaveTest));
            when(usuarioRepository.findById(2L)).thenReturn(Optional.of(responsableTest));
            when(mantenimientoMapper.toEntity(any())).thenReturn(mantenimientoTest);
            when(mantenimientoRepository.save(any())).thenReturn(mantenimientoTest);
            when(mantenimientoMapper.toDTO(any())).thenReturn(mantenimientoDTO);

            MantenimientoDTO resultado = mantenimientoService.registrarMantenimiento(createDTO);

            assertNotNull(resultado);
            assertEquals(EstadoAeronave.EN_MANTENIMIENTO, aeronaveTest.getEstado());
            verify(mantenimientoRepository).save(any());
            verify(aeronaveRepository).save(aeronaveTest);
        }
    }

    @Nested
    @DisplayName("Finalización de Mantenimiento")
    class FinalizacionMantenimientoTests {

        @Test
        @DisplayName("Debe completar un mantenimiento y liberar la aeronave")
        void completarMantenimiento_Exito() {
            aeronaveTest.setEstado(EstadoAeronave.EN_MANTENIMIENTO);
            
            when(mantenimientoRepository.findById(1L)).thenReturn(Optional.of(mantenimientoTest));
            when(mantenimientoRepository.save(any())).thenReturn(mantenimientoTest);
            when(mantenimientoMapper.toDTO(any())).thenReturn(mantenimientoDTO);

            MantenimientoDTO resultado = mantenimientoService.completarMantenimiento(
                    1L, LocalDateTime.now(), "Todo bien");

            assertNotNull(resultado);
            assertTrue(mantenimientoTest.getCompletado());
            assertEquals(EstadoAeronave.DISPONIBLE, aeronaveTest.getEstado());
            verify(aeronaveRepository).save(aeronaveTest);
        }
    }
}
