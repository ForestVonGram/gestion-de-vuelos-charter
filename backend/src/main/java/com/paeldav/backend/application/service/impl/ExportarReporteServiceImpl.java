package com.paeldav.backend.application.service.impl;

import com.paeldav.backend.application.dto.reporte.ReporteDTO;
import com.paeldav.backend.application.mapper.ReporteMapper;
import com.paeldav.backend.application.service.base.ExportarReporteService;
import com.paeldav.backend.application.service.integration.FormateadorCsvService;
import com.paeldav.backend.application.service.integration.FormateadorExcelService;
import com.paeldav.backend.application.service.integration.FormateadorPdfService;
import com.paeldav.backend.domain.enums.FormatoExportacion;
import com.paeldav.backend.infraestructure.repository.ReporteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de exportación de reportes.
 * Coordina los formatadores para exportar reportes en diferentes formatos.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ExportarReporteServiceImpl implements ExportarReporteService {

    private final ReporteRepository reporteRepository; // Repositorio de reportes
    private final ReporteMapper reporteMapper; // Mapper de reportes
    private final FormateadorPdfService formateadorPdf; // Servicio para exportar a PDF
    private final FormateadorExcelService formateadorExcel; // Servicio para exportar a Excel
    private final FormateadorCsvService formateadorCsv; // Servicio para exportar a CSV

    @Override
    public ByteArrayOutputStream exportarReporte(Long reporteId, FormatoExportacion formato) {
        log.info("Iniciando exportación de reporte ID: {} en formato: {}", reporteId, formato);

        // Obtener el reporte
        var reporte = reporteRepository.findById(reporteId)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado con ID: " + reporteId));

        ReporteDTO reporteDTO = reporteMapper.toDTO(reporte);

        // Registrar en logs
        log.info("Exportando reporte tipo: {}, generado por: {}",
                reporte.getTipo(), reporte.getGeneradoPor().getNombre());

        // Seleccionar formateador según el tipo
        return switch (formato) {
            case PDF -> {
                log.debug("Usando formateador PDF");
                yield formateadorPdf.formatearReporte(reporteDTO);
            }
            case EXCEL -> {
                log.debug("Usando formateador Excel");
                yield formateadorExcel.formatearReporte(reporteDTO);
            }
            case CSV -> {
                log.debug("Usando formateador CSV");
                yield formateadorCsv.formatearReporte(reporteDTO);
            }
        };
    }

    @Override
    public ByteArrayOutputStream exportarMultiples(List<Long> reporteIds, FormatoExportacion formato) {
        log.info("Iniciando exportación de {} reportes en formato: {}", reporteIds.size(), formato);

        // Obtener todos los reportes
        List<ReporteDTO> reportes = reporteIds.stream()
                .map(id -> reporteRepository.findById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado con ID: " + id)))
                .map(reporteMapper::toDTO)
                .collect(Collectors.toList());

        if (reportes.isEmpty()) {
            throw new IllegalArgumentException("No se encontraron reportes para exportar");
        }

        log.info("Exportando {} reportes", reportes.size());

        // Seleccionar formateador
        return switch (formato) {
            case PDF -> {
                log.debug("Usando formateador PDF para múltiples reportes");
                yield formateadorPdf.formatearMultiples(reportes);
            }
            case EXCEL -> {
                log.debug("Usando formateador Excel para múltiples reportes");
                yield formateadorExcel.formatearMultiples(reportes);
            }
            case CSV -> {
                log.debug("Usando formateador CSV para múltiples reportes");
                yield formateadorCsv.formatearMultiples(reportes);
            }
        };
    }

    @Override
    public String obtenerNombreArchivo(Long reporteId, FormatoExportacion formato) {
        var reporte = reporteRepository.findById(reporteId)
                .orElseThrow(() -> new IllegalArgumentException("Reporte no encontrado con ID: " + reporteId));

        String nombreBase = String.format("Reporte_%s_%s_%d",
                reporte.getTipo(),
                LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")),
                reporteId);

        return nombreBase + formato.getExtension();
    }

    @Override
    public boolean validarExportacion(Long reporteId, FormatoExportacion formato) {
        log.debug("Validando exportación para reporte ID: {} en formato: {}", reporteId, formato);

        // Validar que el reporte existe
        if (!reporteRepository.existsById(reporteId)) {
            log.warn("Intento de exportar reporte no existente: {}", reporteId);
            return false;
        }

        // Todos los formatos son válidos para todos los reportes
        log.debug("Validación exitosa para reporte ID: {}", reporteId);
        return true;
    }
}