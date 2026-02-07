package com.paeldav.backend.application.dto.tripulante;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO para la creación de un nuevo Tripulante.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TripulanteCreateDTO {

    @NotNull(message = "El usuario es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "El número de licencia es obligatorio")
    private String numeroLicencia;

    private String tipoLicencia;

    private LocalDate fechaExpedicionLicencia;

    private LocalDate fechaVencimientoLicencia;

    private Boolean esPiloto;

    private String certificaciones;

    private String observaciones;
}
