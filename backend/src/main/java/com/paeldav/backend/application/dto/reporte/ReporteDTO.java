package com.paeldav.backend.application.dto.reporte;

import com.paeldav.backend.domain.enums.TipoReporte;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para lectura de reportes.
 * Contiene toda la información de un reporte generado.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteDTO {

    private Long id;
    private TipoReporte tipo;
    private String descripcion;
    private LocalDateTime fechaGeneracion;
    private LocalDateTime fechaInicioRango;
    private LocalDateTime fechaFinRango;
    private String generadoPorNombre; // Nombre del usuario que generó el reporte
    private String rutaArchivo;
    private String datosAgregados;
    private Integer numeroRegistros;
    private String observaciones;
}
