package com.paeldav.backend.application.service.integration;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para el servicio MercadoPagoService.
 * Nota: Estas pruebas están diseñadas para ejecutarse con token de prueba de MercadoPago.
 */
@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@DisplayName("Pruebas unitarias de MercadoPagoService")
@Disabled("Requiere token de MercadoPago válido y conexión a Internet")
class MercadoPagoServiceTest {

    @Autowired
    private MercadoPagoService mercadoPagoService;

    private static final Long TEST_FLIGHT_ID = 1L;
    private static final Double TEST_AMOUNT = 100000.0;
    private static final String TEST_EMAIL = "test@example.com";
    private static final String TEST_DESCRIPTION = "Prueba de vuelo chárter";

    @BeforeEach
    void setUp() {
        assertNotNull(mercadoPagoService, "MercadoPagoService debe ser inyectado");
    }

    @Test
    @DisplayName("Debe crear una preferencia de pago válida")
    void testCrearPreferencia_Success() {
        // Arrange
        // Act
        MercadoPagoService.PreferenciaResponse response = mercadoPagoService.crearPreferencia(
                TEST_FLIGHT_ID,
                TEST_AMOUNT,
                TEST_EMAIL,
                TEST_DESCRIPTION
        );

        // Assert
        assertNotNull(response, "La respuesta no debe ser nula");
        assertNotNull(response.getId(), "El ID debe estar presente");
        assertNotNull(response.getUrlPago(), "La URL de pago debe estar presente");
        assertNotNull(response.getNumeroPreferencia(), "El número de preferencia debe estar presente");
        assertTrue(response.getUrlPago().contains("mercadopago"), "La URL debe contener referencia a MercadoPago");
    }

    @Test
    @DisplayName("Debe validar webhook con headers válidos")
    void testValidarWebhook_Success() {
        // Arrange
        String xRequestId = "123456789";
        String xSignature = "abcdefgh";

        // Act
        boolean result = mercadoPagoService.validarWebhook(xRequestId, xSignature);

        // Assert
        assertTrue(result, "La validación debe retornar true con headers válidos");
    }

    @Test
    @DisplayName("Debe rechazar webhook sin headers requeridos")
    void testValidarWebhook_MissingHeaders() {
        // Act
        boolean result1 = mercadoPagoService.validarWebhook(null, "signature");
        boolean result2 = mercadoPagoService.validarWebhook("id", null);

        // Assert
        assertFalse(result1, "Debe fallar sin xRequestId");
        assertFalse(result2, "Debe fallar sin xSignature");
    }

    @Test
    @DisplayName("Debe rechazar webhook con headers vacíos")
    void testValidarWebhook_EmptyHeaders() {
        // Act
        boolean result1 = mercadoPagoService.validarWebhook("", "signature");
        boolean result2 = mercadoPagoService.validarWebhook("id", "");

        // Assert
        assertFalse(result1, "Debe fallar con xRequestId vacío");
        assertFalse(result2, "Debe fallar con xSignature vacío");
    }

    @Test
    @DisplayName("Debe intentar consultar estado de un pago")
    void testConsultarEstadoPago() {
        // Arrange
        String paymentId = "9999999999"; // ID de prueba

        // Act
        Payment payment = mercadoPagoService.consultarEstadoPago(paymentId);

        // Assert
        // El resultado puede ser null si el pago no existe en sandbox
        // Simplemente verificamos que el método no lanza excepciones
        assertDoesNotThrow(() -> mercadoPagoService.consultarEstadoPago(paymentId),
                "No debe lanzar excepción");
    }

    @Test
    @DisplayName("Debe construir PreferenciaResponse correctamente")
    void testPreferenciaResponseBuilder() {
        // Act
        MercadoPagoService.PreferenciaResponse response = MercadoPagoService.PreferenciaResponse.builder()
                .id("pref123")
                .urlPago("https://mercadopago.com/pref123")
                .numeroPreferencia("pref123")
                .build();

        // Assert
        assertNotNull(response);
        assertEquals("pref123", response.getId());
        assertEquals("https://mercadopago.com/pref123", response.getUrlPago());
        assertEquals("pref123", response.getNumeroPreferencia());
    }

    @Test
    @DisplayName("Debe construir RefundResponse correctamente")
    void testRefundResponseBuilder() {
        // Act
        MercadoPagoService.RefundResponse response = MercadoPagoService.RefundResponse.builder()
                .refundId(1L)
                .paymentId(999L)
                .monto(50000.0)
                .status("approved")
                .build();

        // Assert
        assertNotNull(response);
        assertEquals(1L, response.getRefundId());
        assertEquals(999L, response.getPaymentId());
        assertEquals(50000.0, response.getMonto());
        assertEquals("approved", response.getStatus());
    }
}
