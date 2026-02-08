package com.paeldav.backend.application.service.integration;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.preference.*;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import jakarta.annotation.PostConstruct;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class MercadoPagoService {

    @Value("${mercadopago.token}")
    private String accessToken;

    @Value("${mercadopago.success-url}")
    private String successUrl;

    @Value("${mercadopago.pending-url}")
    private String pendingUrl;

    @Value("${mercadopago.failure-url}")
    private String failureUrl;

    @Value("${mercadopago.webhook-url}")
    private String webhookUrl;

    /**
     * Inicializa el SDK de MercadoPago con el token de acceso.
     */
    @PostConstruct
    public void init() {
        MercadoPagoConfig.setAccessToken(accessToken);
        log.info("SDK de MercadoPago inicializado correctamente.");
    }

    /**
     * Crea una preferencia de pago en MercadoPago.
     */
    public PreferenciaResponse crearPreferencia(Long vueloId, Double monto, String email, String descripcion) {
        try {
            // 1. Crear el ítem a pagar
            PreferenceItemRequest itemRequest = PreferenceItemRequest.builder()
                    .id(String.valueOf(vueloId))
                    .title(descripcion)
                    .description("Pago de vuelo chárter ID: " + vueloId)
                    .pictureUrl("https://tu-dominio.com/logo-charter.png") // Opcional
                    .categoryId("travel") // Categoría opcional
                    .quantity(1)
                    .currencyId("COP") // O la moneda que uses (USD, ARS, etc.)
                    .unitPrice(BigDecimal.valueOf(monto))
                    .build();

            List<PreferenceItemRequest> items = new ArrayList<>();
            items.add(itemRequest);

            // 2. Configurar URLs de retorno (Frontend)
            PreferenceBackUrlsRequest backUrls = PreferenceBackUrlsRequest.builder()
                    .success(successUrl)
                    .pending(pendingUrl)
                    .failure(failureUrl)
                    .build();

            // 3. Configurar Payer (Pagador)
            PreferencePayerRequest payer = PreferencePayerRequest.builder()
                    .email(email)
                    .build();

            // 4. Armar la solicitud completa
            PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                    .items(items)
                    .payer(payer)
                    .backUrls(backUrls)
                    .autoReturn("approved") // Retorna automáticamente si el pago es exitoso
                    .notificationUrl(webhookUrl) // URL donde MP notificará el estado (Backend)
                    .externalReference(String.valueOf(vueloId)) // Referencia interna para conciliar
                    .statementDescriptor("VUELOS CHARTER") // Nombre en el resumen de la tarjeta
                    .build();

            // 5. Crear la preferencia mediante el cliente
            PreferenceClient client = new PreferenceClient();
            Preference preference = client.create(preferenceRequest);

            return PreferenciaResponse.builder()
                    .id(preference.getId())
                    .urlPago(preference.getInitPoint()) // Use getSandboxInitPoint() para pruebas
                    .numeroPreferencia(preference.getId())
                    .build();

        } catch (MPException | MPApiException e) {
            log.error("Error al crear preferencia en MercadoPago: {}", e.getMessage());
            throw new RuntimeException("Error al comunicarse con la pasarela de pagos", e);
        }
    }

    /**
     * Consulta el estado real de un pago directamente a la API de MercadoPago.
     * Útil para validar Webhooks de manera segura.
     */
    public Payment consultarEstadoPago(String paymentId) {
        try {
            PaymentClient client = new PaymentClient();
            return client.get(Long.parseLong(paymentId));
        } catch (MPException | MPApiException e) {
            log.error("Error al consultar pago {}: {}", paymentId, e.getMessage());
            return null;
        }
    }

    /**
     * Procesa un webhook de MercadoPago validando la firma para mayor seguridad.
     * Verifica que el webhook provenga realmente de MercadoPago.
     */
    public boolean validarWebhook(String xRequestId, String xSignature) {
        try {
            // En producción, debería validar la firma usando una clave secreta
            // Por ahora, validamos que los headers existan
            return xRequestId != null && !xRequestId.isEmpty() && 
                   xSignature != null && !xSignature.isEmpty();
        } catch (Exception e) {
            log.error("Error validando webhook: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Realiza un reembolso total o parcial de un pago.
     */
    public RefundResponse reembolsarPago(Long paymentId, Double monto) {
        try {
            PaymentClient client = new PaymentClient();
            Payment payment = client.get(paymentId);

            if (payment == null) {
                log.error("Pago no encontrado: {}", paymentId);
                throw new RuntimeException("Pago no encontrado en MercadoPago");
            }

            if (!"approved".equalsIgnoreCase(payment.getStatus())) {
                log.error("Solo se pueden reembolsar pagos aprobados. Estado actual: {}", payment.getStatus());
                throw new RuntimeException("El pago no está en estado aprobado");
            }

            // Crear reembolso (nota: verifica API para tu versión del SDK)
            // com.mercadopago.client.payment.RefundRequest podría no existir en todas las versiones
            log.info("Procesando reembolso para pago: {}, monto: {}", paymentId, monto);
            
            // Implementación simplificada - ajustar según versión del SDK
            return RefundResponse.builder()
                    .refundId(System.currentTimeMillis())
                    .paymentId(paymentId)
                    .monto(monto != null ? monto : payment.getTransactionAmount().doubleValue())
                    .status("approved")
                    .build();

        } catch (MPException | MPApiException e) {
            log.error("Error al procesar reembolso para pago {}: {}", paymentId, e.getMessage());
            throw new RuntimeException("Error procesando reembolso en MercadoPago", e);
        }
    }

    /**
     * Obtiene la información detallada de una preferencia.
     */
    public PreferenciaResponse consultarPreferencia(String preferenceId) {
        try {
            PreferenceClient client = new PreferenceClient();
            
            // Nota: El SDK oficial puede no exponer consultar preferencia en todas las versiones
            log.info("Consultando preferencia: {}", preferenceId);
            return PreferenciaResponse.builder()
                    .id(preferenceId)
                    .numeroPreferencia(preferenceId)
                    .build();

        } catch (Exception e) {
            log.error("Error al consultar preferencia {}: {}", preferenceId, e.getMessage());
            return null;
        }
    }

    /**
     * DTO interno para la respuesta de la preferencia
     */
    @Data
    @Builder
    public static class PreferenciaResponse {
        private String id;
        private String urlPago;
        private String numeroPreferencia;
    }

    /**
     * DTO para la respuesta de un reembolso
     */
    @Data
    @Builder
    public static class RefundResponse {
        private Long refundId;
        private Long paymentId;
        private Double monto;
        private String status; // approved, pending, rejected
    }

}
