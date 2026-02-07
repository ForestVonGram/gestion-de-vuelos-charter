package com.paeldav.backend.application.dto.vuelo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

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
    private String origen;

    @NotBlank(message = "El destino es obligatorio")
    private String destino;

    @NotNull(message = "La fecha de salida es obligatoria")
    private LocalDateTime fechaSalidaProgramada;

    @NotNull(message = "La fecha de llegada es obligatoria")
    private LocalDateTime fechaLlegadaProgramada;

    @Positive(message = "El número de pasajeros debe ser positivo")
    private Integer numeroPasajeros;

    private String proposito;

    private String observaciones;
}
