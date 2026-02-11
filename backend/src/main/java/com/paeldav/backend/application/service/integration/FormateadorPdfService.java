package com.paeldav.backend.application.service.integration;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.paeldav.backend.application.dto.reporte.ReporteDTO;
import com.paeldav.backend.domain.enums.FormatoExportacion;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Servicio para generar reportes en formato PDF.
 * Utiliza la librería iText7 para crear documentos PDF estructurados.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FormateadorPdfService {

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /**
     * Convierte un reporte a formato PDF.
     *
     * @param reporte DTO del reporte a convertir
     * @return ByteArrayOutputStream con el contenido PDF
     */
    public ByteArrayOutputStream formatearReporte(ReporteDTO reporte) {
        log.info("Generando PDF para reporte ID: {}", reporte.getId());
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Título
            document.add(new Paragraph("REPORTE " + reporte.getTipo())
                    .setFontSize(18)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));

            // Información general
            document.add(new Paragraph("\nInformación del Reporte").setBold().setFontSize(12));
            document.add(new Paragraph("ID: " + reporte.getId()));
            document.add(new Paragraph("Tipo: " + reporte.getTipo()));
            document.add(new Paragraph("Generado por: " + reporte.getGeneradoPorNombre()));
            document.add(new Paragraph("Fecha de generación: " + reporte.getFechaGeneracion().format(FORMATO_FECHA)));
            document.add(new Paragraph("Rango: " + reporte.getFechaInicioRango().format(FORMATO_FECHA) 
                    + " a " + reporte.getFechaFinRango().format(FORMATO_FECHA)));
            
            if (reporte.getNumeroRegistros() != null) {
                document.add(new Paragraph("Registros: " + reporte.getNumeroRegistros()));
            }

            // Descripción
            document.add(new Paragraph("\nDescripción").setBold().setFontSize(12));
            document.add(new Paragraph(reporte.getDescripcion() != null ? reporte.getDescripcion() : "N/A"));

            // Datos agregados como tabla (si existen)
            if (reporte.getDatosAgregados() != null && !reporte.getDatosAgregados().isEmpty()) {
                document.add(new Paragraph("\nDatos Agregados").setBold().setFontSize(12));
                Table table = new Table(2);
                table.addCell(new Cell().add(new Paragraph("Campo").setBold()));
                table.addCell(new Cell().add(new Paragraph("Valor").setBold()));
                
                // Parse JSON simple (para casos básicos)
                String datos = reporte.getDatosAgregados();
                table.addCell(new Cell().add(new Paragraph("Contenido")));
                table.addCell(new Cell().add(new Paragraph(datos)));
                
                document.add(table);
            }

            // Observaciones
            if (reporte.getObservaciones() != null && !reporte.getObservaciones().isEmpty()) {
                document.add(new Paragraph("\nObservaciones").setBold().setFontSize(12));
                document.add(new Paragraph(reporte.getObservaciones()));
            }

            document.close();
            log.info("PDF generado exitosamente para reporte ID: {}", reporte.getId());
        } catch (Exception e) {
            log.error("Error al generar PDF para reporte ID: {}", reporte.getId(), e);
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        }

        return baos;
    }

    /**
     * Convierte múltiples reportes a formato PDF en un único documento.
     *
     * @param reportes Lista de DTOs de reportes
     * @return ByteArrayOutputStream con el contenido PDF
     */
    public ByteArrayOutputStream formatearMultiples(List<ReporteDTO> reportes) {
        log.info("Generando PDF para {} reportes", reportes.size());
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);

            // Página de cubierta
            document.add(new Paragraph("REPORTE CONSOLIDADO")
                    .setFontSize(22)
                    .setBold()
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Total de reportes: " + reportes.size())
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));

            // Agregar cada reporte
            for (int i = 0; i < reportes.size(); i++) {
                ReporteDTO reporte = reportes.get(i);
                
                document.add(new Paragraph("REPORTE " + (i + 1) + ": " + reporte.getTipo())
                        .setFontSize(14)
                        .setBold());
                document.add(new Paragraph("ID: " + reporte.getId()));
                document.add(new Paragraph("Generado por: " + reporte.getGeneradoPorNombre()));
                document.add(new Paragraph("Fecha: " + reporte.getFechaGeneracion().format(FORMATO_FECHA)));
                document.add(new Paragraph(reporte.getDescripcion() != null ? reporte.getDescripcion() : ""));
                document.add(new Paragraph("\n"));
                
                // Agregar nueva página entre reportes (excepto el último)
                if (i < reportes.size() - 1) {
                    document.add(new com.itextpdf.layout.element.AreaBreak());
                }
            }

            document.close();
            log.info("PDF consolidado generado exitosamente para {} reportes", reportes.size());
        } catch (Exception e) {
            log.error("Error al generar PDF consolidado", e);
            throw new RuntimeException("Error al generar PDF consolidado: " + e.getMessage(), e);
        }

        return baos;
    }
}
