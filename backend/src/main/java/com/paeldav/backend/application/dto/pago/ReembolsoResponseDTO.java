package com.paeldav.backend.application.dto.pago;

import com.paeldav.backend.domain.enums.EstadoPago;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para un reembolso procesado.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReembolsoResponseDTO {

    private Long pagoId;

    private Long refundId;

    private Double montoOriginal;

    private Double montoReembolsado;

    private EstadoPago estadoAnterior;

    private EstadoPago estadoNuevo;

    private String motivo;

    private String referenciaMercadoPago;

    private LocalDateTime fechaReembolso;

    private String observaciones;

    private Boolean exitoso;
}
