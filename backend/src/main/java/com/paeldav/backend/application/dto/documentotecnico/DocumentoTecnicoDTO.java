package com.paeldav.backend.application.dto.documentotecnico;

import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO de respuesta para DocumentoTecnico.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoTecnicoDTO {
    private Long id;
    private Long aeronaveId;
    private String aeronaveMatricula;
    private String nombre;
    private String tipo;
    private String descripcion;
    private String urlDocumento;
    private String idCloudinary;
    private LocalDateTime fechaCarga;
    private LocalDateTime fechaVencimiento;
    private String numeroDocumento;
    private Long tamañoBytes;
    private String tipoArchivo;
    private Long cargadoPorId;
    private String cargadoPorNombre;
    private String observaciones;
    private Boolean vigente;
}
