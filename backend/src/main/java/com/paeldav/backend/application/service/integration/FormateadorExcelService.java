package com.paeldav.backend.application.service.integration;

import com.paeldav.backend.application.dto.reporte.ReporteDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio para generar reportes en formato Excel.
 * Utiliza la librería Apache POI para crear documentos XLSX.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FormateadorExcelService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Convierte un reporte a formato Excel.
     *
     * @param reporte DTO del reporte a convertir
     * @return ByteArrayOutputStream con el contenido Excel
     */
    public ByteArrayOutputStream formatearReporte(ReporteDTO reporte) {
        log.info("Generando Excel para reporte ID: {}", reporte.getId());
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet("Reporte");
            
            // Crear estilos
            CellStyle headerStyle = crearEstiloEncabezado(workbook);
            CellStyle dataStyle = crearEstiloDatos(workbook);
            
            int rowNum = 0;
            
            // Encabezado
            XSSFRow row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue("REPORTE " + reporte.getTipo());
            row.getCell(0).setCellStyle(headerStyle);
            
            // Espacio
            rowNum++;
            
            // Información general
            agregarFila(sheet, rowNum++, "Campo", "Valor", headerStyle);
            agregarFila(sheet, rowNum++, "ID", String.valueOf(reporte.getId()), dataStyle);
            agregarFila(sheet, rowNum++, "Tipo", reporte.getTipo().toString(), dataStyle);
            agregarFila(sheet, rowNum++, "Generado por", reporte.getGeneradoPorNombre(), dataStyle);
            agregarFila(sheet, rowNum++, "Fecha de generación", 
                    reporte.getFechaGeneracion().format(FORMATO_FECHA), dataStyle);
            agregarFila(sheet, rowNum++, "Inicio de rango", 
                    reporte.getFechaInicioRango().format(FORMATO_FECHA), dataStyle);
            agregarFila(sheet, rowNum++, "Fin de rango", 
                    reporte.getFechaFinRango().format(FORMATO_FECHA), dataStyle);
            
            if (reporte.getNumeroRegistros() != null) {
                agregarFila(sheet, rowNum++, "Número de registros", 
                        String.valueOf(reporte.getNumeroRegistros()), dataStyle);
            }
            
            // Descripción
            rowNum++;
            agregarFila(sheet, rowNum++, "DESCRIPCIÓN", "", headerStyle);
            agregarFila(sheet, rowNum++, "", 
                    reporte.getDescripcion() != null ? reporte.getDescripcion() : "N/A", dataStyle);
            
            // Datos agregados
            if (reporte.getDatosAgregados() != null && !reporte.getDatosAgregados().isEmpty()) {
                rowNum++;
                agregarFila(sheet, rowNum++, "DATOS AGREGADOS", "", headerStyle);
                agregarFila(sheet, rowNum++, "", reporte.getDatosAgregados(), dataStyle);
            }
            
            // Observaciones
            if (reporte.getObservaciones() != null && !reporte.getObservaciones().isEmpty()) {
                rowNum++;
                agregarFila(sheet, rowNum++, "OBSERVACIONES", "", headerStyle);
                agregarFila(sheet, rowNum++, "", reporte.getObservaciones(), dataStyle);
            }
            
            // Ajustar ancho de columnas
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            
            workbook.write(baos);
            log.info("Excel generado exitosamente para reporte ID: {}", reporte.getId());
        } catch (Exception e) {
            log.error("Error al generar Excel para reporte ID: {}", reporte.getId(), e);
            throw new RuntimeException("Error al generar Excel: " + e.getMessage(), e);
        }
        
        return baos;
    }

    /**
     * Convierte múltiples reportes a formato Excel en una hoja por reporte.
     *
     * @param reportes Lista de DTOs de reportes
     * @return ByteArrayOutputStream con el contenido Excel
     */
    public ByteArrayOutputStream formatearMultiples(List<ReporteDTO> reportes) {
        log.info("Generando Excel para {} reportes", reportes.size());
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            CellStyle headerStyle = crearEstiloEncabezado(workbook);
            CellStyle dataStyle = crearEstiloDatos(workbook);
            
            // Crear hoja de resumen
            XSSFSheet resumen = workbook.createSheet("Resumen");
            int rowNum = 0;
            
            XSSFRow row = resumen.createRow(rowNum++);
            row.createCell(0).setCellValue("REPORTE CONSOLIDADO");
            row.getCell(0).setCellStyle(headerStyle);
            
            rowNum++;
            agregarFila(resumen, rowNum++, "Total de reportes", String.valueOf(reportes.size()), dataStyle);
            rowNum++;
            
            agregarFila(resumen, rowNum++, "ID", "Tipo", headerStyle);
            for (ReporteDTO reporte : reportes) {
                agregarFila(resumen, rowNum++, 
                        String.valueOf(reporte.getId()), 
                        reporte.getTipo().toString(), 
                        dataStyle);
            }
            
            resumen.autoSizeColumn(0);
            resumen.autoSizeColumn(1);
            
            // Crear una hoja por reporte
            for (int i = 0; i < reportes.size(); i++) {
                ReporteDTO reporte = reportes.get(i);
                XSSFSheet sheet = workbook.createSheet("Reporte_" + (i + 1));
                
                rowNum = 0;
                row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue("REPORTE " + reporte.getTipo());
                row.getCell(0).setCellStyle(headerStyle);
                
                rowNum++;
                agregarFila(sheet, rowNum++, "Campo", "Valor", headerStyle);
                agregarFila(sheet, rowNum++, "ID", String.valueOf(reporte.getId()), dataStyle);
                agregarFila(sheet, rowNum++, "Tipo", reporte.getTipo().toString(), dataStyle);
                agregarFila(sheet, rowNum++, "Generado por", reporte.getGeneradoPorNombre(), dataStyle);
                agregarFila(sheet, rowNum++, "Fecha", 
                        reporte.getFechaGeneracion().format(FORMATO_FECHA), dataStyle);
                agregarFila(sheet, rowNum++, "Descripción", 
                        reporte.getDescripcion() != null ? reporte.getDescripcion() : "N/A", dataStyle);
                
                sheet.autoSizeColumn(0);
                sheet.autoSizeColumn(1);
            }
            
            workbook.write(baos);
            log.info("Excel consolidado generado exitosamente para {} reportes", reportes.size());
        } catch (Exception e) {
            log.error("Error al generar Excel consolidado", e);
            throw new RuntimeException("Error al generar Excel consolidado: " + e.getMessage(), e);
        }
        
        return baos;
    }

    private CellStyle crearEstiloEncabezado(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeight((short) 220);
        style.setFont(font);
        style.setFillForegroundColor((short) 200);
        return style;
    }

    private CellStyle crearEstiloDatos(XSSFWorkbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeight((short) 200);
        style.setFont(font);
        return style;
    }

    private void agregarFila(XSSFSheet sheet, int rowNum, String col1, String col2, CellStyle style) {
        XSSFRow row = sheet.createRow(rowNum);
        XSSFCell cell1 = row.createCell(0);
        cell1.setCellValue(col1);
        cell1.setCellStyle(style);
        XSSFCell cell2 = row.createCell(1);
        cell2.setCellValue(col2);
        cell2.setCellStyle(style);
    }
}
