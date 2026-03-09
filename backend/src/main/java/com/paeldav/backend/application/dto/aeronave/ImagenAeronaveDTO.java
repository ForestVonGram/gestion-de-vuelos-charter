package com.paeldav.backend.application.dto.aeronave;

import com.paeldav.backend.domain.enums.TipoImagenAeronave;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO con los datos de una imagen de aeronave para responder.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagenAeronaveDTO {
    private Long id; // Identificador único
    private String urlImagen; // URL de la imagen
    private String idCloudinary; // ID en Cloudinary
    private TipoImagenAeronave tipo; // Tipo de imagen
    private String descripcion; // Descripción
    private Integer ordenVisualizacion; // Orden de visualización
    private LocalDateTime fechaCarga; // Fecha de carga
    private Long tamanoBytes; // Tamaño en bytes
    private String cargadoPorNombre; // Nombre de quien cargó la imagen
}