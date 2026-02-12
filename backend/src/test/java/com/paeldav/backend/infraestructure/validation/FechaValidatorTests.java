package com.paeldav.backend.infraestructure.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests de Validadores de Fecha")
class FechaValidatorTests {

    private FechaFuturaValidator fechaFuturaValidator;
    private FechaPasadaValidator fechaPasadaValidator;
    private ConstraintValidatorContext context;

    @BeforeEach
    void setUp() {
        fechaFuturaValidator = new FechaFuturaValidator();
        fechaPasadaValidator = new FechaPasadaValidator();
        context = Mockito.mock(ConstraintValidatorContext.class);
    }

    // ============= Pruebas para FechaFuturaValidator =============

    @Test
    @DisplayName("FechaFutura debe aceptar fecha futura con LocalDate")
    void testFechaFuturaWithLocalDateFuture() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        boolean isValid = fechaFuturaValidator.isValid(futureDate, context);
        assertTrue(isValid, "Debe aceptar fecha futura");
    }

    @Test
    @DisplayName("FechaFutura debe rechazar fecha pasada con LocalDate")
    void testFechaFuturaWithLocalDatePast() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        boolean isValid = fechaFuturaValidator.isValid(pastDate, context);
        assertFalse(isValid, "Debe rechazar fecha pasada");
    }

    @Test
    @DisplayName("FechaFutura debe rechazar fecha actual con LocalDate")
    void testFechaFuturaWithLocalDateToday() {
        LocalDate today = LocalDate.now();
        boolean isValid = fechaFuturaValidator.isValid(today, context);
        assertFalse(isValid, "Debe rechazar fecha actual");
    }

    @Test
    @DisplayName("FechaFutura debe aceptar fecha futura con LocalDateTime")
    void testFechaFuturaWithLocalDateTimeFuture() {
        LocalDateTime futureDateTime = LocalDateTime.now().plusHours(1);
        boolean isValid = fechaFuturaValidator.isValid(futureDateTime, context);
        assertTrue(isValid, "Debe aceptar fecha-hora futura");
    }

    @Test
    @DisplayName("FechaFutura debe rechazar fecha pasada con LocalDateTime")
    void testFechaFuturaWithLocalDateTimePast() {
        LocalDateTime pastDateTime = LocalDateTime.now().minusHours(1);
        boolean isValid = fechaFuturaValidator.isValid(pastDateTime, context);
        assertFalse(isValid, "Debe rechazar fecha-hora pasada");
    }

    @Test
    @DisplayName("FechaFutura debe aceptar null")
    void testFechaFuturaWithNull() {
        boolean isValid = fechaFuturaValidator.isValid(null, context);
        assertTrue(isValid, "Debe aceptar null");
    }

    @Test
    @DisplayName("FechaFutura debe aceptar tipo desconocido")
    void testFechaFuturaWithUnknownType() {
        String unknownType = "not a date";
        boolean isValid = fechaFuturaValidator.isValid(unknownType, context);
        assertTrue(isValid, "Debe aceptar tipos desconocidos");
    }

    @Test
    @DisplayName("FechaFutura debe aceptar fecha muy lejana")
    void testFechaFuturaWithDistantFuture() {
        LocalDate veryFarFuture = LocalDate.now().plusYears(100);
        boolean isValid = fechaFuturaValidator.isValid(veryFarFuture, context);
        assertTrue(isValid, "Debe aceptar fecha muy lejana en el futuro");
    }

    // ============= Pruebas para FechaPasadaValidator =============

    @Test
    @DisplayName("FechaPasada debe aceptar fecha pasada con LocalDate")
    void testFechaPasadaWithLocalDatePast() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        boolean isValid = fechaPasadaValidator.isValid(pastDate, context);
        assertTrue(isValid, "Debe aceptar fecha pasada");
    }

    @Test
    @DisplayName("FechaPasada debe rechazar fecha futura con LocalDate")
    void testFechaPasadaWithLocalDateFuture() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        boolean isValid = fechaPasadaValidator.isValid(futureDate, context);
        assertFalse(isValid, "Debe rechazar fecha futura");
    }

    @Test
    @DisplayName("FechaPasada debe rechazar fecha actual con LocalDate")
    void testFechaPasadaWithLocalDateToday() {
        LocalDate today = LocalDate.now();
        boolean isValid = fechaPasadaValidator.isValid(today, context);
        assertFalse(isValid, "Debe rechazar fecha actual");
    }

    @Test
    @DisplayName("FechaPasada debe aceptar fecha pasada con LocalDateTime")
    void testFechaPasadaWithLocalDateTimePast() {
        LocalDateTime pastDateTime = LocalDateTime.now().minusHours(1);
        boolean isValid = fechaPasadaValidator.isValid(pastDateTime, context);
        assertTrue(isValid, "Debe aceptar fecha-hora pasada");
    }

    @Test
    @DisplayName("FechaPasada debe rechazar fecha futura con LocalDateTime")
    void testFechaPasadaWithLocalDateTimeFuture() {
        LocalDateTime futureDateTime = LocalDateTime.now().plusHours(1);
        boolean isValid = fechaPasadaValidator.isValid(futureDateTime, context);
        assertFalse(isValid, "Debe rechazar fecha-hora futura");
    }

    @Test
    @DisplayName("FechaPasada debe aceptar null")
    void testFechaPasadaWithNull() {
        boolean isValid = fechaPasadaValidator.isValid(null, context);
        assertTrue(isValid, "Debe aceptar null");
    }

    @Test
    @DisplayName("FechaPasada debe aceptar tipo desconocido")
    void testFechaPasadaWithUnknownType() {
        String unknownType = "not a date";
        boolean isValid = fechaPasadaValidator.isValid(unknownType, context);
        assertTrue(isValid, "Debe aceptar tipos desconocidos");
    }

    @Test
    @DisplayName("FechaPasada debe aceptar fecha muy antigua")
    void testFechaPasadaWithDistantPast() {
        LocalDate veryOldDate = LocalDate.now().minusYears(100);
        boolean isValid = fechaPasadaValidator.isValid(veryOldDate, context);
        assertTrue(isValid, "Debe aceptar fecha muy antigua");
    }

    // ============= Pruebas Complementarias =============

    @Test
    @DisplayName("FechaFutura rechaza fecha hace un segundo")
    void testFechaFuturaWithOneSecondAgo() {
        LocalDateTime oneSecondAgo = LocalDateTime.now().minusSeconds(1);
        boolean isValid = fechaFuturaValidator.isValid(oneSecondAgo, context);
        assertFalse(isValid, "Debe rechazar un segundo atrás");
    }

    @Test
    @DisplayName("FechaPasada rechaza fecha dentro de un segundo")
    void testFechaPasadaWithOneSecondLater() {
        LocalDateTime oneSecondLater = LocalDateTime.now().plusSeconds(1);
        boolean isValid = fechaPasadaValidator.isValid(oneSecondLater, context);
        assertFalse(isValid, "Debe rechazar un segundo adelante");
    }

    @Test
    @DisplayName("Validadores son simétricos para fechas opuestas")
    void testValidatorsAreSymmetric() {
        LocalDate futureDate = LocalDate.now().plusDays(1);
        LocalDate pastDate = LocalDate.now().minusDays(1);

        assertTrue(fechaFuturaValidator.isValid(futureDate, context));
        assertFalse(fechaFuturaValidator.isValid(pastDate, context));

        assertFalse(fechaPasadaValidator.isValid(futureDate, context));
        assertTrue(fechaPasadaValidator.isValid(pastDate, context));
    }

    @Test
    @DisplayName("LocalDate y LocalDateTime tienen comportamiento consistente")
    void testConsistencyBetweenLocalDateAndDateTime() {
        LocalDate futureLocalDate = LocalDate.now().plusDays(1);
        LocalDateTime futureLocalDateTime = LocalDateTime.now().plusDays(1);

        assertTrue(fechaFuturaValidator.isValid(futureLocalDate, context));
        assertTrue(fechaFuturaValidator.isValid(futureLocalDateTime, context));

        LocalDate pastLocalDate = LocalDate.now().minusDays(1);
        LocalDateTime pastLocalDateTime = LocalDateTime.now().minusDays(1);

        assertTrue(fechaPasadaValidator.isValid(pastLocalDate, context));
        assertTrue(fechaPasadaValidator.isValid(pastLocalDateTime, context));
    }
}
