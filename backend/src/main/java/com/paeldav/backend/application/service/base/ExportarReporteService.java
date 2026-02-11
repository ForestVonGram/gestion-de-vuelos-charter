package com.paeldav.backend.application.service.base;

import com.paeldav.backend.domain.enums.FormatoExportacion;
import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Interfaz para servicio de exportación de reportes.
 * Define métodos para exportar reportes en diferentes formatos.
 */
public interface ExportarReporteService {

    /**
     * Exporta un reporte a un formato especificado.
     *
     * @param reporteId ID del reporte a exportar
     * @param formato Formato de exportación (PDF, EXCEL, CSV)
     * @return ByteArrayOutputStream con el contenido del archivo exportado
     */
    ByteArrayOutputStream exportarReporte(Long reporteId, FormatoExportacion formato);

    /**
     * Exporta múltiples reportes en un mismo archivo.
     *
     * @param reporteIds Lista de IDs de reportes a exportar
     * @param formato Formato de exportación
     * @return ByteArrayOutputStream con el contenido del archivo exportado
     */
    ByteArrayOutputStream exportarMultiples(List<Long> reporteIds, FormatoExportacion formato);

    /**
     * Obtiene el nombre de archivo recomendado para una exportación.
     *
     * @param reporteId ID del reporte
     * @param formato Formato de exportación
     * @return Nombre de archivo con extensión apropiada
     */
    String obtenerNombreArchivo(Long reporteId, FormatoExportacion formato);

    /**
     * Valida si el reporte puede ser exportado en el formato especificado.
     *
     * @param reporteId ID del reporte
     * @param formato Formato a validar
     * @return true si es válido, false en caso contrario
     */
    boolean validarExportacion(Long reporteId, FormatoExportacion formato);
}
