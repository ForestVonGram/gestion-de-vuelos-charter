package com.paeldav.backend.application.dto.vuelo;

import lombok.*;

/**
 * DTO para aprobar una solicitud de vuelo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudAprobacionDTO {

    /**
     * Motivo o comentario de aprobación (opcional).
     */
    private String motivo;

    /**
     * Costo estimado del vuelo (opcional, puede asignarse al aprobar).
     */
    private Double costoEstimado;
}
