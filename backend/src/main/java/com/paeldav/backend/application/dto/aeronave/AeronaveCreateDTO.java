package com.paeldav.backend.application.dto.aeronave;

import com.paeldav.backend.domain.enums.EstadoAeronave;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO para la creación de una nueva Aeronave.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AeronaveCreateDTO {

    @NotBlank(message = "La matrícula es obligatoria")
    private String matricula;

    @NotBlank(message = "El modelo es obligatorio")
    private String modelo;

    private String fabricante;

    @Positive(message = "La capacidad de pasajeros debe ser positiva")
    private Integer capacidadPasajeros;

    @Positive(message = "La capacidad de tripulación debe ser positiva")
    private Integer capacidadTripulacion;

    private Double autonomiaKm;

    private Double velocidadCruceroKmh;

    private LocalDate fechaFabricacion;

    private LocalDate fechaUltimaRevision;

    private EstadoAeronave estado;

    private String especificacionesTecnicas;
}
