package com.paeldav.backend.application.dto.aeronave;

import com.paeldav.backend.domain.enums.EstadoAeronave;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;

/**
 * DTO para actualizar una aeronave existente.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AeronaveUpdateDTO {

    private String fabricante; // Fabricante

    @Positive(message = "La capacidad de pasajeros debe ser positiva")
    private Integer capacidadPasajeros; // Nueva capacidad de pasajeros

    @Positive(message = "La capacidad de tripulación debe ser positiva")
    private Integer capacidadTripulacion; // Nueva capacidad de tripulación

    private Double autonomiaKm; // Nueva autonomía

    private Double velocidadCruceroKmh; // Nueva velocidad de crucero

    private LocalDate fechaFabricacion; // Nueva fecha de fabricación

    private LocalDate fechaUltimaRevision; // Nueva fecha de última revisión

    private EstadoAeronave estado; // Nuevo estado

    private String especificacionesTecnicas; // Nuevas especificaciones técnicas
}