package com.paeldav.backend.application.dto.pago;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO extendido para procesar notificaciones de pago desde MercadoPago.
 * Proporciona una estructura más completa que WebhookMercadoPagoDTO.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentWebhookDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("payment_id")
    private Long paymentId;

    @JsonProperty("status")
    private String status; // approved, rejected, cancelled, pending, etc.

    @JsonProperty("external_reference")
    private String externalReference; // Referencia interna (flight ID)

    @JsonProperty("transaction_amount")
    private Double transactionAmount;

    @JsonProperty("currency_id")
    private String currencyId;

    @JsonProperty("payer_email")
    private String payerEmail;

    @JsonProperty("payer_id")
    private Long payerId;

    @JsonProperty("merchant_order_id")
    private Long merchantOrderId;

    @JsonProperty("date_created")
    private LocalDateTime dateCreated;

    @JsonProperty("date_approved")
    private LocalDateTime dateApproved;

    @JsonProperty("date_last_updated")
    private LocalDateTime dateLastUpdated;

    @JsonProperty("reason_code")
    private String reasonCode;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("operation_type")
    private String operationType; // regular_payment, money_transfer, etc.

    @JsonProperty("payment_method")
    private PaymentMethodDTO paymentMethod;

    @JsonProperty("fee_details")
    private FeeDetailsDTO feeDetails;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentMethodDTO {
        @JsonProperty("type")
        private String type;

        @JsonProperty("id")
        private String id;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FeeDetailsDTO {
        @JsonProperty("type")
        private String type;

        @JsonProperty("amount")
        private Double amount;
    }
}
