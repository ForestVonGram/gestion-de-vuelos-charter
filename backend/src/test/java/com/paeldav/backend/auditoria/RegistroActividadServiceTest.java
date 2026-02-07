package com.paeldav.backend.auditoria;

import com.paeldav.backend.application.dto.registroactividad.RegistroActividadDTO;
import com.paeldav.backend.application.mapper.RegistroActividadMapper;
import com.paeldav.backend.application.service.impl.RegistroActividadServiceImpl;
import com.paeldav.backend.domain.entity.RegistroActividad;
import com.paeldav.backend.domain.enums.TipoActividad;
import com.paeldav.backend.infraestructure.repository.RegistroActividadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RegistroActividadService Tests")
class RegistroActividadServiceTest {

    @Mock
    private RegistroActividadRepository registroActividadRepository;

    @Mock
    private RegistroActividadMapper registroActividadMapper;

    @InjectMocks
    private RegistroActividadServiceImpl registroActividadService;

    private RegistroActividad registroActividad;
    private RegistroActividadDTO registroActividadDTO;

    @BeforeEach
    void setUp() {
        registroActividad = RegistroActividad.builder()
                .id(1L)
                .usuarioId(1L)
                .tipoActividad(TipoActividad.CREAR_USUARIO)
                .descripcion("Nuevo usuario creado")
                .entidadAfectada("Usuario")
                .detallesAdicionales("Email: test@example.com")
                .timestamp(LocalDateTime.now())
                .build();

        registroActividadDTO = RegistroActividadDTO.builder()
                .id(1L)
                .usuarioId(1L)
                .tipoActividad(TipoActividad.CREAR_USUARIO)
                .descripcion("Nuevo usuario creado")
                .entidadAfectada("Usuario")
                .detallesAdicionales("Email: test@example.com")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("Registrar Actividad Tests")
    class RegistrarActividadTests {

        @Test
        @DisplayName("Registrar actividad exitosamente")
        void registrarActividad_ConDatosValidos_RegistraActividad() {
            when(registroActividadRepository.save(any(RegistroActividad.class))).thenReturn(registroActividad);
            when(registroActividadMapper.toDTO(registroActividad)).thenReturn(registroActividadDTO);

            RegistroActividadDTO resultado = registroActividadService.registrarActividad(
                    1L, TipoActividad.CREAR_USUARIO, "Nuevo usuario creado", "Usuario", "Email: test@example.com"
            );

            assertNotNull(resultado);
            assertEquals(TipoActividad.CREAR_USUARIO, resultado.getTipoActividad());
            assertEquals(1L, resultado.getUsuarioId());
            verify(registroActividadRepository).save(any(RegistroActividad.class));
        }
    }

    @Nested
    @DisplayName("Obtener Actividades Tests")
    class ObtenerActividadesTests {

        @Test
        @DisplayName("Obtener actividades por usuario")
        void obtenerActividadesPorUsuario_UsuarioExiste_RetornaActividades() {
            List<RegistroActividad> actividades = List.of(registroActividad);
            List<RegistroActividadDTO> actividadesDTO = List.of(registroActividadDTO);

            when(registroActividadRepository.findByUsuarioId(1L)).thenReturn(actividades);
            when(registroActividadMapper.toDTOList(actividades)).thenReturn(actividadesDTO);

            List<RegistroActividadDTO> resultado = registroActividadService.obtenerActividadesPorUsuario(1L);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            assertEquals(1L, resultado.get(0).getUsuarioId());
            verify(registroActividadRepository).findByUsuarioId(1L);
        }

        @Test
        @DisplayName("Obtener actividades por tipo")
        void obtenerActividadesPorTipo_ConTipoValido_RetornaActividades() {
            List<RegistroActividad> actividades = List.of(registroActividad);
            List<RegistroActividadDTO> actividadesDTO = List.of(registroActividadDTO);

            when(registroActividadRepository.findByTipoActividad(TipoActividad.CREAR_USUARIO))
                    .thenReturn(actividades);
            when(registroActividadMapper.toDTOList(actividades)).thenReturn(actividadesDTO);

            List<RegistroActividadDTO> resultado = registroActividadService
                    .obtenerActividadesPorTipo(TipoActividad.CREAR_USUARIO);

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(registroActividadRepository).findByTipoActividad(TipoActividad.CREAR_USUARIO);
        }

        @Test
        @DisplayName("Obtener todas las actividades")
        void obtenerTodasLasActividades_RetornaTodasLasActividades() {
            List<RegistroActividad> actividades = List.of(registroActividad);
            List<RegistroActividadDTO> actividadesDTO = List.of(registroActividadDTO);

            when(registroActividadRepository.findAll()).thenReturn(actividades);
            when(registroActividadMapper.toDTOList(actividades)).thenReturn(actividadesDTO);

            List<RegistroActividadDTO> resultado = registroActividadService.obtenerTodasLasActividades();

            assertNotNull(resultado);
            assertEquals(1, resultado.size());
            verify(registroActividadRepository).findAll();
        }
    }
}
