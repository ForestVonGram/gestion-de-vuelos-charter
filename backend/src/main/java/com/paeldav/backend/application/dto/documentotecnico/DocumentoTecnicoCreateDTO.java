package com.paeldav.backend.application.dto.documentotecnico;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para la creación de un DocumentoTecnico.
 * Nota: La URL del documento es generada por el servicio luego del upload a Cloudinary.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoTecnicoCreateDTO {

    @NotNull(message = "La aeronave es obligatoria")
    private Long aeronaveId;

    @NotBlank(message = "El nombre del documento es obligatorio")
    private String nombre;

    @NotBlank(message = "El tipo de documento es obligatorio")
    private String tipo; // MANUAL, CERTIFICADO, INSPECCION, MANTENIMIENTO, LICENCIA, etc.

    private String descripcion;

    private LocalDateTime fechaVencimiento;

    private String numeroDocumento;

    private Long cargadoPorId;

    private String observaciones;

    // Nota: urlDocumento e idCloudinary se asignan durante el upload del archivo
}
