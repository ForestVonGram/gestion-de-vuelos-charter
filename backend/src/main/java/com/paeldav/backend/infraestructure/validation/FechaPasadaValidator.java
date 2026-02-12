package com.paeldav.backend.infraestructure.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Validador que verifica que una fecha sea pasada.
 */
public class FechaPasadaValidator implements ConstraintValidator<ValidFechaPasada, Object> {
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (value instanceof LocalDate) {
            return ((LocalDate) value).isBefore(LocalDate.now());
        } else if (value instanceof LocalDateTime) {
            return ((LocalDateTime) value).isBefore(LocalDateTime.now());
        }
        return true;
    }
}
