package com.paeldav.backend.application.dto.vuelo;

import jakarta.validation.constraints.*;
import lombok.*;
import com.paeldav.backend.infraestructure.validation.*;

import java.time.LocalDateTime;

/**
 * DTO para la creación de un nuevo Vuelo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VueloCreateDTO {

    @NotNull(message = "El usuario solicitante es obligatorio")
    private Long usuarioId;

    @NotBlank(message = "El origen es obligatorio")
    @Size(min = 2, max = 100, message = "El origen debe tener entre 2 y 100 caracteres")
    private String origen;

    @NotBlank(message = "El destino es obligatorio")
    @Size(min = 2, max = 100, message = "El destino debe tener entre 2 y 100 caracteres")
    private String destino;

    @NotNull(message = "La fecha de salida es obligatoria")
    @ValidFechaFutura(message = "La fecha de salida debe ser futura")
    private LocalDateTime fechaSalidaProgramada;

    @NotNull(message = "La fecha de llegada es obligatoria")
    @ValidFechaFutura(message = "La fecha de llegada debe ser futura")
    private LocalDateTime fechaLlegadaProgramada;

    @NotNull(message = "El número de pasajeros es obligatorio")
    @Positive(message = "El número de pasajeros debe ser positivo")
    @Max(value = 500, message = "El número de pasajeros no puede exceder 500")
    private Integer numeroPasajeros;

    @Size(max = 500, message = "El propósito no puede exceder 500 caracteres")
    private String proposito;

    @Size(max = 1000, message = "Las observaciones no pueden exceder 1000 caracteres")
    private String observaciones;
}
