package com.paeldav.backend.presentation.controller;

import com.paeldav.backend.application.service.base.ExportarReporteService;
import com.paeldav.backend.domain.enums.FormatoExportacion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Controlador REST para la exportación de reportes.
 * Proporciona endpoints para descargar reportes en diferentes formatos.
 */
@RestController
@RequestMapping("/api/reportes/exportar")
@RequiredArgsConstructor
@Slf4j
public class ExportarReporteController {

    private final ExportarReporteService exportarReporteService;

    /**
     * Exporta un reporte individual en el formato especificado.
     *
     * @param id ID del reporte a exportar
     * @param formato Formato de exportación (PDF, EXCEL, CSV)
     * @return ResponseEntity con el archivo descargable
     */
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> exportarReporte(
            @PathVariable Long id,
            @RequestParam(defaultValue = "PDF") String formato) {
        
        log.info("Exportando reporte ID: {} en formato: {}", id, formato);

        try {
            // Validar formato
            FormatoExportacion tipoFormato = FormatoExportacion.valueOf(formato.toUpperCase());

            // Validar que el reporte puede ser exportado
            if (!exportarReporteService.validarExportacion(id, tipoFormato)) {
                log.warn("Intento de exportar reporte no válido: {}", id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            // Exportar
            ByteArrayOutputStream baos = exportarReporteService.exportarReporte(id, tipoFormato);
            
            // Obtener nombre de archivo
            String nombreArchivo = exportarReporteService.obtenerNombreArchivo(id, tipoFormato);

            // Configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.valueOf(tipoFormato.getMimeType()));
            headers.setContentDisposition(
                    ContentDisposition.attachment()
                            .filename(nombreArchivo)
                            .build()
            );
            headers.setContentLength(baos.size());

            log.info("Reporte exportado exitosamente: ID={}, formato={}, tamaño={} bytes", 
                    id, formato, baos.size());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(baos.toByteArray());

        } catch (IllegalArgumentException e) {
            log.error("Formato inválido: {}", formato, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error al exportar reporte ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Exporta múltiples reportes en un mismo archivo.
     *
     * @param ids Lista de IDs de reportes a exportar
     * @param formato Formato de exportación (PDF, EXCEL, CSV)
     * @return ResponseEntity con el archivo descargable consolidado
     */
    @PostMapping("/multiples")
    public ResponseEntity<byte[]> exportarMultiples(
            @RequestBody List<Long> ids,
            @RequestParam(defaultValue = "PDF") String formato) {
        
        log.info("Exportando {} reportes en formato: {}", ids.size(), formato);

        try {
            // Validar que hay reportes
            if (ids == null || ids.isEmpty()) {
                log.warn("Intento de exportar lista vacía de reportes");
                return ResponseEntity.badRequest().build();
            }

            // Validar formato
            FormatoExportacion tipoFormato = FormatoExportacion.valueOf(formato.toUpperCase());

            // Exportar
            ByteArrayOutputStream baos = exportarReporteService.exportarMultiples(ids, tipoFormato);

            // Configurar nombre de archivo
            String nombreArchivo = "Reportes_Consolidados_" 
                    + java.time.LocalDateTime.now()
                            .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
                    + tipoFormato.getExtension();

            // Configurar headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(org.springframework.http.MediaType.valueOf(tipoFormato.getMimeType()));
            headers.setContentDisposition(
                    ContentDisposition.attachment()
                            .filename(nombreArchivo)
                            .build()
            );
            headers.setContentLength(baos.size());

            log.info("Reportes exportados exitosamente: cantidad={}, formato={}, tamaño={} bytes", 
                    ids.size(), formato, baos.size());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(baos.toByteArray());

        } catch (IllegalArgumentException e) {
            log.error("Formato inválido: {}", formato, e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error al exportar reportes múltiples", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene información sobre los formatos de exportación soportados.
     *
     * @return ResponseEntity con información de formatos
     */
    @GetMapping("/formatos")
    public ResponseEntity<?> obtenerFormatosDisponibles() {
        log.debug("Obteniendo formatos disponibles");
        
        var formatos = new java.util.HashMap<>();
        for (FormatoExportacion formato : FormatoExportacion.values()) {
            formatos.put(formato.name(), java.util.Map.of(
                    "mimeType", formato.getMimeType(),
                    "extension", formato.getExtension()
            ));
        }
        
        return ResponseEntity.ok(formatos);
    }
}
