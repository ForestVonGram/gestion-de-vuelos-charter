package com.paeldav.backend.application.dto.registrohorasvuelo;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para la creación de un RegistroHorasVuelo.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroHorasVueloCreateDTO {

    @NotNull(message = "El tripulante es obligatorio")
    private Long tripulanteId;

    @NotNull(message = "El vuelo es obligatorio")
    private Long vueloId;

    @NotNull(message = "Las horas voladas son obligatorias")
    @Positive(message = "Las horas deben ser positivas")
    private Double horasVoladas;

    private String funcionDesempenada;
    private LocalDateTime horaDespegue;
    private LocalDateTime horaAterrizaje;
    private String tipoVuelo;
    private String condicionesMeteorologicas;
    private String observaciones;
}
