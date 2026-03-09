package com.paeldav.backend.infraestructure.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Clase que representa la estructura de una respuesta de error de validación.
 * Se utiliza para devolver información detallada sobre errores de validación de campos.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationErrorResponse {
    private String code; // Código del error (ej: "VALIDATION_ERROR")
    private String message; // Mensaje descriptivo del error
    private List<FieldErrorDetail> fieldErrors; // Lista de errores específicos por campo
    private LocalDateTime timestamp; // Fecha y hora en que ocurrió el error
    private String path; // Ruta de la petición que generó el error
    private int status; // Código de estado HTTP (ej: 400, 404)

    /**
     * Constructor simplificado para crear una respuesta de error de validación.
     * @param message mensaje descriptivo del error
     * @param fieldErrors lista de errores específicos por campo
     */
    public ValidationErrorResponse(String message, List<FieldErrorDetail> fieldErrors) {
        this.code = "VALIDATION_ERROR";
        this.message = message;
        this.fieldErrors = fieldErrors;
        this.status = 400;
        this.timestamp = LocalDateTime.now();
    }
}