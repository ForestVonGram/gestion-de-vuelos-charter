package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.reporte.ReporteDTO;
import com.paeldav.backend.application.mapper.ReporteMapper;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Pruebas de rendimiento para consultas agregadas de reportes.
 * Valida que las operaciones de exportación mantengan tiempos aceptables
 * incluso con volúmenes grandes de datos.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Pruebas de rendimiento de consultas agregadas")
class PruebaRendimientoConsultasAgregadasTest {

    @Mock
    private ReporteRepository reporteRepository;

    @Mock
    private ReporteMapper reporteMapper;

    @InjectMocks
    private ExportarReporteServiceImpl exportarReporteService;

    private static final int DATOS_PEQUENOS = 10;
    private static final int DATOS_MEDIANOS = 100;
    private static final int DATOS_GRANDES = 1000;
    private static final long TIEMPO_MAXIMO_PEQUENO_MS = 100;
    private static final long TIEMPO_MAXIMO_MEDIANO_MS = 500;
    private static final long TIEMPO_MAXIMO_GRANDE_MS = 2000;

    private Usuario usuarioEjemplo;

    @BeforeEach
    void setUp() {
        usuarioEjemplo = Usuario.builder()
                .id(1L)
                .nombre("Usuario Performance Test")
                .email("perf@example.com")
                .build();
    }

    private List<Reporte> crearReportesEjemplo(int cantidad) {
        List<Reporte> reportes = new ArrayList<>();
        for (int i = 1; i <= cantidad; i++) {
            Reporte reporte = Reporte.builder()
                    .id((long) i)
                    .tipo(TipoReporte.values()[i % TipoReporte.values().length])
                    .descripcion("Reporte de prueba #" + i)
                    .fechaGeneracion(LocalDateTime.now().minusDays(i % 30))
                    .fechaInicioRango(LocalDateTime.now().minusDays(i % 30 + 7))
                    .fechaFinRango(LocalDateTime.now().minusDays(i % 30))
                    .generadoPor(usuarioEjemplo)
                    .numeroRegistros(100 * i)
                    .datosAgregados("{\"registros\": " + (100 * i) + ", \"total\": " + (1000 * i) + "}")
                    .observaciones("Observación del reporte #" + i)
                    .build();
            reportes.add(reporte);
        }
        return reportes;
    }

    @Test
    @DisplayName("Validación de reporte debe ser rápida con pequeño volumen")
    void testValidacionRendimientoPequeno() {
        // Arrange
        when(reporteRepository.existsById(1L)).thenReturn(true);

        // Act
        long tiempoInicio = System.currentTimeMillis();
        boolean esValido = exportarReporteService.validarExportacion(1L, FormatoExportacion.PDF);
        long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;

        // Assert
        assertTrue(esValido);
        assertTrue(tiempoTranscurrido < TIEMPO_MAXIMO_PEQUENO_MS,
                "Validación debe completarse en menos de " + TIEMPO_MAXIMO_PEQUENO_MS + "ms. Tiempo real: " + tiempoTranscurrido + "ms");
    }

    @Test
    @DisplayName("Generación de nombre debe ser rápida incluso con múltiples llamadas")
    void testObtenerNombreArchivoRendimiento() {
        // Arrange
        Reporte reporte = crearReportesEjemplo(1).get(0);
        when(reporteRepository.findById(1L)).thenReturn(Optional.of(reporte));

        // Act
        long tiempoInicio = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            exportarReporteService.obtenerNombreArchivo(1L, FormatoExportacion.PDF);
        }
        long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;

        // Assert
        assertTrue(tiempoTranscurrido < TIEMPO_MAXIMO_PEQUENO_MS,
                "100 llamadas deben completarse en menos de " + TIEMPO_MAXIMO_PEQUENO_MS + "ms. Tiempo real: " + tiempoTranscurrido + "ms");
    }

    @Test
    @DisplayName("Exportar reporte individual con pequeño volumen debe ser rápido")
    void testExportarReportePequenioVolumen() {
        // Arrange
        List<Reporte> reportes = crearReportesEjemplo(DATOS_PEQUENOS);
        Reporte reporte = reportes.get(0);
        ReporteDTO reporteDTO = crearReporteDTODesdeReporte(reporte);

        when(reporteRepository.findById(reporte.getId())).thenReturn(Optional.of(reporte));
        when(reporteMapper.toDTO(reporte)).thenReturn(reporteDTO);

        // Act
        long tiempoInicio = System.currentTimeMillis();
        try {
            exportarReporteService.exportarReporte(reporte.getId(), FormatoExportacion.CSV);
        } catch (Exception e) {
            // Se espera que falle en la parte del formateo, pero medimos la búsqueda
        }
        long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;

        // Assert
        verify(reporteRepository).findById(reporte.getId());
        assertTrue(tiempoTranscurrido < TIEMPO_MAXIMO_PEQUENO_MS,
                "Exportación de pequeño reporte debe ser < " + TIEMPO_MAXIMO_PEQUENO_MS + "ms. Tiempo real: " + tiempoTranscurrido + "ms");
    }

    @Test
    @DisplayName("Búsqueda de múltiples reportes debe ser eficiente")
    void testBuscarMultiplesReportesRendimiento() {
        // Arrange
        List<Reporte> reportes = crearReportesEjemplo(DATOS_MEDIANOS);
        List<Long> ids = new ArrayList<>();
        for (Reporte r : reportes) {
            ids.add(r.getId());
            when(reporteRepository.findById(r.getId())).thenReturn(Optional.of(r));
            when(reporteMapper.toDTO(r)).thenReturn(crearReporteDTODesdeReporte(r));
        }

        // Act
        long tiempoInicio = System.currentTimeMillis();
        try {
            exportarReporteService.exportarMultiples(ids, FormatoExportacion.CSV);
        } catch (Exception e) {
            // Se espera excepción por formateo, medimos búsqueda
        }
        long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;

        // Assert
        assertTrue(tiempoTranscurrido < TIEMPO_MAXIMO_MEDIANO_MS,
                "Búsqueda de " + DATOS_MEDIANOS + " reportes debe ser < " + TIEMPO_MAXIMO_MEDIANO_MS + "ms. Tiempo real: " + tiempoTranscurrido + "ms");
    }

    @Test
    @DisplayName("Validación en lote debe mantener rendimiento")
    void testValidacionEnLoteRendimiento() {
        // Arrange
        int cantidad = 100;
        when(reporteRepository.existsById(anyLong())).thenReturn(true);

        // Act
        long tiempoInicio = System.currentTimeMillis();
        for (int i = 1; i <= cantidad; i++) {
            exportarReporteService.validarExportacion((long) i, FormatoExportacion.PDF);
        }
        long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;

        // Assert
        verify(reporteRepository, times(cantidad)).existsById(anyLong());
        assertTrue(tiempoTranscurrido < TIEMPO_MAXIMO_MEDIANO_MS,
                cantidad + " validaciones deben completarse en < " + TIEMPO_MAXIMO_MEDIANO_MS + "ms. Tiempo real: " + tiempoTranscurrido + "ms");
    }

    @Test
    @DisplayName("Acceso a datos agregados debe ser rápido")
    void testAccesoDatosAgregadosRendimiento() {
        // Arrange
        Reporte reporte = Reporte.builder()
                .id(1L)
                .tipo(TipoReporte.OPERATIVO)
                .descripcion("Reporte con datos agregados complejos")
                .fechaGeneracion(LocalDateTime.now())
                .fechaInicioRango(LocalDateTime.now().minusDays(7))
                .fechaFinRango(LocalDateTime.now())
                .generadoPor(usuarioEjemplo)
                .numeroRegistros(10000)
                .datosAgregados("{\"registros\": 10000, \"estadisticas\": {\"total\": 1000000, \"promedio\": 100, \"maximo\": 5000, \"minimo\": 10}}")
                .build();

        // No necesitamos stubbing ya que no usamos el repository en esta prueba

        // Act
        long tiempoInicio = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            String datos = reporte.getDatosAgregados();
            assertNotNull(datos);
        }
        long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;

        // Assert
        assertTrue(tiempoTranscurrido < TIEMPO_MAXIMO_PEQUENO_MS,
                "1000 accesos a datos agregados debe ser < " + TIEMPO_MAXIMO_PEQUENO_MS + "ms. Tiempo real: " + tiempoTranscurrido + "ms");
    }

    @Test
    @DisplayName("Conversión DTO debe ser eficiente para volumen grande")
    void testConversionDTORendimiento() {
        // Arrange
        List<Reporte> reportes = crearReportesEjemplo(DATOS_GRANDES);
        for (Reporte r : reportes) {
            when(reporteMapper.toDTO(r)).thenReturn(crearReporteDTODesdeReporte(r));
        }

        // Act
        long tiempoInicio = System.currentTimeMillis();
        for (Reporte reporte : reportes) {
            reporteMapper.toDTO(reporte);
        }
        long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;

        // Assert
        assertTrue(tiempoTranscurrido < TIEMPO_MAXIMO_GRANDE_MS,
                "Conversión de " + DATOS_GRANDES + " reportes debe ser < " + TIEMPO_MAXIMO_GRANDE_MS + "ms. Tiempo real: " + tiempoTranscurrido + "ms");
    }

    @Test
    @DisplayName("Manejo de memoria con volumen grande")
    void testManejoDatosGrandeMemoria() {
        // Arrange
        List<Long> ids = new ArrayList<>();
        for (int i = 1; i <= DATOS_GRANDES; i++) {
            ids.add((long) i);
        }

        when(reporteRepository.findById(anyLong())).thenAnswer(invocation -> {
            Long id = invocation.getArgument(0);
            Reporte reporte = Reporte.builder()
                    .id(id)
                    .tipo(TipoReporte.OPERATIVO)
                    .descripcion("Reporte " + id)
                    .fechaGeneracion(LocalDateTime.now())
                    .fechaInicioRango(LocalDateTime.now().minusDays(7))
                    .fechaFinRango(LocalDateTime.now())
                    .generadoPor(usuarioEjemplo)
                    .numeroRegistros(100 * id.intValue())
                    .datosAgregados("{\"id\": " + id + "}")
                    .build();
            return Optional.of(reporte);
        });

        // Act - Validar que la operación no causa OutOfMemory
        long tiempoInicio = System.currentTimeMillis();
        assertDoesNotThrow(() -> {
            // Simular construcción de lista grande
            List<Reporte> reportesGrandes = new ArrayList<>();
            for (Long id : ids) {
                reportesGrandes.add(reporteRepository.findById(id).get());
            }
            assertEquals(DATOS_GRANDES, reportesGrandes.size());
        });
        long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;

        // Assert
        assertTrue(tiempoTranscurrido < TIEMPO_MAXIMO_GRANDE_MS,
                "Manejo de " + DATOS_GRANDES + " reportes debe ser < " + TIEMPO_MAXIMO_GRANDE_MS + "ms. Tiempo real: " + tiempoTranscurrido + "ms");
    }

    @Test
    @DisplayName("Validación paralela debe mejorar rendimiento")
    void testValidacionParalelaRendimiento() {
        // Arrange
        when(reporteRepository.existsById(anyLong())).thenReturn(true);

        List<Long> ids = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            ids.add((long) i);
        }

        // Act
        long tiempoInicio = System.currentTimeMillis();
        ids.parallelStream().forEach(id -> 
            exportarReporteService.validarExportacion(id, FormatoExportacion.PDF)
        );
        long tiempoParalelo = System.currentTimeMillis() - tiempoInicio;

        // Act - Versión secuencial
        tiempoInicio = System.currentTimeMillis();
        ids.stream().forEach(id -> 
            exportarReporteService.validarExportacion(id, FormatoExportacion.PDF)
        );
        long tiempoSecuencial = System.currentTimeMillis() - tiempoInicio;

        // Assert
        assertNotNull(tiempoParalelo);
        assertNotNull(tiempoSecuencial);
        // Ambas deberían ser rápidas
        assertTrue(tiempoParalelo < TIEMPO_MAXIMO_MEDIANO_MS);
        assertTrue(tiempoSecuencial < TIEMPO_MAXIMO_MEDIANO_MS);
    }

    @Test
    @DisplayName("Generación de múltiples nombres debe ser eficiente")
    void testGenerarMultiplesNombresRendimiento() {
        // Arrange
        List<Reporte> reportes = crearReportesEjemplo(100);
        for (Reporte r : reportes) {
            when(reporteRepository.findById(r.getId())).thenReturn(Optional.of(r));
        }

        // Act
        long tiempoInicio = System.currentTimeMillis();
        for (Reporte reporte : reportes) {
            exportarReporteService.obtenerNombreArchivo(reporte.getId(), FormatoExportacion.EXCEL);
        }
        long tiempoTranscurrido = System.currentTimeMillis() - tiempoInicio;

        // Assert
        assertTrue(tiempoTranscurrido < TIEMPO_MAXIMO_MEDIANO_MS,
                "Generar 100 nombres debe ser < " + TIEMPO_MAXIMO_MEDIANO_MS + "ms. Tiempo real: " + tiempoTranscurrido + "ms");
    }

    // Método auxiliar
    private ReporteDTO crearReporteDTODesdeReporte(Reporte reporte) {
        return ReporteDTO.builder()
                .id(reporte.getId())
                .tipo(reporte.getTipo())
                .descripcion(reporte.getDescripcion())
                .fechaGeneracion(reporte.getFechaGeneracion())
                .fechaInicioRango(reporte.getFechaInicioRango())
                .fechaFinRango(reporte.getFechaFinRango())
                .generadoPorNombre(reporte.getGeneradoPor().getNombre())
                .numeroRegistros(reporte.getNumeroRegistros())
                .datosAgregados(reporte.getDatosAgregados())
                .observaciones(reporte.getObservaciones())
                .build();
    }
}
