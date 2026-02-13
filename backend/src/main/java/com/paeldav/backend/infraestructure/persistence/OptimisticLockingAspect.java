package com.paeldav.backend.infraestructure.persistence;

import com.paeldav.backend.domain.exception.ConcurrentDataModificationException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import jakarta.persistence.OptimisticLockException;
import org.hibernate.exception.GenericJDBCException;

/**
 * Aspecto que intercepta métodos anotados con @OptimisticLock
 * para detectar y manejar conflictos de concurrencia.
 */
@Slf4j
@Aspect
@Component
public class OptimisticLockingAspect {

    private static final int MAX_RETRY_ATTEMPTS = 3;
    private static final long RETRY_DELAY_MS = 100;

    /**
     * Intercepta métodos anotados con @OptimisticLock e implementa reintentos automáticos
     * para manejar conflictos de concurrencia.
     *
     * @param joinPoint punto de unión del aspecto
     * @param optimisticLock anotación con configuración
     * @return resultado de la ejecución del método
     * @throws Throwable si el método falla después de reintentos
     */
    @Around("@annotation(optimisticLock)")
    public Object handleOptimisticLocking(ProceedingJoinPoint joinPoint, OptimisticLock optimisticLock) throws Throwable {
        int attempts = 0;
        ConcurrentDataModificationException lastException = null;

        while (attempts < MAX_RETRY_ATTEMPTS) {
            try {
                log.debug("Ejecutando método con control de concurrencia optimista - Intento {}/{}", 
                    attempts + 1, MAX_RETRY_ATTEMPTS);
                return joinPoint.proceed();

            } catch (OptimisticLockException | org.hibernate.StaleObjectStateException e) {
                attempts++;
                lastException = new ConcurrentDataModificationException(
                    optimisticLock.message(),
                    e
                );

                if (attempts >= MAX_RETRY_ATTEMPTS) {
                    log.error("Fallo del control de concurrencia después de {} reintentos", MAX_RETRY_ATTEMPTS, e);
                    throw lastException;
                }

                log.warn("Conflicto de concurrencia detectado. Reintentando ({}/{})...", 
                    attempts, MAX_RETRY_ATTEMPTS);

                try {
                    Thread.sleep(RETRY_DELAY_MS * attempts);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw lastException;
                }

            } catch (GenericJDBCException e) {
                // Manejo de excepciones JDBC relacionadas con versionado
                if (e.getCause() != null && 
                    e.getCause().getMessage() != null &&
                    e.getCause().getMessage().contains("version")) {
                    
                    attempts++;
                    lastException = new ConcurrentDataModificationException(
                        optimisticLock.message(),
                        e
                    );

                    if (attempts >= MAX_RETRY_ATTEMPTS) {
                        log.error("Fallo del control de concurrencia JDBC después de {} reintentos", MAX_RETRY_ATTEMPTS, e);
                        throw lastException;
                    }

                    log.warn("Conflicto de concurrencia JDBC detectado. Reintentando ({}/{})...", 
                        attempts, MAX_RETRY_ATTEMPTS);

                    try {
                        Thread.sleep(RETRY_DELAY_MS * attempts);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw lastException;
                    }
                } else {
                    throw e;
                }
            }
        }

        if (lastException != null) {
            throw lastException;
        }

        return joinPoint.proceed();
    }
}
