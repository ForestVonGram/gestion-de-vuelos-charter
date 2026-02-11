package com.paeldav.backend.reportes;

import com.paeldav.backend.application.dto.reporte.ReporteCreateDTO;
import com.paeldav.backend.application.dto.reporte.ReporteFiltroDTO;
import com.paeldav.backend.application.dto.reporte.ReporteDTO;
import com.paeldav.backend.application.mapper.ReporteMapper;
import com.paeldav.backend.application.service.impl.ReporteGeneralServiceImpl;
import com.paeldav.backend.domain.entity.Reporte;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.enums.TipoReporte;
import com.paeldav.backend.exception.UsuarioNoEncontradoException;
import com.paeldav.backend.infraestructure.repository.ReporteRepository;
import com.paeldav.backend.infraestructure.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
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

/**
 * Pruebas unitarias para el servicio de reportes generales.
 */
@ExtendWith(MockitoExtension.class)
class ReporteGeneralServiceTest {

    @Mock
    private ReporteRepository reporteRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ReporteMapper reporteMapper;

    @InjectMocks
    private ReporteGeneralServiceImpl reporteGeneralService;

    private Usuario usuarioTest;
    private Reporte reporteTest;
    private ReporteDTO reporteDTOTest;

    @BeforeEach
    void setUp() {
        usuarioTest = Usuario.builder()
                .id(1L)
                .nombre("Juan Pérez")
                .email("juan@example.com")
                .build();

        reporteTest = Reporte.builder()
                .id(1L)
                .tipo(TipoReporte.OPERATIVO)
                .descripcion("Reporte Operativo")
                .fechaInicioRango(LocalDateTime.now().minusDays(7))
                .fechaFinRango(LocalDateTime.now())
                .generadoPor(usuarioTest)
                .numeroRegistros(5)
                .build();

        reporteDTOTest = ReporteDTO.builder()
                .id(1L)
                .tipo(TipoReporte.OPERATIVO)
                .descripcion("Reporte Operativo")
                .generadoPorNombre("Juan Pérez")
                .numeroRegistros(5)
                .build();
    }

    @Test
    void testGenerarReporteOperativoExitoso() {
        // Given
        ReporteCreateDTO createDTO = ReporteCreateDTO.builder()
                .tipo(TipoReporte.OPERATIVO)
                .descripcion("Reporte Operativo")
                .fechaInicioRango(LocalDateTime.now().minusDays(7))
                .fechaFinRango(LocalDateTime.now())
                .build();

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioTest));
        when(reporteRepository.save(any(Reporte.class))).thenReturn(reporteTest);
        when(reporteMapper.toDTO(any(Reporte.class))).thenReturn(reporteDTOTest);

        // When
        ReporteDTO resultado = reporteGeneralService.generarReporteOperativo(createDTO, 1L);

        // Then
        assertNotNull(resultado);
        assertEquals(TipoReporte.OPERATIVO, resultado.getTipo());
        assertEquals("Juan Pérez", resultado.getGeneradoPorNombre());
        verify(reporteRepository, times(1)).save(any(Reporte.class));
    }

    @Test
    void testGenerarReporteConRangoInvalido() {
        // Given
        ReporteCreateDTO createDTO = ReporteCreateDTO.builder()
                .tipo(TipoReporte.OPERATIVO)
                .fechaInicioRango(LocalDateTime.now())
                .fechaFinRango(LocalDateTime.now().minusDays(1))
                .build();

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> reporteGeneralService.generarReporteOperativo(createDTO, 1L));
    }

    @Test
    void testGenerarReporteUsuarioNoEncontrado() {
        // Given
        ReporteCreateDTO createDTO = ReporteCreateDTO.builder()
                .tipo(TipoReporte.OPERATIVO)
                .fechaInicioRango(LocalDateTime.now().minusDays(7))
                .fechaFinRango(LocalDateTime.now())
                .build();

        when(usuarioRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(UsuarioNoEncontradoException.class,
                () -> reporteGeneralService.generarReporteOperativo(createDTO, 999L));
    }

    @Test
    void testObtenerReportePorIdExitoso() {
        // Given
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporteTest));
        when(reporteMapper.toDTO(reporteTest)).thenReturn(reporteDTOTest);

        // When
        ReporteDTO resultado = reporteGeneralService.obtenerReportePorId(1L);

        // Then
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(TipoReporte.OPERATIVO, resultado.getTipo());
    }

    @Test
    void testObtenerReportePorIdNoEncontrado() {
        // Given
        when(reporteRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> reporteGeneralService.obtenerReportePorId(999L));
    }

    @Test
    void testListarReportesConFiltroTipo() {
        // Given
        ReporteFiltroDTO filtro = ReporteFiltroDTO.builder()
                .tipo(TipoReporte.OPERATIVO)
                .build();

        List<Reporte> reportes = Arrays.asList(reporteTest);
        when(reporteRepository.findByTipo(TipoReporte.OPERATIVO))
                .thenReturn(reportes);
        when(reporteMapper.toDTO(any(Reporte.class))).thenReturn(reporteDTOTest);

        // When
        List<ReporteDTO> resultado = reporteGeneralService.listarReportes(filtro);

        // Then
        assertEquals(1, resultado.size());
        verify(reporteRepository, times(1)).findByTipo(TipoReporte.OPERATIVO);
    }

    @Test
    void testListarReportesSinFiltros() {
        // Given
        ReporteFiltroDTO filtro = ReporteFiltroDTO.builder().build();
        List<Reporte> reportes = Arrays.asList(reporteTest);
        
        when(reporteRepository.findAll()).thenReturn(reportes);
        when(reporteMapper.toDTO(any(Reporte.class))).thenReturn(reporteDTOTest);

        // When
        List<ReporteDTO> resultado = reporteGeneralService.listarReportes(filtro);

        // Then
        assertEquals(1, resultado.size());
        verify(reporteRepository, times(1)).findAll();
    }

    @Test
    void testObtenerTodosReportes() {
        // Given
        List<Reporte> reportes = Arrays.asList(reporteTest);
        when(reporteRepository.findAll()).thenReturn(reportes);
        when(reporteMapper.toDTO(any(Reporte.class))).thenReturn(reporteDTOTest);

        // When
        List<ReporteDTO> resultado = reporteGeneralService.obtenerTodosReportes();

        // Then
        assertEquals(1, resultado.size());
        verify(reporteRepository, times(1)).findAll();
    }

    @Test
    void testEliminarReporteExitoso() {
        // Given
        when(reporteRepository.existsById(1L)).thenReturn(true);

        // When
        reporteGeneralService.eliminarReporte(1L);

        // Then
        verify(reporteRepository, times(1)).deleteById(1L);
    }

    @Test
    void testEliminarReporteNoEncontrado() {
        // Given
        when(reporteRepository.existsById(anyLong())).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class,
                () -> reporteGeneralService.eliminarReporte(999L));
        verify(reporteRepository, never()).deleteById(anyLong());
    }

    @Test
    void testValidarRangoFechasValido() {
        // When
        boolean resultado = reporteGeneralService.validarRangoFechas(1000L, 2000L);

        // Then
        assertTrue(resultado);
    }

    @Test
    void testValidarRangoFechasInvalido() {
        // When
        boolean resultado = reporteGeneralService.validarRangoFechas(2000L, 1000L);

        // Then
        assertFalse(resultado);
    }

    @Test
    void testValidarRangoFechasNull() {
        // When
        boolean resultado = reporteGeneralService.validarRangoFechas(null, 2000L);

        // Then
        assertFalse(resultado);
    }
}
