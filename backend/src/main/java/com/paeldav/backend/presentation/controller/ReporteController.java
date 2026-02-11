package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.dto.reporte.ReporteCreateDTO;
import com.paeldav.backend.application.dto.reporte.ReporteFiltroDTO;
import com.paeldav.backend.application.dto.reporte.ReporteDTO;
import com.paeldav.backend.application.service.base.ReporteFlotaService;
import com.paeldav.backend.application.service.base.ReporteGeneralService;
import com.paeldav.backend.application.service.base.ReporteHorasService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para la gestión de reportes operativos.
 * Proporciona endpoints para generar y consultar reportes de flota, horas y operaciones generales.
 */
@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteGeneralService reporteGeneralService;
    private final ReporteFlotaService reporteFlotaService;
    private final ReporteHorasService reporteHorasService;

    /**
     * Genera un nuevo reporte operativo.
     *
     * @param createDTO DTO con parámetros del reporte
     * @param authentication Información de autenticación del usuario
     * @return ResponseEntity con el reporte generado (201 Created)
     */
    @PostMapping("/generar")
    public ResponseEntity<ReporteDTO> generarReporte(
            @Valid @RequestBody ReporteCreateDTO createDTO,
            Authentication authentication) {
        
        // Obtener ID del usuario autenticado
        Long usuarioId = Long.parseLong(authentication.getName());
        
        ReporteDTO reporte = reporteGeneralService.generarReporteOperativo(createDTO, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(reporte);
    }

    /**
     * Obtiene un reporte por su ID.
     *
     * @param id ID del reporte
     * @return ResponseEntity con los datos del reporte
     */
    @GetMapping("/{id}")
    public ResponseEntity<ReporteDTO> obtenerReportePorId(@PathVariable Long id) {
        ReporteDTO reporte = reporteGeneralService.obtenerReportePorId(id);
        return ResponseEntity.ok(reporte);
    }

    /**
     * Lista reportes con filtros opcionales.
     *
     * @param filtro DTO con criterios de filtrado
     * @return ResponseEntity con lista de reportes
     */
    @GetMapping
    public ResponseEntity<List<ReporteDTO>> listarReportes(
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String fechaDesde,
            @RequestParam(required = false) String fechaHasta) {
        
        ReporteFiltroDTO filtro = new ReporteFiltroDTO();
        if (tipo != null) {
            filtro.setTipo(com.paeldav.backend.domain.enums.TipoReporte.valueOf(tipo.toUpperCase()));
        }
        
        List<ReporteDTO> reportes = reporteGeneralService.listarReportes(filtro);
        return ResponseEntity.ok(reportes);
    }

    /**
     * Obtiene todos los reportes sin filtros.
     *
     * @return ResponseEntity con lista de todos los reportes
     */
    @GetMapping("/todos")
    public ResponseEntity<List<ReporteDTO>> obtenerTodosReportes() {
        List<ReporteDTO> reportes = reporteGeneralService.obtenerTodosReportes();
        return ResponseEntity.ok(reportes);
    }

    /**
     * Elimina un reporte por su ID.
     *
     * @param id ID del reporte a eliminar
     * @return ResponseEntity sin contenido (204 No Content)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarReporte(@PathVariable Long id) {
        reporteGeneralService.eliminarReporte(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Genera un reporte de uso de flota.
     *
     * @param fechaInicio Fecha de inicio (ISO 8601)
     * @param fechaFin Fecha de fin (ISO 8601)
     * @param authentication Información de autenticación
     * @return ResponseEntity con el reporte de flota
     */
    @PostMapping("/flota/generar")
    public ResponseEntity<ReporteDTO> generarReporteFlota(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            Authentication authentication) {
        
        Long usuarioId = Long.parseLong(authentication.getName());
        LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
        LocalDateTime fin = LocalDateTime.parse(fechaFin);
        
        ReporteDTO reporte = reporteFlotaService.generarReporteUsoFlota(inicio, fin, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(reporte);
    }

    /**
     * Obtiene un resumen de uso de flota.
     *
     * @param fechaInicio Fecha de inicio (ISO 8601)
     * @param fechaFin Fecha de fin (ISO 8601)
     * @return ResponseEntity con estadísticas de flota
     */
    @GetMapping("/flota/resumen")
    public ResponseEntity<Map<String, Object>> obtenerResumenFlota(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        
        LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
        LocalDateTime fin = LocalDateTime.parse(fechaFin);
        
        Map<String, Object> estadisticas = reporteFlotaService.calcularEstadisticasPorAeronave(inicio, fin);
        return ResponseEntity.ok(estadisticas);
    }

    /**
     * Obtiene estadísticas de combustible.
     *
     * @param fechaInicio Fecha de inicio (ISO 8601)
     * @param fechaFin Fecha de fin (ISO 8601)
     * @return ResponseEntity con estadísticas de combustible
     */
    @GetMapping("/flota/combustible")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasCombustible(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        
        LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
        LocalDateTime fin = LocalDateTime.parse(fechaFin);
        
        Map<String, Object> estadisticas = reporteFlotaService.obtenerEstadisticasCombustible(inicio, fin);
        return ResponseEntity.ok(estadisticas);
    }

    /**
     * Obtiene disponibilidad actual de la flota.
     *
     * @return ResponseEntity con información de disponibilidad
     */
    @GetMapping("/flota/disponibilidad")
    public ResponseEntity<Map<String, Object>> obtenerDisponibilidadFlota() {
        Map<String, Object> disponibilidad = reporteFlotaService.calcularDisponibilidadFlota();
        return ResponseEntity.ok(disponibilidad);
    }

    /**
     * Genera un reporte de horas trabajadas.
     *
     * @param fechaInicio Fecha de inicio (ISO 8601)
     * @param fechaFin Fecha de fin (ISO 8601)
     * @param authentication Información de autenticación
     * @return ResponseEntity con el reporte de horas
     */
    @PostMapping("/horas/generar")
    public ResponseEntity<ReporteDTO> generarReporteHoras(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            Authentication authentication) {
        
        Long usuarioId = Long.parseLong(authentication.getName());
        LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
        LocalDateTime fin = LocalDateTime.parse(fechaFin);
        
        ReporteDTO reporte = reporteHorasService.generarReporteHorasTrabajadas(inicio, fin, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(reporte);
    }

    /**
     * Obtiene un resumen de horas trabajadas.
     *
     * @param fechaInicio Fecha de inicio (ISO 8601)
     * @param fechaFin Fecha de fin (ISO 8601)
     * @return ResponseEntity con estadísticas de horas
     */
    @GetMapping("/horas/resumen")
    public ResponseEntity<Map<String, Object>> obtenerResumenHoras(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        
        LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
        LocalDateTime fin = LocalDateTime.parse(fechaFin);
        
        Map<String, Object> estadisticas = reporteHorasService.calcularHorasPorTripulante(inicio, fin);
        return ResponseEntity.ok(estadisticas);
    }

    /**
     * Obtiene horas por función desempeñada.
     *
     * @param fechaInicio Fecha de inicio (ISO 8601)
     * @param fechaFin Fecha de fin (ISO 8601)
     * @return ResponseEntity con horas agrupadas por función
     */
    @GetMapping("/horas/por-funcion")
    public ResponseEntity<Map<String, Object>> obtenerHorasPorFuncion(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        
        LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
        LocalDateTime fin = LocalDateTime.parse(fechaFin);
        
        Map<String, Object> estadisticas = reporteHorasService.calcularHorasPorFuncion(inicio, fin);
        return ResponseEntity.ok(estadisticas);
    }

    /**
     * Valida la consistencia de datos administrativos.
     *
     * @param fechaInicio Fecha de inicio (ISO 8601)
     * @param fechaFin Fecha de fin (ISO 8601)
     * @return ResponseEntity con resultados de validación
     */
    @GetMapping("/horas/validar")
    public ResponseEntity<Map<String, Object>> validarConsistenciaDatos(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        
        LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
        LocalDateTime fin = LocalDateTime.parse(fechaFin);
        
        Map<String, Object> validacion = reporteHorasService.validarConsistenciaDatos(inicio, fin);
        return ResponseEntity.ok(validacion);
    }

    /**
     * Obtiene registros de horas pendientes de aprobación.
     *
     * @return ResponseEntity con lista de registros pendientes
     */
    @GetMapping("/horas/pendientes")
    public ResponseEntity<List<Map<String, Object>>> obtenerRegistrosPendientes() {
        List<Map<String, Object>> registros = reporteHorasService.obtenerRegistrosPendientesAprobacion();
        return ResponseEntity.ok(registros);
    }

    /**
     * Obtiene estadísticas de tipos de vuelo.
     *
     * @param fechaInicio Fecha de inicio (ISO 8601)
     * @param fechaFin Fecha de fin (ISO 8601)
     * @return ResponseEntity con horas por tipo de vuelo
     */
    @GetMapping("/horas/tipos-vuelo")
    public ResponseEntity<Map<String, Object>> obtenerEstadisticasTiposVuelo(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin) {
        
        LocalDateTime inicio = LocalDateTime.parse(fechaInicio);
        LocalDateTime fin = LocalDateTime.parse(fechaFin);
        
        Map<String, Object> estadisticas = reporteHorasService.calcularEstadisticasTiposVuelo(inicio, fin);
        return ResponseEntity.ok(estadisticas);
    }
}
