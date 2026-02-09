package com.paeldav.backend.application.dto.aeronave;

import com.paeldav.backend.domain.enums.TipoImagenAeronave;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO para la creación de una nueva imagen de aeronave.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagenAeronaveCreateDTO {

    @NotNull(message = "El tipo de imagen es obligatorio")
    private TipoImagenAeronave tipo;

    private String descripcion;

    private Integer ordenVisualizacion;
}
