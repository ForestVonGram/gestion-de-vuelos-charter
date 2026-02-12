package com.paeldav.backend.infraestructure.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Anotación para validar que una fecha sea futura (posterior a la fecha actual).
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = FechaFuturaValidator.class)
@Documented
public @interface ValidFechaFutura {
    String message() default "La fecha debe ser posterior a la fecha actual";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
