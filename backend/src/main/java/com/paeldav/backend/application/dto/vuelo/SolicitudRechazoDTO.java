package com.paeldav.backend.application.dto.vuelo;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO para rechazar una solicitud de vuelo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudRechazoDTO {

    /**
     * Motivo del rechazo (obligatorio).
     */
    @NotBlank(message = "El motivo del rechazo es obligatorio")
    private String motivo;
}
