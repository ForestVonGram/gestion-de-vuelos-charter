package com.paeldav.backend.application.dto.aeronave;

import com.paeldav.backend.domain.enums.TipoImagenAeronave;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para ImagenAeronave.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImagenAeronaveDTO {
    private Long id;
    private String urlImagen;
    private String idCloudinary;
    private TipoImagenAeronave tipo;
    private String descripcion;
    private Integer ordenVisualizacion;
    private LocalDateTime fechaCarga;
    private Long tamanoBytes;
    private String cargadoPorNombre;
}
