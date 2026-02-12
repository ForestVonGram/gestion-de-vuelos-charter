package com.paeldav.backend.infraestructure.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ValidationErrorResponse {
    private String code;
    private String message;
    private List<FieldErrorDetail> fieldErrors;
    private LocalDateTime timestamp;
    private String path;
    private int status;

    public ValidationErrorResponse(String message, List<FieldErrorDetail> fieldErrors) {
        this.code = "VALIDATION_ERROR";
        this.message = message;
        this.fieldErrors = fieldErrors;
        this.status = 400;
        this.timestamp = LocalDateTime.now();
    }
}
