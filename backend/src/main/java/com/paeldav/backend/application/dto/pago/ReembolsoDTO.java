package com.paeldav.backend.application.dto.pago;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO para solicitar un reembolso de pago.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReembolsoDTO {

    @NotNull(message = "El ID del pago es obligatorio")
    private Long pagoId;

    @NotBlank(message = "El motivo del reembolso es obligatorio")
    private String motivo;

    private String observaciones;

    /**
     * Monto a reembolsar (si es null, se reembolsa el monto total del pago)
     */
    private Double montoReembolso;
}
