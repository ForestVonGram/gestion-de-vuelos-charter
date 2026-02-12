package com.paeldav.backend.infraestructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Anotación para validar que una fecha sea pasada (anterior a la fecha actual).
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FechaPasadaValidator.class)
@Documented
public @interface ValidFechaPasada {
    String message() default "La fecha debe ser anterior a la fecha actual";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
