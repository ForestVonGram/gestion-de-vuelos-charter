package com.paeldav.backend.application.dto.tripulante;

import com.paeldav.backend.domain.enums.EstadoTripulante;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO para la actualización de un Tripulante existente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripulanteUpdateDTO {

    private String tipoLicencia;

    private LocalDate fechaExpedicionLicencia;

    private LocalDate fechaVencimientoLicencia;

    private String certificaciones;

    private String observaciones;

    private EstadoTripulante estado;

    private Boolean esPiloto;
}
