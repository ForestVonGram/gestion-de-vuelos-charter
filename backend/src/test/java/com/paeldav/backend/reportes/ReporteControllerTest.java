package com.paeldav.backend.reportes;

import com.paeldav.backend.application.dto.reporte.ReporteCreateDTO;
import com.paeldav.backend.application.dto.reporte.ReporteDTO;
import com.paeldav.backend.application.service.base.ReporteFlotaService;
import com.paeldav.backend.application.service.base.ReporteGeneralService;
import com.paeldav.backend.application.service.base.ReporteHorasService;
import com.paeldav.backend.domain.enums.TipoReporte;
import com.paeldav.backend.presentation.controller.ReporteController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

/**
 * Pruebas para los endpoints del controlador de reportes.
 */
@ExtendWith(MockitoExtension.class)
class ReporteControllerTest {

    @Mock
    private ReporteGeneralService reporteGeneralService;

    @Mock
    private ReporteFlotaService reporteFlotaService;

    @Mock
    private ReporteHorasService reporteHorasService;

    @InjectMocks
    private ReporteController reporteController;

    private ReporteDTO reporteTest;
    private Authentication authTest;

    @BeforeEach
    void setUp() {
        reporteTest = ReporteDTO.builder()
                .id(1L)
                .tipo(TipoReporte.OPERATIVO)
                .descripcion("Reporte Test")
                .generadoPorNombre("Admin")
                .numeroRegistros(5)
                .build();

        // Crear una autenticación de prueba
        authTest = new UsernamePasswordAuthenticationToken("1", null);
    }

    @Test
    void testGenerarReporteExitoso() {
        // Given
        ReporteCreateDTO createDTO = ReporteCreateDTO.builder()
                .tipo(TipoReporte.OPERATIVO)
                .descripcion("Test Reporte")
                .fechaInicioRango(LocalDateTime.now().minusDays(7))
                .fechaFinRango(LocalDateTime.now())
                .build();

        when(reporteGeneralService.generarReporteOperativo(createDTO, 1L))
                .thenReturn(reporteTest);

        // When
        ResponseEntity<ReporteDTO> response = reporteController.generarReporte(createDTO, authTest);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(TipoReporte.OPERATIVO, response.getBody().getTipo());
    }

    @Test
    void testObtenerReportePorId() {
        // Given
        when(reporteGeneralService.obtenerReportePorId(1L))
                .thenReturn(reporteTest);

        // When
        ResponseEntity<ReporteDTO> response = reporteController.obtenerReportePorId(1L);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void testListarReportesSinFiltros() {
        // Given
        List<ReporteDTO> reportes = Arrays.asList(reporteTest);
        when(reporteGeneralService.listarReportes(any()))
                .thenReturn(reportes);

        // When
        ResponseEntity<List<ReporteDTO>> response = reporteController.listarReportes(null, null, null);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void testEliminarReporte() {
        // When
        ResponseEntity<Void> response = reporteController.eliminarReporte(1L);

        // Then
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testGenerarReporteFlota() {
        // Given
        when(reporteFlotaService.generarReporteUsoFlota(any(), any(), anyLong()))
                .thenReturn(reporteTest);

        // When
        ResponseEntity<ReporteDTO> response = reporteController.generarReporteFlota(
                LocalDateTime.now().minusDays(7).toString(),
                LocalDateTime.now().toString(),
                authTest);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testObtenerResumenFlota() {
        // Given
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("disponibles", 3L);
        estadisticas.put("total", 5L);

        when(reporteFlotaService.calcularEstadisticasPorAeronave(any(), any()))
                .thenReturn(estadisticas);

        // When
        ResponseEntity<Map<String, Object>> response = reporteController.obtenerResumenFlota(
                LocalDateTime.now().minusDays(7).toString(),
                LocalDateTime.now().toString());

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(3L, response.getBody().get("disponibles"));
    }

    @Test
    void testObtenerDisponibilidadFlota() {
        // Given
        Map<String, Object> disponibilidad = new HashMap<>();
        disponibilidad.put("disponibles", 3L);
        disponibilidad.put("total", 5L);
        disponibilidad.put("porcentajeDisponibilidad", 60.0);

        when(reporteFlotaService.calcularDisponibilidadFlota())
                .thenReturn(disponibilidad);

        // When
        ResponseEntity<Map<String, Object>> response = reporteController.obtenerDisponibilidadFlota();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(60.0, response.getBody().get("porcentajeDisponibilidad"));
    }

    @Test
    void testGenerarReporteHoras() {
        // Given
        when(reporteHorasService.generarReporteHorasTrabajadas(any(), any(), anyLong()))
                .thenReturn(reporteTest);

        // When
        ResponseEntity<ReporteDTO> response = reporteController.generarReporteHoras(
                LocalDateTime.now().minusDays(7).toString(),
                LocalDateTime.now().toString(),
                authTest);

        // Then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    void testObtenerResumenHoras() {
        // Given
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("totalHoras", 50.0);

        when(reporteHorasService.calcularHorasPorTripulante(any(), any()))
                .thenReturn(estadisticas);

        // When
        ResponseEntity<Map<String, Object>> response = reporteController.obtenerResumenHoras(
                LocalDateTime.now().minusDays(7).toString(),
                LocalDateTime.now().toString());

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(50.0, response.getBody().get("totalHoras"));
    }

    @Test
    void testValidarConsistenciaDatos() {
        // Given
        Map<String, Object> validacion = new HashMap<>();
        validacion.put("esValido", true);
        validacion.put("anomalias", new ArrayList<>());

        when(reporteHorasService.validarConsistenciaDatos(any(), any()))
                .thenReturn(validacion);

        // When
        ResponseEntity<Map<String, Object>> response = reporteController.validarConsistenciaDatos(
                LocalDateTime.now().minusDays(7).toString(),
                LocalDateTime.now().toString());

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue((boolean) response.getBody().get("esValido"));
    }

    @Test
    void testObtenerRegistrosPendientes() {
        // Given
        List<Map<String, Object>> registros = Arrays.asList(
                Map.of("id", 1L, "tripulante", "Piloto Test", "aprobado", false)
        );

        when(reporteHorasService.obtenerRegistrosPendientesAprobacion())
                .thenReturn(registros);

        // When
        ResponseEntity<List<Map<String, Object>>> response = reporteController.obtenerRegistrosPendientes();

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertFalse((boolean) response.getBody().get(0).get("aprobado"));
    }

    @Test
    void testObtenerEstadisticasTiposVuelo() {
        // Given
        Map<String, Object> estadisticas = new HashMap<>();
        estadisticas.put("horasPorTipoVuelo", Map.of("DIURNO", 30.0, "NOCTURNO", 20.0));

        when(reporteHorasService.calcularEstadisticasTiposVuelo(any(), any()))
                .thenReturn(estadisticas);

        // When
        ResponseEntity<Map<String, Object>> response = reporteController.obtenerEstadisticasTiposVuelo(
                LocalDateTime.now().minusDays(7).toString(),
                LocalDateTime.now().toString());

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody().get("horasPorTipoVuelo"));
    }
}
