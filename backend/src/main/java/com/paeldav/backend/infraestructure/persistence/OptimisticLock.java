package com.paeldav.backend.infraestructure.persistence;

import java.lang.annotation.*;

/**
 * Anotación para marcar métodos que deben aplicar control de concurrencia optimista.
 * Se utiliza junto con OptimisticLockingAspect para detectar y manejar conflictos de versión.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OptimisticLock {

    /**
     * Mensaje de error personalizado a mostrar en caso de conflicto.
     */
    String message() default "Conflicto de concurrencia: la entidad fue modificada por otro usuario. Por favor, recargue e intente nuevamente.";

    /**
     * Indicar si se debe registrar el conflicto en auditoría.
     */
    boolean audit() default true;
}
