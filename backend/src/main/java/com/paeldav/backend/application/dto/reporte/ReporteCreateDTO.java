package com.paeldav.backend.application.dto.reporte;

import com.paeldav.backend.domain.enums.TipoReporte;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * DTO para crear reportes operativos.
 * Contiene los parámetros necesarios para generar un nuevo reporte.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteCreateDTO {

    @NotNull(message = "El tipo de reporte es obligatorio")
    private TipoReporte tipo;

    @NotNull(message = "La fecha de inicio del rango es obligatoria")
    private LocalDateTime fechaInicioRango;

    @NotNull(message = "La fecha de fin del rango es obligatoria")
    private LocalDateTime fechaFinRango;

    private String descripcion;
    private String observaciones;
    
    // Parámetros opcionales de filtrado
    private Long aeronaveId; // Para reportes de flota
    private Long tripulanteId; // Para reportes de horas
}
