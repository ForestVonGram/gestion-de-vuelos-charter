package com.paeldav.backend.application.dto.aeronave;

import com.paeldav.backend.domain.enums.EstadoAeronave;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO para crear una nueva aeronave.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AeronaveCreateDTO {

    @NotBlank(message = "La matrícula es obligatoria")
    private String matricula; // Matrícula de la aeronave

    @NotBlank(message = "El modelo es obligatorio")
    private String modelo; // Modelo de la aeronave

    private String fabricante; // Fabricante de la aeronave

    @Positive(message = "La capacidad de pasajeros debe ser positiva")
    private Integer capacidadPasajeros; // Número de pasajeros que puede transportar

    @Positive(message = "La capacidad de tripulación debe ser positiva")
    private Integer capacidadTripulacion; // Número de tripulantes necesarios

    private Double autonomiaKm; // Autonomía en kilómetros

    private Double velocidadCruceroKmh; // Velocidad de crucero en km/h

    private LocalDate fechaFabricacion; // Fecha en que fue fabricada

    private LocalDate fechaUltimaRevision; // Fecha de la última revisión

    private EstadoAeronave estado; // Estado actual de la aeronave

    private String especificacionesTecnicas; // Especificaciones técnicas adicionales
}