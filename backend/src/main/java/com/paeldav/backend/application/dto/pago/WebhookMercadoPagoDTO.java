package com.paeldav.backend.application.dto.pago;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * DTO para procesar notificaciones de MercadoPago.
 * Representa la estructura de los webhooks enviados por MercadoPago.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookMercadoPagoDTO {

    @JsonProperty("id")
    private String id;

    @JsonProperty("live_mode")
    private Boolean liveMode;

    @JsonProperty("type")
    private String type; // payment.created, payment.updated, etc.

    @JsonProperty("date_created")
    private String dateCreated;

    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("resource")
    private String resource;

    @JsonProperty("data")
    private WebhookDataDTO data;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class WebhookDataDTO {

        @JsonProperty("id")
        private String id;
    }
}
