package com.paeldav.backend.application.service.integration;

import com.opencsv.CSVWriter;
import com.paeldav.backend.application.dto.reporte.ReporteDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio para generar reportes en formato CSV.
 * Utiliza la librería OpenCSV para crear archivos CSV estructurados.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FormateadorCsvService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Convierte un reporte a formato CSV.
     *
     * @param reporte DTO del reporte a convertir
     * @return ByteArrayOutputStream con el contenido CSV
     */
    public ByteArrayOutputStream formatearReporte(ReporteDTO reporte) {
        log.info("Generando CSV para reporte ID: {}", reporte.getId());
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (OutputStreamWriter writer = new OutputStreamWriter(baos);
             CSVWriter csvWriter = new CSVWriter(writer)) {
            
            // Encabezado
            csvWriter.writeNext(new String[]{"REPORTE " + reporte.getTipo()}, false);
            csvWriter.writeNext(new String[]{}, false); // Línea vacía
            
            // Información general
            csvWriter.writeNext(new String[]{"Campo", "Valor"}, false);
            csvWriter.writeNext(new String[]{"ID", String.valueOf(reporte.getId())}, false);
            csvWriter.writeNext(new String[]{"Tipo", reporte.getTipo().toString()}, false);
            csvWriter.writeNext(new String[]{"Generado por", reporte.getGeneradoPorNombre()}, false);
            csvWriter.writeNext(new String[]{"Fecha de generación", 
                    reporte.getFechaGeneracion().format(FORMATO_FECHA)}, false);
            csvWriter.writeNext(new String[]{"Inicio de rango", 
                    reporte.getFechaInicioRango().format(FORMATO_FECHA)}, false);
            csvWriter.writeNext(new String[]{"Fin de rango", 
                    reporte.getFechaFinRango().format(FORMATO_FECHA)}, false);
            
            if (reporte.getNumeroRegistros() != null) {
                csvWriter.writeNext(new String[]{"Número de registros", 
                        String.valueOf(reporte.getNumeroRegistros())}, false);
            }
            
            // Descripción
            csvWriter.writeNext(new String[]{}, false);
            csvWriter.writeNext(new String[]{"DESCRIPCIÓN"}, false);
            csvWriter.writeNext(new String[]{
                    reporte.getDescripcion() != null ? reporte.getDescripcion() : "N/A"}, false);
            
            // Datos agregados
            if (reporte.getDatosAgregados() != null && !reporte.getDatosAgregados().isEmpty()) {
                csvWriter.writeNext(new String[]{}, false);
                csvWriter.writeNext(new String[]{"DATOS AGREGADOS"}, false);
                csvWriter.writeNext(new String[]{reporte.getDatosAgregados()}, false);
            }
            
            // Observaciones
            if (reporte.getObservaciones() != null && !reporte.getObservaciones().isEmpty()) {
                csvWriter.writeNext(new String[]{}, false);
                csvWriter.writeNext(new String[]{"OBSERVACIONES"}, false);
                csvWriter.writeNext(new String[]{reporte.getObservaciones()}, false);
            }
            
            csvWriter.flush();
            log.info("CSV generado exitosamente para reporte ID: {}", reporte.getId());
        } catch (Exception e) {
            log.error("Error al generar CSV para reporte ID: {}", reporte.getId(), e);
            throw new RuntimeException("Error al generar CSV: " + e.getMessage(), e);
        }
        
        return baos;
    }

    /**
     * Convierte múltiples reportes a formato CSV con un reporte por sección.
     *
     * @param reportes Lista de DTOs de reportes
     * @return ByteArrayOutputStream con el contenido CSV
     */
    public ByteArrayOutputStream formatearMultiples(List<ReporteDTO> reportes) {
        log.info("Generando CSV para {} reportes", reportes.size());
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (OutputStreamWriter writer = new OutputStreamWriter(baos);
             CSVWriter csvWriter = new CSVWriter(writer)) {
            
            // Encabezado
            csvWriter.writeNext(new String[]{"REPORTE CONSOLIDADO"}, false);
            csvWriter.writeNext(new String[]{"Total de reportes", String.valueOf(reportes.size())}, false);
            csvWriter.writeNext(new String[]{}, false);
            
            // Tabla de resumen
            csvWriter.writeNext(new String[]{"ID", "Tipo", "Generado por", "Fecha"}, false);
            for (ReporteDTO reporte : reportes) {
                csvWriter.writeNext(new String[]{
                        String.valueOf(reporte.getId()),
                        reporte.getTipo().toString(),
                        reporte.getGeneradoPorNombre(),
                        reporte.getFechaGeneracion().format(FORMATO_FECHA)
                }, false);
            }
            
            // Detalles de cada reporte
            for (int i = 0; i < reportes.size(); i++) {
                ReporteDTO reporte = reportes.get(i);
                
                csvWriter.writeNext(new String[]{}, false);
                csvWriter.writeNext(new String[]{"------- REPORTE " + (i + 1) + ": " + reporte.getTipo() + " -------"}, false);
                csvWriter.writeNext(new String[]{}, false);
                
                csvWriter.writeNext(new String[]{"Campo", "Valor"}, false);
                csvWriter.writeNext(new String[]{"ID", String.valueOf(reporte.getId())}, false);
                csvWriter.writeNext(new String[]{"Tipo", reporte.getTipo().toString()}, false);
                csvWriter.writeNext(new String[]{"Generado por", reporte.getGeneradoPorNombre()}, false);
                csvWriter.writeNext(new String[]{"Fecha", 
                        reporte.getFechaGeneracion().format(FORMATO_FECHA)}, false);
                csvWriter.writeNext(new String[]{"Descripción", 
                        reporte.getDescripcion() != null ? reporte.getDescripcion() : "N/A"}, false);
            }
            
            csvWriter.flush();
            log.info("CSV consolidado generado exitosamente para {} reportes", reportes.size());
        } catch (Exception e) {
            log.error("Error al generar CSV consolidado", e);
            throw new RuntimeException("Error al generar CSV consolidado: " + e.getMessage(), e);
        }
        
        return baos;
    }
}
