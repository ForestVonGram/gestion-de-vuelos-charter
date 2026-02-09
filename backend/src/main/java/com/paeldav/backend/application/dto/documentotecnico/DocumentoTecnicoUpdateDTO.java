package com.paeldav.backend.application.dto.documentotecnico;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para la actualización de un DocumentoTecnico.
 * Permite actualizar metadatos sin cambiar el archivo original.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoTecnicoUpdateDTO {

    private String nombre;

    private String tipo;

    private String descripcion;

    private LocalDateTime fechaVencimiento;

    private String numeroDocumento;

    private String observaciones;

    private Boolean vigente;
}
