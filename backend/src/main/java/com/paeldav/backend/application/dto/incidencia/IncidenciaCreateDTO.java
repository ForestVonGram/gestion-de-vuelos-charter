package com.paeldav.backend.application.dto.incidencia;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO para la creación de una nueva Incidencia.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidenciaCreateDTO {

    @NotNull(message = "El vuelo es obligatorio")
    private Long vueloId;

    @NotNull(message = "El tripulante que reporta es obligatorio")
    private Long reportadoPorId;

    @NotBlank(message = "El título es obligatorio")
    private String titulo;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    private String gravedad;
}
