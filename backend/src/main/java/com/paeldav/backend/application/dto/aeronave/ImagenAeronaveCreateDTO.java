package com.paeldav.backend.application.dto.aeronave;

import com.paeldav.backend.domain.enums.TipoImagenAeronave;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * DTO para crear una nueva imagen de aeronave.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagenAeronaveCreateDTO {

    @NotNull(message = "El tipo de imagen es obligatorio")
    private TipoImagenAeronave tipo; // Tipo de imagen (vista general, cabina, etc.)

    private String descripcion; // Descripción de la imagen

    private Integer ordenVisualizacion; // Orden para mostrar la imagen
}