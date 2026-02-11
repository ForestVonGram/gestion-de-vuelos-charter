package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.reporte.ReporteDTO;
import com.paeldav.backend.application.mapper.ReporteMapper;
import com.paeldav.backend.application.service.integration.FormateadorCsvService;
import com.paeldav.backend.application.service.integration.FormateadorExcelService;
import com.paeldav.backend.application.service.integration.FormateadorPdfService;
import com.paeldav.backend.domain.entity.Reporte;
import com.paeldav.backend.domain.entity.Usuario;
import com.paeldav.backend.domain.enums.FormatoExportacion;
import com.paeldav.backend.domain.enums.TipoReporte;
import com.paeldav.backend.infraestructure.repository.ReporteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Pruebas de exportación de reportes en diferentes formatos.
 * Valida que la exportación a PDF, Excel y CSV funcione correctamente.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de exportación de reportes")
class PruebaExportacionFormatosTest {

    @Mock
    private ReporteRepository reporteRepository;

    @Mock
    private ReporteMapper reporteMapper;

    @Mock
    private FormateadorPdfService formateadorPdf;

    @Mock
    private FormateadorExcelService formateadorExcel;

    @Mock
    private FormateadorCsvService formateadorCsv;

    @InjectMocks
    private ExportarReporteServiceImpl exportarReporteService;

    private Reporte reporteEjemplo;
    private ReporteDTO reporteDTOEjemplo;
    private Usuario usuarioEjemplo;

    @BeforeEach
    void setUp() {
        // Configurar datos de ejemplo
        usuarioEjemplo = Usuario.builder()
                .id(1L)
                .nombre("Usuario Test")
                .email("test@example.com")
                .build();

        reporteEjemplo = Reporte.builder()
                .id(1L)
                .tipo(TipoReporte.OPERATIVO)
                .descripcion("Reporte de prueba")
                .fechaGeneracion(LocalDateTime.now())
                .fechaInicioRango(LocalDateTime.now().minusDays(7))
                .fechaFinRango(LocalDateTime.now())
                .generadoPor(usuarioEjemplo)
                .numeroRegistros(100)
                .datosAgregados("{\"total\": 100}")
                .observaciones("Reporte de prueba para validación")
                .build();

        reporteDTOEjemplo = ReporteDTO.builder()
                .id(1L)
                .tipo(TipoReporte.OPERATIVO)
                .descripcion("Reporte de prueba")
                .fechaGeneracion(LocalDateTime.now())
                .fechaInicioRango(LocalDateTime.now().minusDays(7))
                .fechaFinRango(LocalDateTime.now())
                .generadoPorNombre("Usuario Test")
                .numeroRegistros(100)
                .datosAgregados("{\"total\": 100}")
                .observaciones("Reporte de prueba para validación")
                .build();
    }

    @Test
    @DisplayName("Debe exportar reporte a formato PDF exitosamente")
    void testExportarReportePDF() throws Exception {
        // Arrange
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporteEjemplo));
        when(reporteMapper.toDTO(reporteEjemplo)).thenReturn(reporteDTOEjemplo);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(new byte[]{37, 80, 68, 70}); // PDF magic bytes
        when(formateadorPdf.formatearReporte(reporteDTOEjemplo)).thenReturn(outputStream);

        // Act
        ByteArrayOutputStream resultado = exportarReporteService.exportarReporte(1L, FormatoExportacion.PDF);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.size() > 0);
        verify(reporteRepository, times(1)).findById(1L);
        verify(formateadorPdf, times(1)).formatearReporte(any(ReporteDTO.class));
    }

    @Test
    @DisplayName("Debe exportar reporte a formato Excel exitosamente")
    void testExportarReporteExcel() throws Exception {
        // Arrange
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporteEjemplo));
        when(reporteMapper.toDTO(reporteEjemplo)).thenReturn(reporteDTOEjemplo);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(new byte[]{80, 75, 3, 4}); // XLSX magic bytes
        when(formateadorExcel.formatearReporte(reporteDTOEjemplo)).thenReturn(outputStream);

        // Act
        ByteArrayOutputStream resultado = exportarReporteService.exportarReporte(1L, FormatoExportacion.EXCEL);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.size() > 0);
        verify(reporteRepository, times(1)).findById(1L);
        verify(formateadorExcel, times(1)).formatearReporte(any(ReporteDTO.class));
    }

    @Test
    @DisplayName("Debe exportar reporte a formato CSV exitosamente")
    void testExportarReporteCSV() throws Exception {
        // Arrange
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporteEjemplo));
        when(reporteMapper.toDTO(reporteEjemplo)).thenReturn(reporteDTOEjemplo);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write("ID,Tipo,Descripción\n1,OPERATIVO,Reporte de prueba".getBytes());
        when(formateadorCsv.formatearReporte(reporteDTOEjemplo)).thenReturn(outputStream);

        // Act
        ByteArrayOutputStream resultado = exportarReporteService.exportarReporte(1L, FormatoExportacion.CSV);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.size() > 0);
        verify(reporteRepository, times(1)).findById(1L);
        verify(formateadorCsv, times(1)).formatearReporte(any(ReporteDTO.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando el reporte no existe")
    void testExportarReporteNoExistente() {
        // Arrange
        when(reporteRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            exportarReporteService.exportarReporte(999L, FormatoExportacion.PDF)
        );
        verify(reporteRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("Debe exportar múltiples reportes a PDF")
    void testExportarMultiplesReportePDF() throws Exception {
        // Arrange
        List<Long> ids = Arrays.asList(1L, 2L);
        Reporte reporte2 = Reporte.builder()
                .id(2L)
                .tipo(TipoReporte.FLOTA)
                .descripcion("Reporte flota")
                .fechaGeneracion(LocalDateTime.now())
                .fechaInicioRango(LocalDateTime.now().minusDays(7))
                .fechaFinRango(LocalDateTime.now())
                .generadoPor(usuarioEjemplo)
                .numeroRegistros(50)
                .build();

        ReporteDTO reporteDTO2 = ReporteDTO.builder()
                .id(2L)
                .tipo(TipoReporte.FLOTA)
                .descripcion("Reporte flota")
                .fechaGeneracion(LocalDateTime.now())
                .generadoPorNombre("Usuario Test")
                .numeroRegistros(50)
                .build();

        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporteEjemplo));
        when(reporteRepository.findById(2L)).thenReturn(Optional.of(reporte2));
        when(reporteMapper.toDTO(reporteEjemplo)).thenReturn(reporteDTOEjemplo);
        when(reporteMapper.toDTO(reporte2)).thenReturn(reporteDTO2);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(new byte[]{37, 80, 68, 70});
        when(formateadorPdf.formatearMultiples(any())).thenReturn(outputStream);

        // Act
        ByteArrayOutputStream resultado = exportarReporteService.exportarMultiples(ids, FormatoExportacion.PDF);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.size() > 0);
        verify(formateadorPdf, times(1)).formatearMultiples(any());
    }

    @Test
    @DisplayName("Debe generar nombre de archivo correcto para PDF")
    void testObtenerNombreArchivoPDF() {
        // Arrange
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporteEjemplo));

        // Act
        String nombreArchivo = exportarReporteService.obtenerNombreArchivo(1L, FormatoExportacion.PDF);

        // Assert
        assertNotNull(nombreArchivo);
        assertTrue(nombreArchivo.endsWith(".pdf"));
        assertTrue(nombreArchivo.contains("OPERATIVO"));
        assertTrue(nombreArchivo.contains("1"));
    }

    @Test
    @DisplayName("Debe generar nombre de archivo correcto para Excel")
    void testObtenerNombreArchivoExcel() {
        // Arrange
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporteEjemplo));

        // Act
        String nombreArchivo = exportarReporteService.obtenerNombreArchivo(1L, FormatoExportacion.EXCEL);

        // Assert
        assertNotNull(nombreArchivo);
        assertTrue(nombreArchivo.endsWith(".xlsx"));
        assertTrue(nombreArchivo.contains("OPERATIVO"));
    }

    @Test
    @DisplayName("Debe generar nombre de archivo correcto para CSV")
    void testObtenerNombreArchivoCSV() {
        // Arrange
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporteEjemplo));

        // Act
        String nombreArchivo = exportarReporteService.obtenerNombreArchivo(1L, FormatoExportacion.CSV);

        // Assert
        assertNotNull(nombreArchivo);
        assertTrue(nombreArchivo.endsWith(".csv"));
        assertTrue(nombreArchivo.contains("OPERATIVO"));
    }

    @Test
    @DisplayName("Debe validar exportación correctamente")
    void testValidarExportacion() {
        // Arrange
        when(reporteRepository.existsById(1L)).thenReturn(true);

        // Act
        boolean esValido = exportarReporteService.validarExportacion(1L, FormatoExportacion.PDF);

        // Assert
        assertTrue(esValido);
        verify(reporteRepository, times(1)).existsById(1L);
    }

    @Test
    @DisplayName("Debe fallar validación cuando reporte no existe")
    void testValidarExportacionFalla() {
        // Arrange
        when(reporteRepository.existsById(999L)).thenReturn(false);

        // Act
        boolean esValido = exportarReporteService.validarExportacion(999L, FormatoExportacion.PDF);

        // Assert
        assertFalse(esValido);
        verify(reporteRepository, times(1)).existsById(999L);
    }

    @Test
    @DisplayName("Debe lanzar excepción con lista vacía en múltiples")
    void testExportarMultiplesListVacia() {
        // Arrange
        List<Long> ids = Arrays.asList();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> 
            exportarReporteService.exportarMultiples(ids, FormatoExportacion.PDF)
        );
    }

    @Test
    @DisplayName("Debe exportar múltiples reportes a Excel")
    void testExportarMultiplesReporteExcel() throws Exception {
        // Arrange
        List<Long> ids = Arrays.asList(1L, 2L);
        Reporte reporte2 = Reporte.builder()
                .id(2L)
                .tipo(TipoReporte.FLOTA)
                .build();

        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporteEjemplo));
        when(reporteRepository.findById(2L)).thenReturn(Optional.of(reporte2));
        when(reporteMapper.toDTO(any())).thenReturn(reporteDTOEjemplo);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(new byte[]{80, 75, 3, 4});
        when(formateadorExcel.formatearMultiples(any())).thenReturn(outputStream);

        // Act
        ByteArrayOutputStream resultado = exportarReporteService.exportarMultiples(ids, FormatoExportacion.EXCEL);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.size() > 0);
        verify(formateadorExcel, times(1)).formatearMultiples(any());
    }

    @Test
    @DisplayName("Debe exportar múltiples reportes a CSV")
    void testExportarMultiplesReporteCSV() throws Exception {
        // Arrange
        List<Long> ids = Arrays.asList(1L, 2L);
        Reporte reporte2 = Reporte.builder()
                .id(2L)
                .tipo(TipoReporte.HORAS)
                .build();

        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporteEjemplo));
        when(reporteRepository.findById(2L)).thenReturn(Optional.of(reporte2));
        when(reporteMapper.toDTO(any())).thenReturn(reporteDTOEjemplo);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write("REPORTE CONSOLIDADO".getBytes());
        when(formateadorCsv.formatearMultiples(any())).thenReturn(outputStream);

        // Act
        ByteArrayOutputStream resultado = exportarReporteService.exportarMultiples(ids, FormatoExportacion.CSV);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.size() > 0);
        verify(formateadorCsv, times(1)).formatearMultiples(any());
    }
}
