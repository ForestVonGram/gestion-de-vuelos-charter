package com.paeldav.backend.domain.enums;

/**
 * Enum que define los formatos de exportación soportados para reportes.
 * PDF: Adobe Portable Document Format
 * EXCEL: Microsoft Excel (.xlsx)
 * CSV: Comma-Separated Values
 */
public enum FormatoExportacion {
    PDF("application/pdf", ".pdf"),
    EXCEL("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", ".xlsx"),
    CSV("text/csv", ".csv");

    private final String mimeType;
    private final String extension;

    FormatoExportacion(String mimeType, String extension) {
        this.mimeType = mimeType;
        this.extension = extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension;
    }
}
